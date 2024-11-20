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
                      final int width, final int height){
        return new RectEntity(positionX, positionY, color, width, height);
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
			entities.add(new RectEntity(positionX + 13, screen.getHeight() - 100 - i, Color.DARK_GRAY, 1, 10));
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

    public static TextEntity createTitle(final Screen screen){
        String titleString = "Invaders";
        TextEntity textEntity = createCenteredBigString(screen, titleString, screen.getHeight() / 5);
        textEntity.setColor(Color.GREEN);
        return textEntity;
    }

    public static TextEntity createInstruction(final Screen screen){
        String instructionsString =
				"select with w+s / arrows, confirm with space";
        TextEntity textEntity = createCenteredRegularString(screen, instructionsString, screen.getHeight() / 5 * 2);
        textEntity.setColor(Color.GRAY);
        return textEntity;
    }























    public static TextEntity createCenteredRegularString(final Screen screen,
                                                         final String string, final int height){
        return new TextEntity(screen.getWidth() / 2 - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, Color.GREEN, string, FontManager.getFontRegular());
    }

    public static TextEntity createCenteredBigString(final Screen screen,
                                                         final String string, final int height){
        return new TextEntity(screen.getWidth() / 2 - FontManager.getFontBigMetrics().stringWidth(string) / 2,
                height, Color.GREEN, string, FontManager.getFontBig());
    }

    public static TextEntity createLeftSideScoreRegularString(final Screen screen,
                                                              final String string, final int height){
        return new TextEntity(screen.getWidth() / 4 - FontManager.getFontRegularMetrics().stringWidth(string) / 2,
                height, Color.GREEN, string, FontManager.getFontRegular());
    }








}
