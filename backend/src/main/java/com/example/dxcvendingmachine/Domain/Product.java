package com.example.dxcvendingmachine.Domain;

import com.example.dxcvendingmachine.Exceptions.InvalidProductPriceException;
import com.example.dxcvendingmachine.Exceptions.InvalidProductQuantityException;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

public class Product{
    @Getter @Setter
    private UUID id;
    @Setter @Getter
    private String name;
    @Getter
    private BigDecimal price;
    @Getter
    private int quantity;

    public Product(UUID id, String name, BigDecimal price, int quantity) {
        this.id = id;
        this.name = name;
        setPrice(price);
        setQuantity(quantity);
    }

    public void setPrice(BigDecimal price) {
        if (ProductPriceRules.isInvalid(price)) {
            throw new InvalidProductPriceException(price);
        }
        this.price = price;
    }

    public void setQuantity(int quantity) {
        if (quantity < 0 || quantity > 15) {
            throw new InvalidProductQuantityException(quantity);
        }
        this.quantity = quantity;
    }
}
