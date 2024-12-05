package screen;

import engine.GameLevelState;
import engine.GameSettings;
import engine.GameState;
import engine.Menu;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/**
 * Implements the Two player mode screen, where the action happens.
 */
public class TwoPlayerScreen extends Screen {
    /** Thread pool executor **/
    private final ExecutorService executor;
    /** Game difficulty settings each player **/
    private final GameSettings[] gameSettings = new GameSettings[2];

    private GameState gameState;

    /** Game states for each player **/
    private final GameLevelState[] gameLevelStates = new GameLevelState[2];

    /** Players game task **/
    private final Future<GameLevelState>[] players = new Future[2];

    /** Player game finished flags **/
    private final boolean[] gameFinished = new boolean[2];

    /** Player 1's number**/
    private static final int PLAYER1_NUMBER = 0;
    /** Player 2's number**/
    private static final int PLAYER2_NUMBER = 1;

    /**
     * Constructor, establishes the properties of the screen.
     *
     *
     * @param gameLevelState
     *            Initial game state
     * @param gameSettings
     *            Game settings list.
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public TwoPlayerScreen(final GameState gameState, final GameLevelState gameLevelState, final GameSettings gameSettings,
                           final int width, final int height, final int fps) {
        super(width * 2, height, fps * 2);

        for (int playerNumber = 0; playerNumber < 2; playerNumber++) {
            this.gameSettings[playerNumber] = new GameSettings(gameSettings);
            this.gameLevelStates[playerNumber] = new GameLevelState(gameLevelState);
            gameFinished[playerNumber] = false;
        }

        executor = Executors.newFixedThreadPool(2);
        this.menu = Menu.SCORE;
    }

    @Override
    public void initialize() {
        super.initialize();
        try {
            runGameScreen(PLAYER1_NUMBER);
            runGameScreen(PLAYER2_NUMBER);
        }
        catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);
        drawManager.mergeDrawing(this);
        drawManager.drawVerticalLine(this);
        drawManager.completeDrawing(this);
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    @Override
    protected final void update() {
        draw();
        try {
            if (players[PLAYER1_NUMBER].isDone()) {
                gameLevelStates[PLAYER1_NUMBER] = players[PLAYER1_NUMBER].get();
                gameLevelStates[PLAYER1_NUMBER] = new GameLevelState(gameLevelStates[PLAYER1_NUMBER],
                        gameSettings[PLAYER1_NUMBER]);
                runGameScreen(PLAYER1_NUMBER);
            }
            if (players[PLAYER2_NUMBER].isDone()) {
                gameLevelStates[PLAYER2_NUMBER] = players[PLAYER2_NUMBER].get();
                gameLevelStates[PLAYER2_NUMBER] = new GameLevelState(gameLevelStates[PLAYER2_NUMBER],
                        gameSettings[PLAYER2_NUMBER]);
                runGameScreen(PLAYER2_NUMBER);
            }

            if (gameFinished[PLAYER1_NUMBER] && gameFinished[PLAYER2_NUMBER]) {
                isRunning = false;
                executor.shutdown();
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }
    /**
     * Progression logic each games.
     */
    private void runGameScreen(int playerNumber){
        GameLevelState gameLevelState = playerNumber == 0 ? gameLevelStates[PLAYER1_NUMBER] : gameLevelStates[PLAYER2_NUMBER];

        if (gameLevelState.livesRemaining() > 0) {
            logger.info(MessageFormat.format("difficulty is {0}", gameSettings[playerNumber].difficulty()));
            GameScreen gameScreen = new GameScreen(gameLevelState, gameSettings[playerNumber], width / 2, height, fps / 2);
            gameScreen.initialize();
            players[playerNumber] = executor.submit(gameScreen);
        }
        else gameFinished[playerNumber] = true;
    }

    public GameLevelState getWinnerGameState() {
        return gameLevelStates[getWinnerNumber() - 1];
    }

    public int getWinnerNumber() {
        return ((gameLevelStates[PLAYER1_NUMBER].score() >= gameLevelStates[PLAYER2_NUMBER].score()) ? PLAYER1_NUMBER : PLAYER2_NUMBER) + 1;
    }
}
