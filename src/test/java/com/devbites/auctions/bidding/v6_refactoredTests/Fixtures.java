package com.devbites.auctions.bidding.v6_refactoredTests;

import com.devbites.auctions.bidding.Auction;
import com.devbites.auctions.bidding.Offer;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class Fixtures {
    private Money askingPrice = Money.of(CurrencyUnit.EUR, 100);
    private Money minimalRaise = Money.of(CurrencyUnit.EUR, 10);

    public Auction longRunningAuction() {
        return new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().minusDays(10).toInstant(ZoneOffset.UTC), LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                minimalRaise, null, false, askingPrice
        );
    }

    public Auction longRunningAuctionWithBuyNowPrice(double amount) {
        Auction auction = longRunningAuction();

        auction.enableBuyNow(Money.of(CurrencyUnit.EUR, amount));

        return auction;
    }

    public Auction notOpenedYetAuction() {
        return new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC), LocalDateTime.now().plusDays(10).toInstant(ZoneOffset.UTC),
                minimalRaise, null, false, askingPrice
        );
    }

    public Auction alreadyClosedAuction() {
        return new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().minusDays(10).toInstant(ZoneOffset.UTC), LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC),
                minimalRaise, null, false, askingPrice
        );
    }

    public Auction endingVerySoonAuction(Boolean extraTime) {
        return new Auction(
                UUID.randomUUID(),
                LocalDateTime.now().minusDays(10).toInstant(ZoneOffset.UTC), LocalDateTime.now().plusSeconds(10).toInstant(ZoneOffset.UTC),
                minimalRaise, null, extraTime, askingPrice
        );
    }

    public Auction terminatedAuction() {
        Auction auction = longRunningAuction();

        auction.terminate();

        return auction;
    }

    public Auction longRunningAuctionWithOfferForAskingPrice() {
        Auction auction = longRunningAuction();

        auction.submitOffer(offerFor(askingPrice.getAmount().doubleValue()));

        return auction;
    }

    public Offer offerFor(double amount) {
        return new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, amount),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        );
    }

    public Offer offerFor(CurrencyUnit currencyUnit, double amount) {
        return new Offer(
                UUID.randomUUID(),
                Money.of(currencyUnit, amount),
                LocalDateTime.now().toInstant(ZoneOffset.UTC)
        );
    }

    public Offer offerForAt(double amount, Instant at) {
        return new Offer(
                UUID.randomUUID(),
                Money.of(CurrencyUnit.EUR, amount),
                at
        );
    }
}
