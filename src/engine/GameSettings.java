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
	String playerName1,
	String playerName2,
	Ship.ShipType shipType,
	int maxLives,
	boolean isMultiplayer
) {
	/**
	 * Constructor.
	 *
	 * @param difficulty
	 * 			  Difficulty of game
	 * @param playerName1
	 *            Player name 1
	 * @param playerName2
	 *            Player name 2
	 * @param shipType
	 *            Ship type
	 */
	public GameSettings(final int difficulty, final String playerName1, final String playerName2,
						final Ship.ShipType shipType, final boolean isMultiplayer) {
		this(difficulty, playerName1, playerName2, shipType,
				Wallet.getWallet().getLivesLevel() + 2, isMultiplayer);
	}

	/**
	 * Clone constructor.
	 *
	 * @param origin
	 * 			Origin game settings object
	 */
	public GameSettings(GameSettings origin) {
		this(origin.difficulty, origin.playerName1, origin.playerName2, origin.shipType,
				origin.maxLives, origin.isMultiplayer);
	}
}
