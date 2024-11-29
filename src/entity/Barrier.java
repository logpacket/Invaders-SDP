package entity;

import engine.Renderer.SpriteType;
import engine.Sound;
import engine.SoundManager;

import java.awt.*;

public class Barrier extends Entity{

    private int health;

    /** Initialize singleton instance of SoundManager and return that */
    private final SoundManager soundManager = SoundManager.getInstance();

    public Barrier(final int positionX, final int positionY) {
        super(positionX, positionY, 39 * 2, 11 * 2, Color.GREEN);
        this.spriteType = SpriteType.BARRIER;
        this.health = 1;
    }

    public void reduceHealth(final float balance) {
        this.health--;
        soundManager.playSound(Sound.ITEM_BARRIER_OFF, balance);
    }

    public boolean isDestroyed() {
        return this.health <= 0;
    }
}
