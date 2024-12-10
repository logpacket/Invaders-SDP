package entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import engine.Renderer;
import entity.deserializer.ColorDeserializer;
import entity.deserializer.FontDeserializer;
import entity.deserializer.SpriteTypeDeserializer;
import entity.serializer.ColorSerializer;
import entity.serializer.FontSerializer;
import entity.serializer.SpriteTypeSerializer;

import java.awt.*;

public class EntityMapper extends ObjectMapper {
    public EntityMapper() {
        SimpleModule simpleModule = new SimpleModule();

        /* Add serializers */
        simpleModule.addSerializer(Color.class, new ColorSerializer());
        simpleModule.addSerializer(Renderer.SpriteType.class, new SpriteTypeSerializer());
        simpleModule.addSerializer(Font.class, new FontSerializer());

        /* Add deserializers */
        simpleModule.addDeserializer(Color.class, new ColorDeserializer());
        simpleModule.addDeserializer(Renderer.SpriteType.class, new SpriteTypeDeserializer());
        simpleModule.addDeserializer(Font.class, new FontDeserializer());
    }
}
