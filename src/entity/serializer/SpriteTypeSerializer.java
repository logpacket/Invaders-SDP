package entity.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import engine.Renderer.SpriteType;

import java.io.IOException;

public class SpriteTypeSerializer extends JsonSerializer<SpriteType> {

    @Override
    public void serialize(SpriteType spriteType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(spriteType.ordinal());
    }
}
