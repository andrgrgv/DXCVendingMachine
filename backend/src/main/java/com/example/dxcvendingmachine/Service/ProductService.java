package com.example.dxcvendingmachine.Service;

import com.example.dxcvendingmachine.Domain.Product;
import com.example.dxcvendingmachine.Domain.ProductPriceRules;
import com.example.dxcvendingmachine.Dto.ProductDto;
import com.example.dxcvendingmachine.Exceptions.InvalidProductPriceException;
import com.example.dxcvendingmachine.Exceptions.InvalidProductQuantityException;
import com.example.dxcvendingmachine.Exceptions.NonUniqueProductPriceException;
import com.example.dxcvendingmachine.Exceptions.ProductNotFoundException;
import com.example.dxcvendingmachine.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public ProductDto getProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return toDto(product);
    }

    public ProductDto createProduct(ProductDto dto) {
        validateProduct(dto);

        synchronized (productRepository) {
            validateUniquePrice(dto.price(), null);

            Product product = new Product(
                    UUID.randomUUID(),
                    dto.name(),
                    dto.price(),
                    dto.quantity()
            );

            return toDto(productRepository.save(product));
        }
    }

    public ProductDto updateProduct(UUID id, ProductDto dto) {
        validateProduct(dto);

        synchronized (productRepository) {
            Product existingProduct = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(id));

            validateUniquePrice(dto.price(), id);

            Product updatedProduct = new Product(
                    existingProduct.getId(),
                    dto.name(),
                    dto.price(),
                    dto.quantity()
            );

            return toDto(productRepository.save(updatedProduct));
        }
    }

    public void deleteProduct(UUID id) {
        synchronized (productRepository) {
            if (productRepository.findById(id).isEmpty()) {
                throw new ProductNotFoundException(id);
            }

            productRepository.delete(id);
        }
    }

    private void validateProduct(ProductDto dto) {

        if (dto.name() == null || dto.name().isBlank()) {
            throw new IllegalArgumentException(
                    "Product name cannot be empty"
            );
        }

        if (ProductPriceRules.isInvalid(dto.price())) {
            throw new InvalidProductPriceException(dto.price());
        }

        if (dto.quantity() < 0 || dto.quantity() > 15) {
            throw new InvalidProductQuantityException(dto.quantity());
        }
    }

    private void validateUniquePrice(BigDecimal price, UUID productId) {

        boolean priceExists = productRepository.findAll()
                .stream()
                .anyMatch(product ->
                        product.getPrice().compareTo(price) == 0
                                && !product.getId().equals(productId)
                );

        if (priceExists) {
            throw new NonUniqueProductPriceException(price);
        }
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}
