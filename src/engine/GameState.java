package engine;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.Timer;
import java.util.TimerTask;


import engine.*;
import engine.Menu;
import entity.*;

public class GameState {
    /** Milliseconds until the screen accepts user input. */
    private static final int INPUT_DELAY = 6000;
    /** Bonus score for each life remaining at the end of the level. */
    private static final int LIFE_SCORE = 100;
    /** Minimum time between bonus ship's appearances. */
    private static final int BONUS_SHIP_INTERVAL = 20000;
    /** Maximum variance in the time between bonus ship's appearances. */
    private static final int BONUS_SHIP_VARIANCE = 10000;
    /** Time until bonus ship explosion disappears. */
    private static final int BONUS_SHIP_EXPLOSION = 500;
    /** Time from finishing the level to screen change. */
    private static final int SCREEN_CHANGE_INTERVAL = 1500;
    /** Height of the interface separation line. */
    private static final int SEPARATION_LINE_HEIGHT = 40;

    /** Formation of enemy ships. */
    private EnemyShipFormation enemyShipFormation;
    /** Player's ship. */
    private Ship ship;
    /** Bonus enemy ship that appears sometimes. */
    private EnemyShip enemyShipSpecial;
    /** Minimum time between bonus ship appearances. */
    private Cooldown enemyShipSpecialCooldown;
    /** Time until bonus ship explosion disappears. */
    private Cooldown enemyShipSpecialExplosionCooldown;
    /** Time from finishing the level to screen change. */
    private Cooldown screenFinishedCooldown;
    /** Set of all bullets fired by on-screen ships. */
    private Set<Bullet> bullets;

    private int score;
    /** tempScore records the score up to the previous level. */
    private int tempScore;
    /** Player lives left. */
    private int lives;
    /** Total bullets shoot by the player. */
    private int bulletsShoot;
    /** Total ships destroyed by the player. */
    private int shipsDestroyed;
    /** Number of consecutive hits.
     * maxCombo records the maximum value of combos in that level. */
    private int combo;
    private int maxCombo;
    /** Moment the game starts. */
    private long gameStartTime;

    /** Checks if the level is finished. */
    private boolean levelFinished;
    /** Checks if a bonus life is received. */

    /** Player number for two player mode **/
    private int playerNumber;
    /** list of highScores for find recode. */
    private List<Score> highScores;
    /** Elapsed time while playing this game.
     * lapTime records the time to the previous level. */
    private int elapsedTime;
    private int lapTime;
    /** Keep previous timestamp. */
    private Integer prevTime;
    /** Alert Message when a special enemy appears. */
    private String alertMessage;
    /** checks if it's executed. */
    private boolean isExecuted = false;
    /** Timer */
    private Timer timer;
    /** Spider webs restricting player movement */
    private List<Web> web;
    /** Obstacles preventing a player's bullet */
    private List<Block> block;

    private final List<Blocker> blockers = new ArrayList<>();
    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();
    /** Singleton instance of ItemManager. */
    private ItemManager itemManager;
    /** Item boxes that dropped when kill enemy ships. */
    private Set<ItemBox> itemBoxes;
    /** Barriers appear in game screen. */
    private Set<Barrier> barriers;
    /** Sound balance for each player*/
    private float balance = 0.0f;

    private int maxBlockers = 0;

    private int hitBullets;

}
