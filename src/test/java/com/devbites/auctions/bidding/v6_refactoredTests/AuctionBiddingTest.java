package com.devbites.auctions.bidding.v6_refactoredTests;

import com.devbites.auctions.bidding.AcceptedOffer;
import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;
import com.devbites.auctions.bidding.Rejection;
import io.vavr.control.Either;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionBiddingTest {
    private static Fixtures fixtures;

    @BeforeEach
    void setUp() {
        fixtures = new Fixtures();
    }

    @Test
    public void biddingStartsWithAnInitialOffer() {
        // Given
        Auction auction = fixtures.longRunningAuction();

        //When
        Either result = auction.submitOffer(fixtures.offerFor(100));

        // Then
        assertInstanceOf(AcceptedOffer.class, result.get());
        assertTrue(auction.isLeading((AcceptedOffer) result.get()));
    }

    @Test
    public void betterOfferOutbidCurrentlyLeadingOne() {
        // Given
        Auction auction = fixtures.longRunningAuction();
        AcceptedOffer firstOffer = (AcceptedOffer) auction.submitOffer(fixtures.offerFor(1000)).get();

        // When
        AcceptedOffer secondOffer = (AcceptedOffer) auction.submitOffer(fixtures.offerFor(2000)).get();

        // Then
        assertFalse(auction.isLeading(firstOffer));
        assertTrue(auction.isLeading(secondOffer));
    }

    @Test
    public void submittedOfferWithBuyNowPriceEndsAnAuction() {
        // Given
        Auction auction = fixtures.longRunningAuctionWithBuyNowPrice(2000);

        AcceptedOffer firstOffer = (AcceptedOffer) auction.submitOffer(fixtures.offerFor(5000)).get();

        // When
        Either result = auction.submitOffer(fixtures.offerFor(6000));

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
        assertTrue(auction.isLeading(firstOffer));
    }

    @ParameterizedTest
    @MethodSource("offersToReject")
    public void someOffersMustBeRejected(Auction auction, Offer offer) {
        // When
        Either result = auction.submitOffer(offer);

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

    private static Stream<Arguments> offersToReject() {
        return Stream.of(
                // First offer must equal the asking price
                Arguments.of(fixtures.longRunningAuction(), fixtures.offerFor(99)),
                // Next offer must equal the minimal raise
                Arguments.of(fixtures.longRunningAuctionWithOfferForAskingPrice(), fixtures.offerFor(109)),
                // Offer cannot be submitted before opening
                Arguments.of(fixtures.notOpenedYetAuction(), fixtures.offerFor(1000)),
                // Offer cannot be submitted after closing
                Arguments.of(fixtures.alreadyClosedAuction(), fixtures.offerFor(1000)),
                // Offer cannot be submitted in different currency
                Arguments.of(fixtures.longRunningAuction(), fixtures.offerFor(CurrencyUnit.USD, 1000))
        );
    }
}
