package entity;

import java.awt.*;

public class RotatedSpriteEntity extends SpriteEntity{
    protected double angle;

    public RotatedSpriteEntity(final int positionX, final int positionY, final int width,
						final int height, final Color color, final double angle) {
        super(positionX, positionY, width, height, color);
    }

    public double getAngle(){ return angle;}

    @Override
    public EntityType getType() {
        return EntityType.ROTATED_SPRITE;
    }
}
