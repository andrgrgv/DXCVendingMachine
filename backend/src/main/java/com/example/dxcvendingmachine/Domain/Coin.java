package com.example.dxcvendingmachine.Domain;

import java.math.BigDecimal;

public enum Coin {

    ONE_CENT(new BigDecimal("0.01")),
    TWO_CENTS(new BigDecimal("0.02")),
    FIVE_CENTS(new BigDecimal("0.05")),
    TEN_CENTS(new BigDecimal("0.10")),
    TWENTY_CENTS(new BigDecimal("0.20")),
    FIFTY_CENTS(new BigDecimal("0.50")),
    ONE_EURO(new BigDecimal("1.00")),
    TWO_EURO(new BigDecimal("2.00"));

    private final BigDecimal value;

    Coin(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }
}
