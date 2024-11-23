package screen;

import engine.*;

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

    /** Game states for each player **/
    private final GameState[] gameStates = new GameState[2];

    /** Players game task **/
    private final Future<GameState>[] players = new Future[2];

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
     * @param gameState
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
    public TwoPlayerScreen(final GameState gameState, final GameSettings gameSettings,
                           final int width, final int height, final int fps) {
        super(width * 2, height, fps * 2);

        for (int playerNumber = 0; playerNumber < 2; playerNumber++) {
            this.gameSettings[playerNumber] = new GameSettings(gameSettings);
            this.gameStates[playerNumber] = new GameState(gameState);
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
        renderer.initDrawing(this);
        renderer.mergeDrawing(this);
        renderer.drawVerticalLine(this);
        renderer.completeDrawing(this);
    }

    protected void createEntity(){

        swapBuffers();
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    @Override
    protected final void update() {
        draw();
        try {
            if (players[PLAYER1_NUMBER].isDone()) {
                gameStates[PLAYER1_NUMBER] = players[PLAYER1_NUMBER].get();
                gameStates[PLAYER1_NUMBER] = new GameState(gameStates[PLAYER1_NUMBER],
                        gameSettings[PLAYER1_NUMBER]);
                runGameScreen(PLAYER1_NUMBER);
            }
            if (players[PLAYER2_NUMBER].isDone()) {
                gameStates[PLAYER2_NUMBER] = players[PLAYER2_NUMBER].get();
                gameStates[PLAYER2_NUMBER] = new GameState(gameStates[PLAYER2_NUMBER],
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
        GameState gameState = playerNumber == 0 ? gameStates[PLAYER1_NUMBER] : gameStates[PLAYER2_NUMBER];

        if (gameState.livesRemaining() > 0) {
            logger.info(MessageFormat.format("difficulty is {0}", gameSettings[playerNumber].difficulty()));
            GameScreen gameScreen = new GameScreen(gameState, gameSettings[playerNumber], width / 2, height, fps / 2, playerNumber);
            gameScreen.initialize();
            players[playerNumber] = executor.submit(gameScreen);
        }
        else gameFinished[playerNumber] = true;
    }

    public GameState getWinnerGameState() {
        return gameStates[getWinnerNumber() - 1];
    }

    public int getWinnerNumber() {
        return ((gameStates[PLAYER1_NUMBER].score() >= gameStates[PLAYER2_NUMBER].score()) ? PLAYER1_NUMBER : PLAYER2_NUMBER) + 1;
    }
}
