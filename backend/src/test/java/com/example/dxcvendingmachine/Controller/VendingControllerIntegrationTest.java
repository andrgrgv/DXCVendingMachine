package com.example.dxcvendingmachine.Controller;

import com.example.dxcvendingmachine.Dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class VendingControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void insertCoinsTracksAmountInCurrentHttpSession() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/vending/coins")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ONE_EURO\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1.00));

        mockMvc.perform(post("/api/vending/coins")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"FIFTY_CENTS\""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1.50));
    }

    @Test
    void vendingAmountIsIndependentBetweenHttpSessions() throws Exception {
        MockHttpSession firstSession = new MockHttpSession();
        MockHttpSession secondSession = new MockHttpSession();

        mockMvc.perform(post("/api/vending/coins")
                        .session(firstSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ONE_EURO\""))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/vending/session")
                        .session(firstSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1.00));

        mockMvc.perform(get("/api/vending/session")
                        .session(secondSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));
    }

    @Test
    void resetReturnsInsertedCoinsAndClearsSession() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/vending/coins")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ONE_EURO\""))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/vending/coins")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"TWENTY_CENTS\""))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/vending/reset")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("ONE_EURO"))
                .andExpect(jsonPath("$[1]").value("TWENTY_CENTS"));

        mockMvc.perform(get("/api/vending/session")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));
    }

    @Test
    void purchaseReturnsProductAndChange() throws Exception {
        MockHttpSession session = new MockHttpSession();

        ProductDto coke = findInitialProductByName("Coke");

        mockMvc.perform(post("/api/vending/coins")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"TWO_EURO\""))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/vending/purchase/{productId}", coke.id())
                                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.name").value("Coke"))
                .andExpect(jsonPath("$.product.quantity").value(9))
                .andExpect(jsonPath("$.paidAmount").value(2.00))
                .andExpect(jsonPath("$.changeAmount").value(0.50))
                .andExpect(jsonPath("$.changeCoins[0]")
                        .value("FIFTY_CENTS"));

        mockMvc.perform(get("/api/vending/session")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));
    }

    @Test
    void purchaseWithInsufficientFundsReturnsBadRequest()
            throws Exception {

        MockHttpSession session = new MockHttpSession();

        ProductDto coke = findInitialProductByName("Coke");

        mockMvc.perform(post("/api/vending/coins")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"ONE_EURO\""))
                .andExpect(status().isOk());

        mockMvc.perform(
                        post("/api/vending/purchase/{productId}", coke.id())
                                .session(session))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Insufficient Funds"));
    }

    @Test
    void purchaseMissingProductReturnsNotFound() throws Exception {

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(
                        post(
                                "/api/vending/purchase/{productId}",
                                UUID.randomUUID()
                        )
                                .session(session))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Product Not Found"));
    }

    @Test
    void invalidCoinReturnsBadRequest() throws Exception {

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/vending/coins")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"THREE_EURO\""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Request Body"));
    }

    @Test
    void nullCoinReturnsBadRequestAndDoesNotChangeSession()
            throws Exception {

        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/api/vending/coins")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Request Body"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Request body is missing or contains "
                                        + "invalid values"
                        ));

        mockMvc.perform(get("/api/vending/session")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(0));
    }

    private ProductDto findInitialProductByName(String name)
            throws Exception {

        String response = mockMvc.perform(
                        get("/api/products")
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ProductDto> products =
                objectMapper.readValue(
                        response,
                        new TypeReference<List<ProductDto>>() {}
                );

        return products.stream()
                .filter(product -> product.name().equals(name))
                .findFirst()
                .orElseThrow();
    }
}
