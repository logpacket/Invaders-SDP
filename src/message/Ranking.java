package message;

import engine.network.Body;

public record Ranking(String userId, int highScore) implements Body {
    public String name() {
        return userId;
    }

    public int score() {
        return highScore;
    }
}
