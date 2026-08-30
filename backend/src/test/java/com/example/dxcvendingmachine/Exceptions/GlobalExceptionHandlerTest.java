package com.example.dxcvendingmachine.Exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void productNotFoundReturnsNotFoundProblemDetail() {
        UUID productId = UUID.randomUUID();

        ProblemDetail problemDetail = handler.handleProductNotFound(
                new ProductNotFoundException(productId)
        );

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Product Not Found");
        assertThat(problemDetail.getDetail()).isEqualTo("Product with id " + productId + " was not found");
    }

    @Test
    void insufficientFundsReturnsBadRequestProblemDetail() {
        ProblemDetail problemDetail = handler.handleInsufficientFunds(
                new InsufficientFundsException(new BigDecimal("1.00"), new BigDecimal("1.50"))
        );

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Insufficient Funds");
        assertThat(problemDetail.getDetail()).isEqualTo("Insufficient funds. Inserted: 1.00, required: 1.50");
    }

    @Test
    void outOfStockReturnsConflictProblemDetail() {
        ProblemDetail problemDetail = handler.handleConflict(new OutOfStockException());

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Vending Error");
        assertThat(problemDetail.getDetail()).isEqualTo("Product is out of stock");
    }

    @Test
    void unableToReturnChangeReturnsConflictProblemDetail() {
        ProblemDetail problemDetail = handler.handleConflict(new UnableToReturnChangeException());

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Vending Error");
        assertThat(problemDetail.getDetail()).isEqualTo("Unable to return exact change");
    }

    @Test
    void invalidProductPriceReturnsBadRequestProblemDetail() {
        ProblemDetail problemDetail = handler.handleInvalidProduct(
                new InvalidProductPriceException(BigDecimal.ZERO)
        );

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Invalid Product");
        assertThat(problemDetail.getDetail()).isEqualTo(
                "Product price must be greater than zero and use no more "
                        + "than two decimal places. Provided: 0"
        );
    }

    @Test
    void invalidProductQuantityReturnsBadRequestProblemDetail() {
        ProblemDetail problemDetail = handler.handleInvalidProduct(
                new InvalidProductQuantityException(16)
        );

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Invalid Product");
        assertThat(problemDetail.getDetail()).isEqualTo("Product quantity must be between 0 and 15. Provided: 16");
    }

    @Test
    void nonUniqueProductPriceReturnsBadRequestProblemDetail() {
        ProblemDetail problemDetail = handler.handleInvalidProduct(
                new NonUniqueProductPriceException(new BigDecimal("1.50"))
        );

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Invalid Product");
        assertThat(problemDetail.getDetail()).isEqualTo(
                "Each product type must have a different price. Provided duplicate price: 1.50"
        );
    }

    @Test
    void illegalArgumentReturnsBadRequestProblemDetail() {
        ProblemDetail problemDetail = handler.handleIllegalArgument(
                new IllegalArgumentException("Product name cannot be empty")
        );

        assertThat(problemDetail.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(problemDetail.getTitle()).isEqualTo("Invalid Request");
        assertThat(problemDetail.getDetail()).isEqualTo("Product name cannot be empty");
    }
}
