package com.example.dxcvendingmachine.Exceptions;

import java.math.BigDecimal;

public class InvalidProductPriceException extends RuntimeException {

    public InvalidProductPriceException(BigDecimal price) {
        super(
                "Product price must be greater than zero and use no more "
                        + "than two decimal places. Provided: "
                        + price
        );
    }
}
