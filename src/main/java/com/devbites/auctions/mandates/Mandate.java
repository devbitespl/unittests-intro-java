package com.devbites.auctions.mandates;

import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;

public interface Mandate {
    public Boolean authorizeToSubmit(Auction auction, Offer offer);
}
