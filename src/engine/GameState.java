package engine;

import entity.Ship;

/**
 * Implements an object that stores the state of the game between levels.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameState {

	private GameState instance;
	/** Current game level. */
	private int level;
	/** Current score. */
	private int score;
	/** Current ship type. */
	private Ship.ShipType shipType;
	/** Lives currently remaining. */
	private int livesRemaining;
	/** Bullets shot until now. */
	private int bulletsShot;
	/** Ships destroyed until now. */
	private int shipsDestroyed;

	/**
	 * Constructor.
	 * 
	 * @param level
	 *            Current game level.
	 * @param score
	 *            Current score.
	 * @param shipType
	 * 		  	  Current ship type.
	 * @param livesRemaining
	 *            Lives currently remaining.
	 * @param bulletsShot
	 *            Bullets shot until now.
	 * @param shipsDestroyed
	 *            Ships destroyed until now.
	 */
	public GameState(final int level, final int score,
			final Ship.ShipType shipType,
			final int livesRemaining, final int bulletsShot,
			final int shipsDestroyed) {
		this.level = level;
		this.score = score;
		this.shipType = shipType;
		this.livesRemaining = livesRemaining;
		this.bulletsShot = bulletsShot;
		this.shipsDestroyed = shipsDestroyed;
	}

	public GameState(GameState gameState) {
		this.level = gameState.level;
		this.score = gameState.score;
		this.shipType = gameState.shipType;
		this.livesRemaining = gameState.livesRemaining;
		this.bulletsShot = gameState.bulletsShot;
		this.shipsDestroyed = gameState.shipsDestroyed;
	}


	public GameState(GameState gameState, int nextLevel) {
		this.level = nextLevel;
		this.score = gameState.score;
		this.shipType = gameState.shipType;
		this.livesRemaining = gameState.livesRemaining;
		this.bulletsShot = gameState.bulletsShot;
		this.shipsDestroyed = gameState.shipsDestroyed;
	}


	/**
	 * @return the level
	 */
	public final int getLevel() {
		return level;
	}

	/**
	 * @return the score
	 */
	public final int getScore() {
		return score;
	}

	/**
	 * @return the shipType
	 */
	public final Ship.ShipType getShipType() {
		return shipType;
	}

	/**
	 * @return the livesRemaining
	 */
	public final int getLivesRemaining() {
		return livesRemaining;
	}

	/**
	 * @return the bulletsShot
	 */
	public final int getBulletsShot() {
		return bulletsShot;
	}

	/**
	 * @return the shipsDestroyed
	 */
	public final int getShipsDestroyed() {
		return shipsDestroyed;
	}

	public double getAccuracy() {
		if (bulletsShot == 0){
			return 0;
		}
		return ((double) shipsDestroyed / bulletsShot) *100;
	}

}

