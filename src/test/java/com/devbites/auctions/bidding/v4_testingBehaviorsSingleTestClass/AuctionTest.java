package com.devbites.auctions.bidding.v4_testingBehaviorsSingleTestClass;

import com.devbites.auctions.bidding.AcceptedOffer;
import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;
import com.devbites.auctions.bidding.Rejection;
import io.vavr.control.Either;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionTest {
    private Auction auction;

    @BeforeEach
    void setUp() {
        this.auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().toInstant(ZoneOffset.UTC), LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                Money.of(CurrencyUnit.EUR, 10), null, false, Money.of(CurrencyUnit.EUR, 100)
        );
    }

    @Test
    public void biddingStartsWithAnInitialOffer() {
        // Given
        Offer offer = new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 100),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
        );

        //When
        Either result = this.auction.submitOffer(offer);

        // Then
        assertInstanceOf(AcceptedOffer.class, result.get());
        assertTrue(this.auction.isLeading((AcceptedOffer) result.get()));
    }

    @Test
    public void betterOfferOutbidCurrentlyLeadingOne() {
        // Given
        AcceptedOffer firstOffer = (AcceptedOffer) auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1000),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
        )).get();

        // When
        AcceptedOffer secondOffer = (AcceptedOffer) auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 2000),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
        )).get();

        // Then
        assertFalse(this.auction.isLeading(firstOffer));
        assertTrue(this.auction.isLeading(secondOffer));
    }

    @Test
    public void submittedOfferWithBuyNowPriceEndsAnAuction() {
        // Given
        Auction auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().toInstant(ZoneOffset.UTC), LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                Money.of(CurrencyUnit.EUR, 10), Money.of(CurrencyUnit.EUR, 2000), false, Money.of(CurrencyUnit.EUR, 100)
        );

        AcceptedOffer firstOffer = (AcceptedOffer) auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 5000),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
        )).get();

        // When
        Either result = auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 5000),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
        ));

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
        assertTrue(auction.isLeading(firstOffer));
    }

    @Test
    public void wholeBiddingMustBeConductedInTheSameCurrency() {
        // When
        Either result = this.auction.enableBuyNow(Money.of(CurrencyUnit.USD, 1000));

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

    @Test
    public void firstOfferMustEqualTheAskingPrice() {
        // Given
        Auction auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().toInstant(ZoneOffset.UTC), LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                Money.of(CurrencyUnit.EUR, 10), null, false, Money.of(CurrencyUnit.EUR, 100)
        );

        // When
        Either result = auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 99),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
        ));

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

    @Test
    public void offerCannotBeSubmittedBeforeOpening() {
        // Given
        Auction auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC), LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                Money.of(CurrencyUnit.EUR, 10), null, false, Money.of(CurrencyUnit.EUR, 100)
        );

        // When
        Either result = auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1000),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        ));

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

    @Test
    public void offerCannotBeSubmittedAfterClosing() {
        // Given
        Auction auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC), LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                Money.of(CurrencyUnit.EUR, 10), null, false, Money.of(CurrencyUnit.EUR, 100)
        );

        // When
        Either result = auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1000),
                LocalDateTime.now().plusDays(11).toInstant(ZoneOffset.UTC)
        ));

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

    @Test
    public void offerMustBeSubmittedInTheSameCurrencyAsAuction() {
        // Given
        Auction auction = new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC), LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                Money.of(CurrencyUnit.EUR, 10), null, false, Money.of(CurrencyUnit.EUR, 100)
        );

        // When
        Either result = auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.USD, 1000),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        ));

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

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

    @Test
    public void buyNowCannotBeEnabledWhenAtLeastOneOfferWasSubmitted() {
        // Given
        Money buyNowPrice = Money.of(CurrencyUnit.EUR, 200);

        this.auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1000),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
        ));

        // When
        Either result = this.auction.enableBuyNow(buyNowPrice);

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

    @Test
    public void buyNowCannotBeDisabledWhenAtLeastOneOfferWasSubmitted() {
        // Given
        Money buyNowPrice = Money.of(CurrencyUnit.EUR, 2000);

        this.auction.enableBuyNow(buyNowPrice);
        this.auction.submitOffer(new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, 1000),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)
        ));

        // When
        Either result = this.auction.disableBuyNow();

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }
}
