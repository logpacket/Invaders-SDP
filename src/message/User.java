package message;

import engine.network.Body;

public record User(String username, String password) implements Body { }
