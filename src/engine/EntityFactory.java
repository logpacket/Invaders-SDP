package engine;

import entity.*;
import screen.Screen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EntityFactory {

    public static SpriteEntity createSpriteEntity(final int positionX, final int positionY, final int width,
						final int height, final Color color){
        return new SpriteEntity(positionX, positionY, width, height, color);
    }

    public static TextEntity createTextEntity(final int positionX, final int positionY,
                                              final Color color, final String text, final Font font){
        return new TextEntity(positionX, positionY, color, text, font);
    }

    public static LineEntity createLineEntity(final int positionX, final int positionY,
                      final int positionX2, final int positionY2, final Color color){
        return new LineEntity(positionX, positionY, positionX2, positionY2, color);
    }

    public static RectEntity createRectEntity(final int positionX, final int positionY, final Color color,
                      final int width, final int height, final boolean isFilled){
        return new RectEntity(positionX, positionY, color, width, height, isFilled);
    }

    public static ImageEntity createImageEntity(final int positionX, final int positionY, final Color color,
                       final int width, final int height, final Image image){
        return new ImageEntity(positionX, positionY, color, width, height, image);
    }

    // -------------------------------------------------


    public static TextEntity createScore(final Screen screen, final int score){
        String scoreString = String.format("%04d", score);
        return new TextEntity(screen.getWidth() - 60, 25, Color.WHITE, scoreString, FontManager.getFontRegular());
    }

    public static TextEntity createLevel(final Screen screen, final int level){
        String scoreString = String.format("lv.%d", level);
        return new TextEntity(screen.getWidth() / 2 - 60, 25, Color.WHITE, scoreString, FontManager.getFontRegular());
    }

    public static TextEntity createElapseTime(final Screen screen, final int elapsedTime) {
        int cent = (elapsedTime % 1000)/10;
		int seconds = elapsedTime / 1000;
		int sec = seconds % 60;
		int min = seconds / 60;

        String elapsedTimeString;
        if (min < 1){
            elapsedTimeString = String.format("%d.%02d", sec, cent);
        } else {
            elapsedTimeString = String.format("%d:%02d.%02d", min, sec, cent);
        }
        return new TextEntity(screen.getWidth()/2, 25, Color.LIGHT_GRAY, elapsedTimeString, FontManager.getFontRegular());
    }

    public static TextEntity createAlertMessage(final Screen screen, final String alertMessage){
        return new TextEntity((screen.getWidth() - FontManager.getFontSmallMetrics().stringWidth(alertMessage))/2, 65,
                Color.RED, alertMessage, FontManager.getFontRegular());
    }

    public static TextEntity createLivesString(final Screen screen, final int lives){
        return new TextEntity(20, 25, Color.WHITE, Integer.toString(lives), FontManager.getFontRegular());
    }

    public static List<Entity> createLiveSprites(final Screen screen, final int lives, final Ship.ShipType shipType){
        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < lives; i++)
			entities.add(ShipFactory.create(shipType, 40 + 35 * i, 10));
        return entities;
    }

    /**
     *
     * @param screen screen
     * @param positionX Ship PositionX coordinate of the line.
     *
     * @return RectEntities to draw line
     */
    public static List<Entity> createLaunchTrajectory(final Screen screen, final int positionX){
        List<Entity> entities = new ArrayList<>();
        for (int i = 0; i < screen.getHeight() - 140; i += 20){
			entities.add(new RectEntity(positionX + 13, screen.getHeight() - 100 - i, Color.DARK_GRAY, 1, 10, false));
		}
        return entities;
    }

    public static List<Entity> createHorizontalLines(final Screen screen, final int positionY) {
        List<Entity> entities = new ArrayList<>();
        entities.add(new LineEntity(0, positionY, screen.getWidth(), positionY, Color.GREEN));
        entities.add(new LineEntity(0, positionY + 1, screen.getWidth(), positionY + 1, Color.GREEN));
        return entities;
    }

    public static LineEntity createVerticalLine(final Screen screen){
        return new LineEntity(screen.getWidth() / 2, 0, screen.getWidth() / 2, screen.getHeight(), Color.GREEN);
    }

    public static List<Entity> createTitle(final Screen screen){
        List<Entity> entities = new ArrayList<>();

        String titleString = "Invaders";
        String instructionsString =
                "select with w+s / arrows, confirm with space";

        entities.add(createCenteredBigString(screen, titleString, screen.getHeight() / 5, Color.GREEN));
        entities.add(createCenteredRegularString(screen, instructionsString, screen.getHeight() / 5 * 2, Color.GRAY));

        return entities;
    }


    public static List<Entity> createResults(final Screen screen, final int score,
                                             final int livesRemaining, final int shipsDestroyed,
                                             final double accuracy, final boolean isNewRecord, final int coinsEarned){
        List<Entity> entities = new ArrayList<>();
        String scoreString = String.format("score %04d", score);
        String livesRemainingString = "lives remaining " + livesRemaining;
        String shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
        String accuracyString = String
                .format("accuracy %.2f%%", accuracy);
        String coinsEarnedString = "EARNED COIN " + coinsEarned;

        int height = isNewRecord ? 4 : 2;

        entities.add(createCenteredRegularString(screen, scoreString, screen.getHeight() / height, Color.WHITE));
        entities.add(createCenteredRegularString(screen, livesRemainingString, screen.getHeight() / height
                + FontManager.getFontRegularMetrics().getHeight() * 2, Color.WHITE));
        entities.add(createCenteredRegularString(screen, shipsDestroyedString, screen.getHeight() / height
                + FontManager.getFontRegularMetrics().getHeight() * 4, Color.WHITE));
        entities.add(createCenteredRegularString(screen, coinsEarnedString, screen.getHeight() / height
                + FontManager.getFontRegularMetrics().getHeight() * 9, Color.YELLOW));

        return entities;
    }

    public static List<Entity> createMenu(final Screen screen, final Menu menu){
        List<Entity> entities = new ArrayList<>();

        String coinString = "YOUR COIN: " + Wallet.getWallet().getCoin();

        int lineSpacing = 0;
        for (Menu m: Menu.TITLE_MENU) {
            Color tmpColor;
            if (m.equals(menu)) tmpColor = Color.GREEN;
            else tmpColor = Color.WHITE;

            String menuString = m.equals(Menu.GAME_SETTING) ? "PLAY" :  m.name();

            entities.add(createCenteredRegularString(screen, menuString,
                    screen.getHeight() / 7 * 4 + lineSpacing, tmpColor));

            if (m.equals(Menu.SHOP)) {
                entities.add(createCenteredSmallString(screen, coinString, screen.getHeight()
                / 7 * 4 + lineSpacing + FontManager.getFontRegularMetrics().getHeight(), Color.ORANGE));
            }

            lineSpacing += FontManager.getFontRegularMetrics().getHeight() * 2;
        }

        return entities;
    }

    public static List<Entity> createGameOver(final Screen screen, final boolean acceptsInput,
                                              final boolean isNewRecord){
        List<Entity> entities = new ArrayList<>();
        String gameOverString = "Game Over";
        String continueOrExitString =
                "Press Space to play again, Escape to exit";

        int height = isNewRecord ? 4 : 2;

        entities.add(createCenteredBigString(screen, gameOverString, screen.getHeight()
                / height - FontManager.getFontBigMetrics().getHeight() * 2, Color.GREEN));


        TextEntity textEntity = createCenteredRegularString(screen, continueOrExitString,
                screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 10, Color.GREEN);

        if (acceptsInput)
            textEntity.setColor(Color.GREEN);
        else
            textEntity.setColor(Color.GRAY);

        entities.add(textEntity);

        return entities;
    }

    public static List<Entity> createInGameOver(final Screen screen){
        List<Entity> entities = new ArrayList<>();
        String gameOverString = "Game Over";

        int rectWidth = screen.getWidth();
        int rectHeight = screen.getHeight() / 6;

        entities.add(new RectEntity(0, screen.getHeight() / 2 - rectHeight / 2, Color.BLACK,
                rectWidth, rectHeight, true));
        entities.add(createCenteredBigString(screen, gameOverString,
                screen.getHeight() / 2 + FontManager.getFontBigMetrics().getHeight() / 3, Color.GREEN));

        return entities;
    }

    public static List<Entity> createAchievementMenu(
            final Screen screen, final int totalScore, final int totalPlayTime, final int maxCombo,
            final int currentPerfectStage, final int nextPerfectStage, boolean checkFlawlessFailure){
        List<Entity> entities = new ArrayList<>();
        //high score section
        String highScoreTitle = "High Scores";

        //cumulative section
        String totalScoreTitle = "Total Score";
        String totalPlayTimesTitle = "-Total  Playtime-";

        // centered strings
        String achievementTitle = "Achievement";
        String instructionsString = "Press ESC to return";
        String achievementsStatusTitle = "Achievements Status";
        String achievementsExplain = "Applies to single-player play only";

        // Achievements names
        String maxComboTitle = " Combo Mastery ";
        String perfectClearTitle = "perfect clear";
        String flawlessFailureTitle = "Flawless Failure";
        String eternityTimeTitle = "A time of eternity";

        // create "perfect clear"
        if (currentPerfectStage <= 6){

        } else {

        }


        return entities;
    }
























    public static TextEntity createCenteredSmallString(final Screen screen,
                                                       final String string, final int height, final Color color){
        return new TextEntity(screen.getWidth() / 2 - FontManager.getFontSmallMetrics().stringWidth(string) / 2,
                height, color, string, FontManager.getFontRegular());
    }

    public static TextEntity createCenteredRegularString(final Screen screen,
                                                         final String string, final int height, final Color color){
        return new TextEntity(screen.getWidth() / 2 - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, color, string, FontManager.getFontRegular());
    }

    public static TextEntity createCenteredBigString(final Screen screen,
                                                         final String string, final int height, final Color color){
        return new TextEntity(screen.getWidth() / 2 - FontManager.getFontBigMetrics().stringWidth(string) / 2,
                height, color, string, FontManager.getFontBig());
    }

    public static TextEntity createLeftSideScoreRegularString(final Screen screen,
                                                              final String string, final int height, final Color color){
        return new TextEntity(screen.getWidth() / 4
                - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, color, string, FontManager.getFontRegular());
    }

    public static TextEntity createRightSideCumulativeRegularString(final Screen screen,
                                                                  final String string, final int height, final Color color){
        return new TextEntity(screen.getWidth() * 71 / 100
        - FontManager.getFontRegularMetrics().stringWidth(string)/2,
                height, color, string, FontManager.getFontRegular());
    }

    public static TextEntity createRightSideCumulativeBigString(final Screen screen,
                                                              final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 71 / 100
                - FontManager.getFontBigMetrics().stringWidth(string)/2,
                height, color, string, FontManager.getFontBig());
    }

    public static TextEntity createLeftSideAchievementRegularString(final Screen screen,
                                                                  final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 22 / 100
        - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, color, string, FontManager.getFontRegular());
    }

    public static TextEntity createRightSideAchievementSmallEventString(final Screen screen,
                                                                      final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 65 / 100
        - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, color, string, FontManager.getFontSmall());
    }

    public static TextEntity createRightSideAchievementSmallEventString2(final Screen screen,
                                                                       final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 68 / 100
                - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, color, string, FontManager.getFontSmall());
    }

    public static TextEntity createRightSideAchievementBigString(final Screen screen,
                                                               final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 63 / 100
                - FontManager.getFontRegularMetrics().stringWidth(string),
                height, color, string, FontManager.getFontBig());
    }

    public static TextEntity createRightSideAchievementComboString1(final Screen screen,
                                                                    final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 52 / 100
                        - FontManager.getFontRegularMetrics().stringWidth(string),
                height, color, string, FontManager.getFontSmall());
    }

    public static TextEntity createRightSideAchievementComboString2(final Screen screen,
                                                                    final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 74 / 100
                - FontManager.getFontRegularMetrics().stringWidth(string),
                height, color, string, FontManager.getFontSmall());
    }

    public static TextEntity createRightSideAchievementSmallString1(final Screen screen,
                                                                    final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 59 / 100
                - FontManager.getFontRegularMetrics().stringWidth(string),
                height, color, string, FontManager.getFontSmall());
    }











}
