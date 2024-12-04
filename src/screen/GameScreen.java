package screen;

import engine.Menu;
import engine.*;
import entity.*;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
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
	private static final int SEPARATION_LINE_HEIGHT = 40;

	/** Current game difficulty settings. */
	private final GameSettings gameSettings;
	/** Current level number. */
	private final int level;

	/** Current ship type. */
	private final Ship.ShipType shipType;
	/** Moment the game starts. */
	private long gameStartTime;
	/** list of highScores for find recode. */
	private List<Score>highScores;
	/** Alert Message when a special enemy appears. */
	private String alertMessage;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

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
	public GameScreen(final GameLevelState gameLevelState,
			final GameSettings gameSettings, final int width, final int height, final int fps) {
		super(width, height, fps);
		this.level = gameLevelState.level();
		this.gameSettings = gameSettings;
		this.gameLevelState = gameLevelState;
		this.shipType = gameSettings.shipType();
		this.gameState = new GameState(gameLevelState, gameSettings);

		try {
			this.highScores = FileManager.getInstance().loadHighScores();
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
		this.alertMessage = "";
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	@Override
	public final void initialize() {
		super.initialize();
		gameState.initialize(gameSettings, gameLevelState, this, gameLevelState.formationHeight());

        // Appears each 10-30 seconds.
		logger.info("Player ship created " + this.shipType + " at " + gameState.getShip().getPositionX() + ", " + gameState.getShip().getPositionY());

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

			boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
					|| inputManager.isKeyDown(KeyEvent.VK_D);
			boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
					|| inputManager.isKeyDown(KeyEvent.VK_A);
			gameState.update(playerAttacking, moveRight, moveLeft);
		}

		gameState.updateEnemyShipFormation(this.inputDelay.checkFinished());
		gameState.manageCollisions();
		gameState.cleanBullets();
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
        for (Web web : gameState.getWebList()) {
            drawManager.drawEntity(web, web.getPositionX(),
                    web.getPositionY());
        }
		//draw Blocks
		for (Block b : gameState.getBlock())
			drawManager.drawEntity(b, b.getPositionX(),
					b.getPositionY());

		EnemyShip enemyShipSpecial = gameState.getEnemyShipSpecial();
		if (enemyShipSpecial != null)
			drawManager.drawEntity(enemyShipSpecial,
					enemyShipSpecial.getPositionX(),
					enemyShipSpecial.getPositionY());

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
					gameState.initLapTime();
                    gameState.initTempScore();
                    gameState.initMaxCombo();
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

	/**
	 * Returns a GameState object representing the status of the game.
	 *
	 * @return Current game level state.
	 */
	public final GameLevelState getGameLevelState() {
		return new GameLevelState(gameState.getLevel(), gameState.getScore(), gameState.getLives(),
				gameState.getBulletsShoot(), gameState.getShipsDestroyed(), gameState.getElapsedTime(), gameState.getBonusLife(),
				gameLevelState.formationWidth(), gameLevelState.formationHeight(),
				gameLevelState.baseSpeed(), gameLevelState.shootInterval(),
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
