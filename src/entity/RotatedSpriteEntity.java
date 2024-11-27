package entity;

import java.awt.*;

public class RotatedSpriteEntity extends SpriteEntity{
    public RotatedSpriteEntity(final int positionX, final int positionY, final int width,
						final int height, final Color color) {
        super(positionX, positionY, width, height, color);
    }

    @Override
    public EntityType getType() {
        return EntityType.ROTATED_SPRITE;
    }
}
