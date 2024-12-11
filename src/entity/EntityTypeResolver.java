package entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

import java.io.IOException;

public class EntityTypeResolver extends TypeIdResolverBase {
    private JavaType superType;

    @Override
    public void init(JavaType baseType) {
        // 초기화 로직 (기본 타입 저장 등)
        this.superType = baseType;
    }


    @Override
    public String idFromValue(Object value) {
        return ((Entity) value).getType().name();
    }

    @Override
    public String idFromValueAndType(Object value, Class<?> suggestedType) {
        return null;
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return null;
    }

    @Override
    public JavaType typeFromId(DatabindContext context, String id) throws IOException {
        Class<?> subType = null;
        switch (id) {
            case "TEXT":
                subType = TextEntity.class;
                break;
            case "SPRITE":
                subType = SpriteEntity.class;
                break;
            case "LINE":
                subType = LineEntity.class;
                break;
            case "IMAGE":
                subType = ImageEntity.class;
                break;
            case "RECT":
                subType = RectEntity.class;
                break;
            case "ARC":
                subType = ArcEntity.class;
                break;
            case "POLYGON":
                subType = PolygonEntity.class;
                break;
            case "BLOCKER":
                subType = Blocker.class;
                break;
            default:
                throw new RuntimeException("Invalid Entity type");
        }
        return context.constructSpecializedType(superType, subType);
    }
}
