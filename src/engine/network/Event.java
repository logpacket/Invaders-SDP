package engine.network;

import message.Error;

import java.util.UUID;

public record Event(
    String name,
    Body body,
    UUID id,
    long timestamp
) {}
