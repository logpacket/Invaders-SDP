package engine.network;

public record Event(
    String name,
    Body body,
    Status status,
    long timestamp
) { }
