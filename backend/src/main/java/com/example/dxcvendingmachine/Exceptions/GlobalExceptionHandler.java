package com.example.dxcvendingmachine.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handleProductNotFound(ProductNotFoundException exception) {
        return problemDetail(HttpStatus.NOT_FOUND, "Product Not Found", exception.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    ProblemDetail handleInsufficientFunds(InsufficientFundsException exception) {
        return problemDetail(HttpStatus.BAD_REQUEST, "Insufficient Funds", exception.getMessage());
    }

    @ExceptionHandler({
            OutOfStockException.class,
            UnableToReturnChangeException.class
    })
    ProblemDetail handleConflict(RuntimeException exception) {
        return problemDetail(HttpStatus.CONFLICT, "Vending Error", exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ProblemDetail handleIllegalArgument(IllegalArgumentException exception) {
        return problemDetail(HttpStatus.BAD_REQUEST, "Invalid Request", exception.getMessage());
    }

    @ExceptionHandler({
            InvalidProductPriceException.class,
            InvalidProductQuantityException.class,
            NonUniqueProductPriceException.class
    })
    ProblemDetail handleInvalidProduct(RuntimeException exception) {
        return problemDetail(HttpStatus.BAD_REQUEST, "Invalid Product", exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail handleValidation(MethodArgumentNotValidException exception) {
        String detail = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Request validation failed");

        return problemDetail(HttpStatus.BAD_REQUEST, "Validation Failed", detail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail handleUnreadableMessage(HttpMessageNotReadableException exception) {
        return problemDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid Request Body",
                "Request body is missing or contains invalid values"
        );
    }

    private ProblemDetail problemDetail(HttpStatus status, String title, String detail) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
