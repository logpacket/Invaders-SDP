package entity;

import engine.Renderer.SpriteType;

import java.awt.*;
import java.util.Random;

public class Blocker extends RotatedSpriteEntity {
    /* Move left? */
    private final boolean moveLeft;

    public Blocker(int positionX, int positionY, final SpriteType spriteType, boolean moveLeft) {
        super(positionX, positionY, 82 * 2, 81 * 2, Color.GREEN, 180 * new Random().nextDouble());
        this.spriteType = spriteType;
        this.moveLeft = moveLeft;
    }

    public final void move(final double distanceX, final double distanceY) {
        this.positionX += (int) distanceX;
        this.positionY += (int) distanceY;
    }

    public final void rotate(final double degree) { angle += degree; }

    public boolean getMoveLeft() { return moveLeft; }
}
