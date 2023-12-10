package com.devbites.auctions.mandates;

import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;
import org.joda.money.Money;

import java.util.UUID;

public class SpecificAuctionMandate implements Mandate {
    private UUID auctionId;
    private Money maxOfferedAmount;

    public SpecificAuctionMandate(UUID auctionId, Money maxOfferedAmount) {
        this.auctionId = auctionId;
        this.maxOfferedAmount = maxOfferedAmount;
    }

    @Override
    public Boolean authorizeToSubmit(Auction auction, Offer offer) {
        return auction.id().compareTo(auctionId) == 0 && offer.amount().isLessThan(maxOfferedAmount);
    }
}
