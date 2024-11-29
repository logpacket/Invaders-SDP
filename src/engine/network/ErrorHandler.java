package engine.network;

import message.Error;

@FunctionalInterface
public interface ErrorHandler {
    void handle(Error error);
}
