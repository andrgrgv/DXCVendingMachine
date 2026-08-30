package com.example.dxcvendingmachine.Exceptions;

public class OutOfStockException extends RuntimeException {

    public OutOfStockException() {
        super("Product is out of stock");
    }
}
