package com.example.dxcvendingmachine.Domain;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@SessionScope
public class VendingSession {

    private final List<Coin> insertedCoins = new ArrayList<>();

    public void insertCoin(Coin coin) {
        insertedCoins.add(coin);
    }

    public List<Coin> getInsertedCoins() {
        return List.copyOf(insertedCoins);
    }

    public BigDecimal getInsertedAmount() {
        return insertedCoins.stream()
                .map(Coin::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Coin> clear() {
        List<Coin> coins = List.copyOf(insertedCoins);
        insertedCoins.clear();
        return coins;
    }

    public boolean isEmpty() {
        return insertedCoins.isEmpty();
    }
}