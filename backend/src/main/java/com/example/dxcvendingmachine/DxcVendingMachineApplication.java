package com.example.dxcvendingmachine;

import com.example.dxcvendingmachine.Domain.Product;
import com.example.dxcvendingmachine.ExternalProductClient.ExternalProductClient;
import com.example.dxcvendingmachine.Repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DxcVendingMachineApplication {

    public static void main(String[] args) {
        SpringApplication.run(DxcVendingMachineApplication.class, args);
    }

    @Bean
    CommandLineRunner loadInitialProducts(
            ExternalProductClient externalProductClient,
            ProductRepository productRepository) {
        return args -> externalProductClient.getProducts()
                .stream()
                .map(product -> new Product(
                        product.id(),
                        product.name(),
                        product.price(),
                        product.quantity()
                ))
                .forEach(productRepository::save);
    }
}
