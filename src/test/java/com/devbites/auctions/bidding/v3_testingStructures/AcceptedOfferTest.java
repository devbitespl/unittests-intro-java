package com.devbites.auctions.bidding.v3_testingStructures;

import com.devbites.auctions.bidding.AcceptedOffer;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AcceptedOfferTest {
    @Test
    public void test()
    {
        UUID id = UUID.randomUUID();
        UUID auctionId = UUID.randomUUID();
        UUID bidderId = UUID.randomUUID();
        Money amount = Money.of(CurrencyUnit.EUR, 100);
        Instant acceptedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);

        AcceptedOffer offer = new AcceptedOffer(
                id,
                auctionId,
                bidderId,
                amount,
                acceptedAt
        );

        assertEquals(id, offer.id());
        assertEquals(auctionId, offer.auctionId());
        assertEquals(bidderId, offer.bidderId());
        assertEquals(amount, offer.amount());
        assertEquals(acceptedAt, offer.acceptedAt());
    }
}
