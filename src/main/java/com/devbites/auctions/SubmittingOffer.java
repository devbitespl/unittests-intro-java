package com.devbites.auctions;

import com.devbites.auctions.bidding.*;
import com.devbites.auctions.mandates.Mandate;
import io.vavr.control.Either;

public class SubmittingOffer {
    private EventPublisher eventPublisher;

    public SubmittingOffer(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public Either submitOffer(Auction auction, Offer offer, Mandate mandate) {
        if (!mandate.authorizeToSubmit(auction, offer)) {
            return Either.left(new Rejection("Mandate is not sufficient to submit an offer"));
        }

        Either result = auction.submitOffer(offer);

        if (result.isRight()) {
            this.eventPublisher.publish(new OfferAccepted(
                    auction.id(),
                    ((AcceptedOffer)result.get()).id(),
                    offer.bidderId(),
                    offer.amount()
            ));
        }

        return result;
    }
}