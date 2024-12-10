package message;

import engine.network.Body;

public record HighScore(int score) implements Body { }
