package com.example.dxcvendingmachine.Repository;

import com.example.dxcvendingmachine.Domain.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository {

    Product save(Product product);
    Optional<Product> findById(UUID id);
    void delete(UUID id);
    List<Product> findAll();
}
