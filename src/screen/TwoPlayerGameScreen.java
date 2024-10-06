package screen;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager;
import engine.InputManager;

import java.awt.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Implements a generic screen as a Callable task.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public class TwoPlayerGameScreen implements Callable<Integer> {

    /** Milliseconds until the screen accepts user input. */
    private static final int INPUT_DELAY = 1000;

    /** Draw Manager instance. */
    protected DrawManager drawManager;
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
    /** Screen insets. */
    protected Insets insets;
    /** Time until the screen accepts user input. */
    protected Cooldown inputDelay;

    /** If the screen is running. */
    protected boolean isRunning;
    /** What kind of screen goes next. */
    protected int returnCode;

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
    public TwoPlayerGameScreen(final int width, final int height, final int fps) {
        this.width = width;
        this.height = height;
        this.fps = fps;

        this.drawManager = Core.getDrawManager();
        this.inputManager = Core.getInputManager();
        this.logger = Core.getLogger();
        this.inputDelay = Core.getCooldown(INPUT_DELAY);
        this.inputDelay.reset();
        this.returnCode = 0;
    }

    /**
     * Initializes basic screen properties.
     */
    public void initialize() {

    }

    /**
     * Activates the screen.
     *
     * @return Next screen code.
     */
    @Override
    public Integer call() {
        this.isRunning = true;

        while (this.isRunning) {
            long time = System.currentTimeMillis();

            update();

            // Frame rate control
            time = (1000 / this.fps) - (System.currentTimeMillis() - time);
            if (time > 0) {
                try {
                    TimeUnit.MILLISECONDS.sleep(time);
                } catch (InterruptedException e) {
                    // If the thread is interrupted, stop running and return an error code
                    this.isRunning = false;
                    return 0; // Return error code if interrupted
                }
            }
        }

        // Return the code when screen is finished running
        return 0;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected void update() {
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
}
