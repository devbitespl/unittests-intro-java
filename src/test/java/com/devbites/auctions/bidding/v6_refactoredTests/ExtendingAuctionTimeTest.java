package com.devbites.auctions.bidding.v6_refactoredTests;

import com.devbites.auctions.bidding.AcceptedOffer;
import com.devbites.auctions.bidding.Auction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExtendingAuctionTimeTest {
    private static Fixtures fixtures;

    @BeforeEach
    void setUp() {
        fixtures = new Fixtures();
    }

    @Test
    public void offerAcceptedLastMinuteExtendsAuctionTime() {
        // Given
        Auction auction = fixtures.endingVerySoonAuction(true);

        auction.submitOffer(fixtures.offerFor(1000));

        // When
        AcceptedOffer offerAfterInitialClosingTime = (AcceptedOffer) auction.submitOffer(
                fixtures.offerForAt(2000, LocalDateTime.now().plusSeconds(25).toInstant(ZoneOffset.UTC))
        ).get();

        // Then
        assertTrue(auction.isLeading(offerAfterInitialClosingTime));
    }
}
