package entity.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.awt.*;
import java.io.IOException;

public class FontSerializer extends JsonSerializer<Font> {

    @Override
    public void serialize(Font value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeNumber(value.getSize());
    }

    @Override
    public void serializeWithType(Font font, JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSer) throws IOException {
        jsonGenerator.writeNumber(font.getSize());
    }
}
