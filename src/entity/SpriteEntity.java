package entity;

import java.awt.Color;

import engine.Renderer.SpriteType;

/**
 * Implements a generic game entity.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class SpriteEntity extends Entity{

	/** Width of the entity. */
	protected int width;
	/** Height of the entity. */
	protected int height;
	/** Sprite type assigned to the entity. */
	protected SpriteType spriteType;

	/**
	 * Constructor, establishes the entity's generic properties.
	 * 
	 * @param positionX
	 *            Initial position of the entity in the X axis.
	 * @param positionY
	 *            Initial position of the entity in the Y axis.
	 * @param width
	 *            Width of the entity.
	 * @param height
	 *            Height of the entity.
	 * @param color
	 *            Color of the entity.
	 */
	public SpriteEntity(final int positionX, final int positionY, final int width,
						final int height, final Color color) {
		super(positionX, positionY, color);
		this.width = width;
		this.height = height;
	}

	/**
	 * Getter for the sprite that the entity will be drawn as.
	 * 
	 * @return Sprite corresponding to the entity.
	 */
	public final SpriteType getSpriteType() {
		return this.spriteType;
	}

	/**
	 * Getter for the width of the image associated to the entity.
	 * 
	 * @return Width of the entity.
	 */
	public final int getWidth() {
		return this.width;
	}

	/**
	 * Getter for the height of the image associated to the entity.
	 * 
	 * @return Height of the entity.
	 */
	public final int getHeight() {
		return this.height;
	}
}
