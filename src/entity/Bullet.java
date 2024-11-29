package entity;

import engine.DrawManager.SpriteType;

import java.awt.*;

/**
 * Implements a bullet that moves vertically up or down.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Bullet extends Entity {

	/**
	 * Speed of the bullet, positive or negative depending on direction -
	 * positive is down.
	 */
	private int speed;
	private Ship.ShipType shipType;

	/**
	 * Constructor, establishes the bullet's properties.
	 * 
	 * @param positionX
	 *            Initial position of the bullet in the X axis.
	 * @param positionY
	 *            Initial position of the bullet in the Y axis.
	 * @param speed
	 *            Speed of the bullet, positive or negative depending on
	 *            direction - positive is down.
	 * @param shipType
	 * 			  Ship type for sprite and color
	 */
	public Bullet(final int positionX, final int positionY, final int speed, final Ship.ShipType shipType) {
		super(positionX, positionY, 3 * 2, 5 * 2, switch (shipType) {
			case VOID_REAPER -> Color.GREEN;
			case COSMIC_CRUISER -> Color.BLUE;
			case GALACTIC_GUARDIAN -> Color.RED;
			case STAR_DEFENDER -> Color.WHITE;
		});
		this.speed = speed;
		setSprite(shipType);
	}

	/**
	 * Sets correct sprite for the bullet, based on speed.
	 */
	public final void setSprite(Ship.ShipType shipType) {
		if (speed > 0) {
			this.spriteType = SpriteType.ENEMY_BULLET;
			return;
		}
		this.spriteType = switch(shipType) {
			case VOID_REAPER -> SpriteType.BULLET_TYPE_1;
			case COSMIC_CRUISER -> SpriteType.BULLET_TYPE_2;
			case STAR_DEFENDER -> SpriteType.BULLET_TYPE_3;
			case GALACTIC_GUARDIAN -> SpriteType.BULLET_TYPE_4;
		};
	}

	/**
	 * Sets correct sprite for the bullet, based on speed.
	 */
	public final void setSprite() {
		this.spriteType = SpriteType.ENEMY_BULLET;
	}

	/**
	 * Updates the bullet's position.
	 */
	public final void update() {
		this.positionY += this.speed;
	}

	/**
	 * Setter of the speed of the bullet.
	 * 
	 * @param speed
	 *            New speed of the bullet.
	 */
	public final void setSpeed(final int speed) {
		this.speed = speed;
	}

	/**
	 * Getter for the speed of the bullet.
	 * 
	 * @return Speed of the bullet.
	 */
	public final int getSpeed() {
		return this.speed;
	}
}
