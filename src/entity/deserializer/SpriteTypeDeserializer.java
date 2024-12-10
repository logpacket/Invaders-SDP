package entity.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import engine.Renderer.SpriteType;

import java.io.IOException;

public class SpriteTypeDeserializer extends JsonDeserializer<SpriteType> {
    private final SpriteType[] spriteTypes = SpriteType.values();

    @Override
    public SpriteType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        return spriteTypes[jsonParser.getIntValue()];
    }
}
