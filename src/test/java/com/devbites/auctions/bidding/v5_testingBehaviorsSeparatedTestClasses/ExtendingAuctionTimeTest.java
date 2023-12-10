package com.devbites.auctions.bidding.v5_testingBehaviorsSeparatedTestClasses;

import com.devbites.auctions.bidding.AcceptedOffer;
import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ExtendingAuctionTimeTest {
    @Test
    public void offerAcceptedLastMinuteExtendsAuctionTime() {
        // Given
        Auction auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().minusDays(10).toInstant(ZoneOffset.UTC), LocalDateTime.now().plusSeconds(10).toInstant(ZoneOffset.UTC),
                Money.of(CurrencyUnit.EUR, 10), null, true, Money.of(CurrencyUnit.EUR, 100)
        );

        auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1000),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        ));

        // When
        AcceptedOffer offerAfterInitialClosingTime = (AcceptedOffer) auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 2000),
                LocalDateTime.now().plusSeconds(25).toInstant(ZoneOffset.UTC)
        )).get();

        // Then
        assertTrue(auction.isLeading(offerAfterInitialClosingTime));
    }
}
