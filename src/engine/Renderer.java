package engine;

import entity.*;
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
public final class Renderer {
	/** Singleton instance of the class. */
	private static Renderer instance;
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
	private Renderer() {
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
			imgShootInterval = ImageIO.read(new File("res/image/shot interval.jpg"));
		} catch (IOException e) {
			logger.info("Shop image loading failed");
		}

	}

	/**
	 * Returns shared instance of DrawManager.
	 *
	 * @return Shared instance of DrawManager.
	 */
	public static Renderer getInstance() {
		if (instance == null)
			instance = new Renderer();
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

		FontManager.initializeMetrics(backBufferGraphics);
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

	public void drawEntities(final List<Entity> entities) {
		for (Entity entity : entities) {

			switch (entity.getType()) {
                case TEXT:
					drawTextEntity((TextEntity) entity);
                    break;
                case SPRITE:
					drawSpriteEntity((SpriteEntity) entity);
                    break;
				case IMAGE:
					drawImageEntity((ImageEntity) entity);
					break;
				case RECT:
					drawRectEntity((RectEntity) entity);
					break;
				case LINE:
					drawLineEntity((LineEntity) entity);
					break;
				case POLYGON:
					drawFillPolygonEntity((PolygonEntity) entity);
					break;
				case ARC:
					drawFillArcEntity((ArcEntity) entity);
					break;
				case ROTATED_SPRITE:
					drawRotatedEntity((RotatedSpriteEntity) entity);
					break;
                default:
                    System.out.println("Unknown Entity type: " + entity.getClass().getSimpleName());
            }
		}
	}

	/**
	 * Draws an entity, using the appropriate image.
	 * 
	 * @param spriteEntity
	 *            Entity to be drawn.
	 * @param positionX
	 *            Coordinates for the left side of the image.
	 * @param positionY
	 *            Coordinates for the upper side of the image.
	 */
	public void drawSpriteEntity(final SpriteEntity spriteEntity, final int positionX,
								 final int positionY) {
		boolean[][] image = spriteMap.get(spriteEntity.getSpriteType());

		backBufferGraphics.setColor(spriteEntity.getColor());
		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[i].length; j++)
				if (image[i][j])
					backBufferGraphics.drawRect(positionX + i * 2, positionY
							+ j * 2, 1, 1);
	}

    /**
     * Draws an entity, using the appropriate image.
     *
     * @param spriteEntity
     *            Entity to be drawn.
     * @param positionX
     *            Coordinates for the left side of the image.
     * @param positionY
     *            Coordinates for the upper side of the image.
     * @param threadNumber
     *            Thread number for two player mode
     */
    public void drawSpriteEntity(final SpriteEntity spriteEntity, final int positionX,
								 final int positionY, final int threadNumber) {
        boolean[][] image = spriteMap.get(spriteEntity.getSpriteType());

        threadBufferGraphics[threadNumber].setColor(spriteEntity.getColor());
        for (int i = 0; i < image.length; i++)
            for (int j = 0; j < image[i].length; j++)
                if (image[i][j])
                    threadBufferGraphics[threadNumber].drawRect(positionX + i * 2, positionY
                            + j * 2, 1, 1);
    }

	public void drawSpriteEntity(final SpriteEntity spriteEntity) {
		boolean[][] image = spriteMap.get(spriteEntity.getSpriteType());

		backBufferGraphics.setColor(spriteEntity.getColor());
		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[i].length; j++)
				if (image[i][j])
					backBufferGraphics.drawRect(spriteEntity.getPositionX() + i * 2, spriteEntity.getPositionY()
							+ j * 2, 1, 1);
	}

	public void drawTextEntity(final TextEntity textEntity){
		backBufferGraphics.setColor(textEntity.getColor());
		backBufferGraphics.setFont(textEntity.getFont());
		backBufferGraphics.drawString(textEntity.getText(), textEntity.getPositionX(), textEntity.getPositionY());
	}

	public void drawImageEntity(final ImageEntity imageEntity){
		backBufferGraphics.setColor(imageEntity.getColor());
		backBufferGraphics.drawImage(imageEntity.getImage(), imageEntity.getPositionX(), imageEntity.getPositionY()
				, imageEntity.getWidth(), imageEntity.getHeight(), null);

	}

	public void drawLineEntity(final LineEntity lineEntity){
		backBufferGraphics.setColor(lineEntity.getColor());
		backBufferGraphics.drawLine(lineEntity.getPositionX(), lineEntity.getPositionY()
				, lineEntity.getPositionX2(), lineEntity.getPositionY2());
	}

	public void drawFillArcEntity(final ArcEntity arcEntity){
		backBufferGraphics.setColor(arcEntity.getColor());
		backBufferGraphics.fillArc(arcEntity.getPositionX(), arcEntity.getPositionY(),
				arcEntity.getWidth(), arcEntity.getHeight(), arcEntity.getStartAngle(), arcEntity.getEndAngle());
	}

	public void drawRectEntity(final RectEntity rectEntity){
		backBufferGraphics.setColor(rectEntity.getColor());
		if (rectEntity.getIsFilled())
			backBufferGraphics.fillRect(rectEntity.getPositionX(), rectEntity.getPositionY(),
					rectEntity.getWidth(), rectEntity.getHeight());
		else
			backBufferGraphics.drawRect(rectEntity.getPositionX(), rectEntity.getPositionY(),
					rectEntity.getWidth(), rectEntity.getHeight());
	}

	public void drawFillPolygonEntity(final PolygonEntity polygonEntity){
		backBufferGraphics.setColor(polygonEntity.getColor());
		backBufferGraphics.fillPolygon(polygonEntity.getXPoints(), polygonEntity.getYPoints(), polygonEntity.getNPoints());
	}


	//Drawing an Entity (Blocker) that requires angle setting
	public void drawRotatedEntity(RotatedSpriteEntity rotatedSpriteEntity) {
		Graphics2D g2d = (Graphics2D) backBufferGraphics; // Convert to Graphics2D
		AffineTransform oldTransform = g2d.getTransform(); // Save previous conversion

		//Set center point to rotate
		int centerX = rotatedSpriteEntity.getPositionX() + rotatedSpriteEntity.getWidth() / 2;
		int centerY = rotatedSpriteEntity.getPositionY() + rotatedSpriteEntity.getHeight() / 2;

		//rotate by a given angle
		g2d.rotate(Math.toRadians(rotatedSpriteEntity.getAngle()), centerX, centerY);

		//Drawing entities
		drawSpriteEntity(rotatedSpriteEntity, rotatedSpriteEntity.getPositionX(), rotatedSpriteEntity.getPositionY());

		g2d.setTransform(oldTransform); // Restore to original conversion state
	}

	//Drawing an Entity (Blocker) that requires angle setting
	public void drawRotatedEntity(SpriteEntity spriteEntity, int x, int y, double angle, final int threadNumber) {
		Graphics2D g2d = (Graphics2D) threadBufferGraphics[threadNumber]; // Convert to Graphics2D
		AffineTransform oldTransform = g2d.getTransform(); // Save previous conversion

		//Set center point to rotate
		int centerX = x + spriteEntity.getWidth() / 2;
		int centerY = y + spriteEntity.getHeight() / 2;

		//rotate by a given angle
		g2d.rotate(Math.toRadians(angle), centerX, centerY);

		//Drawing entities
		drawSpriteEntity(spriteEntity, x, y, threadNumber);

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
	 * @param threadNumber
	 *            Thread number for two player mode
	 */
	public void drawLives(final Screen screen, final int lives, final Ship.ShipType shipType, final int threadNumber) {
		threadBufferGraphics[threadNumber].setFont(fontRegular);
		threadBufferGraphics[threadNumber].setColor(Color.WHITE);
		threadBufferGraphics[threadNumber].drawString(Integer.toString(lives), 20, 25);
		Ship dummyShip = ShipFactory.create(shipType, 0, 0);
		for (int i = 0; i < lives; i++)
			drawSpriteEntity(dummyShip, 40 + 35 * i, 10, threadNumber);
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


	public void drawGameTitle(final Screen screen, final int threadNumber) {
		String titleString = "Invaders";
		threadBufferGraphics[threadNumber].setColor(Color.DARK_GRAY);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 2, threadNumber);
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
