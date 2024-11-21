package entity;

import java.awt.*;

public class RectEntity extends Entity{
    protected int width;
    protected int height;

    public RectEntity(final int positionX, final int positionY, final Color color,
                      final int width, final int height){
        super(positionX, positionY, color);
        this.width = width;
        this.height = height;

    }

	@Override
	public EntityType getType() {
		return EntityType.RECT;
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
