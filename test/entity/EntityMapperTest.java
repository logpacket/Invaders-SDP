package entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import engine.FontManager;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;

import java.awt.*;

public class EntityMapperTest {
    @Test
    public void textEntitySerializeTest() throws JsonProcessingException {
        TextEntity entity = new TextEntity(1, 1, Color.WHITE, "test", FontManager.getFontBig());
        EntityMapper mapper = new EntityMapper();


        Reflections reflections2 = new Reflections("entity");
        for (Class<? extends Entity> entityClass : reflections2.getSubTypesOf(Entity.class)) {
            mapper.registerSubtypes(entityClass);
        }

        String s = mapper.writeValueAsString(entity);
        TextEntity deserializedEntity = mapper.readValue(s, TextEntity.class);
        assert deserializedEntity.text.equals(entity.getText());
    }
    @Test
    public void spriteEntitySerializeTest() throws JsonProcessingException {
        Ship ship = ShipFactory.create(Ship.ShipType.COSMIC_CRUISER, 1, 1);
        EntityMapper mapper = new EntityMapper();

        Reflections reflections2 = new Reflections("entity");
        for (Class<? extends Entity> entityClass : reflections2.getSubTypesOf(Entity.class)) {
            mapper.registerSubtypes(entityClass);
        }

        String s = mapper.writeValueAsString(ship);
        SpriteEntity deserializedEntity = mapper.readValue(s, SpriteEntity.class);
        assert deserializedEntity.getSpriteType().equals(ship.getSpriteType());
    }
}
