package com.devbites.auctions.bidding.v5_testingBehaviorsSeparatedTestClasses;

import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;
import com.devbites.auctions.bidding.Rejection;
import io.vavr.control.Either;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class SwitchingBuyNowTest {
    private Auction auction;

    @BeforeEach
    void setUp() {
        this.auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().toInstant(ZoneOffset.UTC), LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                Money.of(CurrencyUnit.EUR, 10), null, false, Money.of(CurrencyUnit.EUR, 100)
        );
    }

    @Test
    public void buyNowCannotBeEnabledWhenAtLeastOneOfferWasSubmitted() {
        // Given
        Money buyNowPrice = Money.of(CurrencyUnit.EUR, 200);

        this.auction.submitOffer(new Offer(
            UUID.randomUUID(),
            Money.of(CurrencyUnit.EUR, 1000),
            LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
        ));

        // When
        Either result = this.auction.enableBuyNow(buyNowPrice);

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

    @Test
    public void buyNowCannotBeDisabledWhenAtLeastOneOfferWasSubmitted() {
        // Given
        Money buyNowPrice = Money.of(CurrencyUnit.EUR, 2000);

        this.auction.enableBuyNow(buyNowPrice);
        this.auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1000),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
        ));

        // When
        Either result = this.auction.disableBuyNow();

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }
}
