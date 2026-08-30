package com.example.dxcvendingmachine.Repository;

import com.example.dxcvendingmachine.Domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryProductRepositoryTest {

    private InMemoryProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
    }

    @Test
    void savesAndFindsProduct() {
        Product product = new Product(
                UUID.randomUUID(),
                "Coke",
                new BigDecimal("1.50"),
                2
        );

        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getId())
                .orElseThrow();

        assertThat(foundProduct.getName()).isEqualTo("Coke");
    }

    @Test
    void findByIdReturnsCopyOfStoredProduct() {
        Product product = productRepository.save(
                new Product(UUID.randomUUID(), "Coke", new BigDecimal("1.50"), 1)
        );

        Product foundProduct = productRepository.findById(product.getId())
                .orElseThrow();

        foundProduct.setQuantity(15);

        Product storedProduct = productRepository.findById(product.getId())
                .orElseThrow();

        assertThat(storedProduct.getQuantity()).isEqualTo(1);
    }

    @Test
    void findAllReturnsCopiesOfStoredProducts() {
        Product product = productRepository.save(
                new Product(UUID.randomUUID(), "Water", new BigDecimal("1.00"), 1)
        );

        Product listedProduct = productRepository.findAll().getFirst();

        listedProduct.setQuantity(15);

        Product storedProduct = productRepository.findById(product.getId())
                .orElseThrow();

        assertThat(storedProduct.getQuantity()).isEqualTo(1);
    }

    @Test
    void deletesProduct() {
        Product product = productRepository.save(
                new Product(UUID.randomUUID(), "Coke", new BigDecimal("1.50"), 1)
        );

        productRepository.delete(product.getId());

        assertThat(productRepository.findById(product.getId())).isEmpty();
    }
}
