package screen;

import engine.Cooldown;
import engine.Core;
import engine.GameSettings;
import engine.GameState;
import entity.*;

import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Implements the game screen, where the action happens.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameScreen2P extends GameScreen implements Callable<Integer> {
    /** Check if the player's game is over */
    private boolean gameFinished = false;
    /** Separate each player. */
    private int playerNumber;
    /** Levels between extra life. */
    private int extraLifeFrequency;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param gameState
     *            Current game state.
     * @param gameSettings
     *            Current game settings.
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public GameScreen2P(final GameState gameState,
                        final GameSettings gameSettings, final int extraLifeFrequency,
                        final int width, final int height, final int fps, final int playerNumber){
        super(gameState, gameSettings, false, width, height, fps);

        this.playerNumber = playerNumber;
        this.extraLifeFrequency = extraLifeFrequency;
    }

    /**
     * Initializes basic screen properties, and adds necessary elements.
     */
    public final void initialize() {
        super.initialize();
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    @Override
    public final Integer call() throws Exception{
        this.gameFinished = false;

        if (!this.gameFinished) {
            super.run();

            this.score += LIFE_SCORE * (this.lives - 1);
            this.logger.info("Screen cleared with a score of " + this.score);

            if (!this.gameFinished) this.newRound();
        }
        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        if (this.inputDelay.checkFinished() && !this.levelFinished) {

            if (!this.ship.isDestroyed()) {
                boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
                        || inputManager.isKeyDown(KeyEvent.VK_D);
                boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
                        || inputManager.isKeyDown(KeyEvent.VK_A);

                boolean isRightBorder = this.ship.getPositionX()
                        + this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
                boolean isLeftBorder = this.ship.getPositionX()
                        - this.ship.getSpeed() < 1;

                if (moveRight && !isRightBorder) {
                    this.ship.moveRight();
                }
                if (moveLeft && !isLeftBorder) {
                    this.ship.moveLeft();
                }
                if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
                    if (this.ship.shoot(this.bullets)) {
                        this.bulletsShot++;
                    }



            }

            if (this.enemyShipSpecial != null) {
                if (!this.enemyShipSpecial.isDestroyed())
                    this.enemyShipSpecial.move(2, 0);
                else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
                    this.enemyShipSpecial = null;

            }
            if (this.enemyShipSpecial == null
                    && this.enemyShipSpecialCooldown.checkFinished()) {
                this.enemyShipSpecial = new EnemyShip();
                this.enemyShipSpecialCooldown.reset();
                this.logger.info("A special ship appears");
            }
            if (this.enemyShipSpecial != null
                    && this.enemyShipSpecial.getPositionX() > this.width) {
                this.enemyShipSpecial = null;
                this.logger.info("The special ship has escaped");
            }

            this.ship.update();
            this.enemyShipFormation.update();
            this.enemyShipFormation.shoot(this.bullets);
        }

        manageCollisions();
        cleanBullets();
        draw();

        if ((this.enemyShipFormation.isEmpty() || this.lives == 0)
                && !this.levelFinished) {
            if (this.lives == 0) this.gameFinished = true;
            this.levelFinished = true;
            this.screenFinishedCooldown.reset();
        }

        if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
            this.isRunning = false;

    }

    /**
     * Initialize the screen, reset the various elements.
     */
    private final void newRound(){
        this.isRunning = true;
        this.levelFinished = false;
        /*
          TO DO
          1. bonusLife logic -> use this.extraLifeFrequency
          2. this.initialize()
          3. level up logic
          4. The necessary logic as the other levels change and the other levels change
         */

    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawEntity(this.ship, this.ship.getPositionX(),
                this.ship.getPositionY());
        if (this.enemyShipSpecial != null)
            drawManager.drawEntity(this.enemyShipSpecial,
                    this.enemyShipSpecial.getPositionX(),
                    this.enemyShipSpecial.getPositionY());

        enemyShipFormation.draw();

        for (Bullet bullet : this.bullets)
            drawManager.drawEntity(bullet, bullet.getPositionX(),
                    bullet.getPositionY());

        // Interface.
        drawManager.drawScore(this, this.score);
        drawManager.drawLives(this, this.lives);
        drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);

        // Countdown to game start.
        if (!this.inputDelay.checkFinished()) {
            int countdown = (int) ((INPUT_DELAY
                    - (System.currentTimeMillis()
                    - this.gameStartTime)) / 1000);
            drawManager.drawCountDown(this, this.level, countdown,
                    this.bonusLife);
            drawManager.drawHorizontalLine(this, this.height / 2 - this.height
                    / 12);
            drawManager.drawHorizontalLine(this, this.height / 2 + this.height
                    / 12);
        }

        drawManager.completeDrawing2P(this, playerNumber);
    }

    /**
     * Cleans bullets that go off screen.
     */
    private void cleanBullets() {
        Set<Bullet> recyclable = new HashSet<Bullet>();
        for (Bullet bullet : this.bullets) {
            bullet.update();
            if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
                    || bullet.getPositionY() > this.height)
                recyclable.add(bullet);
        }
        this.bullets.removeAll(recyclable);
        BulletPool.recycle(recyclable);
    }

    /**
     * Manages collisions between bullets and ships.
     */
    private void manageCollisions() {
        Set<Bullet> recyclable = new HashSet<Bullet>();
        for (Bullet bullet : this.bullets)
            if (bullet.getSpeed() > 0) {
                if (checkCollision(bullet, this.ship) && !this.levelFinished) {
                    recyclable.add(bullet);
                    if (!this.ship.isDestroyed()) {
                        this.ship.destroy();
                        this.lives--;
                        this.logger.info("Hit on " + this.playerNumber + "player ship, " + this.lives
                                + " lives remaining.");
                    }
                }
            } else {
                for (EnemyShip enemyShip : this.enemyShipFormation)
                    if (!enemyShip.isDestroyed()
                            && checkCollision(bullet, enemyShip)) {
                        this.score += enemyShip.getPointValue();
                        this.shipsDestroyed++;
                        this.enemyShipFormation.destroy(enemyShip);
                        recyclable.add(bullet);
                    }
                if (this.enemyShipSpecial != null
                        && !this.enemyShipSpecial.isDestroyed()
                        && checkCollision(bullet, this.enemyShipSpecial)) {
                    this.score += this.enemyShipSpecial.getPointValue();
                    this.shipsDestroyed++;
                    this.enemyShipSpecial.destroy();
                    this.enemyShipSpecialExplosionCooldown.reset();
                    recyclable.add(bullet);
                }
            }
        this.bullets.removeAll(recyclable);
        BulletPool.recycle(recyclable);
    }

    /**
     * Checks if two entities are colliding.
     *
     * @param a
     *            First entity, the bullet.
     * @param b
     *            Second entity, the ship.
     * @return Result of the collision test.
     */
    private boolean checkCollision(final Entity a, final Entity b) {
        // Calculate center point of the entities in both axis.
        int centerAX = a.getPositionX() + a.getWidth() / 2;
        int centerAY = a.getPositionY() + a.getHeight() / 2;
        int centerBX = b.getPositionX() + b.getWidth() / 2;
        int centerBY = b.getPositionY() + b.getHeight() / 2;
        // Calculate maximum distance without collision.
        int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
        int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
        // Calculates distance.
        int distanceX = Math.abs(centerAX - centerBX);
        int distanceY = Math.abs(centerAY - centerBY);

        return distanceX < maxDistanceX && distanceY < maxDistanceY;
    }

    /**
     * Returns a GameState object representing the status of the game.
     *
     * @return Current game state.
     */
    public final GameState getGameState() {
        return new GameState(this.level, this.score, this.lives,
                this.bulletsShot, this.shipsDestroyed);
    }
}