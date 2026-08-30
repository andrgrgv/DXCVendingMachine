package com.example.dxcvendingmachine.Dto;

import com.example.dxcvendingmachine.Domain.Coin;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseResponseDto(
        ProductDto product,
        BigDecimal paidAmount,
        BigDecimal changeAmount,
        List<Coin> changeCoins
) {
}