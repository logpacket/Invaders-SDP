package entity;

import java.awt.*;

public class PolygonEntity extends Entity{
    protected int[] xPoints;
    protected int[] yPoints;
    protected int nPoints;
    protected boolean iSFilled;

    public PolygonEntity(final int[] xPoints, final int[] yPoints, final Color color,
                         final int nPoints, final boolean isFilled){
        super(0,0, color);
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.nPoints = nPoints;
        this.iSFilled = isFilled;
    }
    public PolygonEntity() {}

    public int[] getXPoints() { return this.xPoints;}

    public int[] getYPoints() { return this.yPoints;}

    public int getNPoints () { return this.nPoints; }

    public boolean getIsFilled() { return this.iSFilled;}

    @Override
    public EntityType getType(){
        return EntityType.POLYGON;
    }
}
