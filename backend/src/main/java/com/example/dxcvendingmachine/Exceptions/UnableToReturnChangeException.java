package com.example.dxcvendingmachine.Exceptions;

public class UnableToReturnChangeException extends RuntimeException {

    public UnableToReturnChangeException() {
        super("Unable to return exact change");
    }
}
