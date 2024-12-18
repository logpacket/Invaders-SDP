package engine;

import engine.network.NetworkManager;
import screen.*;

import java.io.IOException;
import java.util.logging.*;

/**
 * Implements core game logic.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class Core {

	/** Width of current screen. */
	private static final int WIDTH = 600;
	/** Height of current screen. */
	private static final int HEIGHT = 650;
	/** Max fps of current screen. */
	private static final int FPS = 60;

    /** Application logger. */
	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Screen starting logging format */
	private static final String SCREEN_STARTING_LOG_FORMAT = "Starting %dx%d %s screen at %d fps";
	/** Screen closing logging format */
	private static final String SCREEN_CLOSING_LOG_FORMAT = "Closing %s screen";
	/** Logger handler for printing to disk. */
	private static Handler fileHandler;
    /** Initialize singleton instance of SoundManager and return that */
	private static final SoundManager soundManager = SoundManager.getInstance();
	private static final NetworkManager networkManager = NetworkManager.getInstance();

	/**
	 * Test implementation.
	 * 
	 * @param args
	 *            Program args, ignored.
	 */
	public static void main(final String[] args) throws IOException {
		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

            ConsoleHandler consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Failed to load logger", e);
		}

        /* Frame to draw the screen on. */
        Frame frame = new Frame(WIDTH, HEIGHT);
		Renderer.getInstance().setFrame(frame);
		int width = frame.getWidth();
		int height = frame.getHeight();

		AchievementManager achievementManager = new AchievementManager();

		Menu menu = Menu.LOGIN;
		GameLevelState gameLevelState = null;
		GameSettings gameSettings = null;
		GameState gameState = null;
		String playerName = "";
		Screen currentScreen;
		do {
			LOGGER.info(SCREEN_STARTING_LOG_FORMAT.formatted(menu == Menu.MULTI_PLAY ? WIDTH * 2 : WIDTH, HEIGHT, menu.name(),  FPS));

			switch (menu) {
				case LOGIN:
					currentScreen = new LoginScreen(width, height, FPS);
					menu = frame.setScreen(currentScreen);
					break;
				case SIGN_UP:
					currentScreen = new SignUpScreen(width, height, FPS);
					menu = frame.setScreen(currentScreen);
					break;
                case MAIN:
					if (gameLevelState == null) {
						gameLevelState = new GameLevelState();
					}
					menu = frame.setScreen(new TitleScreen(width, height, FPS));
					break;
				case GAME_SETTING:
					currentScreen = new GameSettingScreen(width, height, FPS);
					menu = frame.setScreen(currentScreen);
					gameSettings = ((GameSettingScreen) currentScreen).getGameSettings();
					break;
				case SINGLE_PLAY:
					assert gameSettings != null;
					GameLevelState currentGameLevelState;

					do {
						long startTime = System.currentTimeMillis();
						currentScreen = new GameScreen(gameLevelState, gameSettings, width, height, FPS);

						menu = frame.setScreen(currentScreen);

						currentGameLevelState = ((GameScreen) currentScreen).getGameLevelState();
						gameLevelState = new GameLevelState(currentGameLevelState, gameSettings);

						long endTime = System.currentTimeMillis();
						achievementManager.updatePlaying(gameLevelState.maxCombo(),(int) (endTime - startTime) / 1000, gameSettings.maxLives(), gameLevelState.livesRemaining(), gameLevelState.level() - 1);
					} while (currentGameLevelState.livesRemaining() > 0);
					break;

				case MULTI_PLAY:
					assert gameSettings != null;
					gameState = new GameState(gameLevelState, gameSettings);

					frame.setSize(WIDTH*2, HEIGHT);
					frame.moveToMiddle();

					currentScreen = new TwoPlayerScreen(gameState, gameLevelState, gameSettings, width, height, FPS);
					menu = frame.setScreen(currentScreen);

					frame.setSize(WIDTH, HEIGHT);
					frame.moveToMiddle();
					Renderer.getInstance().setFrame(frame);

					gameLevelState = ((TwoPlayerScreen) currentScreen).getWinnerGameState();
					//int winnerNumber = ((TwoPlayerScreen) currentScreen).getWinnerNumber();
					//playerName = "";

					break;

				case SCORE:
					assert gameSettings != null;

					achievementManager.updatePlayed(gameLevelState.getAccuracy(), gameLevelState.score());
					achievementManager.updateAllAchievements();
					currentScreen = new ScoreScreen(playerName, width, height, FPS, gameLevelState, achievementManager, gameSettings.isOnlinePlay());

					menu = frame.setScreen(currentScreen);
					gameLevelState = new GameLevelState();
					break;

				case SHOP:
					currentScreen = new ShopScreen(width, height, FPS);
					menu = frame.setScreen(currentScreen);
					break;

				case ACHIEVEMENT:
					currentScreen = new AchievementScreen(width, height, FPS);
					menu = frame.setScreen(currentScreen);
					break;

				case SETTING:
					currentScreen = new SettingScreen(width, height, FPS);
					menu = frame.setScreen(currentScreen);
					break;

				case CREDIT:
					currentScreen = new CreditScreen(width, height, FPS);
					menu = frame.setScreen(currentScreen);
					break;

				case MATCHMAKING:
					currentScreen = new MatchmakingScreen(width, height, FPS, gameSettings);
					menu = frame.setScreen(currentScreen);
					break;

				case RANKING:
					currentScreen = new RankingScreen(width, height, FPS, gameState);
					menu = frame.setScreen(currentScreen);
					break;

                default:
                    break;
            }

			LOGGER.info(SCREEN_CLOSING_LOG_FORMAT.formatted(menu.name()));
		} while (menu != Menu.EXIT);
		fileHandler.flush();
		fileHandler.close();
		soundManager.closeAllSounds();
		networkManager.close();

		System.exit(0);
	}

	/**
	 * Constructor, not called.
	 */
	private Core() { }

	/**
	 * Controls access to the logger.
	 * 
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls creation of new cooldowns.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @return A new cooldown.
	 */
	public static Cooldown getCooldown(final int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
			final int variance) {
		return new Cooldown(milliseconds, variance);
	}
}