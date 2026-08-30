package com.example.dxcvendingmachine.Service;

import com.example.dxcvendingmachine.Domain.Coin;
import com.example.dxcvendingmachine.Domain.Product;
import com.example.dxcvendingmachine.Domain.VendingSession;
import com.example.dxcvendingmachine.Dto.ProductDto;
import com.example.dxcvendingmachine.Dto.PurchaseResponseDto;
import com.example.dxcvendingmachine.Exceptions.InsufficientFundsException;
import com.example.dxcvendingmachine.Exceptions.OutOfStockException;
import com.example.dxcvendingmachine.Exceptions.ProductNotFoundException;
import com.example.dxcvendingmachine.Exceptions.UnableToReturnChangeException;
import com.example.dxcvendingmachine.Repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class VendingService {

    private final ProductRepository productRepository;
    private final VendingSession vendingSession;

    public VendingService(
            ProductRepository productRepository,
            VendingSession vendingSession) {
        this.productRepository = productRepository;
        this.vendingSession = vendingSession;
    }

    public synchronized void insertCoin(Coin coin) {
        vendingSession.insertCoin(coin);
    }

    public synchronized PurchaseResponseDto purchase(UUID productId) {

        BigDecimal insertedAmount = vendingSession.getInsertedAmount();

        synchronized (productRepository) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductNotFoundException(productId));

            if (product.getQuantity() <= 0) {
                throw new OutOfStockException();
            }

            if (insertedAmount.compareTo(product.getPrice()) < 0) {
                throw new InsufficientFundsException(insertedAmount, product.getPrice());
            }

            BigDecimal change = insertedAmount.subtract(product.getPrice());
            List<Coin> changeCoins = calculateChange(change);

            Product purchasedProduct = new Product(
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity() - 1
            );

            purchasedProduct = productRepository.save(purchasedProduct);

            vendingSession.clear();

            return new PurchaseResponseDto(
                    toDto(purchasedProduct),
                    insertedAmount,
                    change,
                    changeCoins
            );
        }
    }

    public synchronized List<Coin> reset() {
        return vendingSession.clear();
    }

    public BigDecimal getInsertedAmount() {
        return vendingSession.getInsertedAmount();
    }

    private List<Coin> calculateChange(BigDecimal amount) {

        List<Coin> change = new java.util.ArrayList<>();

        List<Coin> denominations = List.of(
                Coin.TWO_EURO,
                Coin.ONE_EURO,
                Coin.FIFTY_CENTS,
                Coin.TWENTY_CENTS,
                Coin.TEN_CENTS,
                Coin.FIVE_CENTS,
                Coin.TWO_CENTS,
                Coin.ONE_CENT
        );

        for (Coin coin : denominations) {
            while (amount.compareTo(coin.getValue()) >= 0) {
                change.add(coin);
                amount = amount.subtract(coin.getValue());
            }
        }

        if (amount.compareTo(BigDecimal.ZERO) != 0) {
            throw new UnableToReturnChangeException();
        }

        return change;
    }

    private ProductDto toDto(Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}
