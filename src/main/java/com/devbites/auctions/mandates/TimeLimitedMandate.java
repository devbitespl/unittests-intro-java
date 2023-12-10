package com.devbites.auctions.mandates;

import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;

import java.time.Instant;

public class TimeLimitedMandate implements Mandate {
    private Instant validTo;

    public TimeLimitedMandate(Instant validTo) {
        this.validTo = validTo;
    }

    @Override
    public Boolean authorizeToSubmit(Auction auction, Offer offer) {
        return offer.submittedAt().isBefore(validTo);
    }
}
