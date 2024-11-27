package entity;

import java.awt.*;

public abstract class Entity {
    /** Position in the x-axis of the upper left corner of the entity. */
	protected int positionX;
	/** Position in the y-axis of the upper left corner of the entity. */
	protected int positionY;
    /** Color of the entity. */
	protected Color color;

    public Entity(final int positionX, final int positionY, final Color color){
        this.positionX = positionX;
        this.positionY = positionY;
        this.color = color;

    }

	public abstract EntityType getType();

    /**
	 * Setter for the color of the entity.
	 *
	 * @param color
	 *            New color of the entity
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Getter for the color of the entity.
	 *
	 * @return Color of the entity, used when drawing it.
	 */
	public final Color getColor() {
		return color;
	}

    /**
	 * Getter for the X axis position of the entity.
	 *
	 * @return Position of the entity in the X axis.
	 */
	public final int getPositionX() {
		return this.positionX;
	}

	/**
	 * Getter for the Y axis position of the entity.
	 *
	 * @return Position of the entity in the Y axis.
	 */
	public final int getPositionY() {
		return this.positionY;
	}

	/**
	 * Setter for the X axis position of the entity.
	 *
	 * @param positionX
	 *            New position of the entity in the X axis.
	 */
	public final void setPositionX(final int positionX) {
		this.positionX = positionX;
	}

	/**
	 * Setter for the Y axis position of the entity.
	 *
	 * @param positionY
	 *            New position of the entity in the Y axis.
	 */
	public final void setPositionY(final int positionY) {
		this.positionY = positionY;
	}

	public enum EntityType {
		TEXT, SPRITE, LINE, IMAGE, RECT, ARC, POLYGON, ROTATED_SPRITE
	}
}
