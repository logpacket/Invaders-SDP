package message;

import engine.network.Body;

public record Error(String message) implements Body { }
