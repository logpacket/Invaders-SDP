package entity;

import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.Renderer.SpriteType;
import engine.Sound;
import engine.SoundManager;

/**
 * Implements a ship, to be controlled by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public abstract class Ship extends SpriteEntity {

	/** Time between shoots. */
	private int shootInterval = 750;
	/** Speed of the bullets shoot by the ship. */
	private int bulletSpeed = -6;
	/** Movement of the ship for each unit of time. */
	private static final int SPEED = 2;

    /** Play the sound every 0.5 second */
	private static final int SOUND_COOLDOWN_INTERVAL = 500;
    /** Cooldown for playing sound */
	private final Cooldown soundCooldown;

	/** Multipliers for the ship's properties. */
	protected final ShipMultipliers multipliers;
	/** Name of the ship. */
	public final String name;
	/** Type of ship*/
	private final ShipType shipType;
	/** Type of sprite to be drawn. */
	private final SpriteType baseSprite;

	/** Minimum time between shoots. */
	private Cooldown shootCooldown;
	/** Time spent inactive between hits. */
	private final Cooldown destructionCooldown;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();


	private long lastShootTime;
	private boolean threadWeb = false;

	public void setThreadWeb(boolean threadWeb) {
		this.threadWeb = threadWeb;
	}

	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 * @param name
	 * 		  	  Name of the ship.
	 * @param multipliers
	 * 		      Multipliers for the ship's properties.
	 * 		      @see ShipMultipliers
	 * @param spriteType
	 * 		      Type of sprite to be drawn.
	 * 		      @see SpriteType
	 */
	protected Ship(final int positionX, final int positionY,
				   final String name, final ShipMultipliers multipliers,
				   final SpriteType spriteType, final ShipType shipType) {
		super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);

		this.name = name;
		this.multipliers = multipliers;
		this.baseSprite = spriteType;
		this.spriteType = spriteType;
		this.shipType = shipType;
		this.shootCooldown = Core.getCooldown(this.getShootInterval());
		this.destructionCooldown = Core.getCooldown(1000);
		this.lastShootTime = 0;
		this.soundCooldown = Core.getCooldown(SOUND_COOLDOWN_INTERVAL);
	}

	/**
	 * Types of ships available.
	 */
	public enum ShipType {
		STAR_DEFENDER,
		VOID_REAPER,
		GALACTIC_GUARDIAN,
		COSMIC_CRUISER,
	}

	/**
	 * Moves the ship speed uni ts right, or until the right screen border is
	 * reached.
	 */
	public final void moveRight() {
		moveRight(0.0f);
	}

	/**
	 * Moves the ship speed units left, or until the left screen border is
	 * reached.
	 */
	public final void moveLeft() {
		moveLeft(0.0f);
	}

	public final void moveRight(float balance) {
		if(threadWeb){
			this.positionX += this.getSpeed() / 2;
		} else {
			this.positionX += this.getSpeed();
		}
		if (soundCooldown.checkFinished()) {
			soundManager.playSound(Sound.PLAYER_MOVE, balance);
			soundCooldown.reset();
		}
	}

	public final void moveLeft(float balance) {
		if(threadWeb){
			this.positionX -= this.getSpeed() / 2;
		} else {
			this.positionX -= this.getSpeed();
		}
		if (soundCooldown.checkFinished()) {
			soundManager.playSound(Sound.PLAYER_MOVE, balance);
			soundCooldown.reset();
		}
	}

	/**
	 * Shoots a bullet upwards.
	 * 
	 * @param bullets
	 *            List of bullets on screen, to add the new bullet.
	 * @return Checks if the bullet was shoot correctly.
	 */
	public final boolean shoot(final Set<Bullet> bullets, int shootNum) {
		return shoot(bullets, shootNum, 0.0f);
	}

	/**
	 * bullet sound (2-players)
	 * @param bullets
	 *          List of bullets on screen, to add the new bullet.
	 * @param balance
	 * 			1p -1.0, 2p 1.0, both 0.0
	 * @param shootNum
	 * 			Upgraded shoot.
	 *
	 * @return Checks if the bullet was shoot correctly.
	 */
	public final boolean shoot(final Set<Bullet> bullets, int shootNum, float balance) {
		if (this.shootCooldown.checkFinished()) {

			this.shootCooldown.reset();
			this.lastShootTime = System.currentTimeMillis();

			switch (shootNum) {
				case 1:
					bullets.add(BulletPool.getBullet(positionX + this.width / 2, positionY, this.getBulletSpeed(), shipType));
					soundManager.playSound(Sound.PLAYER_LASER, balance);
					break;
				case 2:
					bullets.add(BulletPool.getBullet(positionX + this.width, positionY, this.getBulletSpeed(), shipType));
					bullets.add(BulletPool.getBullet(positionX, positionY, this.getBulletSpeed(), shipType));
					soundManager.playSound(Sound.ITEM_2SHOT, balance);
					break;
				case 3:
					bullets.add(BulletPool.getBullet(positionX + this.width, positionY, this.getBulletSpeed(), shipType));
					bullets.add(BulletPool.getBullet(positionX, positionY, this.getBulletSpeed(), shipType));
					bullets.add(BulletPool.getBullet(positionX + this.width / 2, positionY, this.getBulletSpeed(), shipType));
					soundManager.playSound(Sound.ITEM_3SHOT, balance);
					break;
			}

			return true;
		}

		return false;
	}

	/**
	 * Updates status of the ship.
	 */
	public final void update() {
		if (!this.destructionCooldown.checkFinished())
			this.spriteType = SpriteType.SHIP_DESTROYED;
		else
			this.spriteType = this.baseSprite;
	}

	/**
	 * Switches the ship to its destroyed state.
	 */
	public final void destroy(float balance) {
		this.destructionCooldown.reset();
		soundManager.playSound(Sound.PLAYER_HIT, balance);
	}

	/**
	 * Checks if the ship is destroyed.
	 * 
	 * @return True if the ship is currently destroyed.
	 */
	public final boolean isDestroyed() {
		return !this.destructionCooldown.checkFinished();
	}

	/**
	 * Getter for the ship's speed.
	 * 
	 * @return Speed of the ship.
	 */
	public final int getSpeed() {
		return Math.round(SPEED * this.multipliers.speed());
	}

	/**
	 * Getter for the ship's bullet speed.
	 * @return Speed of the bullets.
	 */
	public final int getBulletSpeed() {
		return Math.round(bulletSpeed * this.multipliers.bulletSpeed());
	}

	/**
	 * Getter for the ship's shoot frequency.
	 * @return Time between shoots.
	 */
	public final int getShootInterval() {
		return Math.round(shootInterval * this.multipliers.shootInterval());
	}

	/**
	 * Getter for the ship's multipliers.
	 * @return Multipliers for the ship's properties.
	 */
	public final ShipMultipliers getMultipliers() {
		return this.multipliers;
	}

	public long getRemainingReloadTime(){
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - this.lastShootTime;
		long remainingTime = this.getShootInterval() - elapsedTime;
		return remainingTime > 0 ? remainingTime : 0;
	}


	public void applyItem(){
		Wallet wallet = Wallet.getWallet();
		int bulletLevel = wallet.getBulletLevel();
		switch (bulletLevel){
            case 2:
				bulletSpeed = -7;
				break;
			case 3:
				bulletSpeed = -9;
				break;
			case 4:
				bulletSpeed = -10;
				break;
            default:
				bulletSpeed = -6;
		}

		int shootLevel = wallet.getShootLevel();
		switch (shootLevel){
			case 1: //생성자에서 이미 초기화함
				break;
			case 2:
				shootInterval = 675;
				shootCooldown = Core.getCooldown(this.getShootInterval());
				break;
			case 3:
				shootInterval = 607;
				shootCooldown = Core.getCooldown(this.getShootInterval());
				break;
			case 4:
				shootInterval = 546;
				shootCooldown = Core.getCooldown(this.getShootInterval());
				break;
			default:
				shootInterval = 750;
				shootCooldown = Core.getCooldown(this.getShootInterval());
		}
	}
}
