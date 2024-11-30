package screen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import engine.*;
import entity.Entity;

/**
 * Implements a generic screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public abstract class Screen {
	
	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 1000;

	/** Draw Manager instance. */
	protected Renderer renderer;
	/** Input Manager instance. */
	protected InputManager inputManager;
	/** Application logger. */
	protected Logger logger;

	/** Screen width. */
	protected int width;
	/** Screen height. */
	protected int height;
	/** Frames per second shown on the screen. */
	protected int fps;
	/** Time until the screen accepts user input. */
	protected Cooldown inputDelay;

	/** If the screen is running. */
	protected boolean isRunning;
	/** What kind of screen goes next. */
	protected Menu menu;

	protected List<Entity> backBufferEntities;
	protected List<Entity> frontBufferEntities;

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
	protected Screen(final int width, final int height, final int fps) {
		this.width = width;
		this.height = height;
		this.fps = fps;

		this.renderer = Renderer.getInstance();
		this.inputManager = InputManager.getInstance();
		this.logger = Core.getLogger();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
		this.menu = Menu.MAIN;
		this.frontBufferEntities = new ArrayList<Entity>();
		this.backBufferEntities = new ArrayList<Entity>();
	}

	/**
	 * Initializes basic screen properties.
	 */
	public void initialize() { }

	/**
	 * Activates the screen.
	 * 
	 * @return Next screen code.
	 */
	public Menu run() {
		this.isRunning = true;

		while (this.isRunning) {
			long time = System.currentTimeMillis();

			update();

			time = (1000 / this.fps) - (System.currentTimeMillis() - time);
			if (time > 0) {
				try {
					TimeUnit.MILLISECONDS.sleep(time);
				} catch (InterruptedException e) {
					return this.menu;
				}
			}
		}

		return this.menu;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected void update() {
		updateEntity();
		draw();
	}

	/**
	 * Create Entities to BackBufferEntities And swapBuffer
	 */
	protected abstract void updateEntity();

	protected void draw() {
        renderer.initDrawing(this);
        renderer.drawEntities(frontBufferEntities);
        renderer.completeDrawing(this);
    }

	/**
	 * Getter for screen width.
	 * 
	 * @return Screen width.
	 */
	public final int getWidth() {
		return this.width;
	}

	/**
	 * Getter for screen height.
	 * 
	 * @return Screen height.
	 */
	public final int getHeight() {
		return this.height;
	}

	public void swapBuffers() {
		List<Entity> temp = frontBufferEntities;
		frontBufferEntities = backBufferEntities;
		backBufferEntities = temp;

		backBufferEntities.clear();
	}
}