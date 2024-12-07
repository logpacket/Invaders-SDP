package screen;

import engine.*;
import message.Ranking;
import service.RankingService;

import java.awt.event.KeyEvent;
import java.util.List;

public class RankingScreen extends Screen {

    private final SoundManager soundManager = SoundManager.getInstance();
    private final RankingService rankingService = new RankingService();
    private List<Ranking> rankings;
    private boolean isLoading = true;
    private int scrollOffset = 0;
    private final int rowsPerPage = 10;

    public RankingScreen(int width, int height, int fps) {
        super(width, height, fps);
        loadRankings();
    }

    private void loadRankings() {
        isLoading = true;

        rankingService.fetchRankings(
                event -> {
                    if (event.body() instanceof List<?> fetchedData) {
                        try {
                            rankings = (List<Ranking>) fetchedData;
                            isLoading = false;
                        } catch (ClassCastException e) {
                            System.err.println("Invalid data format received from the server.");
                            isLoading = false;
                        }
                    } else {
                        System.err.println("Failed to parse rankings data.");
                        isLoading = false;
                    }
                },
                error -> {
                    System.err.println("Failed to fetch rankings: " + error.message());
                    isLoading = false;
                }
        );
    }

    @Override
    protected void update() {
        super.update();

        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)
                && this.inputDelay.checkFinished()) {
            this.isRunning = false;
            soundManager.playSound(Sound.MENU_BACK);
        }

        if (inputManager.isKeyDown(KeyEvent.VK_UP) && inputDelay.checkFinished()) {
            scrollOffset = Math.max(0, scrollOffset - 1);
            inputDelay.reset();
        }

        if (inputManager.isKeyDown(KeyEvent.VK_DOWN) && inputDelay.checkFinished()) {
            scrollOffset = Math.min(scrollOffset + 1, Math.max(0, rankings.size() - rowsPerPage));
            inputDelay.reset();
        }

        draw();
    }

    private void draw() {
        drawManager.initDrawing(this);

        if (isLoading) {
            drawManager.drawLoadingScreen(this);
        } else if (rankings == null || rankings.isEmpty()) {
            drawManager.drawErrorMessage(this, "No rankings available.");
        } else {
            drawManager.drawRankingScreen(this, rankings, scrollOffset);
        }

        drawManager.completeDrawing(this);
    }
}
