package com.example.dxcvendingmachine.Domain;

import com.example.dxcvendingmachine.Exceptions.InvalidProductPriceException;
import com.example.dxcvendingmachine.Exceptions.InvalidProductQuantityException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void constructorAcceptsValidProduct() {
        Product product = new Product(
                UUID.randomUUID(),
                "Water",
                new BigDecimal("1.00"),
                15
        );

        assertThat(product.getName()).isEqualTo("Water");
        assertThat(product.getPrice()).isEqualByComparingTo("1.00");
        assertThat(product.getQuantity()).isEqualTo(15);
    }

    @Test
    void constructorRejectsNullPrice() {
        assertThatThrownBy(() -> new Product(UUID.randomUUID(), "Water", null, 1))
                .isInstanceOf(InvalidProductPriceException.class)
                .hasMessageContaining("greater than zero");
    }

    @Test
    void constructorRejectsZeroPrice() {
        assertThatThrownBy(() -> new Product(UUID.randomUUID(), "Water", BigDecimal.ZERO, 1))
                .isInstanceOf(InvalidProductPriceException.class);
    }

    @Test
    void constructorRejectsNegativePrice() {
        assertThatThrownBy(() -> new Product(UUID.randomUUID(), "Water", new BigDecimal("-0.50"), 1))
                .isInstanceOf(InvalidProductPriceException.class);
    }

    @Test
    void constructorRejectsSubCentPrice() {
        assertThatThrownBy(() -> new Product(UUID.randomUUID(), "Water", new BigDecimal("1.005"), 1))
                .isInstanceOf(InvalidProductPriceException.class);
    }

    @Test
    void constructorRejectsNegativeQuantity() {
        assertThatThrownBy(() -> new Product(UUID.randomUUID(), "Water", new BigDecimal("1.00"), -1))
                .isInstanceOf(InvalidProductQuantityException.class);
    }

    @Test
    void constructorRejectsQuantityAboveInventoryLimit() {
        assertThatThrownBy(() -> new Product(UUID.randomUUID(), "Water", new BigDecimal("1.00"), 16))
                .isInstanceOf(InvalidProductQuantityException.class);
    }
}
