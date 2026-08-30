package com.example.dxcvendingmachine.ExternalProductClient;

import com.example.dxcvendingmachine.Dto.ProductDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class MockExternalProductClient implements ExternalProductClient {

    @Override
    public List<ProductDto> getProducts() {
        return List.of(
            new ProductDto(UUID.randomUUID(), "Coke", new BigDecimal("1.50"), 10),
            new ProductDto(UUID.randomUUID(), "Water", new BigDecimal("1.00"), 15),
            new ProductDto(UUID.randomUUID(),"Chocolate", new BigDecimal("2.20"), 8)
        );
    }
}