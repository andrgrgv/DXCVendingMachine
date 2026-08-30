package com.example.dxcvendingmachine.Domain;

import java.math.BigDecimal;

public final class ProductPriceRules {

    private ProductPriceRules() {
    }

    public static boolean isInvalid(BigDecimal price) {
        return price == null
                || price.compareTo(BigDecimal.ZERO) <= 0
                || hasSubCentPrecision(price);
    }

    private static boolean hasSubCentPrecision(BigDecimal price) {
        return price.stripTrailingZeros().scale() > 2;
    }
}
