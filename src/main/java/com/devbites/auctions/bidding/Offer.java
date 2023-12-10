package com.devbites.auctions.bidding;

import org.joda.money.Money;

import java.time.Instant;
import java.util.UUID;

public record Offer(
    UUID bidderId,
    Money amount,
    Instant submittedAt
) {}