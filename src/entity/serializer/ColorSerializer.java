package entity.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import java.awt.*;
import java.io.IOException;

public class ColorSerializer extends JsonSerializer<Color> {
    @Override
    public void serialize(Color color, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException {
        jsonGenerator.writeNumber(color.getRGB());
    }

    @Override
    public void serializeWithType(Color color, JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSer) throws IOException {
        jsonGenerator.writeNumber(color.getRGB());
    }
}
