package com.devbites.auctions.bidding.v8_dependencies;

import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;
import com.devbites.auctions.bidding.v6_refactoredTests.Fixtures;
import com.devbites.auctions.mandates.Mandate;
import com.devbites.auctions.mandates.TimeLimitedMandate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class TimeLimitedMandateTest {
    private static Fixtures fixtures;

    @BeforeEach
    void setUp() {
        fixtures = new Fixtures();
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test(Instant validTo, Boolean expectedResult) {
        // Given
        Auction auction = fixtures.endingVerySoonAuction(false);
        Offer offer = fixtures.offerFor(100);
        Mandate mandate = new TimeLimitedMandate(validTo);

        // When && Then
        assertEquals(expectedResult, mandate.authorizeToSubmit(auction, offer));
    }

    private static Stream<Arguments> dataProvider() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().plusSeconds(1).toInstant(ZoneOffset.UTC), true),
                Arguments.of(LocalDateTime.now().minusSeconds(1).toInstant(ZoneOffset.UTC), false)
        );
    }
}
