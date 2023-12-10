package com.devbites.auctions.bidding.v6_refactoredTests;

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

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class TerminatingAuctionTest {
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
    public void terminationEndsAcceptingOffers() {
        // Given
        this.auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1000),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        ));
        this.auction.terminate();

        //When
        Either result = this.auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 2000),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        ));

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }
}
