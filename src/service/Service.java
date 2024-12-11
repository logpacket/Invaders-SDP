package service;

import engine.Cooldown;
import engine.Core;
import engine.network.*;
import message.Error;

import java.util.UUID;

public abstract class Service {
    protected NetworkManager networkManager = NetworkManager.getInstance();
    private UUID requestId;
    private final String eventName;
    private final Cooldown requestCooldown;
    private static final int REQUEST_TIME = 1500;

    protected Service(String eventName) {
        this.eventName = eventName;
        this.requestCooldown = Core.getCooldown(REQUEST_TIME);
    }

    protected void request(Body body, EventHandler callback, ErrorHandler errorCallback) {
        if (!requestCooldown.checkFinished()) return;
        if (networkManager.isRequested(requestId)) return;
        requestId = networkManager.request(eventName, body);
        networkManager.registerEventHandler(eventName, event -> {
            if (event.body() instanceof Error e) errorCallback.handle(e);
            else callback.handle(event);
            requestCooldown.reset();
        });
    }

    protected void sendEvent(Body body) {
        networkManager.sendEvent(eventName, body, UUID.randomUUID());
    }
}
