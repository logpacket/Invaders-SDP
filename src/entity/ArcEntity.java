package entity;

import java.awt.*;

public class ArcEntity extends Entity{
    protected int width;
    protected int height;
    protected int startAngle;
    protected int endAngle;
    protected boolean isFilled;

    public ArcEntity(final int positionX, final int positionY, final int width, final int height,
                     final int startAngle, final int endAngle, final boolean isFilled, final Color color){
        super(positionX, positionY, color);

        this.width = width;
        this.height = height;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.isFilled = isFilled;
    }

    public int getWidth(){ return this.width;}

    public int getHeight(){ return this.height;}

    public int getStartAngle(){ return this.startAngle;}

    public int getEndAngle(){ return this.endAngle;}

    public boolean getIsFilled(){ return this.isFilled; }

    @Override
    public EntityType getType() {
        return EntityType.ARC;
    }
}
