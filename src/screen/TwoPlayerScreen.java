package screen;

import engine.GameSettings;
import engine.GameState;
import engine.Frame;
import entity.Wallet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Implements the Two player mode screen, where the action happens.
 */
public class TwoPlayerScreen extends Screen {
    /** Thread pool executor **/
    private final ExecutorService executor;
    /** Current game difficulty settings **/
    private GameSettings gameSettings;
    /** Current game wallet **/
    private final Wallet wallet;

    /** Game state for player 1 **/
    private GameState gameState1;
    /** Game state for player 2 **/
    private GameState gameState2;

    /** Player 1's game task **/
    private Future<GameState> player1;
    /** Player 2's game task **/
    private Future<GameState> player2;

    /** Max lives. */
    private static int MAX_LIVES;
    /** Levels between extra life. */
    private static final int EXTRA_LIFE_FRECUENCY = 3;
    /** Total number of levels. */
    private static final int NUM_LEVELS = 7;
    private Frame frame;

    /** Difficulty settings for level 1. */
    private static final GameSettings SETTINGS_LEVEL_1 =
            new GameSettings(5, 4, 60, 2500);
    /** Difficulty settings for level 2. */
    private static final GameSettings SETTINGS_LEVEL_2 =
            new GameSettings(5, 5, 50, 2500);
    /** Difficulty settings for level 3. */
    private static final GameSettings SETTINGS_LEVEL_3 =
            new GameSettings(6, 5, 40, 1500);
    /** Difficulty settings for level 4. */
    private static final GameSettings SETTINGS_LEVEL_4 =
            new GameSettings(6, 6, 30, 1500);
    /** Difficulty settings for level 5. */
    private static final GameSettings SETTINGS_LEVEL_5 =
            new GameSettings(7, 6, 20, 1000);
    /** Difficulty settings for level 6. */
    private static final GameSettings SETTINGS_LEVEL_6 =
            new GameSettings(7, 7, 10, 1000);
    /** Difficulty settings for level 7. */
    private static final GameSettings SETTINGS_LEVEL_7 =
            new GameSettings(8, 7, 2, 500);

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param gameSettings Current game settings.
     * @param width        Screen width.
     * @param height       Screen height.
     * @param fps          Frames per second, frame rate at which the game is run.
     */
    public TwoPlayerScreen(final GameState gameState, final GameSettings gameSettings, final int extraLifeFrequency,
                           final int width, final int height, final int fps, Wallet wallet, Frame frame) {
        super(width * 2, height, fps * 2);

        this.frame = frame;
        this.gameState1 = new GameState(gameState);
        this.gameState2 = new GameState(gameState);
        this.wallet = wallet;
        this.gameSettings = gameSettings;  // 단일 GameSettings 사용
        executor = Executors.newFixedThreadPool(2);
        this.returnCode = 1;
    }

    /**
     * Run each game screen.
     */
    public void runGameScreens() {
        GameScreen player1Screen = new GameScreen(gameState1, gameSettings,
                false, width / 2, height, fps / 2, wallet, 0);
        GameScreen player2Screen = new GameScreen(gameState2, gameSettings,
                false, width / 2, height, fps / 2, wallet, 1);

        player1Screen.initialize();
        player2Screen.initialize();

        player1 = executor.submit(player1Screen);
        player2 = executor.submit(player2Screen);
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public int run(){
        try {
            runGameScreens();
        }
        catch (Exception e) {
            // TODO handle exception
            e.printStackTrace();
        }
        super.run();
        return returnCode;
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);
        drawManager.mergeDrawing(this);
        drawManager.completeDrawing(this);
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        try {
            do{
                if (player1.isDone()) {
                    gameState1 = player1.get();
                    processPlayer1();
                }
                if (player2.isDone()) {
                    gameState2 = player2.get();
                    processPlayer2();
                }

                if (gameState1.getLivesRemaining() <= 0 && gameState2.getLivesRemaining() <= 0) {
                    executor.shutdown();
                    isRunning = false;
                }

                if (player1.isDone() && player2.isDone()) {
                    executor.shutdown();
                    isRunning = false;
                }

                draw();

            } while (isRunning);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Player 1's game progression logic.
     */
    private  void processPlayer1(){
        if (gameState1.getLivesRemaining() > 0) {
            if (gameState1.getLevel() <= NUM_LEVELS) {
                updatePlayerLevelSettings(gameState1);
                restartPlayer1();
            } else {
                player1.cancel(true);
            }
        }
    }
    /**
     * Player 2's game progression logic.
     */
    private void processPlayer2() {
        if (gameState2.getLivesRemaining() > 0) {
            if (gameState2.getLevel() <= NUM_LEVELS) {
                updatePlayerLevelSettings(gameState2);  // Player 2 레벨 업데이트
                restartPlayer2();  // Player 2 게임 재시작
            } else {
                player2.cancel(true);  // Player 2 게임 종료
            }
        }
    }
    /**
     * Restarts Player 1's game with updated settings.
     */
    private void restartPlayer1() {
        GameScreen player1Screen = new GameScreen(gameState1, gameSettings,
                false, width / 2, height, fps / 2, wallet, 0);
        player1Screen.initialize();
        player1 = executor.submit(player1Screen);
    }

    /**
     * Restarts Player 2's game with updated settings.
     */
    private void restartPlayer2() {
        GameScreen player2Screen = new GameScreen(gameState2, gameSettings,
                false, width / 2, height, fps / 2, wallet, 1);
        player2Screen.initialize();
        player2 = executor.submit(player2Screen);
    }


    /**
     * Update Player's level settings.
     */
    private void updatePlayerLevelSettings(GameState gameState) {
        int nextLevel = gameState.getLevel() + 1;

        gameState = new GameState(nextLevel,
                gameState.getScore(),
                gameState.getLivesRemaining(),
                gameState.getBulletsShot(),
                gameState.getShipsDestroyed());

        // Check if player should receive a bonus life
        increaseLives(gameState);

        if(nextLevel <= NUM_LEVELS)
            gameSettings = getUpdatedGameSettings(nextLevel);
    }

    /**
     * Increase lives if the player meets the criteria.
     */
    private void increaseLives(GameState gameState) {
        int currentLives = gameState.getLivesRemaining();
        if (gameState.getLevel() % EXTRA_LIFE_FRECUENCY == 0 && currentLives < MAX_LIVES) {
            if (gameState == gameState1) {
                gameState1 = new GameState(gameState.getLevel(), gameState.getScore(),
                        currentLives + 1, gameState.getBulletsShot(), gameState.getShipsDestroyed());
            } else if (gameState == gameState2) {
                gameState2 = new GameState(gameState.getLevel(), gameState.getScore(),
                        currentLives + 1, gameState.getBulletsShot(), gameState.getShipsDestroyed());
            }
        }
    }
    private GameSettings getUpdatedGameSettings(int level) {
        switch (level) {
            case 1:
                return SETTINGS_LEVEL_1;
            case 2:
                return SETTINGS_LEVEL_2;
            case 3:
                return SETTINGS_LEVEL_3;
            case 4:
                return SETTINGS_LEVEL_4;
            case 5:
                return SETTINGS_LEVEL_5;
            case 6:
                return SETTINGS_LEVEL_6;
            case 7:
                return SETTINGS_LEVEL_7;
            default:
                return null;
        }

    }


}
