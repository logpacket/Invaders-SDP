package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.logging.Logger;

import engine.*;
import entity.RankingService;
import entity.RankingEntry;

public class RankingScreen extends Screen{

    /** List of ranking entries fetched from the server.*/
    private List<RankingEntry> rankings;
    private final RankingService rankingService = new RankingService();
    /** Singleton instance of SoundManager*/
    private final SoundManager soundManager = SoundManager.getInstance();

    private boolean isLoading = true;
    private int scrollOffset = 0;
    private final int rowsPerPage;
    private static final int COOLDOWN_TIME = 200;
    private static final Logger logger = Logger.getLogger(RankingScreen.class.getName());

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width Screen width.
     * @param height Screen height.
     * @param fps Frames per second, frame rate at which the game is run.
     */

    public RankingScreen(final int width, final int height, final int fps) {
        super(width, height, fps);
        rowsPerPage = 16;
        this.inputDelay = Core.getCooldown(COOLDOWN_TIME);
        loadRankings();
    }

    private void loadRankings(){
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            executor.submit(() -> {
                try {
                    rankings = rankingService.fetchRankingsFromServer();
                } catch (Exception e) {
                    logger.warning("Failed to load rankings: " + e.getMessage());
                    rankings = List.of(); // Set empty list if loading fails
                } finally {
                    isLoading = false; // Mark loading as complete
                }
            });
            executor.shutdown();
            if (!executor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.warning("Executor was interrupted: " + e.getMessage());
        }

    }

    /**
     * Updates the elements on screen and checks for events.
     */
    @Override
    protected final void update() {
        super.update();

        if (inputManager.isKeyDown(KeyEvent.VK_UP) && this.inputDelay.checkFinished()) {
            scrollOffset = Math.max(scrollOffset - 1, 0);
            inputDelay.reset();
            soundManager.playSound(Sound.MENU_MOVE);
        }
        if (inputManager.isKeyDown(KeyEvent.VK_DOWN) && this.inputDelay.checkFinished()) {
            scrollOffset = Math.min(scrollOffset + 1, Math.max(0, rankings.size() - rowsPerPage));
            inputDelay.reset();
            soundManager.playSound(Sound.MENU_MOVE);
        }
        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE) && this.inputDelay.checkFinished()) {
            this.isRunning = false; // Exit the screen
            soundManager.playSound(Sound.MENU_BACK);
        }

        draw();
    }

    private void draw(){
        drawManager.initDrawing(this);

        if (isLoading) {
            drawManager.drawLoadingScreen(this);
        } else if (rankings.isEmpty()) {
            drawManager.drawErrorMessage(this, "Failed to load rankings.");
        } else {
            drawManager.drawRankingScreen(this, rankings, scrollOffset);
        }

        drawManager.completeDrawing(this);
    }

}
