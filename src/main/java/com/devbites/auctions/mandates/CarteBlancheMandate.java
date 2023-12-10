package com.devbites.auctions.mandates;

import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;

public class CarteBlancheMandate implements Mandate {
    @Override
    public Boolean authorizeToSubmit(Auction auction, Offer offer) {
        return true;
    }
}
