package screen;

import engine.*;
import message.Ranking;
import message.RankingList;
import service.RankingService;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

public class RankingScreen extends Screen {

    private final SoundManager soundManager = SoundManager.getInstance();
    private final RankingService rankingService = new RankingService();
    private List<Ranking> rankings;
    private boolean isLoading = true;
    private int scrollOffset = 0;
    private final int rowsPerPage;
    private final GameState gameState;

    public RankingScreen(int width, int height, int fps, GameState gameState) {
        super(width, height, fps);
        this.rowsPerPage = Math.max(1, height / 70);
        this.gameState = gameState;
        loadRankings();
    }

    private void loadRankings() {
        isLoading = true;

        rankingService.fetchRankings(
                event -> {
                    if (event.body() instanceof RankingList response) {
                        try {
                            rankings = response.rankings();
                            isLoading = false;
                        } catch (ClassCastException e) {
                            logger.warning("Invalid data format received from the server.");
                            isLoading = false;
                        }
                    } else {
                        logger.warning("Failed to parse rankings data.");
                        isLoading = false;
                    }
                },
                error -> {
                    logger.warning("Failed to fetch rankings: " + error.message());
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
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    @Override
    protected void updateEntity() {
        entityList.clear();

        if (isLoading) {
            entityList.add(EntityFactory.createCenteredBigString(this, "Loading...", Math.round(getHeight() * 0.5f), Color.YELLOW));
        } else if (rankings == null || rankings.isEmpty()) {
            entityList.add(EntityFactory.createCenteredBigString(this, "No rankings available.", Math.round(getHeight() * 0.5f), Color.RED));
        } else {
            entityList.addAll(EntityFactory.createRankingScreen(this, rankings));
        }
    }
}
