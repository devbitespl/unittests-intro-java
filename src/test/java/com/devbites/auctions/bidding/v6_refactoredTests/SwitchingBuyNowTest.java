package com.devbites.auctions.bidding.v6_refactoredTests;

import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Rejection;
import io.vavr.control.Either;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class SwitchingBuyNowTest {
    private static Fixtures fixtures;

    @BeforeEach
    void setUp() {
        fixtures = new Fixtures();
    }

    @Test
    public void buyNowCannotBeEnabledWhenAtLeastOneOfferWasSubmitted() {
        // Given
        Auction auction = fixtures.longRunningAuctionWithOfferForAskingPrice();

        // When
        Either result = auction.enableBuyNow(Money.of(CurrencyUnit.EUR, 200));

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

    @Test
    public void buyNowCannotBeDisabledWhenAtLeastOneOfferWasSubmitted() {
        // Given
        Auction auction = fixtures.longRunningAuctionWithBuyNowPrice(200);

        auction.submitOffer(fixtures.offerFor(150));

        // When
        Either result = auction.disableBuyNow();

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }
}
