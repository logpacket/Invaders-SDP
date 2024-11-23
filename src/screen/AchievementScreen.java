package screen;

import engine.*;
import entity.Achievement;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

/**
 * Implements the achievement screen.
 * Team NOF
 * 
 */
public class AchievementScreen extends Screen {

	/** List of past high scores. */
	private List<Score> highScores;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

	private int totalScore;
	private int totalPlayTime;
	private int currentPerfectStage;
	private int maxCombo;
	private boolean checkFlawlessFailure;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public AchievementScreen(final int width, final int height, final int fps) {
		super(width, height, fps);

		this.menu = Menu.MAIN;
		FileManager fileManager = FileManager.getInstance();


		try {
			Achievement achievement = fileManager.loadAchievement();
			this.highScores = fileManager.loadHighScores();
			this.totalScore = achievement.getTotalScore();
			this.totalPlayTime = fileManager.loadAchievement().getTotalPlayTime();
			this.currentPerfectStage = fileManager.loadAchievement().currentPerfectStage;
			this.maxCombo = fileManager.loadAchievement().maxCombo;
			this.checkFlawlessFailure = fileManager.loadAchievement().flawlessFailure;
		} catch (NumberFormatException | IOException e) {
			logger.warning("Couldn't load total achievement!");
		}
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		createEntity();
		draw();
		if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)
				&& this.inputDelay.checkFinished()) {
			this.isRunning = false;
			soundManager.playSound(Sound.MENU_BACK);
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		renderer.initDrawing(this);
		renderer.drawEntities(frontBufferEntities);
		renderer.completeDrawing(this);
	}

	protected void createEntity(){
		backBufferEntities.addAll(EntityFactory.createAchievementMenu(this, this.totalScore, this.totalPlayTime,
				this.maxCombo, this.currentPerfectStage, this.currentPerfectStage+1,
				this.checkFlawlessFailure));
		backBufferEntities.addAll(EntityFactory.createHighScores(this, this.highScores));

		swapBuffers();
	}
}
