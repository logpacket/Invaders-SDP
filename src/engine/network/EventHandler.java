package engine.network;

@FunctionalInterface
public interface EventHandler {
    void handle(Event event);
}
