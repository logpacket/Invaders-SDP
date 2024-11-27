package entity;

import java.awt.*;

public class LineEntity extends Entity{
    /** Position X at the end point of line */
    protected int positionX2;
    /** Position Y at the end point of line */
    protected int positionY2;


    public LineEntity(final int positionX, final int positionY,
                      final int positionX2, final int positionY2, final Color color){
        super(positionX, positionY, color);

        this.positionX2 = positionX2;
        this.positionY2 = positionY2;
    }

    @Override
	public EntityType getType() {
		return EntityType.LINE;
	}

    /**
     * Getter for X at the end point of line
     *
     * @return X at the end point of line
     */
    public int getPositionX2() { return this.positionX2;}

    /**
     * Getter for Y at the end point of line
     *
     * @return Y at the end point of line
     */
    public int getPositionY2() { return this.positionY2;}


}
