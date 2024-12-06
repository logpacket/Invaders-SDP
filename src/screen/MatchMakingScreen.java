package screen;

import engine.*;

public class MatchMakingScreen extends Screen {

    /** Timer start time in milliseconds. */
    private long startTime;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second, frame rate at which the game is run.
     */
    public MatchMakingScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        this.startTime = System.currentTimeMillis();
        this.menu = Menu.MATCHMAKING;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    @Override
    protected final void update() {
        super.update();
        draw();
    }

    /**
     * Draws the matchmaking screen elements.
     */
    private void draw() {
        drawManager.initDrawing(this);

        // Calculate elapsed time in seconds
        long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;

        // Display "Waiting for player" at the center
        drawManager.drawCenteredRegularString(this, "Waiting for player", this.height / 2 - 20);

        // Display elapsed time in MM:SS format
        drawManager.drawCenteredRegularString(this, formatTime(elapsedTime), this.height / 2 + 20);

        drawManager.completeDrawing(this);
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

    /**
     * Main method for standalone testing of MatchMakingScreen.
     */
    public static void main(final String[] args) {
        Frame frame = new Frame(600, 650); // 화면 크기 설정
        System.out.println("Frame created."); // Debugging log
        DrawManager.getInstance().setFrame(frame);

        Screen matchMakingScreen = new MatchMakingScreen(600, 650, 60); // MatchMakingScreen 생성
        System.out.println("MatchMakingScreen created."); // Debugging log
        frame.setScreen(matchMakingScreen);
        System.out.println("Screen set successfully."); // Debugging log
    }
}
