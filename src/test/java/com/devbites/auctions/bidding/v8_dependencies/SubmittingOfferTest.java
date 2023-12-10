package com.devbites.auctions.bidding.v8_dependencies;

import com.devbites.auctions.EventPublisher;
import com.devbites.auctions.SubmittingOffer;
import com.devbites.auctions.bidding.*;
import com.devbites.auctions.bidding.v6_refactoredTests.Fixtures;
import com.devbites.auctions.mandates.Mandate;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class SubmittingOfferTest {
    private static Fixtures fixtures;

    @BeforeEach
    void setUp() {
        fixtures = new Fixtures();
    }

    @Test
    public void mandateAllowsToSubmitOfferInAuction() {
        // Given
        Auction auction = fixtures.longRunningAuction();
        Offer offer = fixtures.offerFor(100);
        EventPublisher publisher = mock(EventPublisher.class);
        Mandate mandate = mock(Mandate.class);
        SubmittingOffer service = new SubmittingOffer(publisher);

        when(mandate.authorizeToSubmit(auction, offer)).thenReturn(true);

        // When
        Either result = service.submitOffer(auction, offer, mandate);

        // Then
        assertTrue(result.isRight());
        verify(publisher).publish(any(OfferAccepted.class));
    }

    @Test
    public void offerCannotBeSubmittedWithIncorrectMandate() {
        // Given
        Auction auction = fixtures.longRunningAuction();
        Offer offer = fixtures.offerFor(100);
        EventPublisher publisher = mock(EventPublisher.class);
        Mandate mandate = mock(Mandate.class);
        SubmittingOffer service = new SubmittingOffer(publisher);

        when(mandate.authorizeToSubmit(auction, offer)).thenReturn(false);

        // When
        Either result = service.submitOffer(auction, offer, mandate);

        // Then
        assertTrue(result.isLeft());
        verifyNoInteractions(publisher);
    }
}
