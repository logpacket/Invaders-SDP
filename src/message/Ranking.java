package message;

import engine.network.Body;

public record Ranking(String username, int highScore) implements Body { }
