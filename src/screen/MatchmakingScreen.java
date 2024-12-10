package screen;

import engine.*;
import entity.Entity;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class MatchmakingScreen extends Screen {

    /** Timer start time in milliseconds. */
    private long startTime;
    /** Rendeerer instance **/
    private final Renderer renderer;
    /** Game difficulty level **/
    private final int difficultyLevel;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second, frame rate at which the game is run.
     */
    public MatchmakingScreen(final int width, final int height, final int fps, final GameSettings gameSettings) {
        super(width, height, fps);
        this.startTime = System.currentTimeMillis();
        this.menu = Menu.MATCHMAKING;
        this.renderer = Renderer.getInstance();
        this.difficultyLevel = gameSettings.difficulty();
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    @Override
    protected final void update() {
        super.update();
        draw();
    }

    @Override
    protected void updateEntity() {

    }

    /**
     * Draws the matchmaking screen elements.
     */
    public void draw() {
        List<Entity> entities = new ArrayList<Entity>();

        // Calculate elapsed time in seconds
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;

        // Create waiting text
        String difficultyString = "Difficulty : " + switch(difficultyLevel) {
            case 0 -> "Easy";
            case 1 -> "Normal";
            case 2 -> "Hard";
            default -> "";
        };
        entities.add(EntityFactory.createCenteredRegularString(
                this,
                difficultyString,
                this.getHeight() / 2 - 60,
                Color.GREEN
        ));

        entities.add(EntityFactory.createCenteredRegularString(
                this,
                "Waiting for player",
                this.getHeight() / 2 - 20,
                Color.WHITE
        ));

        // Create timer text
        entities.add(EntityFactory.createCenteredRegularString(
                this,
                formatTime(elapsedTime),
                this.getHeight() / 2 + 20,
                Color.WHITE
        ));

        // Initialize drawing
        renderer.initDrawing(this);

        // Draw all entities
        renderer.drawEntities(entities);

        // Complete drawing
        renderer.completeDrawing(this);
    }

    /**
     * Formats elapsed time in seconds into MM:SS format.
     *
     * @param elapsedTime Elapsed time in seconds.
     * @return Formatted time string.
     */
    private String formatTime(long elapsedTime) {
        long minutes = elapsedTime / 60;
        long seconds = elapsedTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
