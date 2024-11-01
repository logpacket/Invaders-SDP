package engine;

import entity.Ship;
import entity.Wallet;

/**
 * Implements an object that stores a single game's difficulty settings.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameSettings {
	public final int difficulty;
	/** Player name1 */
	public final String playerName1;
	/** Player name2 */
	public final String playerName2;
	/** Ship type */
	public final Ship.ShipType shipType;
	/** Max lives of game*/
	public final int maxLives;
	/** Flag of multi play mode */
	public final boolean isMultiplayer;


	public GameSettings() {
		difficulty = 1;
		playerName1 = "P1";
		playerName2 = "P2";
		shipType = Ship.ShipType.STAR_DEFENDER;
		this.isMultiplayer = false;
		this.maxLives = Wallet.getWallet().getLivesLevel() + 2;
	}

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
		this.difficulty = difficulty;
		this.playerName1 = playerName1;
		this.playerName2 = playerName2;
		this.shipType = shipType;
		this.isMultiplayer = isMultiplayer;
		this.maxLives = Wallet.getWallet().getLivesLevel() + 2;
	}

	/**
	 * Clone constructor.
	 *
	 * @param origin
	 * 			Origin game settings object
	 */
	public GameSettings(GameSettings origin) {
		this.difficulty = origin.difficulty;
		this.playerName1 = origin.playerName1;
		this.playerName2 = origin.playerName2;
		this.shipType = origin.shipType;
		this.isMultiplayer = origin.isMultiplayer;
		this.maxLives = origin.maxLives;
	}
}
