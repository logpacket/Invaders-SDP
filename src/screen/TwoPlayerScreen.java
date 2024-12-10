package screen;

import engine.network.NetworkManager;
import entity.Entity;
import engine.GameLevelState;
import engine.GameSettings;
import engine.Menu;
import message.Entities;
import service.GameService;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Implements the Two player mode screen, where the action happens.
 */
public class TwoPlayerScreen extends Screen {
    /** Thread pool executor **/
    private final ExecutorService executor;
    /** Game difficulty settings **/
    private final GameSettings gameSettings;
    /** Game states **/
    private GameLevelState gameLevelState;
    /** Players game task **/
    private Future<GameLevelState> levelStateFuture;

    /** Player game finished flags **/
    private final boolean[] gameFinished = new boolean[2];

    /** Player 1's number**/
    private static final int PLAYER = 0;
    /** Player 2's number**/
    private static final int RIVAL = 1;

    private GameScreen gameScreen;
    private final List<Entity>[] playersEntities = new List[2];
    private GameService gameService;

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
    public TwoPlayerScreen(final GameLevelState gameLevelState, final GameSettings gameSettings,
                           final int width, final int height, final int fps) {
        super(width * 2, height, fps * 2);
        this.gameSettings = gameSettings;
        this.gameLevelState = gameLevelState;
        this.gameFinished[PLAYER] = false;
        this.gameFinished[RIVAL] = false;
        executor = Executors.newSingleThreadExecutor();
        playersEntities[PLAYER] = new ArrayList<>();

        this.menu = Menu.SCORE;
    }

    @Override
    public void initialize() {
        super.initialize();
        gameService = new GameService();
        try { runGameScreen(); }
        catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    @Override
    protected void draw() {
        renderer.initDrawing(this);
        renderer.drawEntities(playersEntities[PLAYER]);
        renderer.drawEntities(playersEntities[RIVAL], this.width);
        renderer.drawVerticalLine(this);
        renderer.completeDrawing(this);
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    @Override
    protected final void update() {
        try {
            if (levelStateFuture.isDone()) {
                gameLevelState = levelStateFuture.get();
                gameLevelState = new GameLevelState(gameLevelState, gameSettings);
                runGameScreen();
            }

            if (gameFinished[PLAYER] && gameFinished[RIVAL]) {
                isRunning = false;
                executor.shutdown();
            }
        } catch (InterruptedException | ExecutionException e) {
            logger.warning(e.getMessage());
            Thread.currentThread().interrupt();
        }

        playersEntities[PLAYER] = gameScreen.getEntities();
        gameService.syncGame(playersEntities[PLAYER]);
        playersEntities[RIVAL] = gameService.getEntityList();
        draw();
    }

    @Override
    protected void updateEntity() { }

    /**
     * Progression logic each games.
     */
    private void  runGameScreen(){
        if (gameLevelState.livesRemaining() > 0) {
            logger.info(MessageFormat.format("difficulty is {0}", gameSettings.difficulty()));
            gameScreen = new GameScreen(gameLevelState, gameSettings, width / 2, height, fps / 2);
            gameScreen.initialize();
            levelStateFuture = executor.submit(gameScreen);
        }
        else gameFinished[PLAYER] = true;
    }

    public GameLevelState getGameState() {
        return gameLevelState;
    }
}
