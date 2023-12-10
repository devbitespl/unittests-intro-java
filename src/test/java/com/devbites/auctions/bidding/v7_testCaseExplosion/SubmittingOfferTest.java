package com.devbites.auctions.bidding.v7_testCaseExplosion;

import com.devbites.auctions.EventPublisher;
import com.devbites.auctions.SubmittingOffer;
import com.devbites.auctions.bidding.AcceptedOffer;
import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Rejection;
import com.devbites.auctions.bidding.v6_refactoredTests.Fixtures;
import com.devbites.auctions.mandates.CarteBlancheMandate;
import com.devbites.auctions.mandates.Mandate;
import com.devbites.auctions.mandates.SpecificAuctionMandate;
import com.devbites.auctions.mandates.TimeLimitedMandate;
import io.vavr.control.Either;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class SubmittingOfferTest {
    private static Fixtures fixtures;

    @BeforeEach
    void setUp() {
        fixtures = new Fixtures();
    }

    @ParameterizedTest
    @MethodSource("mandates")
    public void offerCanBeSubmittedWithMandate() {
        // Given
        EventPublisher publisher = mock(EventPublisher.class);
        Auction auction = fixtures.longRunningAuction();
        Mandate mandate = new TimeLimitedMandate(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC));
        SubmittingOffer service = new SubmittingOffer(publisher);

        // When
        Either result = service.submitOffer(auction, fixtures.offerFor(100), mandate);

        // Then
        assertInstanceOf(AcceptedOffer.class, result.get());
    }

    private static Stream<Arguments> mandates() {
        return Stream.of(
                Arguments.of(new TimeLimitedMandate(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC))),
                Arguments.of(new CarteBlancheMandate())
        );
    }

    @Test
    public void offerCannotBeSubmittedAfterExpirationOfMandate() {
        // Given
        EventPublisher publisher = mock(EventPublisher.class);
        Auction auction = fixtures.longRunningAuction();
        Mandate mandate = new TimeLimitedMandate(LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC));
        SubmittingOffer service = new SubmittingOffer(publisher);

        // When
        Either result = service.submitOffer(auction, fixtures.offerFor(100), mandate);

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

    @Test
    public void offerCannotBeSubmittedWhenMandateIsLimitedToGivenAmount() {
        EventPublisher publisher = mock(EventPublisher.class);
        Auction auction = fixtures.longRunningAuction();
        Mandate mandate = new SpecificAuctionMandate(auction.id(), Money.of(CurrencyUnit.EUR, 500));
        SubmittingOffer service = new SubmittingOffer(publisher);

        // When
        Either result = service.submitOffer(auction, fixtures.offerFor(1000), mandate);

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }

    /**
     * The structure of this test can be slightly improved so that it directly reveals (and absolutely guarantees)
     * the auction behavior tested here.
     *
     * What can you propose?
     */
    @Test
    public void offerCannotBeSubmittedWhenMandateAllowsToBidInDifferentAuction() {
        EventPublisher publisher = mock(EventPublisher.class);
        Auction auction = fixtures.longRunningAuction();
        Mandate mandate = new SpecificAuctionMandate(UUID.randomUUID(), Money.of(CurrencyUnit.EUR, 500));
        SubmittingOffer service = new SubmittingOffer(publisher);

        // When
        Either result = service.submitOffer(auction, fixtures.offerFor(100), mandate);

        // Then
        assertInstanceOf(Rejection.class, result.getLeft());
    }
}
