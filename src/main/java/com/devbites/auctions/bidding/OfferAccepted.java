package com.devbites.auctions.bidding;

import com.devbites.auctions.Event;
import org.joda.money.Money;

import java.util.UUID;

public class OfferAccepted extends Event  {
    private UUID auctionId;
    private UUID offerId;
    private UUID bidderId;
    private Money amount;

    public OfferAccepted(UUID auctionId, UUID offerId, UUID bidderId, Money amount) {
        super();

        this.auctionId = auctionId;
        this.offerId = offerId;
        this.bidderId = bidderId;
        this.amount = amount;
    }

    public UUID getAuctionId() {
        return auctionId;
    }

    public UUID getOfferId() {
        return offerId;
    }

    public UUID getBidderId() {
        return bidderId;
    }

    public Money getAmount() {
        return amount;
    }
}
