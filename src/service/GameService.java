package service;

import engine.network.ErrorHandler;
import engine.network.EventHandler;
import entity.Entity;
import message.Entities;
import message.GameSettings;

import java.util.List;

public class GameService extends Service{
    List<Entity> entityList;

    public GameService() {
        super("game");
        networkManager.registerEventHandler("game", event -> {
            if (event.body() instanceof Entities(List<Entity> entities)) {
                this.entityList = entities;
            }
        });
    }

    public void syncGame(List<Entity> entityList) {
        sendEvent(new Entities(entityList));
    }

    public void enqueueMatch(int difficulty, EventHandler eventHandler, ErrorHandler errorHandler) {
        request(new GameSettings(difficulty), eventHandler, errorHandler);
    }

    public List<Entity> getEntityList() {
        if (entityList == null) return List.of();
        return entityList;
    }
}
