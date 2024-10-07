package screen;

import engine.GameSettings;
import engine.GameState;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Implements the Two player mode screen, where the action happens.
 *
 * @author ZARA Team
 */

public class TwoPlayerScreen extends Screen {
    /** Thread pool executor **/
    private final ExecutorService executor;
    /** Current game difficulty settings **/
    private final GameSettings gameSettings;

    /** Game state for player 1 **/
    private GameState gameState1;
    /** Game state for player 2 **/
    private GameState gameState2;

    /** Player 1's game task **/
    private Future<GameState> player1;
    /** Player 2's game task **/
    private Future<GameState> player2;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param gameSettings
     *            Current game settings.
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public TwoPlayerScreen(final GameState gameState,
                           final GameSettings gameSettings, final int extraLifeFrequency,
                           final int width, final int height, final int fps) {
        super(width * 2, height, fps / 2);

        gameState1 = new GameState(gameState);
        gameState2 = new GameState(gameState);
        this.gameSettings = gameSettings;
        executor = Executors.newFixedThreadPool(2);
        this.returnCode = 1;
    }

    /**
     * Run each game screens.
     */
    public void runGameScreens() {
        GameScreen player1Screen = new GameScreen(gameState1, gameSettings,
                false, width / 2 , height, fps, 0);
        GameScreen player2Screen = new GameScreen(gameState2, gameSettings,
                false, width / 2 , height, fps, 1);

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
    private void draw(){
        drawManager.initDrawing(this);
        drawManager.mergeDrawing(this);
        drawManager.completeDrawing(this);
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        try {
            if (player1.isDone()) gameState1 = player1.get();
            if (player2.isDone()) gameState2 = player2.get();
            if (player1.isDone() && player2.isDone()) {
                executor.shutdown();
                isRunning = false;
            }
            else {draw();}
        }
        catch (Exception e) {
            // TODO handle exception
            e.printStackTrace();
        }
    }
}