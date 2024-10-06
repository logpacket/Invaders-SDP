package screen;

import engine.Core;
import engine.GameSettings;
import engine.GameState;
import entity.EnemyShipFormation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


/**
 * Implements the game screen, where the action happens.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */

public class Screen2P extends Screen {
    private GameScreen2P player1Screen;
    private GameScreen2P player2Screen;
    private ExecutorService executor;//


    /** Milliseconds until the screen accepts user input. */
    private static final int INPUT_DELAY = 6000;

    /** Current game difficulty settings. */
    private GameSettings gameSettings;
    private GameSettings gameSettings2;
    /** Formation of enemy ships. */
    private EnemyShipFormation enemyShipFormation;
    private EnemyShipFormation enemyShipFormation2;


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
    public Screen2P(final GameState gameState1, final GameState gameState2,
                    final GameSettings gameSettings, final GameSettings gameSettings2, final int extraLifeFrequency,
                    final int width, final int height, final int fps) {

        super(width, height, fps);

        player1Screen = new GameScreen2P(gameState1, gameSettings, extraLifeFrequency, width , height, fps, 1
        );
        player2Screen = new GameScreen2P(gameState2, gameSettings2, extraLifeFrequency, width , height, fps, 2
        );
        executor = Executors.newFixedThreadPool(2);
        this.gameSettings = gameSettings;
        this.gameSettings2 = gameSettings2;

    }

    public void show() throws Exception {
        Future<Integer> player1Result = executor.submit(player1Screen);
        Future<Integer> player2Result = executor.submit(player2Screen);
    }

    /**
     * Initializes basic screen properties, and adds necessary elements.
     */
    public final void initialize() {
        player1Screen.initialize();
        player2Screen.initialize();

//        enemyShipFormation = new EnemyShipFormation(this.gameSettings);
//        enemyShipFormation2 = new EnemyShipFormation(this.gameSettings2);
//        enemyShipFormation.attach(player1Screen);
//        enemyShipFormation.attach(player1Screen);
//        // Special input delay / countdown.
//        this.inputDelay = Core.getCooldown(INPUT_DELAY);
//        this.inputDelay.reset();
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public int run(){
        try {
            show();
        } catch (Exception e){
            e.printStackTrace();
        }
        super.run();

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
//    protected final void update() {
//
//        // Update both screen
//        player1Screen.update();
//        player2Screen.update();
//
//    }
}