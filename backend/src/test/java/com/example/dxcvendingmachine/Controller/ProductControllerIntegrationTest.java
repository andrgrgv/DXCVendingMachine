package com.example.dxcvendingmachine.Controller;

import com.example.dxcvendingmachine.Dto.ProductDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getProductsReturnsInitialProductsLoadedFromMockExternalResource()
            throws Exception {

        String response = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ProductDto> products =
                objectMapper.readValue(
                        response,
                        new TypeReference<List<ProductDto>>() {}
                );

        assertThat(products)
                .extracting(ProductDto::name)
                .containsExactlyInAnyOrder(
                        "Coke",
                        "Water",
                        "Chocolate"
                );
    }

    @Test
    void getProductReturnsExistingProduct() throws Exception {

        String response = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ProductDto> products =
                objectMapper.readValue(
                        response,
                        new TypeReference<List<ProductDto>>() {}
                );

        ProductDto product = products.get(0);

        mockMvc.perform(get("/api/products/{id}", product.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.id().toString()))
                .andExpect(jsonPath("$.name").value(product.name()))
                .andExpect(jsonPath("$.quantity").value(product.quantity()));
    }

    @Test
    void getMissingProductReturnsProblemDetail() throws Exception {

        UUID missingId = UUID.randomUUID();

        mockMvc.perform(get("/api/products/{id}", missingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Product Not Found"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Product with id "
                                        + missingId
                                        + " was not found"
                        ));
    }

    @Test
    void createProductAddsProductToApplicationState() throws Exception {

        ProductDto request = new ProductDto(
                null,
                "Chips",
                new BigDecimal("1.75"),
                5
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Chips"))
                .andExpect(jsonPath("$.price").value(1.75))
                .andExpect(jsonPath("$.quantity").value(5));

        String response = mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ProductDto> products =
                objectMapper.readValue(
                        response,
                        new TypeReference<List<ProductDto>>() {}
                );

        assertThat(products)
                .extracting(ProductDto::name)
                .contains("Chips");
    }

    @Test
    void createProductRejectsDuplicatePrice() throws Exception {

        ProductDto request = new ProductDto(
                null,
                "Duplicate",
                new BigDecimal("1.50"),
                5
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Product"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Each product type must have a different price. "
                                        + "Provided duplicate price: 1.50"
                        ));
    }

    @Test
    void createProductRejectsInvalidQuantity() throws Exception {

        ProductDto request = new ProductDto(
                null,
                "Too Many",
                new BigDecimal("3.00"),
                16
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Product"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Product quantity must be between 0 and 15. "
                                        + "Provided: 16"
                        ));
    }

    @Test
    void createProductRejectsInvalidPrice() throws Exception {

        ProductDto request = new ProductDto(
                null,
                "Free",
                BigDecimal.ZERO,
                1
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Product"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Product price must be greater than zero "
                                        + "and use no more than two "
                                        + "decimal places. "
                                        + "Provided: 0"
                        ));
    }

    @Test
    void createProductRejectsSubCentPrice() throws Exception {

        ProductDto request = new ProductDto(
                null,
                "Precise",
                new BigDecimal("1.005"),
                1
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Product"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Product price must be greater than zero "
                                        + "and use no more than two "
                                        + "decimal places. "
                                        + "Provided: 1.005"
                        ));
    }

    @Test
    void updateProductChangesExistingProduct() throws Exception {

        ProductDto created =
                createProduct(
                        "Chips",
                        new BigDecimal("1.75"),
                        5
                );

        ProductDto update = new ProductDto(
                created.id(),
                "Salted Chips",
                new BigDecimal("1.80"),
                4
        );

        mockMvc.perform(put("/api/products/{id}", created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(created.id().toString()))
                .andExpect(jsonPath("$.name")
                        .value("Salted Chips"))
                .andExpect(jsonPath("$.price")
                        .value(1.80))
                .andExpect(jsonPath("$.quantity")
                        .value(4));
    }

    @Test
    void updateMissingProductReturnsProblemDetail() throws Exception {

        UUID missingId = UUID.randomUUID();

        ProductDto update = new ProductDto(
                missingId,
                "Chips",
                new BigDecimal("1.80"),
                5
        );

        mockMvc.perform(put("/api/products/{id}", missingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Product Not Found"))
                .andExpect(jsonPath("$.detail")
                        .value(
                                "Product with id "
                                        + missingId
                                        + " was not found"
                        ));
    }

    @Test
    void updateProductRejectsDuplicatePrice() throws Exception {

        ProductDto created =
                createProduct(
                        "Chips",
                        new BigDecimal("1.75"),
                        5
                );

        ProductDto update = new ProductDto(
                created.id(),
                "Chips",
                new BigDecimal("1.50"),
                5
        );

        mockMvc.perform(put("/api/products/{id}", created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title")
                        .value("Invalid Product"));
    }

    @Test
    void deleteProductRemovesExistingProduct() throws Exception {

        ProductDto created =
                createProduct(
                        "Chips",
                        new BigDecimal("1.75"),
                        5
                );

        mockMvc.perform(delete("/api/products/{id}", created.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/products/{id}", created.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMissingProductReturnsProblemDetail() throws Exception {

        UUID missingId = UUID.randomUUID();

        mockMvc.perform(delete("/api/products/{id}", missingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title")
                        .value("Product Not Found"));
    }

    private ProductDto createProduct(
            String name,
            BigDecimal price,
            int quantity) throws Exception {

        ProductDto request = new ProductDto(
                null,
                name,
                price,
                quantity
        );

        String response = mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(request)
                                )
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(
                response,
                ProductDto.class
        );
    }
}
