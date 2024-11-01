package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import engine.*;
import entity.Wallet;

/**
 * Implements the score screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class ScoreScreen extends Screen {

	/** Maximum number of high scores. */
	private static final int MAX_HIGH_SCORE_NUM = 3;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();


	/** Current score. */
	private final int score;
	/** Player lives left. */
	private final int livesRemaining;
	/** Total ships destroyed by the player. */
	private final int shipsDestroyed;
	/** List of past high scores. */
	private List<Score> highScores;
	/** Checks if current score is a new high score. */
	private final double accuracy;
	private boolean isNewRecord;
	/** Number of coins earned in the game */
	private int coinsEarned;
	/** Player's name */
	private final String name1;
	/** Two player mode flags*/
	private final boolean isMultiplayer;

	// Set ratios for each coinLevel - placed in an array in the order of lv1, lv2, lv3, lv4, and will be used accordingly,
	// e.g., lv1; score 100 * 0.1
	private static final double[] COIN_RATIOS = {0.1, 0.13, 0.16, 0.19};

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 * @param gameState
	 *            Current game state.
	 */
	public ScoreScreen(final String name1, final int width, final int height, final int fps,
			final GameState gameState, final AchievementManager achievementManager,
		    final boolean isMultiplayer) {
		super(width, height, fps);

		this.name1 = name1;

		this.score = gameState.score;
		this.livesRemaining = gameState.livesRemaining;
		this.shipsDestroyed = gameState.shipsDestroyed;
		this.isMultiplayer = isMultiplayer;

		Wallet wallet = Wallet.getWallet();

		// Get the user's coinLevel
		int coinLevel = wallet.getCoinLevel();

		// Apply different ratios based on coinLevel
		double coinRatio = COIN_RATIOS[coinLevel-1];

		// Adjust coin earning ratios based on the game level upgrade stage score
		// Since coins are in integer units, round the decimal points and convert to int
		this.coinsEarned = (int)Math.round(this.score * coinRatio);
		this.coinsEarned += achievementManager.getAchievementReward();

		// deposit the earned coins to wallet
		this.accuracy = gameState.getAccuracy();
		wallet.deposit(coinsEarned);

		soundManager.loopSound(Sound.BGM_GAME_OVER);

		try {
			this.highScores = FileManager.getInstance().loadHighScores();
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	@Override
	protected final void update() {
		super.update();

		draw();
		if (this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
				// Return to main menu.
				this.menu = Menu.MAIN;
				this.isRunning = false;
				soundManager.stopSound(Sound.BGM_GAME_OVER);
				soundManager.playSound(Sound.MENU_BACK);
				saveScore();
			} else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				// Play again.
				this.menu = isMultiplayer ? Menu.MULTI_PLAY : Menu.SINGLE_PLAY;
				this.isRunning = false;
				soundManager.stopSound(Sound.BGM_GAME_OVER);
				soundManager.playSound(Sound.MENU_CLICK);
				saveScore();
			}
		}
	}

	/**
	 * Saves the score as a high score.
	 * 중복 방지를 위한 로직 추가.
	 */
	private void saveScore() {
		if (highScores.size() > MAX_HIGH_SCORE_NUM) {
			int index = 0;
			for (Score loadScore : highScores) {
				if (name1.equals(loadScore.name()) && score > loadScore.score()) {
					highScores.remove(index);
					highScores.add(new Score(name1, score));
					break;
				}
				index += 1;
			}
		} else {
			boolean checkDuplicate = false;
			int index = 0;
			for (Score loadScore : highScores) {
				if (name1.equals(loadScore.name())) {
					checkDuplicate = true;
					if (score > loadScore.score()) {
						highScores.remove(index);
						highScores.add(new Score(name1, score));
						break;
					}
				}
				index += 1;
			}
			if (!checkDuplicate) {
				highScores.add(new Score(name1, score));
			}
		}
		Collections.sort(highScores);
		try {
			FileManager.getInstance().saveHighScores(highScores);
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawGameOver(this, this.inputDelay.checkFinished(),
				this.isNewRecord);
		drawManager.drawResults(this, this.score, this.livesRemaining,
				this.shipsDestroyed, this.accuracy, this.isNewRecord, this.coinsEarned);

		drawManager.completeDrawing(this);
	}
}
