package entity;

import java.awt.*;

public class TextEntity extends Entity{
    protected String text;
    protected Font font;

    public TextEntity(final int positionX, final int positionY, final Color color, final String text, final Font font){
        super(positionX, positionY, color);
        this.text = text;
        this.font = font;
    }

    @Override
	public EntityType getType() {
		return EntityType.TEXT;
	}

    /**
     * Setter for the text of the entity.
     *
     * @param text text
     */
    public void setText(String text) { this.text = text; }

    /**
     * Getter for the text of the entity.
     *
     * @return Text of the entity.
     */
    public final String getText() {return this.text;}

    /**
     * Setter for the font of the entity.
     *
     * @param font font
     */
    public void setFont(Font font) { this.font = font; }

    /**
     * Getter for the Font of the entity
     *
     * @return Font of the entity.
     */
    public final Font getFont() {return this.font;}





}
