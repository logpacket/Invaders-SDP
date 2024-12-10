package screen;

import java.awt.event.KeyEvent;

import engine.*;


/**
 * Implements the title screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class TitleScreen extends Screen {

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;

	/** Time between changes in user selection. */
	private final Cooldown selectionCooldown;

	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

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
	public TitleScreen(final int width, final int height, final int fps) {
		super(width, height, fps);

		// Defaults to play.
		if (!soundManager.isSoundPlaying(Sound.BGM_MAIN))
			soundManager.loopSound(Sound.BGM_MAIN);

		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
		this.menu = Menu.GAME_SETTING;

		renderer.initDrawing(this); //to initialize FontManager.fontMetrics
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		if (this.selectionCooldown.checkFinished()
				&& this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_UP)
					|| inputManager.isKeyDown(KeyEvent.VK_W)) {
				this.menu = this.menu.getPrev();
				this.selectionCooldown.reset();
				soundManager.playSound(Sound.MENU_MOVE);
			}
			if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
					|| inputManager.isKeyDown(KeyEvent.VK_S)) {
				this.menu = this.menu.getNext();
				this.selectionCooldown.reset();
				soundManager.playSound(Sound.MENU_MOVE);
			}
			if (inputManager.isKeyDown(KeyEvent.VK_SPACE)){
				this.isRunning = false;
				soundManager.playSound(Sound.MENU_CLICK);
			}
		}
	}


	protected void updateEntity() {
		entityList.addAll(EntityFactory.createTitle(this));
		entityList.addAll(EntityFactory.createMenu(this, this.menu));

	}


}