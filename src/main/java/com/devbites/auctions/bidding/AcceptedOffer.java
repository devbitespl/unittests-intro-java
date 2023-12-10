package com.devbites.auctions.bidding;

import org.joda.money.Money;

import java.time.Instant;
import java.util.UUID;

public class AcceptedOffer {
    private UUID id;

    private UUID auctionId;

    private UUID bidderId;

    private Money amount;

    private Instant acceptedAt;

    public AcceptedOffer(
            UUID id,
            UUID auctionId,
            UUID bidderId,
            Money amount,
            Instant acceptedAt
    ) {
        this.id = id;
        this.auctionId = auctionId;
        this.bidderId = bidderId;
        this.amount = amount;
        this.acceptedAt = acceptedAt;
    }

    public UUID id() {
        return id;
    }

    public UUID auctionId() {
        return auctionId;
    }

    public UUID bidderId() {
        return bidderId;
    }

    public Money amount() {
        return amount;
    }

    public Instant acceptedAt() {
        return acceptedAt;
    }
}
