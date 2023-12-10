package com.devbites.auctions.bidding.v2_testingManyThingsAtOnce;

import com.devbites.auctions.bidding.AcceptedOffer;
import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;
import com.devbites.auctions.bidding.Rejection;
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
    public void test() {
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

        assertNull(auction.leadingOfferId());

        auction.enableBuyNow(Money.of(CurrencyUnit.EUR, 500));

        AcceptedOffer firstOffer = (AcceptedOffer) auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 100),
                LocalDateTime.now().minusHours(1).toInstant(ZoneOffset.UTC)
        )).get();

        assertEquals(firstOffer.amount(), auction.leadingOfferAmount());
        assertEquals(firstOffer.id(), auction.leadingOfferId());
        assertEquals(initialClosing, auction.closing());

        AcceptedOffer secondOffer = (AcceptedOffer) auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1000),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        )).get();

        assertEquals(secondOffer.amount(), auction.leadingOfferAmount());
        assertEquals(secondOffer.id(), auction.leadingOfferId());
        assertTrue(auction.closing().isAfter(initialClosing));

        auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1009),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        ));

        assertEquals(secondOffer.amount(), auction.leadingOfferAmount());
        assertEquals(secondOffer.id(), auction.leadingOfferId());
    }

    @Test
    public void testFail() {
        Instant initialClosing = LocalDateTime.now().plusSeconds(10).toInstant(ZoneOffset.UTC);
        Auction auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC),
                initialClosing,
                Money.of(CurrencyUnit.EUR, 10),
                null,
                false,
                Money.of(CurrencyUnit.EUR, 100)
        );

        assertNull(auction.leadingOfferId());

        Either result = auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 99),
                LocalDateTime.now().plusSeconds(20).toInstant(ZoneOffset.UTC)
        ));

        assertInstanceOf(Rejection.class, result.getLeft());
        assertNull(auction.leadingOfferId());
    }
}
