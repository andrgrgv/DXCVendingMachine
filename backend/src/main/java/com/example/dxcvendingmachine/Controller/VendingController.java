package com.example.dxcvendingmachine.Controller;

import com.example.dxcvendingmachine.Domain.Coin;
import com.example.dxcvendingmachine.Dto.PurchaseResponseDto;
import com.example.dxcvendingmachine.Service.VendingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vending")
public class VendingController {

    private final VendingService vendingService;

    public VendingController(VendingService vendingService) {
        this.vendingService = vendingService;
    }

    @PostMapping("/coins")
    public ResponseEntity<BigDecimal> insertCoin(
            @RequestBody Coin coin) {

        if (coin == null) {
            throw new IllegalArgumentException(
                    "Coin denomination is required"
            );
        }

        vendingService.insertCoin(coin);

        return ResponseEntity.ok(
                vendingService.getInsertedAmount()
        );
    }

    @PostMapping("/purchase/{productId}")
    public ResponseEntity<PurchaseResponseDto> purchase(
            @PathVariable UUID productId) {

        return ResponseEntity.ok(
                vendingService.purchase(productId)
        );
    }

    @PostMapping("/reset")
    public ResponseEntity<List<Coin>> reset() {

        return ResponseEntity.ok(
                vendingService.reset()
        );
    }

    @GetMapping("/session")
    public ResponseEntity<BigDecimal> getInsertedAmount() {

        return ResponseEntity.ok(
                vendingService.getInsertedAmount()
        );
    }
}
