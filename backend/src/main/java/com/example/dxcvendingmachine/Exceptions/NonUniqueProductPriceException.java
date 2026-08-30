package com.example.dxcvendingmachine.Exceptions;

import java.math.BigDecimal;

public class NonUniqueProductPriceException extends RuntimeException {

    public NonUniqueProductPriceException(BigDecimal price) {
        super("Each product type must have a different price. Provided duplicate price: " + price);
    }
}
