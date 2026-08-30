package com.example.dxcvendingmachine.Exceptions;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(BigDecimal insertedAmount, BigDecimal requiredAmount) {
        super("Insufficient funds. Inserted: " + insertedAmount + ", required: " + requiredAmount);
    }
}
