package com.example.dxcvendingmachine.Repository;

import com.example.dxcvendingmachine.Domain.Product;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Repository
public class InMemoryProductRepository implements ProductRepository {

    private final Map<UUID, Product> products = new ConcurrentHashMap<>();

    @Override
    public synchronized List<Product> findAll() {
        return products.values()
                .stream()
                .map(this::copy)
                .toList();
    }

    @Override
    public synchronized Optional<Product> findById(UUID id) {
        return Optional.ofNullable(products.get(id))
                .map(this::copy);
    }

    @Override
    public synchronized Product save(Product product) {
        Product storedProduct = copy(product);
        products.put(storedProduct.getId(), storedProduct);

        return copy(storedProduct);
    }

    @Override
    public synchronized void delete(UUID id) {
        products.remove(id);
    }

    private Product copy(Product product) {
        return new Product(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}
