package engine;

import entity.*;
import screen.Screen;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class EntityFactory {

    private static final String[] PERFECT_COIN_REWARD = { "200", "400", "800", "2000", "3000", "4000", "5000"};
	private static final String[] ACCURACY_COIN_REWARD = {"500", "1500", "2000", "2500"};
    /** Small sized font. */
    private static final Font fontSmall = FontManager.getFontSmall();
    /** Regular sized font. */
    private static final Font fontRegular = FontManager.getFontRegular();
    /** Big sized font. */
    private static final Font fontBig = FontManager.getFontBig();

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
        return new TextEntity(screen.getWidth() - 60, 25, Color.WHITE, scoreString, fontRegular);
    }

    public static TextEntity createLevel(final Screen screen, final int level){
        String scoreString = String.format("lv.%d", level);
        return new TextEntity(screen.getWidth() / 2 - 60, 25, Color.WHITE, scoreString, fontRegular);
    }

    public static TextEntity createElapsedTime(final Screen screen, final int elapsedTime) {
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
        return new TextEntity(screen.getWidth()/2, 25, Color.LIGHT_GRAY, elapsedTimeString, fontRegular);
    }

    public static TextEntity createAlertMessage(final Screen screen, final String alertMessage){
        return new TextEntity((screen.getWidth() - FontManager.getFontRegularMetrics().stringWidth(alertMessage))/2, 65,
                Color.RED, alertMessage, fontRegular);
    }

    public static TextEntity createLivesString(final Screen screen, final int lives){
        return new TextEntity(20, 25, Color.WHITE, Integer.toString(lives), fontRegular);
    }

    public static List<Entity> createLivesSprites(final Screen screen, final int lives, final Ship.ShipType shipType){
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

    public static TextEntity createGameTitle(final Screen screen){
        String titleString = "Invaders";
        return createCenteredBigString(screen, titleString, screen.getHeight() / 2, Color.DARK_GRAY);
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
        entities.add(createCenteredRegularString(screen, accuracyString, screen.getHeight() / height
                + FontManager.getFontRegularMetrics().getHeight() * 6, Color.WHITE));
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


        entities.add(createCenteredRegularString(screen, continueOrExitString, screen.getHeight() / 2 +
                FontManager.getFontRegularMetrics().getHeight() * 10, acceptsInput ? Color.GREEN : Color.GRAY));

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
            entities.add(createRightSideAchievementCoinBigString(screen, PERFECT_COIN_REWARD[currentPerfectStage],
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 3
            + FontManager.getFontBigMetrics().getHeight() * 3, Color.ORANGE));

            entities.add(createRightSideAchievementSmallString1(screen, "current",
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 3
            + FontManager.getFontBigMetrics().getHeight() * 2 + 7, Color.GREEN));

            entities.add(createRightSideAchievementSmallString2(screen, "target",
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 3
            + FontManager.getFontBigMetrics().getHeight() * 2 + 7, Color.RED));

            String sampleAchievementsString2 = "lv." + currentPerfectStage + "   =>  lv." +
					nextPerfectStage;
            entities.add(createRightSideAchievementBigString(screen, sampleAchievementsString2,
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 3
            + FontManager.getFontBigMetrics().getHeight() * 3, Color.WHITE));
        } else {
            entities.add(createRightSideAchievementCoinBigString(screen, "5000",
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 3
                            + FontManager.getFontBigMetrics().getHeight() * 3, Color.gray));
            entities.add(createRightSideAchievementSmallEventString2(screen, "You clear all levels perfectly",
					screen.getHeight() /2 + FontManager.getFontRegularMetrics().getHeight()*2
                            + FontManager.getFontBigMetrics().getHeight()* 3 - 5, Color.GREEN));

            String sampleAchievementsString2 = " 100% Clear !! ";
            entities.add(createRightSideAchievementBigString(screen, sampleAchievementsString2,
                    screen.getHeight() /2 + FontManager.getFontRegularMetrics().getHeight()*3
                            + FontManager.getFontBigMetrics().getHeight()* 3, Color.GREEN));
        }

        // draw "achievement"
        entities.add(createCenteredBigString(screen, achievementTitle, screen.getHeight() / 8 ,Color.GREEN));

        // draw instruction
        entities.add(createCenteredRegularString(screen, instructionsString,
                screen.getHeight() / 8 + FontManager.getFontRegularMetrics().getHeight(), Color.GRAY));

        entities.add(createCenteredRegularString(screen, achievementsExplain,
                screen.getHeight() / 7 + FontManager.getFontBigMetrics().getHeight(), Color.cyan));

        // draw "high score"
        entities.add(createLeftSideScoreRegularString(screen, highScoreTitle,
				screen.getHeight() / 5+ FontManager.getFontBigMetrics().getHeight(), Color.GREEN));

        // draw total score
        entities.add(createRightSideCumulativeRegularString(screen, totalScoreTitle,
                screen.getHeight() / 5 + FontManager.getFontBigMetrics().getHeight(), Color.yellow));

        // draw "Total play-time"
        entities.add(createRightSideCumulativeRegularString(screen, totalPlayTimesTitle,
                screen.getHeight() / 5 + 2 * FontManager.getFontRegularMetrics().getHeight()
                        + 2 * FontManager.getFontBigMetrics().getHeight()+ 10, Color.yellow));

        // draw "Total Score"
        String totalScoreString = String.format("%s", totalScore);
        entities.add(createRightSideCumulativeBigString(screen, totalScoreString,
                screen.getHeight() / 3 - FontManager.getFontRegularMetrics().getHeight() + 10, Color.WHITE));

        // draw "achievement status"
        entities.add(createCenteredBigString(screen, achievementsStatusTitle,
                screen.getHeight() / 2 + FontManager.getFontBigMetrics().getHeight(), Color.MAGENTA));

        // draw "high accuracy"
        entities.add(createLeftSideAchievementRegularString(screen, maxComboTitle,
                screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 3
        + FontManager.getFontBigMetrics().getHeight() + 7, Color.WHITE));

        // draw "Perfect clear"
        entities.add(createLeftSideAchievementRegularString(screen, perfectClearTitle,
               screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 4
        + FontManager.getFontBigMetrics().getHeight() * 2 + 7, Color.WHITE ));

        // draw "Flawless Failure"
        entities.add(createLeftSideAchievementRegularString(screen, flawlessFailureTitle,
               screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 5
        + FontManager.getFontBigMetrics().getHeight() * 3 + 5, Color.WHITE));

        // draw "best friends"
        entities.add(createLeftSideAchievementRegularString(screen, eternityTimeTitle,
               screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 6
        + FontManager.getFontBigMetrics().getHeight() * 4 + 3, Color.WHITE));

        int totalHours = totalPlayTime / 3600;
		int remainHours = totalPlayTime % 3600;

		int totalMinutes = remainHours / 60;
		int remainMinutes = remainHours % 60;

		int totalSeconds = remainMinutes % 60;

        // draw total play time record
        String totalPlayTimeeString = String.format("%02dH %02dm %02ds",totalHours,totalMinutes,totalSeconds);
        entities.add(createRightSideCumulativeBigString(screen, totalPlayTimeeString, screen.getHeight() / 2
        - FontManager.getFontRegularMetrics().getHeight() - 15, Color.WHITE));

        // draw accuracy achievement
        if (maxCombo >= 25) {
            entities.add(createRightSideAchievementCoinBigString(screen, ACCURACY_COIN_REWARD[3],
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 2
                            + FontManager.getFontBigMetrics().getHeight() * 2, Color.gray));
            entities.add(createRightSideAchievementSmallEventString(screen, "You record high combo",
					screen.getHeight() /2 + FontManager.getFontRegularMetrics().getHeight() * 2
                            + FontManager.getFontBigMetrics().getHeight() + 8, Color.GREEN));
            entities.add(createRightSideAchievementBigString(screen, "You are crazy!",
					screen.getHeight() /2 + FontManager.getFontRegularMetrics().getHeight() * 2
                            + FontManager.getFontBigMetrics().getHeight() * 2, Color.GREEN));
        } else {
            entities.add(createRightSideAchievementComboString1(screen, "combo",
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 5 + 5, Color.orange));
            entities.add(createRightSideAchievementComboString2(screen, "combo",
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 5 + 5, Color.orange));

            entities.add(createRightSideAchievementSmallString1(screen, "current",
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 4 - 2, Color.green));
            entities.add(createRightSideAchievementSmallString2(screen, "target",
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 4 - 2, Color.red));
            if (maxCombo < 10) {
                entities.add(createRightSideAchievementCoinBigString(screen, ACCURACY_COIN_REWARD[0],
                        screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 2
                + FontManager.getFontBigMetrics().getHeight() * 2, Color.orange));

                String accuracyAchievement = String.format("             %d", maxCombo) + " =>" + "         10";
                entities.add(createRightSideAchievementBigString(screen, accuracyAchievement,
                        screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 5 + 5, Color.WHITE));
            } else {
                entities.add(createRightSideAchievementCoinBigString(screen, ACCURACY_COIN_REWARD[maxCombo / 5 - 1],
                        screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 2
                + FontManager.getFontBigMetrics().getHeight() * 2, Color.orange));

                String accuracyAchievement = String.format("             %d", maxCombo) + " =>" + String.format("         %d", ((maxCombo - 10) / 5 + 1) * 5 + 10);
                entities.add(createRightSideAchievementBigString(screen, accuracyAchievement,
                        screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 5 + 5, Color.WHITE));
            }
        }

        // draw flawless failure achievement
        String flawlessFailureReward = "1000";
        if (checkFlawlessFailure) {
            entities.add(createRightSideAchievementBigString(screen, "Complete!",
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 4
            + FontManager.getFontBigMetrics().getHeight() * 4 - 5, Color.GREEN));
            entities.add(createRightSideAchievementCoinBigString(screen, flawlessFailureReward,
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 4
            + FontManager.getFontBigMetrics().getHeight() * 4 - 5, Color.gray));
        } else {
            String explainFlawlessFailure1 = "    Achieved when the game ends";
			String explainFlawlessFailure2 = "                with 0% accuracy.";
            entities.add(createRightSideAchievementSmallString3(screen, explainFlawlessFailure1,
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 4
                + FontManager.getFontBigMetrics().getHeight() * 3
                    +FontManager.getFontSmallMetrics().getHeight(), Color.GRAY));
            entities.add(createRightSideAchievementSmallString3(screen, explainFlawlessFailure2,
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 4
                + FontManager.getFontBigMetrics().getHeight() * 3
                    +FontManager.getFontSmallMetrics().getHeight() * 2, Color.GRAY));
            entities.add(createRightSideAchievementCoinBigString(screen, flawlessFailureReward,
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 4
                + FontManager.getFontBigMetrics().getHeight() * 4 - 5, Color.orange));

        }

        // draw play time achievement
        String eternityTimeReward = "1000";
		String sampleAchievementsString = "complete!";
		String explainEternityTime1 = "              Total play time ";
		String explainEternityTime2 = "        must exceed 10 minutes...";
        if (totalPlayTime >= 600) {
            entities.add(createRightSideAchievementBigString(screen, sampleAchievementsString,
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 5
                + FontManager.getFontBigMetrics().getHeight() * 5 - 5, Color.GREEN));
            entities.add(createRightSideAchievementCoinBigString(screen, eternityTimeReward,
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 5
                + FontManager.getFontBigMetrics().getHeight() * 5 - 5, Color.GREEN));
        } else {
            entities.add(createRightSideAchievementSmallString3(screen, explainEternityTime1,
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 5
                + FontManager.getFontBigMetrics().getHeight() * 4
                    +FontManager.getFontSmallMetrics().getHeight(), Color.GRAY));
            entities.add(createRightSideAchievementSmallString3(screen, explainEternityTime2,
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 5
                + FontManager.getFontBigMetrics().getHeight() * 4
                    +FontManager.getFontSmallMetrics().getHeight() * 2, Color.GRAY));
            entities.add(createRightSideAchievementCoinBigString(screen, eternityTimeReward,
                    screen.getHeight() / 2 + FontManager.getFontRegularMetrics().getHeight() * 5
                + FontManager.getFontBigMetrics().getHeight() * 5 - 5, Color.orange));
        }

        return entities;
    }

    /**
	 * Create high scores entities.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param highScores
	 *            List of high scores.
	 */
    public static List<Entity> createHighScores(final Screen screen,
                                                final List<Score> highScores) {
        List<Entity> entities = new ArrayList<>();
        int i = 0;
        String scoreString = "";

        final int limitDrawingScore = 3;
		int countDrawingScore = 0;
        for (Score score : highScores) {
            scoreString = String.format("%s        %04d", score.name(),
					score.score());
            entities.add(createLeftSideScoreRegularString(screen, scoreString, screen.getHeight() / 4
            + FontManager.getFontRegularMetrics().getHeight() * (i + 1) * 2, Color.WHITE));
            i++;
			countDrawingScore++;
			if(countDrawingScore>=limitDrawingScore) {
                break;
            }
        }

        return entities;
    }

    public static List<Entity> createEndingCredit(final Screen screen, List<String> creditlist, int currentFrame) {
        List<Entity> entities = new ArrayList<>();
        final int startPoint = screen.getHeight() / 2;

		for (int i = 0;i < creditlist.size(); i++) {
            String target = creditlist.get(i);
            entities.add(createCenteredRegularString(screen, target, startPoint
            + (FontManager.getFontRegularMetrics().getHeight() * 2) * i - currentFrame, Color.WHITE));
        }
        return entities;
    }


    public static TextEntity createCenteredSmallString(final Screen screen,
                                                       final String string, final int height, final Color color){
        return new TextEntity(screen.getWidth() / 2 - FontManager.getFontSmallMetrics().stringWidth(string) / 2,
                height, color, string, fontSmall);
    }

    public static TextEntity createCenteredRegularString(final Screen screen,
                                                         final String string, final int height, final Color color){
        return new TextEntity(screen.getWidth() / 2 - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, color, string, fontRegular);
    }

    public static TextEntity createCenteredBigString(final Screen screen,
                                                         final String string, final int height, final Color color){
        return new TextEntity(screen.getWidth() / 2 - FontManager.getFontBigMetrics().stringWidth(string) / 2,
                height, color, string, fontBig);
    }

    public static TextEntity createLeftSideScoreRegularString(final Screen screen,
                                                              final String string, final int height, final Color color){
        return new TextEntity(screen.getWidth() / 4
                - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, color, string, fontRegular);
    }

    public static TextEntity createRightSideCumulativeRegularString(final Screen screen,
                                                                  final String string, final int height, final Color color){
        return new TextEntity(screen.getWidth() * 71 / 100
        - FontManager.getFontRegularMetrics().stringWidth(string)/2,
                height, color, string, fontRegular);
    }

    public static TextEntity createRightSideCumulativeBigString(final Screen screen,
                                                              final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 71 / 100
                - FontManager.getFontBigMetrics().stringWidth(string)/2,
                height, color, string, fontBig);
    }

    public static TextEntity createLeftSideAchievementRegularString(final Screen screen,
                                                                  final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 22 / 100
        - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, color, string, fontRegular);
    }

    public static TextEntity createRightSideAchievementSmallEventString(final Screen screen,
                                                                      final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 65 / 100
        - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, color, string, fontSmall);
    }

    public static TextEntity createRightSideAchievementSmallEventString2(final Screen screen,
                                                                       final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 68 / 100
                - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, color, string, fontSmall);
    }

    public static TextEntity createRightSideAchievementBigString(final Screen screen,
                                                               final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 63 / 100
                - FontManager.getFontRegularMetrics().stringWidth(string),
                height, color, string, fontBig);
    }

    public static TextEntity createRightSideAchievementComboString1(final Screen screen,
                                                                    final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 52 / 100
                        - FontManager.getFontRegularMetrics().stringWidth(string),
                height, color, string, fontSmall);
    }

    public static TextEntity createRightSideAchievementComboString2(final Screen screen,
                                                                    final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 74 / 100
                - FontManager.getFontRegularMetrics().stringWidth(string),
                height, color, string, fontSmall);
    }

    public static TextEntity createRightSideAchievementSmallString1(final Screen screen,
                                                                    final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 59 / 100
                - FontManager.getFontRegularMetrics().stringWidth(string),
                height, color, string, fontSmall);
    }

    public static TextEntity createRightSideAchievementSmallString2(final Screen screen,
                                                                    final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 77 / 100
                - FontManager.getFontRegularMetrics().stringWidth(string),
                height, color, string, fontSmall);
    }

    public static TextEntity createRightSideAchievementSmallString3(final Screen screen,
                                                                    final String string, final int height, Color color){
        return new TextEntity(screen.getWidth()  / 2
                - FontManager.getFontRegularMetrics().stringWidth(string) / 7,
                height, color, string, fontSmall);
    }

    public static TextEntity createRightSideAchievementCoinBigString(final Screen screen,
                                                                    final String string, final int height, Color color){
        return new TextEntity(screen.getWidth() * 81 / 100,
                height, color, string, fontBig);
    }

    public static List<Entity> createCountDown(final Screen screen, final int level,
                                             final int number, final boolean bonusLife) {
        List<Entity> entities = new ArrayList<>();
        int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
        entities.add(new RectEntity(0, screen.getHeight() / 2 - rectHeight / 2,
                Color.BLACK, rectWidth, rectHeight, true));
        if (number >= 4){
            if(!bonusLife)
                entities.add(createCenteredBigString(screen, "Level " + level,
                        screen.getHeight() / 2
                                + FontManager.getFontBigMetrics().getHeight() / 3, Color.GREEN));
            else
                entities.add(createCenteredBigString(screen, "Level " + level + " - Bonus life!",
                        screen.getHeight() / 2
                + FontManager.getFontBigMetrics().getHeight() / 3, Color.GREEN));
        }
        else if (number != 0)
            entities.add(createCenteredBigString(screen, Integer.toString(number),
                    screen.getHeight() / 2 + FontManager.getFontBigMetrics().getHeight() / 3, Color.GREEN));
        else
            entities.add(createCenteredBigString(screen, "GO!", screen.getHeight() / 2
                    + FontManager.getFontBigMetrics().getHeight() / 3, Color.GREEN));

        return entities;
    }

    public static TextEntity createPing(final Screen screen, long ping) {
        String pingText = "Ping: " + ping + "MS";
        return createTextEntity(screen.getWidth() - 100, 60, Color.WHITE, pingText, fontRegular);
    }

    /**
     * Create recorded highest score on screen TextEntity
     *
     * @param screen screen
     * @param highScores highScore
     *
     * @return highest record TextEntity
     */
    public static TextEntity createRecord(final Screen screen, final List<Score> highScores){

        //add variable for highest score
		int highestScore = -1;
		String highestPlayer = "";

		// find the highest score from highScores list
		for (Score entry : highScores) {
			if (entry.score() > highestScore) {
				highestScore = entry.score();
				highestPlayer = entry.name();
			}
		}

        String string = highestPlayer + " " + highestScore;

        return new TextEntity(screen.getWidth() - FontManager.getFontRegularMetrics().stringWidth(string) - 76,
                25, Color.LIGHT_GRAY, string, fontRegular);
    }

    public static ArcEntity createReloadTimer(final Screen screen, final Ship ship,
                                               final long remainingTime, final Ship.ShipType shipType){
        if(remainingTime > 0){
            int shipX = ship.getPositionX();
			int shipY = ship.getPositionY();
			int shipWidth = ship.getWidth();
			int circleSize = 16;
			int startAngle = 90;
			int endAngle = switch (shipType) {
                case VOID_REAPER -> 360 * (int) remainingTime / (int) (750 * 0.4);
                case COSMIC_CRUISER -> 360 * (int) remainingTime / (int) (750 * 1.6);
                case STAR_DEFENDER -> 360 * (int) remainingTime / (int) (750 * 1.0);
                case GALACTIC_GUARDIAN -> 360 * (int) remainingTime / (int) (750 * 1.2);
            };

            return new ArcEntity(shipX + shipWidth / 2 - circleSize / 2, shipY - 3 * circleSize / 2,
                    circleSize, circleSize, startAngle, endAngle, true, Color.WHITE);
        }
        else
            return new ArcEntity(0, 0, 0, 0, 0, 0, false, Color.WHITE);

    }

    /**
     * Draws Combo on screen.
     *
     * @param screen
     *            Screen to draw on.
     * @param combo
     *            Number of enemies killed in a row.
     */
    public static TextEntity createCombo(final Screen screen, final int combo) {
        String comboString = String.format("Combo %03d", combo);
        return new TextEntity(screen.getWidth() - 100, 85, Color.WHITE, comboString, fontRegular);
    }

    /**
     * Draws intermediate aggregation on screen.
     *
     * @param screen
     *            Screen to draw on.
     * @param maxCombo
     *            Value of maxCombo.
     * @param prevTime
     *            Value of prevTime.
     * @param score
     *            Value of score/prevScore.
     * @param tempScore
     *            Value of tempScore.
     */
    public static List<Entity> createAggre(final Screen screen, final int level, final int maxCombo,
                                           final int prevTime, final int score, final int tempScore) {

        List<Entity> entities = new ArrayList<>();
        int prevScore = score - tempScore;
        int pCent = (prevTime % 1000)/10;
        int pSeconds = prevTime / 1000;
        int pSec = pSeconds % 60;
        int pMin = pSeconds / 60;

        String timeString;
        if (pMin < 1){
            timeString = String.format("Elapsed Time: %d.%02d", pSec, pCent);
        } else {
            timeString = String.format("Elapsed Time: %d:%02d.%02d", pMin, pSec, pCent);
        }

        String levelString = String.format("Statistics at Level %d", level);
        String comboString = String.format("MAX COMBO: %03d", maxCombo);
        String scoreString = String.format("Scores earned: %04d", prevScore);

        entities.add(new TextEntity(
                (screen.getWidth() - FontManager.getFontRegularMetrics().stringWidth(levelString)) / 2,
                5*screen.getHeight() / 7,
                Color.GREEN,
                levelString,
                fontRegular
        ));
        entities.add(new TextEntity(
                (screen.getWidth() - FontManager.getFontRegularMetrics().stringWidth(comboString)) / 2,
                5 * screen.getHeight() / 7 + 21,
                Color.WHITE,
                comboString,
                fontRegular
        ));
        entities.add(new TextEntity(
                (screen.getWidth() - FontManager.getFontRegularMetrics().stringWidth(timeString)) / 2,
                5 * screen.getHeight() / 7 + 42,
                Color.WHITE,
                timeString,
                fontRegular
        ));
        entities.add(new TextEntity(
                (screen.getWidth() - FontManager.getFontRegularMetrics().stringWidth(scoreString)) / 2,
                5 * screen.getHeight() / 7 + 63,
                Color.GREEN,
                scoreString,
                fontRegular
        ));

        return entities;
    }

    /**
     * Draws the game setting screen.
     *
     * @param screen
     *            Screen to draw on.
     */
    public static TextEntity createGameSetting(final Screen screen) {
        String titleString = "Game Setting";
        return createCenteredBigString(screen, titleString, screen.getHeight() / 100 * 25, Color.GREEN);
    }

    public static TextEntity createSettingsScreen(final Screen screen) {
        String settingsTitle = "Settings";
        return createCenteredBigString(screen, settingsTitle, screen.getHeight() / 8, Color.GREEN);
    }

    public static List<Entity> createVolumeBar(Screen screen, int x, int y, int totalWidth, int filledWidth, boolean isSelected) {
        List<Entity> entities = new ArrayList<>();
        entities.add(new RectEntity(x, y, isSelected ? Color.GREEN : Color.WHITE, filledWidth, 10, true));
        entities.add(new RectEntity(x + filledWidth, y, Color.GRAY, totalWidth - filledWidth, 10, true));
        return entities;
    }

    public static TextEntity createVolumePercentage(Screen screen, int y, int volume, boolean isSelected) {
        String volumeText = volume + "%";
        return createCenteredRegularString(screen, volumeText, y, isSelected ? Color.GREEN : Color.WHITE);
    }

    public static TextEntity createCenteredRegularString(final Screen screen, final String string, final int height, boolean isSelected) {
        return new TextEntity(screen.getWidth() / 2
                - FontManager.getFontRegularMetrics().stringWidth(string) / 2, height, isSelected ? Color.GREEN : Color.WHITE, string, fontRegular);
    }

    /**
     * Draws the game setting row.
     *
     * @param screen
     *            Screen to draw on.
     * @param selectedRow
     *            Selected row.
     *
     * @author <a href="mailto:dayeon.dev@gmail.com">Dayeon Oh</a>
     *
     */
    public static RectEntity createGameSettingRow(final Screen screen, final int selectedRow) {
        int y = 0;
        int height = 0;
        int screenHeight = screen.getHeight();

        if (selectedRow == 0) {
            y = screenHeight / 100 * 30;
            height = screen.getHeight() / 100 * 22;
        } else if (selectedRow == 1) {
            y = screenHeight / 100 * 52;
            height = screen.getHeight() / 100 * 18;
        } else if (selectedRow == 2) {
            y = screenHeight / 100 * 70;
            height = screen.getHeight() / 100 * 22;
        } else if (selectedRow == 3) {
            y = screenHeight / 100 * 92;
            height = screen.getHeight() / 100 * 10;
        }
        return new RectEntity(0, y, Color.DARK_GRAY, screen.getWidth(), height,true);
    }

    /**
     * Draws the game setting elements.
     *
     * @param screen
     *            Screen to draw on.
     * @param selectedRow
     *            Selected row.
     * @param isOnlinePlay
     *            If the game is multiplayer.
     * @param name1
     *            Player 1 name.
     * @param name2
     *            Player 2 name.
     * @param difficultyLevel
     *            Difficulty level.
     *
     * @author <a href="mailto:dayeon.dev@gmail.com">Dayeon Oh</a>
     *
     */
    public static List<Entity> createGameSettingElements(final Screen screen, final int selectedRow,
                                                         final boolean isOnlinePlay, final int difficultyLevel,
                                                         final Ship.ShipType shipType) {
        List<Entity> entities = new ArrayList<>();

        String spaceString = " ";
        String singlePlayString = "Single Play";
        String onlinePlayString = "Online Play";
        String levelEasyString = "Easy";
        String levelNormalString = "Normal";
        String levelHardString = "Hard";
        String startString = "Start";
        Color color;

        if (!isOnlinePlay) color = Color.GREEN;
        else color = Color.WHITE;

        entities.add(createCenteredRegularString(screen, singlePlayString + spaceString.repeat(40),
                screen.getHeight() / 100 * 38, color));

        if (!isOnlinePlay) color = Color.WHITE;
        else color = Color.GREEN;

        entities.add(createCenteredRegularString(screen, spaceString.repeat(40) + onlinePlayString,
                screen.getHeight() / 100 * 38, color));

        if (difficultyLevel==0) color = Color.GREEN;
        else color = Color.WHITE;
        entities.add(createCenteredRegularString(screen, levelEasyString + spaceString.repeat(60),
                screen.getHeight() / 100 * 62, color));

        if (difficultyLevel==1) color = Color.GREEN;
        else color = Color.WHITE;
        entities.add(createCenteredRegularString(screen, levelNormalString, screen.getHeight() / 100 * 62, color));

        if (difficultyLevel==2) color = Color.GREEN;
        else color = Color.WHITE;
        entities.add(createCenteredRegularString(screen, spaceString.repeat(60) + levelHardString, screen.getHeight() / 100 * 62,color));

        Ship.ShipType[] shipTypes = Ship.ShipType.values();
        int shipIndex = 0;
        for (int i = 0; i < shipTypes.length; i++) {
            if (shipTypes[i] == shipType) {
                shipIndex = i;
                break;
            }
        }

        // Ship selection
        final int SHIP_OFFSET = screen.getWidth() / 100 * 30;
        final int ARROW_OFFSET = 50;

        Ship currentShip = ShipFactory.create(shipType, 0, 0);
        currentShip.setColor(Color.GREEN);
        currentShip.setPositionX(screen.getWidth() / 2 - 13);
        currentShip.setPositionY(screen.getHeight() / 100 * 80);
        entities.add(currentShip);

        entities.add(new TextEntity(screen.getWidth() / 2 -  FontManager.getFontRegularMetrics().stringWidth(shipType.name()) / 2,
                screen.getHeight() / 100 * 80 - 35,Color.GREEN,shipType.name(),fontRegular));

        final ShipMultipliers multipliers = currentShip.getMultipliers();
        final Object[][] stats = new Object[][]{
                {"SPD", multipliers.speed()},
                {"BUL SPD", multipliers.bulletSpeed()},
                {"SHOT INT", multipliers.shootInterval()},
        };

        List<String> statsStr = new ArrayList<>();
        for (Object[] stat : stats) {
            // Format it as percentage (+/-)
            String mult;
            if ((float) stat[1] < 1) {
                mult = "-" + Math.round((1 - (float) stat[1]) * 100) + "%";
            } else {
                mult = "+" + Math.round(((float) stat[1] - 1) * 100) + "%";
            }
            statsStr.add(mult + " " + stat[0]);
        }

        entities.add(createCenteredSmallString(screen, String.join(", ", statsStr), screen.getHeight() / 100 * 80 + 38,Color.GREEN));

        if (shipIndex > 0) {
            Ship previousShip = ShipFactory.create(shipTypes[shipIndex - 1], 0, 0);
            previousShip.setColor(Color.WHITE);
            previousShip.setPositionX(screen.getWidth() / 2 - SHIP_OFFSET - 13);
            previousShip.setPositionY(screen.getHeight() / 100 * 80);
            entities.add(previousShip);

            entities.add(new TextEntity(screen.getWidth() / 2 - SHIP_OFFSET - FontManager.getFontRegularMetrics().stringWidth(shipTypes[shipIndex - 1].name()) / 2,
                    screen.getHeight() / 100 * 80 - 35, Color.WHITE, shipTypes[shipIndex - 1].name(), fontRegular));


            entities.add(new PolygonEntity(new int[]{
                 screen.getWidth() / 2 - SHIP_OFFSET - ARROW_OFFSET - 30,
                 screen.getWidth() / 2 - SHIP_OFFSET - ARROW_OFFSET - 15,
                 screen.getWidth() / 2 - SHIP_OFFSET - ARROW_OFFSET - 15},
                 new int[]{screen.getHeight() / 100 * 80,
                 screen.getHeight() / 100 * 80 - 15,
                 screen.getHeight() / 100 * 80 + 15},
                 Color.WHITE, 3, true)
            );
        }

        if (shipIndex < shipTypes.length - 1) {
            Ship nextShip = ShipFactory.create(shipTypes[shipIndex + 1], 0, 0);
            nextShip.setColor(Color.WHITE);
            nextShip.setPositionX(screen.getWidth() / 2 + SHIP_OFFSET - 13);
            nextShip.setPositionY(screen.getHeight() / 100 * 80);
            entities.add(nextShip);
            entities.add(new TextEntity(screen.getWidth() / 2 + SHIP_OFFSET - FontManager.getFontRegularMetrics().stringWidth(shipTypes[shipIndex + 1].name()) / 2,
                    screen.getHeight() / 100 * 80 - 35, Color.WHITE, shipTypes[shipIndex + 1].name(), fontRegular));

             // Create arrow right
            entities.add(new PolygonEntity(
                    new int[]{screen.getWidth() / 2 + SHIP_OFFSET + ARROW_OFFSET + 30,
                         screen.getWidth() / 2 + SHIP_OFFSET + ARROW_OFFSET + 15,
                         screen.getWidth() / 2 + SHIP_OFFSET + ARROW_OFFSET + 15},
                    new int[]{screen.getHeight() / 100 * 80,
                         screen.getHeight() / 100 * 80 - 15,
                         screen.getHeight() / 100 * 80 + 15},
                 Color.WHITE, 3, true)
            );
        }

        if (selectedRow == 3) color = Color.GREEN;
        else color = Color.WHITE;
        entities.add(createCenteredRegularString(screen, startString, screen.getHeight() / 100 * 98, color));

        return entities;
    }


    public static List<Entity> createLoginScreen(final Screen screen, final String usernameInput, final String passwordInput,
                                final boolean isUsernameActive, final boolean isPasswordActive,
                                final int selectedOption, final boolean showAlert) {

        List<Entity> entities = new ArrayList<>();

        String loginTitle = "Login";
        String usernameLabel = "Username: ";
        String passwordLabel = "Password: ";
        String loginButton = "Press SPACE to Login";
        String signUpButton = "Press SPACE to Sign Up";
        String alertMessage = "Invalid Username or Password";

        int titleY = Math.round(screen.getHeight() * 0.15f);
        int inputStartX = screen.getWidth() / 5;
        int inputStartY = Math.round(screen.getHeight() * 0.4f);
        int inputSpacing = Math.round(screen.getHeight() * 0.1f);
        int inputWidth = screen.getWidth() / 2;
        int inputHeight = Math.round(screen.getHeight() * 0.07f);

        entities.add(createCenteredBigString(screen, loginTitle, titleY, Color.GREEN));
        entities.add(new TextEntity(inputStartX, inputStartY, isUsernameActive ? Color.YELLOW : Color.WHITE, usernameLabel, fontBig));
        entities.add(createRectEntity(inputStartX + 150, inputStartY - inputHeight + 10, isUsernameActive ? Color.YELLOW : Color.WHITE, inputWidth, inputHeight, false));
        entities.add(new TextEntity(inputStartX + 160, inputStartY - 5, isUsernameActive ? Color.YELLOW : Color.WHITE, usernameInput, fontBig));

        entities.add(new TextEntity(inputStartX, inputStartY + inputSpacing, isPasswordActive ? Color.YELLOW : Color.WHITE, passwordLabel, fontBig));
        entities.add(createRectEntity(inputStartX + 150, inputStartY + inputSpacing - inputHeight + 10, isPasswordActive ? Color.YELLOW : Color.WHITE, inputWidth, inputHeight, false));

        String maskedPassword = "*".repeat(passwordInput.length());
        entities.add(new TextEntity(inputStartX + 160, inputStartY + inputSpacing - 5, isPasswordActive ? Color.YELLOW : Color.WHITE, maskedPassword, fontBig));

        entities.add(createCenteredRegularString(screen, loginButton, inputStartY + inputSpacing * 2, selectedOption == 2 ? Color.YELLOW : Color.CYAN));
        entities.add(createCenteredRegularString(screen, signUpButton, inputStartY + inputSpacing * 3, selectedOption == 3 ? Color.YELLOW : Color.CYAN));

        if (showAlert) {
            entities.add(createCenteredBigString(screen, alertMessage, inputStartY + inputSpacing * 4, Color.RED));
        }

        return entities;

    }

    public static List<Entity> createSignUpScreen(final Screen screen, final String usernameInput, final String passwordInput,
                                 final String confirmPasswordInput, final boolean isUsernameActive,
                                 final boolean isPasswordActive, final boolean isConfirmPasswordActive,
                                 final boolean showAlert) {

        List<Entity> entities = new ArrayList<>();

        String signUpTitle = "Sign Up";
        String usernameLabel = "Username: ";
        String passwordLabel = "Password: ";
        String confirmPasswordLabel = "Confirm: ";
        String signUpButton = "Press SPACE to Sign Up";
        String alertMessage = "Duplicate username";

        int titleY = Math.round(screen.getHeight() * 0.15f);
        int inputStartX = screen.getWidth() / 5;
        int inputStartY = Math.round(screen.getHeight() * 0.3f);
        int inputSpacing = Math.round(screen.getHeight() * 0.1f);
        int inputWidth = screen.getWidth() / 2;
        int inputHeight = Math.round(screen.getHeight() * 0.07f);

        entities.add(createCenteredBigString(screen, signUpTitle, titleY, Color.GREEN));


        entities.add(new TextEntity(inputStartX, inputStartY,
                isUsernameActive ? Color.YELLOW : Color.WHITE, usernameLabel, fontBig));
        entities.add(createRectEntity(inputStartX + 150, inputStartY - inputHeight + 10,
                isUsernameActive ? Color.YELLOW : Color.WHITE, inputWidth, inputHeight, false));
        entities.add(new TextEntity(inputStartX + 160, inputStartY - 5,
                isUsernameActive ? Color.YELLOW : Color.WHITE, usernameInput, fontBig));

        entities.add(new TextEntity(inputStartX, inputStartY + inputSpacing,
                isPasswordActive ? Color.YELLOW : Color.WHITE, passwordLabel, fontBig));
        entities.add(createRectEntity(inputStartX + 150, inputStartY + inputSpacing - inputHeight + 10,
                isPasswordActive ? Color.YELLOW : Color.WHITE, inputWidth, inputHeight, false));
        String maskedPassword = "*".repeat(passwordInput.length());
        entities.add(new TextEntity(inputStartX + 160, inputStartY + inputSpacing - 5,
                isPasswordActive ? Color.YELLOW : Color.WHITE, maskedPassword, fontBig));

        entities.add(new TextEntity(inputStartX, inputStartY + 2 * inputSpacing,
                isConfirmPasswordActive ? Color.YELLOW : Color.WHITE, confirmPasswordLabel, fontBig));
        entities.add(createRectEntity(inputStartX + 150, inputStartY + 2 * inputSpacing - inputHeight + 10,
                isConfirmPasswordActive ? Color.YELLOW : Color.WHITE, inputWidth, inputHeight, false));
        String maskedConfirmPassword = "*".repeat(confirmPasswordInput.length());
        entities.add(new TextEntity(inputStartX + 160, inputStartY + 2 * inputSpacing - 5,
                isConfirmPasswordActive ? Color.YELLOW : Color.WHITE, maskedConfirmPassword, fontBig));

        entities.add(createCenteredRegularString(screen, signUpButton, inputStartY + 3 * inputSpacing,Color.YELLOW));

        if (showAlert) {
            entities.add(createCenteredBigString(screen, alertMessage, inputStartY + 4 * inputSpacing, Color.RED));
        }
        return entities;

    }


    }

