package engine;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

import entity.*;
import screen.Screen;

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
	/** Regular sized font. */
	private Font fontRegular;
	/** Big sized font. */
	private Font fontBig;
	/** Vertical line width for two player mode **/
	private static final int LINE_WIDTH = 1;

	/** Sprite types mapped to their images. */
	private static final Map<SpriteType, boolean[][]> spriteMap = new LinkedHashMap<>();

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

		FontManager.initializeMetrics(backBufferGraphics);
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

}
