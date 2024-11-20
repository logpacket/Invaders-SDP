package entity;

import java.awt.*;

public class ImageEntity extends Entity{
    protected Image image;
    protected int width;
    protected int height;

    public ImageEntity(final int positionX, final int positionY, final Color color,
                       final int width, final int height, final Image image){
        super(positionX, positionY, color);
        this.image = image;
        this.width = width;
        this.height = height;
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

	/**
	 * Getter for the image associated to the entity.
	 *
	 * @return Image of the entity.
	 */
    public final Image getImage() { return this.image; }
}
