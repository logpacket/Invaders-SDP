package entity.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import engine.FontManager;

import java.awt.*;
import java.io.IOException;

public class FontDeserializer extends JsonDeserializer<Font> {
    @Override
    public Font deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, IllegalStateException {
        int size = jsonParser.getIntValue();
        return switch (size) {
            case 10 -> FontManager.getFontSmall();
            case 14 -> FontManager.getFontRegular();
            case 24 -> FontManager.getFontBig();
            default -> throw new IllegalStateException("Unexpected value: " + size);
        };
    }
}
