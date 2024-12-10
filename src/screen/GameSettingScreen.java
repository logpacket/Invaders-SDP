package screen;

import engine.Cooldown;
import engine.Core;
import engine.GameSettings;
import engine.*;
import entity.Ship;

import java.awt.event.KeyEvent;

/**
 * Implements the game setting screen.
 *
 * @author <a href="mailto:dayeon.dev@gmail.com">Dayeon Oh</a>
 *
 */
public class GameSettingScreen extends Screen {
	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;
	/** Maximum number of characters for player name. */
	private static final int NAME_LIMIT = 4;

	/** Multiplayer mode. */
	private boolean isOnlinePlay = false;
	/** Difficulty level. */
	private int difficulty;
	/** Selected row. */
	private int selectedRow;
	/** Ship type. */
	private Ship.ShipType shipType;
	/** Time between changes in user selection. */
	private final Cooldown selectionCooldown;
	/** Total number of rows for selection. */
	private static final int TOTAL_ROWS = 4; // Multiplayer, Difficulty, Ship Type, Start
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

	/**
	 * Constructor, establishes the properties of the screen.
	 *
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public GameSettingScreen(final int width, final int height, final int fps) {
		super(width, height, fps);

		// row 0: multiplayer
		this.isOnlinePlay = false;

		// row 1: difficulty level
		this.difficulty = 1; 	// 0: easy, 1: normal, 2: hard

		// row 2: ship type
		this.shipType = Ship.ShipType.values()[0];

		// row 3: start
		this.selectedRow = 0;

		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	@Override
	protected final void update() {
		super.update();

		if (this.inputDelay.checkFinished() && this.selectionCooldown.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_UP)){
				this.selectedRow = (this.selectedRow - 1 + TOTAL_ROWS) % TOTAL_ROWS;
				this.selectionCooldown.reset();
				soundManager.playSound(Sound.MENU_MOVE);
			} else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
				this.selectedRow = (this.selectedRow + 1) % TOTAL_ROWS;
				this.selectionCooldown.reset();
				soundManager.playSound(Sound.MENU_MOVE);
			}

			if (this.selectedRow == 0) {
				if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
					this.isOnlinePlay = false;
					this.selectionCooldown.reset();
					soundManager.playSound(Sound.MENU_MOVE);
				} else if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
					this.isOnlinePlay = true;
					this.selectionCooldown.reset();
					soundManager.playSound(Sound.MENU_MOVE);
				}
			} else if (this.selectedRow == 1) {
				if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
					if (this.difficulty != 0) {
						this.difficulty--;
						this.selectionCooldown.reset();
						soundManager.playSound(Sound.MENU_MOVE);
					}
				}
				else if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) && this.difficulty != 2) {
					this.difficulty++;
					this.selectionCooldown.reset();
					soundManager.playSound(Sound.MENU_MOVE);
				}

			} else if (this.selectedRow == 2) {
				if (inputManager.isKeyDown(KeyEvent.VK_LEFT) || inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
					Ship.ShipType[] shipTypes = Ship.ShipType.values();
					int index = 0;
					for (int i = 0; i < shipTypes.length; i++) {
						if (shipTypes[i] == this.shipType) {
							index = i;
							break;
						}
					}

					if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
						if (index < shipTypes.length - 1) {
							this.shipType = shipTypes[index + 1];
						}
					} else {
						if (index > 0) {
							this.shipType = shipTypes[index - 1];
						}
					}
					this.selectionCooldown.reset();
				}
			}
			else if (this.selectedRow == 3 && inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				this.menu = isOnlinePlay ? Menu.MATCHMAKING : Menu.SINGLE_PLAY;
				this.isRunning = false;
				soundManager.playSound(Sound.MENU_CLICK);
			}
			if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
				// Return to main menu.
				this.menu = Menu.MAIN;
				this.isRunning = false;
				soundManager.playSound(Sound.MENU_BACK);
			}
		}

	}


	protected void updateEntity(){

		entityList.add(EntityFactory.createGameSetting(this));

		entityList.add(EntityFactory.createGameSettingRow(this, this.selectedRow));

		entityList.addAll(EntityFactory.createGameSettingElements(this, this.selectedRow,
				isOnlinePlay, difficulty, shipType));

	}

	public GameSettings getGameSettings() {
		return new GameSettings(difficulty,	 shipType, isOnlinePlay);
	}
}