package message;

import engine.network.Body;

public record Ping(long sendTimestamp) implements Body { }
