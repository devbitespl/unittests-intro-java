package com.devbites.auctions.bidding;

import io.vavr.control.Either;
import org.joda.money.Money;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Auction {
    protected UUID id;

    private Instant opening;

    private Money askingPrice;

    private Money minimalRaise;

    private Instant closing;

    private Boolean finalized = false;

    private UUID leadingOfferId = null;

    private Money leadingOfferAmount = null;

    private Money buyNowPrice;

    private Boolean extraTime;

    // constructor
    Auction() {
    }

    public Auction(
            UUID id, Instant opening, Instant closing, Money minimalRaise, Money buyNowPrice, Boolean extraTime, Money askingPrice
    ) {
        this.id = id;
        this.opening = opening;
        this.closing = closing;
        this.askingPrice = askingPrice;
        this.minimalRaise = minimalRaise;
        this.buyNowPrice = buyNowPrice;
        this.extraTime = extraTime;
    }

    public Either submitOffer(Offer offer) {
        // if same currency
        if (!offer.amount().isSameCurrency(this.askingPrice)) {
            return Either.left(new Rejection("Offer must be in the same currency as auction"));
        }

        if (offer.submittedAt().isBefore(this.opening) || offer.submittedAt().isAfter(this.closing) || this.finalized) {
            return Either.left(new Rejection("Offer must be submitted when auction is open"));
        }

        if (this.leadingOfferId == null) {
            if (offer.amount().isLessThan(this.askingPrice)) {
                return Either.left(new Rejection("First offer must equal asking price"));
            }
        }

        if (this.buyNowPrice == null &&
                this.leadingOfferId != null &&
                offer.amount().isLessThan(this.leadingOfferAmount.plus(this.minimalRaise))) {
            return Either.left(new Rejection("Next offer must equal minimal raise"));
        }

        // State changes:
        // a) Extending auction time when offer was submitted in last 30 minutes
        if (this.extraTime &&
                offer.submittedAt().isAfter(this.closing.minus(30, ChronoUnit.MINUTES))) {
            this.closing = this.closing.plus(30, ChronoUnit.SECONDS);
        }

        // b) Closing auction when offer beats buy now price
        if (this.buyNowPrice != null && offer.amount().isGreaterThan(this.buyNowPrice)) {
            this.finalized = true;
        }

        // c) Changing leading offer
        AcceptedOffer acceptedOffer = new AcceptedOffer(
                UUID.randomUUID(),
                this.id,
                offer.bidderId(),
                offer.amount(),
                offer.submittedAt()
        );

        this.leadingOfferId = acceptedOffer.id();
        this.leadingOfferAmount = acceptedOffer.amount();

        return Either.right(acceptedOffer);
    }

    public Either enableBuyNow(Money buyNowPrice) {
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);

        if (now.isAfter(this.closing) || this.finalized) {
            return Either.left(new Rejection("Offer must be submitted when auction is open"));
        }

        if (!buyNowPrice.isSameCurrency(this.askingPrice)) {
            return Either.left(new Rejection("Whole bidding must be in the same currency"));
        }

        if (this.leadingOfferId != null) {
            return Either.left(new Rejection("At least one offer was submitted"));
        }

        Money minimum = this.askingPrice.plus(this.minimalRaise);

        if (this.buyNowPrice != null && !this.buyNowPrice.isGreaterThan(minimum)) {
            return Either.left(new Rejection("Buy now price is too low"));
        }

        this.buyNowPrice = buyNowPrice;

        return Either.right(new Success());
    }

    public Either disableBuyNow() {
        Instant now = LocalDateTime.now().toInstant(ZoneOffset.UTC);

        if (now.isAfter(this.closing) || this.finalized) {
            return Either.left(new Rejection("Offer must be submitted when auction is open"));
        }

        if (this.leadingOfferId != null) {
            return Either.left(new Rejection("At least one offer was submitted"));
        }

        this.buyNowPrice = null;

        return Either.right(new Success());
    }

    public void terminate() {
        this.finalized = true;
    }

    public UUID id() {
        return id;
    }

    public Money askingPrice() {
        return askingPrice;
    }

    public Money minimalRaise() {
        return minimalRaise;
    }

    public Instant closing() { return closing; }

    public UUID leadingOfferId() {
        return leadingOfferId;
    }

    public Money leadingOfferAmount() {
        return leadingOfferAmount;
    }

    /**
     * Method introduced currently for testing purposes to hide internal details
     * of class implementation.
     *
     * Can be used in future also to support UI needs.
     */
    public Boolean isLeading(AcceptedOffer other) {
        return this.leadingOfferId.equals(other.id());
    }

    // Other methods

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
