package com.example.dxcvendingmachine.Exceptions;

public class InvalidProductQuantityException extends RuntimeException {

    public InvalidProductQuantityException(int quantity) {
        super("Product quantity must be between 0 and 15. Provided: " + quantity);
    }
}
