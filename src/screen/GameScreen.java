package screen;

import engine.Menu;
import engine.*;
import entity.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.*;
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
	private List<Score> highScores;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

	private final GameLevelState gameLevelState;

	private final Game game;

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
		this.game = new Game(gameLevelState, gameSettings);

		try {
			this.highScores = FileManager.getInstance().loadHighScores();
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	@Override
	public final void initialize() {
		super.initialize();
		game.initialize(gameSettings, gameLevelState, this, gameLevelState.formationHeight());

        // Appears each 10-30 seconds.
		logger.info("Player ship created " + this.shipType + " at " + game.getShip().getPositionX() + ", " + game.getShip().getPositionY());

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
		game.addScore(game.getLives());
		this.logger.info("Screen cleared with a score of " + game.getScore());
		return this.menu;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	@Override
	protected final void update() {
		super.update();
		if (this.inputDelay.checkFinished() && !game.isLevelFinished()) {
			boolean playerAttacking = inputManager.isKeyDown(KeyEvent.VK_SPACE);

			boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
					|| inputManager.isKeyDown(KeyEvent.VK_D);
			boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
					|| inputManager.isKeyDown(KeyEvent.VK_A);
			game.update(playerAttacking, moveRight, moveLeft, this.inputDelay.checkFinished());
		}

		if ((game.getEnemyShips().isEmpty() || game.getLives() <= 0) && !game.isLevelFinished()) {
			soundManager.stopSound(soundManager.getCurrentBGM());
			if (game.getLives() == 0)
				soundManager.playSound(Sound.GAME_END);
			game.setScreenFinishedCooldown();
			game.setLevelFinished();
		}
		// leave this in GameScreen
		if (game.isLevelFinished() && game.isScreenFinished()) {
			//Reset alert message when level is finished
			this.menu = Menu.SCORE;
			this.isRunning = false;
		}
	}


	protected void updateEntity() {
		try {
			entityList.add(EntityFactory.createGameTitle(this));
			entityList.addAll(EntityFactory.createLaunchTrajectory(this, game.getShip().getPositionX()));
			entityList.add(EntityFactory.createPing(this, game.getPing()));

			entityList.add(game.getShip());
			entityList.addAll(game.getWebs());
			entityList.addAll(game.getBlocks());
			entityList.addAll(game.getBlockers());
			entityList.addAll(game.getEnemyShips());
			entityList.addAll(game.getItemBoxes());
			entityList.addAll(game.getBarriers());
			entityList.addAll(game.getBullets());

			if (game.getEnemyShipSpecial() != null)
				entityList.add(game.getEnemyShipSpecial());

			// Interface.
			entityList.add(EntityFactory.createScore(this, game.getScore()));
			entityList.add(EntityFactory.createElapsedTime(this, game.getElapsedTime()));
			entityList.add(EntityFactory.createAlertMessage(this, game.getAlertMessage()));
			entityList.add(EntityFactory.createLivesString(this, game.getLives()));
			entityList.addAll(EntityFactory.createLivesSprites(this, game.getLives(), this.shipType));
			entityList.add(EntityFactory.createLevel(this, this.level));
			entityList.addAll(EntityFactory.createHorizontalLines(this, SEPARATION_LINE_HEIGHT - 1));
			entityList.add(EntityFactory.createReloadTimer(this, game.getShip(), game.getShip().getRemainingReloadTime(), this.shipType));
			entityList.add(EntityFactory.createCombo(this, game.getCombo()));


			if (this.gameSettings.isOnlinePlay() && game.isLevelFinished() && game.isScreenFinished() && game.getLives() <= 0) {
				entityList.addAll(EntityFactory.createInGameOver(this));
				entityList.addAll(EntityFactory.createHorizontalLines(this, this.height / 2
						- this.height / 12));
				entityList.addAll(EntityFactory.createHorizontalLines(this, this.height / 2
						+ this.height / 12));
			}

			// Countdown to game start.
			if (!this.inputDelay.checkFinished()) {
				int countdown = (int) ((INPUT_DELAY - (System.currentTimeMillis() - this.gameStartTime)) / 1000);
				entityList.addAll(EntityFactory.createCountDown(this, this.level, countdown, game.getBonusLife()));
				entityList.addAll(EntityFactory.createHorizontalLines(this, this.height / 2 - this.height / 12));
				entityList.addAll(EntityFactory.createHorizontalLines(this, this.height / 2 + this.height / 12));

				//Intermediate aggregation
				if (this.level > 1) {
					if (countdown == 0) {
						//Reset mac combo and edit temporary values
						game.initLapTime();
						game.initTempScore();
						game.initMaxCombo();
					} else {
						// Don't show it just before the game starts, i.e. when the countdown is zero.
						entityList.addAll(EntityFactory.createAggre(this, this.level - 1, game.getMaxCombo(), game.getElapsedTime() - game.getLapTime(), game.getScore(), game.getTempScore()));
					}
				}
			}

			entityList.add(EntityFactory.createRecord(this, highScores));
		} catch (Exception e) {
			entityList.clear();
			entityList.add(EntityFactory.createCenteredSmallString(this, "", 0, Color.BLACK));
		}
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 *
	 * @return Current game level state.
	 */
	public final GameLevelState getGameLevelState() {
		return new GameLevelState(game.getLevel(), game.getScore(), game.getLives(),
				game.getBulletsShoot(), game.getShipsDestroyed(), game.getElapsedTime(), game.getBonusLife(),
				gameLevelState.formationWidth(), gameLevelState.formationHeight(),
				gameLevelState.baseSpeed(), gameLevelState.shootInterval(),
				game.getMaxCombo(), game.getLapTime(), game.getScore(), game.getHitBullets());
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

	public List<Entity> getEntities() {
		updateEntity();
		List<Entity> tmpEntities = (List<Entity>) ((ArrayList<Entity>) entityList).clone();
		entityList.clear();
		return tmpEntities;
	}
}
