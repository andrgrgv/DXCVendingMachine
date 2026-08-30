package com.example.dxcvendingmachine.Service;

import com.example.dxcvendingmachine.Domain.Coin;
import com.example.dxcvendingmachine.Domain.Product;
import com.example.dxcvendingmachine.Domain.VendingSession;
import com.example.dxcvendingmachine.Dto.PurchaseResponseDto;
import com.example.dxcvendingmachine.Exceptions.InsufficientFundsException;
import com.example.dxcvendingmachine.Exceptions.OutOfStockException;
import com.example.dxcvendingmachine.Exceptions.ProductNotFoundException;
import com.example.dxcvendingmachine.Repository.InMemoryProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VendingServiceTest {

    private InMemoryProductRepository productRepository;
    private VendingService vendingService;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        vendingService = new VendingService(productRepository, new VendingSession());
    }

    @Test
    void insertCoinIncreasesInsertedAmount() {
        vendingService.insertCoin(Coin.ONE_EURO);
        vendingService.insertCoin(Coin.FIFTY_CENTS);

        assertThat(vendingService.getInsertedAmount()).isEqualByComparingTo("1.50");
    }

    @Test
    void resetReturnsInsertedCoinsAndClearsAmount() {
        vendingService.insertCoin(Coin.ONE_EURO);
        vendingService.insertCoin(Coin.TWENTY_CENTS);

        List<Coin> returnedCoins = vendingService.reset();

        assertThat(returnedCoins).containsExactly(Coin.ONE_EURO, Coin.TWENTY_CENTS);
        assertThat(vendingService.getInsertedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void purchaseDecreasesStockAndReturnsChange() {
        Product product = productRepository.save(
                new Product(UUID.randomUUID(), "Coke", new BigDecimal("1.37"), 2)
        );
        vendingService.insertCoin(Coin.TWO_EURO);

        PurchaseResponseDto response = vendingService.purchase(product.getId());

        assertThat(response.product().quantity()).isEqualTo(1);
        assertThat(response.paidAmount()).isEqualByComparingTo("2.00");
        assertThat(response.changeAmount()).isEqualByComparingTo("0.63");
        assertThat(response.changeCoins()).containsExactly(
                Coin.FIFTY_CENTS,
                Coin.TEN_CENTS,
                Coin.TWO_CENTS,
                Coin.ONE_CENT
        );
        assertThat(vendingService.getInsertedAmount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void purchaseRejectsInsufficientFunds() {
        Product product = productRepository.save(
                new Product(UUID.randomUUID(), "Coke", new BigDecimal("1.50"), 2)
        );
        vendingService.insertCoin(Coin.ONE_EURO);

        assertThatThrownBy(() -> vendingService.purchase(product.getId()))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void purchaseRejectsOutOfStockProduct() {
        Product product = productRepository.save(
                new Product(UUID.randomUUID(), "Coke", new BigDecimal("1.50"), 0)
        );
        vendingService.insertCoin(Coin.TWO_EURO);

        assertThatThrownBy(() -> vendingService.purchase(product.getId()))
                .isInstanceOf(OutOfStockException.class);
    }

    @Test
    void purchaseRejectsMissingProduct() {
        vendingService.insertCoin(Coin.TWO_EURO);

        assertThatThrownBy(() -> vendingService.purchase(UUID.randomUUID()))
                .isInstanceOf(ProductNotFoundException.class);
    }

}
