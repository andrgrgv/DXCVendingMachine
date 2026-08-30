package com.example.dxcvendingmachine.Service;

import com.example.dxcvendingmachine.Domain.Product;
import com.example.dxcvendingmachine.Dto.ProductDto;
import com.example.dxcvendingmachine.Exceptions.InvalidProductPriceException;
import com.example.dxcvendingmachine.Exceptions.InvalidProductQuantityException;
import com.example.dxcvendingmachine.Exceptions.NonUniqueProductPriceException;
import com.example.dxcvendingmachine.Exceptions.ProductNotFoundException;
import com.example.dxcvendingmachine.Repository.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductServiceTest {

    private InMemoryProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        productService = new ProductService(productRepository);
    }

    @Test
    void createsProductInApplicationState() {
        ProductDto created = productService.createProduct(
                new ProductDto(null, "Coke", new BigDecimal("1.50"), 10)
        );

        assertThat(created.id()).isNotNull();
        assertThat(created.name()).isEqualTo("Coke");
        assertThat(created.price()).isEqualByComparingTo("1.50");
        assertThat(productService.getAllProducts()).hasSize(1);
    }

    @Test
    void rejectsBlankProductName() {
        assertThatThrownBy(() -> productService.createProduct(
                new ProductDto(null, " ", new BigDecimal("1.50"), 10)
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsInvalidProductPrice() {
        assertThatThrownBy(() -> productService.createProduct(
                new ProductDto(null, "Coke", BigDecimal.ZERO, 10)
        )).isInstanceOf(InvalidProductPriceException.class);
    }

    @Test
    void rejectsSubCentProductPrice() {
        assertThatThrownBy(() -> productService.createProduct(
                new ProductDto(null, "Coke", new BigDecimal("1.005"), 10)
        )).isInstanceOf(InvalidProductPriceException.class);
    }

    @Test
    void rejectsInvalidProductQuantity() {
        assertThatThrownBy(() -> productService.createProduct(
                new ProductDto(null, "Coke", new BigDecimal("1.50"), 16)
        )).isInstanceOf(InvalidProductQuantityException.class);
    }

    @Test
    void rejectsDuplicatePriceOnCreate() {
        productService.createProduct(new ProductDto(null, "Coke", new BigDecimal("1.50"), 10));

        assertThatThrownBy(() -> productService.createProduct(
                new ProductDto(null, "Water", new BigDecimal("1.50"), 10)
        )).isInstanceOf(NonUniqueProductPriceException.class);
    }

    @Test
    void rejectsDuplicatePriceOnUpdate() {
        Product first = productRepository.save(
                new Product(UUID.randomUUID(), "Coke", new BigDecimal("1.50"), 10)
        );
        productRepository.save(
                new Product(UUID.randomUUID(), "Water", new BigDecimal("1.00"), 10)
        );

        assertThatThrownBy(() -> productService.updateProduct(
                first.getId(),
                new ProductDto(first.getId(), "Coke", new BigDecimal("1.00"), 10)
        )).isInstanceOf(NonUniqueProductPriceException.class);
    }

    @Test
    void deletesProduct() {
        ProductDto created = productService.createProduct(
                new ProductDto(null, "Water", new BigDecimal("1.00"), 10)
        );

        productService.deleteProduct(created.id());

        assertThat(productService.getAllProducts()).isEmpty();
    }

    @Test
    void throwsWhenProductDoesNotExist() {
        UUID missingId = UUID.randomUUID();

        assertThatThrownBy(() -> productService.getProduct(missingId))
                .isInstanceOf(ProductNotFoundException.class);
    }
}
