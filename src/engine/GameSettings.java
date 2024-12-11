package engine;

import entity.Ship;
import entity.Wallet;

/**
 * Implements an object that stores a single game's difficulty settings.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public record GameSettings(
	int difficulty,
	Ship.ShipType shipType,
	int maxLives,
	boolean isOnlinePlay
) {
	/**
	 * Constructor.
	 *
	 * @param difficulty
	 * 			  Difficulty of game
	 * @param shipType
	 *            Ship type
	 * @param isMultiplayer
	 * 			  Multi play mode flag
	 */
	public GameSettings(final int difficulty, final Ship.ShipType shipType, final boolean isMultiplayer) {
		this(difficulty, shipType,
				Wallet.getWallet().getLivesLevel() + 2, isMultiplayer);
	}

	/**
	 * Clone constructor.
	 *
	 * @param origin
	 * 			Origin game settings object
	 */
	public GameSettings(GameSettings origin) {
		this(origin.difficulty, origin.shipType, origin.maxLives, origin.isOnlinePlay);
	}
}
