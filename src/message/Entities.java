package message;

import engine.network.Body;
import entity.Entity;

import java.util.List;

public record Entities(List<Entity> entities) implements Body { }
