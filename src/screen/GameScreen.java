package screen;

import engine.Menu;
import engine.*;
import entity.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Implements the game screen, where the action happens.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class GameScreen extends Screen implements Callable<GameLevelState> {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Minimum time between bonus ship's appearances. */
	private static final int BONUS_SHIP_INTERVAL = 20000;
	/** Maximum variance in the time between bonus ship's appearances. */
	private static final int BONUS_SHIP_VARIANCE = 10000;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;

	/** Current game difficulty settings. */
	private final GameSettings gameSettings;
	/** Current level number. */
	private final int level;
	/** Bonus enemy ship that appears sometimes. */
	private EnemyShip enemyShipSpecial;
	/** Minimum time between bonus ship appearances. */
	private Cooldown enemyShipSpecialCooldown;
	/** Time until bonus ship explosion disappears. */
	private Cooldown enemyShipSpecialExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;

	/** Current ship type. */
	private final Ship.ShipType shipType;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Player number for two player mode **/
	private int playerNumber;
	/** list of highScores for find recode. */
	private List<Score>highScores;
	/** Alert Message when a special enemy appears. */
	private String alertMessage;
	/** Blocker appearance cooldown */
	private final Cooldown blockerCooldown;
	private final Random random;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();
	/** Singleton instance of ItemManager. */
	private ItemManager itemManager;
	/** Sound balance for each player*/
	private float balance = 0.0f;
	private int maxBlockers = 0;

	private final GameLevelState gameLevelState;

	private final GameState gameState;

    /**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param gameLevelState
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
	public GameScreen(final GameState gameState, final GameLevelState gameLevelState,
			final GameSettings gameSettings, final int width, final int height, final int fps) {
		super(width, height, fps);
		this.level = gameLevelState.level();
		this.gameSettings = gameSettings;
		this.gameLevelState = gameLevelState;
		this.gameState = gameState;
		this.shipType = gameSettings.shipType();

		try {
			this.highScores = FileManager.getInstance().loadHighScores();
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}

		this.random = new Random();
		this.blockerCooldown = Core.getVariableCooldown(10000, 14000);
		this.blockerCooldown.reset();
		this.alertMessage = "";
	}

	/**
	 * Constructor, establishes the properties of the screen for two player mode.
	 * @param gameState
	 *            Current game state.
	 * @param gameLevelState
	 *            Current game level state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 * @param playerNumber
	 *            Player number for two player mode
	 */
	public GameScreen(final GameState gameState, final GameLevelState gameLevelState,
					  final GameSettings gameSettings,
					  final int width, final int height, final int fps,
					  final int playerNumber) {
		this(gameState,  gameLevelState, gameSettings, width, height, fps);
		this.playerNumber = playerNumber;
		this.balance = switch (playerNumber) {
			case 0: yield -1.0f; // 1P
			case 1: yield 1.0f;  // 2P
			default: yield 0.0f; // default
		};
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	@Override
	public final void initialize() {
		super.initialize();

		// Initialize EnemyShipFormation in GameState
		gameState.initializeEnemyShipFormation(this.gameSettings, this.gameLevelState);
		gameState.getEnemyShipFormation().attach(this);
		gameState.setShip(this);

        // Appears each 10-30 seconds.
		logger.info("Player ship created " + this.shipType + " at " + gameState.getShip().getPositionX() + ", " + gameState.getShip().getPositionY());
		gameState.getShip().applyItem();

		gameState.initialize(gameState.getLevel(),	this, gameLevelState.formationHeight()
		);

		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.itemManager = new ItemManager(gameState.getShip(), gameState.getEnemyShipFormation(), gameState.getBarriers(), this.width, this.height, this.balance);

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
		if (soundManager.isSoundPlaying(Sound.BGM_MAIN))
			soundManager.stopSound(Sound.BGM_MAIN);
		soundManager.playSound(Sound.COUNTDOWN);

		switch (this.level) {
			case 1: soundManager.loopSound(Sound.BGM_LV1); break;
			case 2: soundManager.loopSound(Sound.BGM_LV2); break;
			case 3: soundManager.loopSound(Sound.BGM_LV3); break;
			case 4: soundManager.loopSound(Sound.BGM_LV4); break;
			case 5: soundManager.loopSound(Sound.BGM_LV5); break;
			case 6: soundManager.loopSound(Sound.BGM_LV6); break;
            case 7:
				// From level 7 and above, it continues to play at BGM_LV7.
            default: soundManager.loopSound(Sound.BGM_LV7); break;
		}
	}

	/**
	 * Starts the action.
	 *
	 * @return Next screen code.
	 */
	@Override
	public final Menu run() {
		super.run();
		gameState.addScore(gameState.getLives());
		this.logger.info("Screen cleared with a score of " + gameState.getScore());
		return this.menu;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	@Override
	protected final void update() {
		super.update();
		if (this.inputDelay.checkFinished() && !gameState.isLevelFinished()) {
			boolean playerAttacking = inputManager.isKeyDown(KeyEvent.VK_SPACE);
			if (playerAttacking && gameState.getShip().shoot(gameState.getBullets(), this.itemManager.getShootNum()))
				gameState.setBulletsShoot();

			if (gameState.getPrevTime() != null)
				gameState.setElapsedTime();

			gameState.setPrevTime();

			if(!itemManager.isGhostActive())
				gameState.getShip().setColor(Color.GREEN);

			if (!gameState.getShip().isDestroyed()) {
				boolean moveRight;
				boolean moveLeft;

				moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
						|| inputManager.isKeyDown(KeyEvent.VK_D);
				moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
						|| inputManager.isKeyDown(KeyEvent.VK_A);

				// TODO: add isRightBorder method to Ship class
				boolean isRightBorder = gameState.getShip().getPositionX()
						+ gameState.getShip().getWidth() + gameState.getShip().getSpeed() > this.width - 1;
				// -> ship.getNextRightPosition() > this.width -1;
				boolean isLeftBorder = gameState.getShip().getPositionX()
						- gameState.getShip().getSpeed() < 1;

				if (moveRight && !isRightBorder) {
					if (playerNumber == -1)
						gameState.getShip().moveRight();
					else
						gameState.getShip().moveRight(balance);
				}
				if (moveLeft && !isLeftBorder) {
					if (playerNumber == -1)
						gameState.getShip().moveLeft();
					else
						gameState.getShip().moveLeft(balance);
				}
				for (Web web : gameState.getWeb()) {
					//escape Spider Web
					if (gameState.getShip().getPositionX() + 6 <= web.getPositionX() - 6
							|| web.getPositionX() + 6 <= web.getPositionX() - 6) {
						gameState.getShip().setThreadWeb(false);
					}
					//get caught in a spider's web
					else {
						gameState.getShip().setThreadWeb(true);
						break;
					}
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
				this.alertMessage = "";
				this.enemyShipSpecialCooldown.reset();
				soundManager.playSound(Sound.UFO_APPEAR, balance);
				this.logger.info("A special ship appears");
			}
			if(this.enemyShipSpecial == null
					&& this.enemyShipSpecialCooldown.checkAlert()) {
				switch (this.enemyShipSpecialCooldown.checkAlertAnimation()){
					case 1: this.alertMessage = "--! ALERT !--";
						break;

					case 2: this.alertMessage = "-!! ALERT !!-";
						break;

					case 3: this.alertMessage = "!!! ALERT !!!";
						break;

					default: this.alertMessage = "";
						break;
				}
			}
			if (this.enemyShipSpecial != null
					&& this.enemyShipSpecial.getPositionX() > this.width) {
				this.enemyShipSpecial = null;
				this.logger.info("The special ship has escaped");
			}

			gameState.getShip().update();

			// If Time-stop is active, Stop updating enemy ships' move and their shoots.
			if (!itemManager.isTimeStopActive()) {
				gameState.getEnemyShipFormation().update();
				gameState.getEnemyShipFormation().shoot(gameState.getBullets(), this.level, balance);
			}

			if (level >= 3) { //Events where vision obstructions appear start from level 3 onwards.
				handleBlockerAppearance();
			}
		}

		if(this.inputDelay.checkFinished() && !itemManager.isTimeStopActive()) {
			gameState.getEnemyShipFormation().updateSmooth();
		}

		gameState.manageCollisions();
		gameState.cleanBullets(this);

		draw();

		if ((gameState.getEnemyShipFormation().isEmpty() || gameState.getLives() <= 0)
				&& !gameState.isLevelFinished()) {
			gameState.setLevelFinished();

			soundManager.stopSound(soundManager.getCurrentBGM());
			if (gameState.getLives() == 0)
				soundManager.playSound(Sound.GAME_END);
			gameState.setScreenFinishedCooldown().reset();
		}
		// leave this in GameScreen
		if (gameState.isLevelFinished() && gameState.setScreenFinishedCooldown().checkFinished()) {
			//Reset alert message when level is finished
			this.alertMessage = "";
			this.menu = Menu.SCORE;
			this.isRunning = false;
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		drawManager.drawGameTitle(this);
		drawManager.drawLaunchTrajectory( this,gameState.getShip().getPositionX());
		drawManager.drawEntity(gameState.getShip(), gameState.getShip().getPositionX(), gameState.getShip().getPositionY());

		//draw Spider Web
        for (Web web : gameState.getWeb()) {
            drawManager.drawEntity(web, web.getPositionX(),
                    web.getPositionY());
        }
		//draw Blocks
		for (Block b : gameState.getBlock())
			drawManager.drawEntity(b, b.getPositionX(),
					b.getPositionY());

		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY());

		gameState.getEnemyShipFormation().draw();

		for (ItemBox itemBox : gameState.getItemBoxes())
			drawManager.drawEntity(itemBox, itemBox.getPositionX(), itemBox.getPositionY());

		for (Barrier barrier :gameState.getBarriers())
			drawManager.drawEntity(barrier, barrier.getPositionX(), barrier.getPositionY());

		for (Bullet bullet : gameState.getBullets())
			drawManager.drawEntity(bullet, bullet.getPositionX(), bullet.getPositionY());

		// Interface.
		drawManager.drawScore(this, gameState.getScore());
		drawManager.drawElapsedTime(this, gameState.getElapsedTime());
		drawManager.drawAlertMessage(this, this.alertMessage);
		drawManager.drawLives(this, gameState.getLives(), this.shipType);
		drawManager.drawLevel(this, this.level);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);
		drawManager.drawReloadTimer(this, gameState.getShip(), gameState.getShip().getRemainingReloadTime(), this.shipType);
		drawManager.drawCombo(this, gameState.getCombo());

		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY - (System.currentTimeMillis() - this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown, gameState.getBonusLife());
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height / 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height / 12);

			//Intermediate aggregation
			if (this.level > 1){
                if (countdown == 0) {
					//Reset mac combo and edit temporary values
                    gameState.setLapTime();
                    gameState.setTempScore();
                    gameState.setMaxCombo();
                } else {
					// Don't show it just before the game starts, i.e. when the countdown is zero.
                    drawManager.interAggre(this, this.level - 1, gameState.getMaxCombo(), gameState.getElapsedTime() - gameState.getLapTime(), gameState.getScore(), gameState.getTempScore());
                }
			}
		}

		//add drawRecord method for drawing
		drawManager.drawRecord(highScores,this);

		// Blocker drawing part
		if (!gameState.getBlockers().isEmpty()) {
			for (Blocker blocker : gameState.getBlockers()) {
				drawManager.drawRotatedEntity(blocker, blocker.getPositionX(), blocker.getPositionY(), blocker.getAngle());
			}
		}
		drawManager.completeDrawing(this);
	}

	// Methods that handle the position, angle, sprite, etc. of the blocker (called repeatedly in update.)
	private void handleBlockerAppearance() {

		if (level >= 3 && level < 6) maxBlockers = 1;
		else if (level >= 6 && level < 11) maxBlockers = 2;
		else if (level >= 11) maxBlockers = 3;

		int kind = random.nextInt(2 - 1 + 1) + 1; // 1~2
		DrawManager.SpriteType newSprite = switch (kind) {
			case 1 -> DrawManager.SpriteType.BLOCKER_1; // artificial satellite
			case 2 -> DrawManager.SpriteType.BLOCKER_2; // astronaut
			default -> DrawManager.SpriteType.BLOCKER_1;
		};

		// Check number of blockers and cooldown
		if (gameState.getBlockers().size() < maxBlockers && blockerCooldown.checkFinished()) {
			boolean isLeftDirection = random.nextBoolean(); // Random movement direction
			int startY = random.nextInt(this.height - 90) + 25; // Random Y position
			int startX = isLeftDirection ? this.width + 300 : -300; // Start position based on direction

			// Add new Blocker to GameState
			Blocker newBlocker = new Blocker(startX, startY, newSprite, isLeftDirection);
			gameState.addBlocker(newBlocker);
			blockerCooldown.reset();
		}

		// Manage existing blockers in GameState
		List<Blocker> blockers = gameState.getBlockers();
		for (int i = 0; i < blockers.size(); i++) {
			Blocker blocker = blockers.get(i);

			// Remove blockers that leave the screen
			if (blocker.getMoveLeft() && blocker.getPositionX() < -300
					|| !blocker.getMoveLeft() && blocker.getPositionX() > this.width + 300) {
				gameState.removeBlocker(blocker);
				i--;
				continue;
			}

			// Update blocker position and rotation
			if (blocker.getMoveLeft()) {
				blocker.move(-1.5, 0); // Move left
			} else {
				blocker.move(1.5, 0); // Move right
			}
			blocker.rotate(0.2); // Rotate blocker
		}
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 *
	 * @return Current game state.
	 */
	public final GameLevelState getGameLevelState() {
		return new GameLevelState(gameState.getLevel(), gameState.getScore(), gameState.getLives(),
				gameState.getBulletsShoot(), gameState.getShipsDestroyed(), gameState.getElapsedTime(), gameState.getBonusLife(),
				this.gameLevelState.formationWidth(), this.gameLevelState.formationHeight(),
				this.gameLevelState.baseSpeed(), this.gameLevelState.shootInterval(),
				gameState.getMaxCombo(), gameState.getLapTime(), gameState.getTempScore(), gameState.getHitBullets());
	}
	/**
	 * Start the action for two player mode
	 *
	 * @return Current game state.
	 */
	@Override
	public final GameLevelState call() {
		run();
		return getGameLevelState();
	}
}
