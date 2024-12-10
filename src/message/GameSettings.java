package message;

import engine.network.Body;

public record GameSettings(int difficulty) implements Body { }
