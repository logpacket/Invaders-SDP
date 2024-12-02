package engine;

import entity.*;
import screen.GameScreen;
import screen.Screen;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages screen drawing.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class DrawManager {
	/** Singleton instance of the class. */
	private static DrawManager instance;
	/** Current frame. */
	private Frame frame;
    /** Graphics context. */
	private Graphics graphics;
	/** Buffer Graphics. */
	private Graphics backBufferGraphics;
	/** Buffer Graphics for multi screens. */
	private final Graphics[] threadBufferGraphics = new Graphics[2];
	/** Buffer image. */
	private BufferedImage backBuffer;
	/** Buffer images for multi screens **/
	private final BufferedImage[] threadBuffers = new BufferedImage[4];
	/** Small sized font. */
	private Font fontSmall;
	/** Small sized font properties. */
	private FontMetrics fontSmallMetrics;
	/** Regular sized font. */
	private Font fontRegular;
	/** Regular sized font properties. */
	private FontMetrics fontRegularMetrics;
	/** Big sized font. */
	private Font fontBig;
	/** Big sized font properties. */
	private FontMetrics fontBigMetrics;
	/** Vertical line width for two player mode **/
	private static final int LINE_WIDTH = 1;
	private static final String[] PERFECT_COIN_REWARD = { "200", "400", "800", "2000", "3000", "4000", "5000"};
	private static final String[] ACCURACY_COIN_REWARD = {"500", "1500", "2000", "2500"};

	/** Sprite types mapped to their images. */
	private static final Map<SpriteType, boolean[][]> spriteMap = new LinkedHashMap<>();

	/** For Shop screen image */
	private BufferedImage imgAdditionalLife;
	private BufferedImage imgBulletSpeed;
	private BufferedImage imgCoin;
	private BufferedImage imgCoinGain;
	private BufferedImage imgShootInterval;


	/** Sprite types. */
	public enum SpriteType {
		/** Player ship. */
		SHIP,
		/** 2nd player ship. */
		SHIP_2,
		/** 3rd player ship. */
		SHIP_3,
		/** 4th player ship. */
		SHIP_4,
		/** Destroyed player ship. */
		SHIP_DESTROYED,
		/** Ship 1 bullet. */
		BULLET_TYPE_1,
		/** Ship 2 bullet. */
		BULLET_TYPE_2,
		/** Ship 3 bullet. */
		BULLET_TYPE_3,
		/** Ship 4 bullet. */
		BULLET_TYPE_4,
		/** Enemy bullet. */
		ENEMY_BULLET,
		/** First enemy ship - first form. */
		ENEMY_SHIP_A1,
		/** First enemy ship - second form. */
		ENEMY_SHIP_A2,
		/** Second enemy ship - first form. */
		ENEMY_SHIP_B1,
		/** Second enemy ship - second form. */
		ENEMY_SHIP_B2,
		/** Third enemy ship - first form. */
		ENEMY_SHIP_C1,
		/** Third enemy ship - second form. */
		ENEMY_SHIP_C2,
		/** Fourth enemy ship - first form. */
		ENEMY_SHIP_D1,
		/** Fourth enemy ship - second form. */
		ENEMY_SHIP_D2,
		/** Fifth enemy ship - first form. */
		ENEMY_SHIP_E1,
		/** Fifth enemy ship - second form. */
		ENEMY_SHIP_E2,
		/** Diver enemy ship - first form. */
		ENEMY_SHIP_F1,
		/** Diver enemy ship - first form. */
		ENEMY_SHIP_F2,
		/** Bonus ship. */
		ENEMY_SHIP_SPECIAL,
		/** Item Box. */
		ITEM_BOX,
		/** Spider webs restricting player movement */
		WEB,
		/** Obstacles preventing a player's bullet */
		BLOCK,
		/** Obstruction 1 (satellite) */
		BLOCKER_1,
		/** Obstruction 2 (Astronaut) */
		BLOCKER_2,
		/** Destroyed enemy ship. */
		EXPLOSION,
		/** Barrier. */
		BARRIER,
	}

	/**
	 * Private constructor.
	 */
	private DrawManager() {
        /* FileManager instance. */
        FileManager fileManager = FileManager.getInstance();
        /* Application logger. */
        Logger logger = Core.getLogger();
		logger.info("Started loading resources.");

		try {
			spriteMap.put(SpriteType.SHIP, new boolean[13][8]);
			spriteMap.put(SpriteType.SHIP_DESTROYED, new boolean[13][8]);
			spriteMap.put(SpriteType.BULLET_TYPE_1, new boolean[3][5]);
			spriteMap.put(SpriteType.BULLET_TYPE_2, new boolean[3][5]);
			spriteMap.put(SpriteType.BULLET_TYPE_3, new boolean[3][5]);
			spriteMap.put(SpriteType.BULLET_TYPE_4, new boolean[3][5]);
			spriteMap.put(SpriteType.ENEMY_BULLET, new boolean[3][5]);
			spriteMap.put(SpriteType.ENEMY_SHIP_A1, new boolean[12][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_A2, new boolean[12][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_B1, new boolean[12][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_B2, new boolean[12][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_C1, new boolean[12][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_C2, new boolean[12][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_SPECIAL, new boolean[16][7]);
			spriteMap.put(SpriteType.EXPLOSION, new boolean[13][7]);
			spriteMap.put(SpriteType.BARRIER, new boolean[39][11]);
			spriteMap.put(SpriteType.ITEM_BOX, new boolean[7][7]);
			spriteMap.put(SpriteType.WEB, new boolean[12][8]);
			spriteMap.put(SpriteType.BLOCK, new boolean[20][7]);
			spriteMap.put(SpriteType.BLOCKER_1, new boolean[182][93]); // artificial satellite
			spriteMap.put(SpriteType.BLOCKER_2, new boolean[82][81]); // astronaut
			spriteMap.put(SpriteType.SHIP_2, new boolean[13][8]);
			spriteMap.put(SpriteType.SHIP_3, new boolean[13][8]);
			spriteMap.put(SpriteType.SHIP_4, new boolean[13][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_D1, new boolean[12][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_D2, new boolean[12][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_E1, new boolean[12][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_E2, new boolean[12][8]);
			spriteMap.put(SpriteType.ENEMY_SHIP_F1, new boolean[16][7]);
			spriteMap.put(SpriteType.ENEMY_SHIP_F2, new boolean[16][7]);

			fileManager.loadSprite(spriteMap);
			logger.info("Finished loading the sprites.");

			// Font loading.
			fontSmall = fileManager.loadFont(10f);
			fontRegular = fileManager.loadFont(14f);
			fontBig = fileManager.loadFont(24f);
			logger.info("Finished loading the fonts.");

		} catch (IOException e) {
			logger.warning("Loading failed.");
		} catch (FontFormatException e) {
			logger.warning("Font formating failed.");
		}

		try{
			imgAdditionalLife = ImageIO.read(new File("res/image/additional life.jpg"));
			imgBulletSpeed = ImageIO.read(new File("res/image/bullet speed.jpg"));
			imgCoin = ImageIO.read(new File("res/image/coin.jpg"));
			imgCoinGain = ImageIO.read(new File("res/image/coin gain.jpg"));
			imgShootInterval = ImageIO.read(new File("res/image/shoot interval.jpg"));
		} catch (IOException e) {
			logger.info("Shop image loading failed");
		}

	}

	/**
	 * Returns shared instance of DrawManager.
	 *
	 * @return Shared instance of DrawManager.
	 */
	public static DrawManager getInstance() {
		if (instance == null)
			instance = new DrawManager();
		return instance;
	}

	/**
	 * Sets the frame to draw the image on.
	 *
	 * @param currentFrame
	 *            Frame to draw on.
	 */
	public void setFrame(final Frame currentFrame) {
		frame = currentFrame;
	}

	/**
	 * First part of the drawing process. Initializes buffers, draws the
	 * background and prepares the images.
	 *
	 * @param screen
	 *            Screen to draw in.
	 */
	public void initDrawing(final Screen screen) {
		backBuffer = new BufferedImage(screen.getWidth(), screen.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		graphics = frame.getGraphics();
		backBufferGraphics = backBuffer.getGraphics();

		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics
				.fillRect(0, 0, screen.getWidth(), screen.getHeight());

		fontSmallMetrics = backBufferGraphics.getFontMetrics(fontSmall);
		fontRegularMetrics = backBufferGraphics.getFontMetrics(fontRegular);
		fontBigMetrics = backBufferGraphics.getFontMetrics(fontBig);
	}
	/**
	 * First part of the drawing process in thread. Initializes buffers each thread, draws the
	 * background and prepares the images.
	 *
	 * @param screen
	 *            Screen to draw in.
	 * @param threadNumber
	 * 			  Thread number for two player mode
	 */

	public void initThreadDrawing(final Screen screen, final int threadNumber) {
		BufferedImage threadBuffer = new BufferedImage(screen.getWidth(),screen.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics threadGraphic = threadBuffer.getGraphics();

		threadBuffers[threadNumber] = threadBuffer;
		threadBufferGraphics[threadNumber] = threadGraphic;
	}

	/**
	 * Draws the completed drawing on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void completeDrawing(final Screen screen) {
		graphics.drawImage(backBuffer, frame.getInsets().left,
				frame.getInsets().top, frame);
	}

	/**
	 * Merge second buffers to back buffer
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void mergeDrawing(final Screen screen) {
		backBufferGraphics.drawImage(threadBuffers[2], 0, 0, frame);
		backBufferGraphics.drawImage(threadBuffers[3], screen.getWidth() / 2 + LINE_WIDTH, 0, frame);
	}

	/**
	 * Flush buffer to second buffer
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param threadNumber
	 * 			  Thread number for two player mode
	 */
	public void flushBuffer(final Screen screen, final int threadNumber) {
		BufferedImage threadBuffer = new BufferedImage(screen.getWidth(),screen.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics threadGraphic = threadBuffer.getGraphics();

		threadGraphic.drawImage(threadBuffers[threadNumber], 0, 0, frame);
		threadBuffers[threadNumber + 2] = threadBuffer;
	}

	/**
	 * Draws an entity, using the appropriate image.
	 * 
	 * @param entity
	 *            Entity to be drawn.
	 * @param positionX
	 *            Coordinates for the left side of the image.
	 * @param positionY
	 *            Coordinates for the upper side of the image.
	 */
	public void drawEntity(final Entity entity, final int positionX,
			final int positionY) {
		boolean[][] image = spriteMap.get(entity.getSpriteType());

		backBufferGraphics.setColor(entity.getColor());
		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[i].length; j++)
				if (image[i][j])
					backBufferGraphics.drawRect(positionX + i * 2, positionY
							+ j * 2, 1, 1);
	}

    /**
     * Draws an entity, using the appropriate image.
     *
     * @param entity
     *            Entity to be drawn.
     * @param positionX
     *            Coordinates for the left side of the image.
     * @param positionY
     *            Coordinates for the upper side of the image.
     * @param threadNumber
     *            Thread number for two player mode
     */
    public void drawEntity(final Entity entity, final int positionX,
                           final int positionY, final int threadNumber) {
        boolean[][] image = spriteMap.get(entity.getSpriteType());

        threadBufferGraphics[threadNumber].setColor(entity.getColor());
        for (int i = 0; i < image.length; i++)
            for (int j = 0; j < image[i].length; j++)
                if (image[i][j])
                    threadBufferGraphics[threadNumber].drawRect(positionX + i * 2, positionY
                            + j * 2, 1, 1);
    }

	//Drawing an Entity (Blocker) that requires angle setting
	public void drawRotatedEntity(Entity entity, int x, int y, double angle) {
		Graphics2D g2d = (Graphics2D) backBufferGraphics; // Convert to Graphics2D
		AffineTransform oldTransform = g2d.getTransform(); // Save previous conversion

		//Set center point to rotate
		int centerX = x + entity.getWidth() / 2;
		int centerY = y + entity.getHeight() / 2;

		//rotate by a given angle
		g2d.rotate(Math.toRadians(angle), centerX, centerY);

		//Drawing entities
		drawEntity(entity, x, y);

		g2d.setTransform(oldTransform); // Restore to original conversion state
	}

	//Drawing an Entity (Blocker) that requires angle setting
	public void drawRotatedEntity(Entity entity, int x, int y, double angle, final int threadNumber) {
		Graphics2D g2d = (Graphics2D) threadBufferGraphics[threadNumber]; // Convert to Graphics2D
		AffineTransform oldTransform = g2d.getTransform(); // Save previous conversion

		//Set center point to rotate
		int centerX = x + entity.getWidth() / 2;
		int centerY = y + entity.getHeight() / 2;

		//rotate by a given angle
		g2d.rotate(Math.toRadians(angle), centerX, centerY);

		//Drawing entities
		drawEntity(entity, x, y, threadNumber);

		g2d.setTransform(oldTransform); // Restore to original conversion state
	}

	/**
	 * For debugging purposes, draws the canvas borders.
	 * 
	 * @param screen
	 *            Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawBorders(final Screen screen) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(0, 0, screen.getWidth() - 1, 0);
		backBufferGraphics.drawLine(0, 0, 0, screen.getHeight() - 1);
		backBufferGraphics.drawLine(screen.getWidth() - 1, 0,
				screen.getWidth() - 1, screen.getHeight() - 1);
		backBufferGraphics.drawLine(0, screen.getHeight() - 1,
				screen.getWidth() - 1, screen.getHeight() - 1);
	}

	/**
	 * For debugging purposes, draws a grid over the canvas.
	 * 
	 * @param screen
	 *            Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawGrid(final Screen screen) {
		backBufferGraphics.setColor(Color.DARK_GRAY);
		for (int i = 0; i < screen.getHeight() - 1; i += 2)
			backBufferGraphics.drawLine(0, i, screen.getWidth() - 1, i);
		for (int j = 0; j < screen.getWidth() - 1; j += 2)
			backBufferGraphics.drawLine(j, 0, j, screen.getHeight() - 1);
	}

	/**
	 * Draws current score on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Current score.
	 */
	public void drawScore(final Screen screen, final int score) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String scoreString = String.format("%04d", score);
		backBufferGraphics.drawString(scoreString, screen.getWidth() - 60, 25);
	}
    /**
	 * Draws level on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Current level.
	 */
	public void drawLevel(final Screen screen, final int level) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String scoreString = String.format("lv.%d", level);
		backBufferGraphics.drawString(scoreString, screen.getWidth() / 2 - 60, 25);
	}
	/**
	 * Draws level on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Current level.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawLevel(final Screen screen, final int level, final int threadNumber) {
		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.WHITE);
		String scoreString = String.format("lv.%d", level);
		threadBufferGraphics[threadNumber].drawString(scoreString, screen.getWidth() / 2 - 60, 25);
	}

	/**
	 * Draws current score on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Current score.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawScore(final Screen screen, final int score, final int threadNumber) {
		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.WHITE);
		String scoreString = String.format("%04d", score);
		threadBufferGraphics[threadNumber].drawString(scoreString, screen.getWidth() - 60, 25);
	}

	/**
	 * Draws elapsed time on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param elapsedTime
	 *            Elapsed time.
	 */
	public void drawElapsedTime(final Screen screen, final int elapsedTime) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.LIGHT_GRAY);

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
        backBufferGraphics.drawString(elapsedTimeString, screen.getWidth()/2, 25);
    }

	/**
	 * Draws elapsed time on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param elapsedTime
	 *            Elapsed time.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawElapsedTime(final Screen screen, final int elapsedTime, final int threadNumber) {
		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.LIGHT_GRAY);

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
		threadBufferGraphics[threadNumber].drawString(elapsedTimeString, screen.getWidth()/2, 25);
	}

	/**

	 * Draws alert message on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param alertMessage
	 *            Alert message.
	 */
	public void drawAlertMessage(final Screen screen, final String alertMessage) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.RED);
		backBufferGraphics.drawString(alertMessage,
				(screen.getWidth() - fontRegularMetrics.stringWidth(alertMessage))/2, 65);
	}

	/**

	 * Draws alert message on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param alertMessage
	 *            Alert message.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawAlertMessage(final Screen screen, final String alertMessage, final int threadNumber) {
		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.RED);
		threadBufferGraphics[threadNumber].drawString(alertMessage,
				(screen.getWidth() - fontRegularMetrics.stringWidth(alertMessage))/2, 65);
	}

	/**

	 * Draws number of remaining lives on screen.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param lives
	 *            Current lives.
	 */
	public void drawLives(final Screen screen, final int lives, final Ship.ShipType shipType) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(Integer.toString(lives), 20, 25);
		Ship dummyShip = ShipFactory.create(shipType, 0, 0);
		for (int i = 0; i < lives; i++)
			drawEntity(dummyShip, 40 + 35 * i, 10);
	}


	/**
	 * Draws number of remaining lives on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param lives
	 *            Current lives.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawLives(final Screen screen, final int lives, final Ship.ShipType shipType, final int threadNumber) {
		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.WHITE);
		threadBufferGraphics[threadNumber].drawString(Integer.toString(lives), 20, 25);
		Ship dummyShip = ShipFactory.create(shipType, 0, 0);
		for (int i = 0; i < lives; i++)
			drawEntity(dummyShip, 40 + 35 * i, 10, threadNumber);
	}

	/**
	 * Draws launch trajectory on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param positionX
	 *            X coordinate of the line.
	 */

	public void drawLaunchTrajectory(final Screen screen, final int positionX) {
		backBufferGraphics.setColor(Color.DARK_GRAY);
		for (int i = 0; i < screen.getHeight() - 140; i += 20){
			backBufferGraphics.drawRect(positionX + 13, screen.getHeight() - 100 - i,1,10);
		}
    }
	/**
	 * Draws launch trajectory on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param positionX
	 *            X coordinate of the line.
	 */
	public void drawLaunchTrajectory(final Screen screen, final int positionX,
									 final int threadNumber) {
		threadBufferGraphics[threadNumber].setColor(Color.DARK_GRAY);
		for (int i = 0; i < screen.getHeight() - 140; i += 20){
			threadBufferGraphics[threadNumber].drawRect(positionX + 13, screen.getHeight() - 100 - i,1,10);
		}
	}

	/**
	 * Draws a thick line from side to side of the screen.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param positionY
	 *            Y coordinate of the line.
	 */
	public void drawHorizontalLine(final Screen screen, final int positionY) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(0, positionY, screen.getWidth(), positionY);
		backBufferGraphics.drawLine(0, positionY + 1, screen.getWidth(),
				positionY + 1);
	}

	/**
	 * Draws a thick line from side to side of the screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param positionY
	 *            Y coordinate of the line.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawHorizontalLine(final Screen screen, final int positionY, final int threadNumber) {
		threadBufferGraphics[threadNumber].setColor(Color.GREEN);
		threadBufferGraphics[threadNumber].drawLine(0, positionY, screen.getWidth(), positionY);
		threadBufferGraphics[threadNumber].drawLine(0, positionY + 1, screen.getWidth(),
				positionY + 1);
	}

	/**
	 * Draws a thick line from side to side of the screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawVerticalLine(final Screen screen) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(screen.getWidth() /2  ,0,screen.getWidth() / 2,screen.getHeight());
	}

	/**
	 * Draws game title.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawTitle(final Screen screen) {
		String titleString = "Invaders";
		String instructionsString =
				"select with w+s / arrows, confirm with space";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 5 * 2);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 5);
	}

	public void drawGameTitle(final Screen screen) {
		String titleString = "Invaders";
		backBufferGraphics.setColor(Color.DARK_GRAY);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 2);
	}

	public void drawGameTitle(final Screen screen, final int threadNumber) {
		String titleString = "Invaders";
		threadBufferGraphics[threadNumber].setColor(Color.DARK_GRAY);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 2, threadNumber);
	}


	/**
	 * Draws the ping value on the screen
	 *
	 * @param screen
	 *           Screen to draw on.
	 * @param ping
	 *           The latency value in milliseconds.
	 */
	public void drawPing(GameScreen screen, long ping) {
		String pingText = "PING: " + ping + " MS";
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(pingText, screen.getWidth() - 100, 60); // 우측 상단
	}

	/**
	 * Draws the ping value on the screen
	 *
	 * @param screen
	 *           screen to draw on.
	 * @param ping
	 *           The latency value in milliseconds.
	 * @param threadNumber
	 *           Thread number for two player mode
	 */

	public void drawPing(GameScreen  screen,final long ping, final int threadNumber) {
		String pingText = "PING: " + ping + " MS";
		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.WHITE);
		threadBufferGraphics[threadNumber].drawString(pingText, screen.getWidth() -100, 60);
	}




	/**
	 * Draws main menu.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param menu
	 *            Selected menu.
	 */
	public void drawMenu(final Screen screen, final Menu menu) {
		String coinString = "YOUR COIN: " + Wallet.getWallet().getCoin();

		int lineSpacing = 0;
		for (Menu m: Menu.TITLE_MENU) {
			if (m.equals(menu)) backBufferGraphics.setColor(Color.GREEN);
			else backBufferGraphics.setColor(Color.WHITE);

			String menuString = m.equals(Menu.GAME_SETTING) ? "PLAY" :  m.name();

			drawCenteredRegularString(screen, menuString,
					screen.getHeight() / 7 * 4 + lineSpacing);

			if (m.equals(Menu.SHOP)) {
				backBufferGraphics.setColor(Color.ORANGE);
				drawCenteredSmallString(screen, coinString, screen.getHeight()
						/ 7 * 4 + lineSpacing + fontRegularMetrics.getHeight());
			}

			lineSpacing += fontRegularMetrics.getHeight() * 2;
		}
	}



	/**
	 * Draws game results.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Score obtained.
	 * @param livesRemaining
	 *            Lives remaining when finished.
	 * @param shipsDestroyed
	 *            Total ships destroyed.
	 * @param accuracy
	 *            Total accuracy.
	 * @param isNewRecord
	 *            If the score is a new high score.
	 */
	public void drawResults(final Screen screen, final int score,
			final int livesRemaining, final int shipsDestroyed,
			final double accuracy, final boolean isNewRecord, final int coinsEarned) {
		String scoreString = String.format("score %04d", score);
		String livesRemainingString = "lives remaining " + livesRemaining;
		String shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
		String accuracyString = String
				.format("accuracy %.2f%%", accuracy);
		String coinsEarnedString = "EARNED COIN " + coinsEarned;

		int height = isNewRecord ? 4 : 2;

		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, scoreString, screen.getHeight()
				/ height);
		drawCenteredRegularString(screen, livesRemainingString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 2);
		drawCenteredRegularString(screen, shipsDestroyedString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 4);
		drawCenteredRegularString(screen, accuracyString, screen.getHeight()
				/ height + fontRegularMetrics.getHeight() * 6);
		backBufferGraphics.setColor(Color.YELLOW);
		drawCenteredRegularString(screen, coinsEarnedString, screen.getHeight()
				/ height + fontRegularMetrics.getHeight() * 9);
	}

	/**
	 * Draws basic content of game over screen.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param acceptsInput
	 *            If the screen accepts input.
	 * @param isNewRecord
	 *            If the score is a new high score.
	 */
	public void drawGameOver(final Screen screen, final boolean acceptsInput,
			final boolean isNewRecord) {
		String gameOverString = "Game Over";
		String continueOrExitString =
				"Press Space to play again, Escape to exit";

		int height = isNewRecord ? 4 : 2;

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, gameOverString, screen.getHeight()
				/ height - fontBigMetrics.getHeight() * 2);

		if (acceptsInput)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, continueOrExitString,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10);
	}


	/**
	 * Draws game over for 2player mode
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawInGameOver(final Screen screen, final int threadNumber) {
		String gameOverString = "Game Over";

		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
		threadBufferGraphics[threadNumber].setColor(Color.BLACK);
		threadBufferGraphics[threadNumber].fillRect(0, screen.getHeight() / 2 - rectHeight / 2, rectWidth, rectHeight);
		threadBufferGraphics[threadNumber].setColor(Color.GREEN);

		drawCenteredBigString(screen, gameOverString,
				screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3, threadNumber);

	}

	/**
	 * Draws achievement screen title and instructions.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawAchievementMenu(final Screen screen, final int totalScore, final int totalPlayTime, final int maxCombo,
									final int currentPerfectStage, final int nextPerfectStage, boolean checkFlawlessFailure) {
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

		// draw "perfect clear"
		if (currentPerfectStage <= 6) {
			backBufferGraphics.setColor(Color.orange);
			drawRightSideAchievementCoinBigString(screen, PERFECT_COIN_REWARD[currentPerfectStage],
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*3);

			backBufferGraphics.setColor(Color.green);
			drawRightSideAchievementSmallString1(screen,"current",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*2+7);
			backBufferGraphics.setColor(Color.red);
			drawRightSideAchievementSmallString2(screen,"target",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*2+7);

			String sampleAchievementsString2 = "lv." + currentPerfectStage + "   =>  lv." +
					nextPerfectStage;
			backBufferGraphics.setColor(Color.WHITE);
			drawRightSideAchievementBigString(screen, sampleAchievementsString2,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*3);

		}
		else{
			backBufferGraphics.setColor(Color.gray);
			drawRightSideAchievementCoinBigString(screen, "5000",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*3);

			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementSmallEventString2(screen, "You clear all levels perfectly",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*2+fontBigMetrics.getHeight()*3-5);

			String sampleAchievementsString2 = " 100% Clear !! ";
			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementBigString(screen, sampleAchievementsString2,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()*3);

		}

		// draw "achievement"
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, achievementTitle, screen.getHeight() / 8);

		// draw instruction
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 8 + fontRegularMetrics.getHeight());

		backBufferGraphics.setColor(Color.cyan);
		drawCenteredRegularString(screen, achievementsExplain,
				screen.getHeight() / 7 + fontBigMetrics.getHeight() );

		// draw "high score"
		backBufferGraphics.setColor(Color.GREEN);
		drawLeftSideScoreRegularString(screen, highScoreTitle,
				screen.getHeight() / 5+ fontBigMetrics.getHeight());

		// draw total score
		backBufferGraphics.setColor(Color.yellow);
		drawRightSideCumulativeRegularString(screen, totalScoreTitle,
				screen.getHeight() / 5 + fontBigMetrics.getHeight());

		// draw "Total play-time"
		backBufferGraphics.setColor(Color.yellow);
		drawRightSideCumulativeRegularString(screen, totalPlayTimesTitle,
				screen.getHeight() / 5 + 2*fontRegularMetrics.getHeight()+2* fontBigMetrics.getHeight()+10 );

		// draw "Total Score"
		backBufferGraphics.setColor(Color.WHITE);
		String totalScoreString = String.format("%s", totalScore);
		drawRightSideCumulativeBigString(screen, totalScoreString, screen.getHeight() / 3
				- fontRegularMetrics.getHeight() + 10);

		// draw "achievement status"
		backBufferGraphics.setColor(Color.MAGENTA);
		drawCenteredBigString(screen, achievementsStatusTitle,
				screen.getHeight() / 2 + fontBigMetrics.getHeight() );



		// draw "high accuracy"
		backBufferGraphics.setColor(Color.WHITE);
		drawLeftSideAchievementRegularString(screen, maxComboTitle,
				screen.getHeight() /2 + fontRegularMetrics.getHeight()*3+fontBigMetrics.getHeight()+7);

		// draw "Perfect clear"
		backBufferGraphics.setColor(Color.WHITE);
		drawLeftSideAchievementRegularString(screen, perfectClearTitle,
				screen.getHeight() /2 + fontRegularMetrics.getHeight()*4+fontBigMetrics.getHeight()*2+7);

		// draw "Flawless Failure"
		backBufferGraphics.setColor(Color.WHITE);
		drawLeftSideAchievementRegularString(screen, flawlessFailureTitle,
				screen.getHeight() /2 + fontRegularMetrics.getHeight()*5+fontBigMetrics.getHeight()*3+5);

		// draw "best friends"
		backBufferGraphics.setColor(Color.WHITE);
		drawLeftSideAchievementRegularString(screen, eternityTimeTitle,
				screen.getHeight() /2 + fontRegularMetrics.getHeight()*6+fontBigMetrics.getHeight()*4+3);

		int totalHours = totalPlayTime / 3600;
		int remainHours = totalPlayTime % 3600;

		int totalMinutes = remainHours / 60;
		int remainMinutes = remainHours % 60;

		int totalSeconds = remainMinutes % 60;

		// draw total play time record
		String totalPlayTimeeString = String.format("%02dH %02dm %02ds",totalHours,totalMinutes,totalSeconds);
		backBufferGraphics.setColor(Color.WHITE);
		drawRightSideCumulativeBigString(screen, totalPlayTimeeString, screen.getHeight() / 2
				- fontRegularMetrics.getHeight() - 15);

		// draw accuracy achievement
		if (maxCombo >= 25) {
			backBufferGraphics.setColor(Color.gray);
			drawRightSideAchievementCoinBigString(screen, ACCURACY_COIN_REWARD[3],
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*2+fontBigMetrics.getHeight()*2);

			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementSmallEventString(screen, "You record high combo",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*2+fontBigMetrics.getHeight()+8);

			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementBigString(screen, "You are crazy!",
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*2+fontBigMetrics.getHeight()*2);
		} else {
			backBufferGraphics.setColor(Color.orange);

			drawRightSideAchievementComboString1(screen, "combo",
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 5+5);
			drawRightSideAchievementComboString2(screen, "combo",
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 5+5);


			backBufferGraphics.setColor(Color.green);
			drawRightSideAchievementSmallString1(screen, "current",
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 4 - 2);
			backBufferGraphics.setColor(Color.red);
			drawRightSideAchievementSmallString2(screen, "target",
					screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 4 - 2);
			if (maxCombo < 10) {
				backBufferGraphics.setColor(Color.orange);
				drawRightSideAchievementCoinBigString(screen, ACCURACY_COIN_REWARD[0],
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 2 + fontBigMetrics.getHeight() * 2);

				backBufferGraphics.setColor(Color.WHITE);
				String accuracyAchievement = String.format("             %d", maxCombo) + " =>" + "         10";
				drawRightSideAchievementBigString(screen, accuracyAchievement,
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 5 + 5);
			} else {
				backBufferGraphics.setColor(Color.orange);
				drawRightSideAchievementCoinBigString(screen, ACCURACY_COIN_REWARD[maxCombo / 5 - 1],
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 2 + fontBigMetrics.getHeight() * 2);

				backBufferGraphics.setColor(Color.WHITE);
				String accuracyAchievement = String.format("             %d", maxCombo) + " =>" + String.format("         %d", ((maxCombo - 10) / 5 + 1) * 5 + 10);
				drawRightSideAchievementBigString(screen, accuracyAchievement,
						screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 5 + 5);
			}
		}

		// draw flawless failure achievement
		String flawlessFailureReward = "1000";
		if (checkFlawlessFailure) {
				backBufferGraphics.setColor(Color.GREEN);
				drawRightSideAchievementBigString(screen, "Complete!",
						screen.getHeight() /2 + fontRegularMetrics.getHeight()*4+fontBigMetrics.getHeight()*4-5);
				backBufferGraphics.setColor(Color.gray);
				drawRightSideAchievementCoinBigString(screen, flawlessFailureReward,
						screen.getHeight() /2 + fontRegularMetrics.getHeight()*4+fontBigMetrics.getHeight()*4-5);

		} else {
			String explainFlawlessFailure1 = "    Achieved when the game ends";
			String explainFlawlessFailure2 = "                with 0% accuracy.";
			backBufferGraphics.setColor(Color.GRAY);
			drawRightSideAchievementSmallString3(screen, explainFlawlessFailure1,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*4+fontBigMetrics.getHeight()*3+fontSmallMetrics.getHeight());

			backBufferGraphics.setColor(Color.GRAY);
			drawRightSideAchievementSmallString3(screen, explainFlawlessFailure2,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*4+fontBigMetrics.getHeight()*3+fontSmallMetrics.getHeight()*2);
			backBufferGraphics.setColor(Color.orange);
			drawRightSideAchievementCoinBigString(screen, flawlessFailureReward,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*4+fontBigMetrics.getHeight()*4-5);
		}

		// draw play time achievement
		String eternityTimeReward = "1000";
		String sampleAchievementsString = "complete!";
		String explainEternityTime1 = "              Total play time ";
		String explainEternityTime2 = "        must exceed 10 minutes...";
		if (totalPlayTime >= 600) {
			backBufferGraphics.setColor(Color.GREEN);
			drawRightSideAchievementBigString(screen, sampleAchievementsString,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*5+fontBigMetrics.getHeight()*5-5);
			backBufferGraphics.setColor(Color.gray);
			drawRightSideAchievementCoinBigString(screen, eternityTimeReward,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*5+fontBigMetrics.getHeight()*5-5);

		} else {
			backBufferGraphics.setColor(Color.GRAY);
			drawRightSideAchievementSmallString3(screen, explainEternityTime1,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*5+fontBigMetrics.getHeight()*4+fontSmallMetrics.getHeight());
			backBufferGraphics.setColor(Color.GRAY);
			drawRightSideAchievementSmallString3(screen, explainEternityTime2,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*5+fontBigMetrics.getHeight()*4+fontSmallMetrics.getHeight()*2);
			backBufferGraphics.setColor(Color.orange);
			drawRightSideAchievementCoinBigString(screen, eternityTimeReward,
					screen.getHeight() /2 + fontRegularMetrics.getHeight()*5+fontBigMetrics.getHeight()*5-5);

			}
	}

	/**
	 * Draws high scores.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param highScores
	 *            List of high scores.
	 */
	public void drawHighScores(final Screen screen,
			final List<Score> highScores) {
		backBufferGraphics.setColor(Color.WHITE);
		int i = 0;
		String scoreString = "";

		final int limitDrawingScore = 3;
		int countDrawingScore = 0;
		for (Score score : highScores) {
			scoreString = String.format("%s        %04d", score.name(),
					score.score());
			drawLeftSideScoreRegularString(screen, scoreString, screen.getHeight()
					/ 4 + fontRegularMetrics.getHeight() * (i + 1) * 2);
			i++;
			countDrawingScore++;
			if(countDrawingScore>=limitDrawingScore){
				break;
			}
		}
	}

	/**
	 * Draws a centered string on small font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	private void drawCenteredSmallString(final Screen screen, final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontSmallMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Draws credit screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawEndingCredit(final Screen screen, List<String> creditlist, int currentFrame)  {
		backBufferGraphics.setColor(Color.WHITE);
		final int startPoint = screen.getHeight() / 2;

		for (int i = 0;i < creditlist.size(); i++) {
			String target = creditlist.get(i);
			drawCenteredRegularString(screen, target,startPoint + (fontRegularMetrics.getHeight() * 2) * i - currentFrame);
		}


	}

	/**
	 * Draws a centered string on regular font.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredRegularString(final Screen screen,
			final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Draws a regular string.
	 * @param screen Screen to draw on.
	 * @param string String to draw.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 */
	public void drawRegularString(final Screen screen, final String string, final int x, final int y) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, x, y);
	}

	/**
	 * Draws a small string.
	 * @param screen Screen to draw on.
	 * @param string String to draw.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 */
	public void drawSmallString(final Screen screen, final String string, final int x, final int y) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, x, y);
	}

	/**
	 * Draws a centered string on big font.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredBigString(final Screen screen, final String string,
			final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontBigMetrics.stringWidth(string) / 2, height);
	}

	// left side score
	public void drawLeftSideScoreRegularString(final Screen screen,
											   final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 4
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}
	public void drawLeftSideScoreSmallString(final Screen screen,
											   final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() / 4
				- fontRegularMetrics.stringWidth(string) / 3, height);
	}

	//right side Cumulative score
	public void drawRightSideCumulativeRegularString(final Screen screen,
													 final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() *71/ 100
				- fontRegularMetrics.stringWidth(string)/2 , height);
	}
	public void drawRightSideCumulativeBigString(final Screen screen,
												 final String string, final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth() *71/ 100
				- fontBigMetrics.stringWidth(string)/2, height);
	}


	// left side achievement
	public void drawLeftSideAchievementRegularString(final Screen screen,
													 final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() *22/ 100
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}
	public void drawLeftSideAchievementSmallString(final Screen screen,
													 final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *26/ 100
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

	// right side achievement(sample)
	public void drawRightSideAchievementSmallEventString(final Screen screen,
												  final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *65/100-
				fontRegularMetrics.stringWidth(string)/2, height);
	}
	public void drawRightSideAchievementSmallEventString2(final Screen screen,
												  final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *68/100-
				fontRegularMetrics.stringWidth(string)/2, height);
	}

	public void drawRightSideAchievementBigString(final Screen screen,
												  final String string, final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth() *63/100-
						fontRegularMetrics.stringWidth(string), height);
	}
	public void drawRightSideAchievementComboString1(final Screen screen,
													 final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *52/100-
				fontRegularMetrics.stringWidth(string), height);
	}public void drawRightSideAchievementComboString2(final Screen screen,
													  final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *74/100-
				fontRegularMetrics.stringWidth(string), height);
	}
	public void drawRightSideAchievementSmallString1(final Screen screen,
													 final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *59/100-
				fontRegularMetrics.stringWidth(string), height);
	}public void drawRightSideAchievementSmallString2(final Screen screen,
													  final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() *77/100-
				fontRegularMetrics.stringWidth(string), height);
	}

	public void drawRightSideAchievementSmallString3(final Screen screen,
													 final String string, final int height) {
		backBufferGraphics.setFont(fontSmall);
		backBufferGraphics.drawString(string, screen.getWidth() / 2-
				fontRegularMetrics.stringWidth(string) / 7, height);
	}

	public void drawRightSideAchievementCoinBigString(final Screen screen,
													final String string, final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth()*81/100 , height);
	}




	/**
	 * Draws a centered string on big font.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawCenteredBigString(final Screen screen, final String string,
									  final int height, final int threadNumber) {
		threadBufferGraphics[threadNumber].setFont(fontBig);
		threadBufferGraphics[threadNumber].drawString(string, screen.getWidth() / 2
				- fontBigMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Countdown to game start.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Game difficulty level.
	 * @param number
	 *            Countdown number.
	 * @param bonusLife
	 *            Checks if a bonus life is received.
	 */

	public void drawCountDown(final Screen screen, final int level,
			final int number, final boolean bonusLife) {
		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
				rectWidth, rectHeight);
		backBufferGraphics.setColor(Color.GREEN);
		if (number >= 4){
			if (!bonusLife)
				drawCenteredBigString(screen, "Level " + level,
						screen.getHeight() / 2
								+ fontBigMetrics.getHeight() / 3);
			else
				drawCenteredBigString(screen, "Level " + level
								+ " - Bonus life!",
						screen.getHeight() / 2
								+ fontBigMetrics.getHeight() / 3);
		}
		else if (number != 0)
			drawCenteredBigString(screen, Integer.toString(number),
					screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
		else
			drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
					+ fontBigMetrics.getHeight() / 3);
	}

	/**
	 * Countdown to game start.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Game difficulty level.
	 * @param number
	 *            Countdown number.
	 * @param bonusLife
	 *            Checks if a bonus life is received.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawCountDown(final Screen screen, final int level,
							  final int number, final boolean bonusLife, final int threadNumber) {
		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
		threadBufferGraphics[threadNumber].setColor(Color.BLACK);
		threadBufferGraphics[threadNumber].fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
				rectWidth, rectHeight);
		threadBufferGraphics[threadNumber].setColor(Color.GREEN);
		if (number >= 4){
			if (!bonusLife)
				drawCenteredBigString(screen, "Level " + level,
						screen.getHeight() / 2
								+ fontBigMetrics.getHeight() / 3, threadNumber);
			else
				drawCenteredBigString(screen, "Level " + level
								+ " - Bonus life!",
						screen.getHeight() / 2
								+ fontBigMetrics.getHeight() / 3, threadNumber);
		}
		else if (number != 0)
			drawCenteredBigString(screen, Integer.toString(number),
					screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3, threadNumber);
		else
			drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
					+ fontBigMetrics.getHeight() / 3, threadNumber);
	}

   /**
	 * Draws recorded highscores on screen.
	 *
	 * @param highScores
	 *            Recorded highscores.
   */

	public void drawRecord(List<Score> highScores, final Screen screen) {

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


		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.LIGHT_GRAY);
		FontMetrics metrics = backBufferGraphics.getFontMetrics(fontRegular);
		String highScoreDisplay = highestPlayer + " " + highestScore;

		backBufferGraphics.drawString(highScoreDisplay,
				screen.getWidth() - metrics.stringWidth(highScoreDisplay) - 76, 25);
	}
	/**
	 * Draws recorded high scores on screen.
	 *
	 * @param highScores
	 *            Recorded high scores.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */

	public void drawRecord(List<Score> highScores, final Screen screen, final int threadNumber) {

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


		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.LIGHT_GRAY);
		FontMetrics metrics = threadBufferGraphics[threadNumber].getFontMetrics(fontRegular);
		String highScoreDisplay = highestPlayer + " " + highestScore;

		threadBufferGraphics[threadNumber].drawString(highScoreDisplay,
				screen.getWidth() - metrics.stringWidth(highScoreDisplay) - 76, 25);
	}
	/**
	 * Draws ReloadTimer on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param ship
	 *            player's ship.
     * @param remainingTime
	 *            remaining reload time.
	 */
	public void drawReloadTimer(final Screen screen, final Ship ship, final long remainingTime, final Ship.ShipType shipType) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
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

            backBufferGraphics.fillArc(shipX + shipWidth/2 - circleSize/2, shipY - 3*circleSize/2,
					circleSize, circleSize, startAngle, endAngle);
		}
	}
	/**
	 * Draws ReloadTimer on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param ship
	 *            player's ship.
	 * @param remainingTime
	 *            remaining reload time.
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawReloadTimer(final Screen screen, final Ship ship, final long remainingTime, final Ship.ShipType shipType, final int threadNumber) {
		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.WHITE);
		if(remainingTime > 0){

			int shipX = ship.getPositionX();
			int shipY = ship.getPositionY();
			int shipWidth = ship.getWidth();
			int circleSize = 16;
			int startAngle = 90;
			int endAngle = switch (shipType) {
                case Ship.ShipType.VOID_REAPER -> 360 * (int) remainingTime / (int) (750 * 0.4);
                case Ship.ShipType.COSMIC_CRUISER -> 360 * (int) remainingTime / (int) (750 * 1.6);
                case Ship.ShipType.STAR_DEFENDER -> 360 * (int) remainingTime / (int) (750 * 1.0);
                case Ship.ShipType.GALACTIC_GUARDIAN -> 360 * (int) remainingTime / (int) (750 * 1.2);
            };
            threadBufferGraphics[threadNumber].fillArc(shipX + shipWidth/2 - circleSize/2, shipY - 3*circleSize/2,
					circleSize, circleSize, startAngle, endAngle);
		}
	}

  /**
	 * Draws Combo on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param combo
	 *            Number of enemies killed in a row.
	 */
	public void drawCombo(final Screen screen, final int combo) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		if (combo >= 2) {
			String comboString = String.format("Combo %03d", combo);
			backBufferGraphics.drawString(comboString, screen.getWidth() - 100, 85);
		}
	}
	/**
	 * Draws Combo on screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param combo
	 *            Number of enemies killed in a row.
	 * @param threadNumber
	 * 			  Thread number for two player mode
	 */
	public void drawCombo(final Screen screen, final int combo, final int threadNumber) {
		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.WHITE);
		if (combo >= 2) {
			String comboString = String.format("Combo %03d", combo);
			threadBufferGraphics[threadNumber].drawString(comboString, screen.getWidth() - 100, 85);
		}
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
	public void interAggre(final Screen screen, final int level, final int maxCombo,
						   final int prevTime, final int score, final int tempScore) {
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

		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawString(levelString,
				(screen.getWidth() - fontRegularMetrics.stringWidth(levelString))/2,
				5*screen.getHeight()/7);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(comboString,
			(screen.getWidth() - fontRegularMetrics.stringWidth(comboString))/2,
				5*screen.getHeight()/7 + 21);
		backBufferGraphics.drawString(timeString,
						(screen.getWidth() - fontRegularMetrics.stringWidth(timeString))/2,
				5*screen.getHeight()/7 + 42);
		backBufferGraphics.drawString(scoreString,
				(screen.getWidth() - fontRegularMetrics.stringWidth(scoreString))/2,
				5*screen.getHeight()/7 + 63);
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
	 * @param threadNumber
	 * 			  Thread number for two player mode
	 */
	public void interAggre(final Screen screen, final int level, final int maxCombo,
						   final int prevTime, final int score, final int tempScore, final int threadNumber) {
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

		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.GREEN);
		threadBufferGraphics[threadNumber].drawString(levelString,
				(screen.getWidth() - fontRegularMetrics.stringWidth(levelString))/2,
				5*screen.getHeight()/7);
		threadBufferGraphics[threadNumber].setColor(Color.WHITE);
		threadBufferGraphics[threadNumber].drawString(comboString,
				(screen.getWidth() - fontRegularMetrics.stringWidth(comboString))/2,
				5*screen.getHeight()/7 + 21);
		threadBufferGraphics[threadNumber].drawString(timeString,
				(screen.getWidth() - fontRegularMetrics.stringWidth(timeString))/2,
				5*screen.getHeight()/7 + 42);
		threadBufferGraphics[threadNumber].drawString(scoreString,
				(screen.getWidth() - fontRegularMetrics.stringWidth(scoreString))/2,
				5*screen.getHeight()/7 + 63);
	}

	/**
	 * Draws the game setting screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawGameSetting(final Screen screen) {
		String titleString = "Game Setting";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 100 * 25);
	}

	public void drawSettingsScreen(final Screen screen) {
		String settingsTitle = "Settings"; // 타이틀

		// 타이틀을 초록색으로 중앙에 그리기
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, settingsTitle, screen.getHeight() / 8);
	}

	/** 볼륨 바를 그리는 메서드 */
	public void drawVolumeBar(Screen screen, int x, int y, int totalWidth, int filledWidth, boolean isSelected) {
		// 선택된 경우 초록색, 그렇지 않으면 흰색으로 표시
		backBufferGraphics.setColor(isSelected ? Color.GREEN : Color.WHITE);
		backBufferGraphics.fillRect(x, y, filledWidth, 10); // 채워진 부분

		// 나머지 부분은 회색으로 표시
		backBufferGraphics.setColor(Color.GRAY);
		backBufferGraphics.fillRect(x + filledWidth, y, totalWidth - filledWidth, 10); // 바의 나머지 부분
	}

	/** 퍼센트 값을 그리는 메서드 */
	public void drawVolumePercentage(Screen screen, int y, int volume, boolean isSelected) {
		String volumeText = volume + "%";
		// 선택된 경우 초록색, 그렇지 않으면 흰색으로 표시
		backBufferGraphics.setColor(isSelected ? Color.GREEN : Color.WHITE);
		drawCenteredRegularString(screen, volumeText, y); // 퍼센트 값을 중앙에 표시
	}

	public void drawCenteredRegularString(final Screen screen,
										  final String string, final int height, boolean isSelected) {
		backBufferGraphics.setFont(fontRegular);
		// 선택된 경우 초록색, 그렇지 않으면 흰색으로 표시
		backBufferGraphics.setColor(isSelected ? Color.GREEN : Color.WHITE);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularMetrics.stringWidth(string) / 2, height);
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
	public void drawGameSettingRow(final Screen screen, final int selectedRow) {
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

		backBufferGraphics.setColor(Color.DARK_GRAY);
		backBufferGraphics.fillRect(0, y, screen.getWidth(), height);
	}

	/**
	 * Draws the game setting elements.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param selectedRow
	 *            Selected row.
	 * @param isMultiPlayer
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
	public void drawGameSettingElements(final Screen screen, final int selectedRow,
										final boolean isMultiPlayer, final String name1, final String name2, final int difficultyLevel,
										final Ship.ShipType shipType) {
		String spaceString = " ";
		String player1String = "1 Player";
		String player2String = "2 Player";
		String levelEasyString = "Easy";
		String levelNormalString = "Normal";
		String levelHardString = "Hard";
		String startString = "Start";

		if (!isMultiPlayer) backBufferGraphics.setColor(Color.GREEN);
		else backBufferGraphics.setColor(Color.WHITE);

		drawCenteredRegularString(screen, player1String + spaceString.repeat(40), screen.getHeight() / 100 * 38);
		drawCenteredRegularString(screen, name1 + spaceString.repeat(40), screen.getHeight() / 100 * 46);

		if (!isMultiPlayer) backBufferGraphics.setColor(Color.WHITE);
		else backBufferGraphics.setColor(Color.GREEN);

		drawCenteredRegularString(screen, spaceString.repeat(40) + player2String, screen.getHeight() / 100 * 38);
		drawCenteredRegularString(screen, spaceString.repeat(40) + name2, screen.getHeight() / 100 * 46);

		if (difficultyLevel==0) backBufferGraphics.setColor(Color.GREEN);
		else backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, levelEasyString + spaceString.repeat(60), screen.getHeight() / 100 * 62);

		if (difficultyLevel==1) backBufferGraphics.setColor(Color.GREEN);
		else backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, levelNormalString, screen.getHeight() / 100 * 62);

		if (difficultyLevel==2) backBufferGraphics.setColor(Color.GREEN);
		else backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, spaceString.repeat(60) + levelHardString, screen.getHeight() / 100 * 62);

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
		drawEntity(currentShip, screen.getWidth() / 2 - 13, screen.getHeight() / 100 * 80);
		drawRegularString(screen, shipType.name(),
				screen.getWidth() / 2 - fontRegularMetrics.stringWidth(shipType.name()) / 2,
				screen.getHeight() / 100 * 80 - 35
		);

		// Draw ship stats
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
		drawCenteredSmallString(screen, String.join(", ", statsStr), screen.getHeight() / 100 * 80 + 38);

		if (shipIndex > 0) {
			Ship previousShip = ShipFactory.create(shipTypes[shipIndex - 1], 0, 0);
			previousShip.setColor(Color.WHITE);
			drawEntity(previousShip, screen.getWidth() / 2 - SHIP_OFFSET - 13, screen.getHeight() / 100 * 80);
			drawRegularString(screen, shipTypes[shipIndex - 1].name(),
					screen.getWidth() / 2 - SHIP_OFFSET - fontRegularMetrics.stringWidth(shipTypes[shipIndex - 1].name()) / 2,
					screen.getHeight() / 100 * 80 - 35
			);

			// Draw arrow left
			backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.fillPolygon(
					new int[]{
							screen.getWidth() / 2 - SHIP_OFFSET - ARROW_OFFSET - 30,
							screen.getWidth() / 2 - SHIP_OFFSET - ARROW_OFFSET - 15,
							screen.getWidth() / 2 - SHIP_OFFSET - ARROW_OFFSET - 15},
					new int[]{screen.getHeight() / 100 * 80,
							screen.getHeight() / 100 * 80 - 15,
							screen.getHeight() / 100 * 80 + 15},
					3
			);
		}
		if (shipIndex < shipTypes.length - 1) {
			Ship nextShip = ShipFactory.create(shipTypes[shipIndex + 1], 0, 0);
			nextShip.setColor(Color.WHITE);
			drawEntity(nextShip, screen.getWidth() / 2 + SHIP_OFFSET - 13, screen.getHeight() / 100 * 80);
			drawRegularString(screen, shipTypes[shipIndex + 1].name(),
					screen.getWidth() / 2 + SHIP_OFFSET - fontRegularMetrics.stringWidth(shipTypes[shipIndex + 1].name()) / 2,
					screen.getHeight() / 100 * 80 - 35
			);

			// Draw arrow right
			backBufferGraphics.setColor(Color.WHITE);
			backBufferGraphics.fillPolygon(
					new int[]{screen.getWidth() / 2 + SHIP_OFFSET + ARROW_OFFSET + 30,
							screen.getWidth() / 2 + SHIP_OFFSET + ARROW_OFFSET + 15,
							screen.getWidth() / 2 + SHIP_OFFSET + ARROW_OFFSET + 15},
					new int[]{screen.getHeight() / 100 * 80,
							screen.getHeight() / 100 * 80 - 15,
							screen.getHeight() / 100 * 80 + 15},
					3
			);
		}


		if (selectedRow == 3) backBufferGraphics.setColor(Color.GREEN);
		else backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, startString, screen.getHeight() / 100 * 98);
	}

	/**
	 *  draw shop
	 * @param screen
	 * 				Screen to draw on.
	 * @param option
	 * 				selected shop item
	 * @param wallet
	 * 				player's wallet
	 * @param moneyAlertCooldown
	 * 				cooldown for insufficient coin alert
	 * @param maxAlertCooldown
	 * 				cooldown for max level alert
	 */
	public void drawShop(final Screen screen, final int option, final Wallet wallet, final Cooldown moneyAlertCooldown, final Cooldown maxAlertCooldown) {

		String shopString = "Shop";
		int shopStringY = Math.round(screen.getHeight() * 0.15f);


		String coinString = ":  " + wallet.getCoin();
		String exitString = "PRESS \"ESC\" TO RETURN TO MAIN MENU";
		String[] costs = new String[] {"2000", "4000", "8000", "MAX LEVEL"};

		String[] itemString = new String[]{"BULLET SPEED", "SHOT INTERVAL", "ADDITIONAL LIFE","COIN GAIN"};
		int[] walletLevel = new int[]{wallet.getBulletLevel(), wallet.getShootLevel(), wallet.getLivesLevel(), wallet.getCoinLevel()};

		BufferedImage[] itemImages = new BufferedImage[]{imgBulletSpeed, imgShootInterval, imgAdditionalLife, imgCoinGain};

		int imgStartX = screen.getWidth()/80*23;
		int imgStartY = screen.getHeight()/80*27;
		int imgDis = screen.getHeight()/80*12;
		int coinStartX = screen.getWidth()/80*55;
		int coinStartY = screen.getHeight()/160*66;
		int coinDis = screen.getHeight()/80*12;
		int coinSize = 20;
		int coinTextStartX = screen.getWidth()/80*60;
		int coinTextStartY = screen.getHeight()/160*71;
		int coinTextDis = screen.getHeight()/80*12;

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, shopString, shopStringY);
		backBufferGraphics.drawImage(imgCoin, screen.getWidth()/80*39-(coinString.length()-3)*screen.getWidth()/80,screen.getHeight()/80*18,coinSize,coinSize,null);
		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(coinString,screen.getWidth()/80*44-(coinString.length()-3)*screen.getWidth()/80,screen.getHeight()/80*20);

		for(int i = 0;i<4;i++)
		{
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen,itemString[i],screen.getHeight()/80*(28 + 12*i));
			for (int j = 0; j < 3; j++)
			{
				if (j + 2 <= walletLevel[i])
				{
					backBufferGraphics.setColor(Color.GREEN);
					backBufferGraphics.fillRect(screen.getWidth() / 40 * (33 / 2) + j * (screen.getWidth() / 10), screen.getHeight() / 80 * (30 + 12*i), 20, 20);
				} else
				{
					backBufferGraphics.setColor(Color.WHITE);
					backBufferGraphics.fillRect(screen.getWidth() / 40 * (33 / 2) + j * (screen.getWidth() / 10), screen.getHeight() / 80 * (30 + 12*i), 20, 20);
				}
			}
		}

		backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawImage(itemImages[option-1],imgStartX,imgStartY + (imgDis*(option-1)),50,40,null);
		backBufferGraphics.drawImage(imgCoin,coinStartX,coinStartY + (coinDis*(option-1)),coinSize,coinSize,null);
		backBufferGraphics.drawString("X "+costs[walletLevel[option-1]-1],coinTextStartX,coinTextStartY + (coinTextDis*(option-1)));

		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen,exitString,screen.getHeight()/80*80);

		if (!moneyAlertCooldown.checkFinished())
		{
			backBufferGraphics.setColor(Color.red);
			backBufferGraphics.fillRect((screen.getWidth()-300)/2, (screen.getHeight()-100)/2, 300, 80);
			backBufferGraphics.setColor(Color.black);
			drawCenteredBigString(screen, "Insufficient coin", screen.getHeight()/2);
		}
		if(!maxAlertCooldown.checkFinished())
		{
			backBufferGraphics.setColor(Color.red);
			backBufferGraphics.fillRect((screen.getWidth()-300)/2, (screen.getHeight()-100)/2, 300, 80);
			backBufferGraphics.setColor(Color.black);
			drawCenteredBigString(screen, "Already max level", screen.getHeight()/2);

		}
	}

	public void drawLoginScreen(final Screen screen, final String usernameInput, final String passwordInput,
								final boolean isUsernameActive, final boolean isPasswordActive,
								final int selectedOption, final boolean showAlert) {

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

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, loginTitle, titleY);

		backBufferGraphics.setColor(isUsernameActive ? Color.YELLOW : Color.WHITE);
		backBufferGraphics.drawString(usernameLabel, inputStartX, inputStartY);
		backBufferGraphics.drawRect(inputStartX + 150, inputStartY - inputHeight + 10, inputWidth, inputHeight);
		backBufferGraphics.drawString(usernameInput, inputStartX + 160, inputStartY - 5);

		backBufferGraphics.setColor(isPasswordActive ? Color.YELLOW : Color.WHITE);
		backBufferGraphics.drawString(passwordLabel, inputStartX, inputStartY + inputSpacing);
		backBufferGraphics.drawRect(inputStartX + 150, inputStartY + inputSpacing - inputHeight + 10, inputWidth, inputHeight);

		String maskedPassword = "*".repeat(passwordInput.length());
		backBufferGraphics.drawString(maskedPassword, inputStartX + 160, inputStartY + inputSpacing - 5);

		backBufferGraphics.setColor(selectedOption == 2 ? Color.YELLOW : Color.CYAN);
		drawCenteredRegularString(screen, loginButton, inputStartY + inputSpacing * 2);

		backBufferGraphics.setColor(selectedOption == 3 ? Color.YELLOW : Color.CYAN);
		drawCenteredRegularString(screen, signUpButton, inputStartY + inputSpacing * 3);

		if (showAlert) {
			backBufferGraphics.setColor(Color.RED);
			drawCenteredBigString(screen, alertMessage, inputStartY + inputSpacing * 4);
		}
	}

	public void drawSignUpScreen(final Screen screen, final String usernameInput, final String passwordInput,
								 final String confirmPasswordInput, final boolean isUsernameActive,
								 final boolean isPasswordActive, final boolean isConfirmPasswordActive,
								 final boolean showAlert) {

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

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, signUpTitle, titleY);

		backBufferGraphics.setColor(isUsernameActive ? Color.YELLOW : Color.WHITE);
		backBufferGraphics.drawString(usernameLabel, inputStartX, inputStartY);
		backBufferGraphics.drawRect(inputStartX + 150, inputStartY - inputHeight + 10, inputWidth, inputHeight);
		backBufferGraphics.drawString(usernameInput, inputStartX + 160, inputStartY - 5);

		backBufferGraphics.setColor(isPasswordActive ? Color.YELLOW : Color.WHITE);
		backBufferGraphics.drawString(passwordLabel, inputStartX, inputStartY + inputSpacing);
		backBufferGraphics.drawRect(inputStartX + 150, inputStartY + inputSpacing - inputHeight + 10, inputWidth, inputHeight);
		String maskedPassword = "*".repeat(passwordInput.length());
		backBufferGraphics.drawString(maskedPassword, inputStartX + 160, inputStartY + inputSpacing - 5);

		backBufferGraphics.setColor(isConfirmPasswordActive ? Color.YELLOW : Color.WHITE);
		backBufferGraphics.drawString(confirmPasswordLabel, inputStartX, inputStartY + 2 * inputSpacing);
		backBufferGraphics.drawRect(inputStartX + 150, inputStartY + 2 * inputSpacing - inputHeight + 10, inputWidth, inputHeight);
		String maskedConfirmPassword = "*".repeat(confirmPasswordInput.length());
		backBufferGraphics.drawString(maskedConfirmPassword, inputStartX + 160, inputStartY + 2 * inputSpacing - 5);

		backBufferGraphics.setColor(Color.YELLOW);
		drawCenteredRegularString(screen, signUpButton, inputStartY + 3 * inputSpacing);

		if (showAlert) {
			backBufferGraphics.setColor(Color.RED);
			drawCenteredRegularString(screen, alertMessage, inputStartY + 4 * inputSpacing);
		}
	}
}
