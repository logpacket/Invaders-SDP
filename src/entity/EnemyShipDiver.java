package entity;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager;
import engine.GameLevelState;

import java.awt.*;
import java.util.Random;

public class EnemyShipDiver extends EnemyShip {

    private static final int POINTS = 50;
    private static final int DIVE_INTERVAL = 4000;
    private static final int DIVE_VARIANCE = -1000;
    public static final int SPEED_X = 7;
    public static final int SPEED_DIVE = 3;
    private final Cooldown diveCooldown;
    private int state;


    /**
     * Creates a diver ship with the given x and y positions
     * @param positionX
     * @param positionY
     */
    public EnemyShipDiver(final int positionX, final int positionY, final GameLevelState gameLevelState, final int difficulty) {
        super(positionX, positionY, DrawManager.SpriteType.ENEMY_SHIP_F1, gameLevelState, difficulty);
        this.diveCooldown = Core.getVariableCooldown(DIVE_INTERVAL, DIVE_VARIANCE);
        this.diveCooldown.reset();
        this.pointValue = POINTS;
        this.state = (int)Math.round(new Random().nextDouble(1.0));
    }

    /**
     * Updates attributes, mainly used for animation purposes.
     */
    @Override
    public final void update() {
        if ((this.state / 10) % 2 == 0 && this.state != 2) {
            this.color = Color.YELLOW;
        } else {
            this.color = Color.RED;
        }

        if (this.color == Color.RED) {
            this.spriteType = DrawManager.SpriteType.ENEMY_SHIP_F2;
        } else {
            this.spriteType = DrawManager.SpriteType.ENEMY_SHIP_F1;

        }
    }

    /**
     * @return The state of the diver
     */
    public int getState() {
        return state;
    }

    /**
     * Updates the state
     * @param state
     *          Diver state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * @return The Cooldown for diving
     */
    public Cooldown getDiveCooldown() {
        return diveCooldown;
    }
}
