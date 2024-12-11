package entity.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import engine.Renderer.SpriteType;

import java.awt.*;
import java.io.IOException;

public class SpriteTypeSerializer extends JsonSerializer<SpriteType> {

    @Override
    public void serialize(SpriteType spriteType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(spriteType.ordinal());
    }

    @Override
    public void serializeWithType(SpriteType spriteType, JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSer) throws IOException {
        jsonGenerator.writeNumber(spriteType.ordinal());
    }
}
