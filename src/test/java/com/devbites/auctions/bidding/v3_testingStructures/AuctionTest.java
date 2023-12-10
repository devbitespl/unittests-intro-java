package com.devbites.auctions.bidding.v3_testingStructures;

import com.devbites.auctions.bidding.AcceptedOffer;
import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;
import io.vavr.control.Either;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionTest {
    @Test
    public void auctionHasNoLeaderAtTheBeginning() {
        // Given & When
        Auction auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().toInstant(ZoneOffset.UTC), LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                Money.of(CurrencyUnit.EUR, 10), null, false, Money.of(CurrencyUnit.EUR, 100)
        );

        // Then
        assertNull(auction.leadingOfferAmount());
        assertNull(auction.leadingOfferId());
    }

    @Test
    public void offerAcceptedLastMinuteExtendsAuctionTime() {
        // Given
        Instant initialClosing = LocalDateTime.now().plusSeconds(10).toInstant(ZoneOffset.UTC);
        Auction auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC),
                initialClosing,
                Money.of(CurrencyUnit.EUR, 10),
                null,
                true,
                Money.of(CurrencyUnit.EUR, 100)
        );

        auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 100),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        ));

        // When
        Either result = auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 200),
                LocalDateTime.now().plusSeconds(25).toInstant(ZoneOffset.UTC)
        ));

        // Then
        assertInstanceOf(AcceptedOffer.class, result.get());
    }

    @Test
    public void betterOfferOutbidCurrentlyLeadingOne() {
        // Given
        Auction auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().toInstant(ZoneOffset.UTC),
                LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                Money.of(CurrencyUnit.EUR, 10),
                null, false, Money.of(CurrencyUnit.EUR, 100)
        );

        auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1000),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        )).get();

        // When
        AcceptedOffer acceptedOffer = (AcceptedOffer) auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 2000),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        )).get();

        // Then
        assertEquals(acceptedOffer.amount(), auction.leadingOfferAmount());
        assertEquals(acceptedOffer.id(), auction.leadingOfferId());
    }
}
