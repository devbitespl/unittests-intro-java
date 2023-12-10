package com.devbites.auctions;

import java.time.Instant;
import java.util.UUID;

abstract public class Event {
    private UUID id;
    public Event() {
        this.id = UUID.randomUUID();
    }
}
