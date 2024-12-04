package engine;

import entity.*;
import screen.GameScreen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents the overall state of the game, containing all entities and game information.
 */
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

    /** Application logger. */
    protected Logger logger;
    /** Screen width. */
    private int width;
    /** Screen height. */
    private int height;
    protected int screenWidth = 586;
    protected int screenHeight = 613;

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();
    /** Singleton instance of ItemManager. */
    private ItemManager itemManager;

    /** Formation of enemy ships. */
    private EnemyShipFormation enemyShipFormation;
    /** Player's ship. */
    private Ship ship;
    /** Current ship type. */
    private final Ship.ShipType shipType;
    /** Bonus enemy ship that appears sometimes. */
    private EnemyShip enemyShipSpecial;
    /** Set of all bullets fired by on-screen ships. */
    private final Set<Bullet> bullets;
    /** Item boxes that dropped when kill enemy ships. */
    private Set<ItemBox> itemBoxes;

    private List<Block> block;
    private List<Blocker> blockers;
    private List<Web> web;
    /** Barriers appear in game screen. */
    private Set<Barrier> barriers;
    /** Keep previous timestamp. */
    private Integer prevTime;
    /** Alert Message when a special enemy appears. */
    private String alertMessage;
    /** Checks if the level is finished. */
    private boolean levelFinished;
    /** Sound balance for each player*/
    private float balance = 0.0f;
    /** checks if it's executed. */
    private boolean isExecuted = false;
    /** Minimum time between bonus ship appearances. */
    private Cooldown enemyShipSpecialCooldown;
    /** Time until bonus ship explosion disappears. */
    private Cooldown enemyShipSpecialExplosionCooldown;
    /** Time from finishing the level to screen change. */
    private Cooldown screenFinishedCooldown;
    /** Timer */
    private Timer timer;
    private int level;
    private int score;
    /** Player lives left. */
    private int lives;
    /** Number of consecutive hits.
     * maxCombo records the maximum value of combos in that level. */
    private int combo;
    private int maxCombo;
    private int bulletsShoot;
    private int shipsDestroyed;
    private int elapsedTime;

    private int hitBullets;

    private GameScreen gameScreen;

    private static final int EXTRA_LIFE_FREQUENCY = 3;
    private static final int DEFAULT_FORMATION_SIZE = 4;
    private static final int DEFAULT_SPEED = 60;

    public GameState(final GameLevelState gameLevelState, final GameSettings gameSettings) {
        this.bullets = new HashSet<>();
        this.itemBoxes = new HashSet<>();
        this.block = new ArrayList<>();
        this.blockers = new ArrayList<>();
        this.web = new ArrayList<>();
        this.barriers = new HashSet<>();
        this.level = gameLevelState.level();
        this.score = gameLevelState.score();
        this.lives = gameLevelState.livesRemaining();
        this.maxCombo = gameLevelState.maxCombo();
        this.bulletsShoot = gameLevelState.bulletsShoot();
        this.shipsDestroyed = gameLevelState.shipsDestroyed();
        this.elapsedTime = gameLevelState.elapsedTime();
        this.hitBullets = gameLevelState.hitBullets();
        this.shipType = gameSettings.shipType();
        this.logger = Core.getLogger();
    }

    public int getLives() {
        return lives;
    }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public Ship getShip() {
        return ship;
    }

    public void setShip() {
        this.ship = ShipFactory.create(this.shipType, screenWidth / 2, screenHeight - 30);;
    }

    public Set<Bullet> getBullets() {
        return bullets;
    }

    public Set<ItemBox> getItemBoxes() {
        return itemBoxes;
    }

    public Set<Barrier> getBarriers() {
        return barriers;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<Blocker> getBlockers() {
        return blockers;
    }

    public void addBlocker(Blocker blocker) {
        blockers.add(blocker);
    }

    public void removeBlocker(Blocker blocker) {
        blockers.remove(blocker);
    }

    // Getter for EnemyShipFormation
    public EnemyShipFormation getEnemyShipFormation() {
        return enemyShipFormation;
    }

    /**
     * Initializes the EnemyShipFormation with the given settings and level state.
     *
     * @param gameSettings The current game settings.
     * @param gameLevelState The current level state of the game.
     */
    public void initializeEnemyShipFormation(GameSettings gameSettings, GameLevelState gameLevelState) {
        this.enemyShipFormation = new EnemyShipFormation(gameSettings, gameLevelState);
    }

    public List<Web> getWeb() {
        return web;
    }

    public List<Block> getBlock() {
        if (block == null) {
            block = new ArrayList<>();
        }
        return block;
    }

    public void initialize (int level, GameScreen gameScreen, int formationHeight) {
        int width = gameScreen.getWidth();
        int height = gameScreen.getHeight();
        this.itemManager = new ItemManager(this.ship, this.enemyShipFormation, this.barriers, gameScreen.getWidth(), gameScreen.getHeight(), this.balance);

        // initialize web
        if (this.web == null) {
            this.web = new ArrayList<>(); // web 초기화
        }
        int webCount = 1 + level / 3;
        for (int i = 0; i < webCount; i++) {
            double randomValue = Math.random();
            int positionX = (int) Math.max(0, randomValue * width - 12 * 2);
            int positionY = height - 30;
            this.web.add(new Web(positionX, positionY)); // Create a new Web
        }

        // initialize block
        if (this.block == null) {
            this.block = new ArrayList<>();
        }
        int blockCount = level / 2;
        int playerTopYContainBarrier = height - 40 - 150;
        int enemyBottomY = 100 + (formationHeight - 1) * 48;
        this.block.clear(); // Clear existing blocks

        for (int i = 0; i < blockCount; i++) {
            Block newBlock;
            boolean overlapping;

            do {
                newBlock = new Block(0, 0);
                int positionX = (int) (Math.random() * (width - newBlock.getWidth()));
                int positionY = (int) (Math.random() * (playerTopYContainBarrier - enemyBottomY - newBlock.getHeight())) + enemyBottomY;
                newBlock = new Block(positionX, positionY);

                overlapping = false;
                for (Block b : block) {
                    if (checkCollision(newBlock, b)) {
                        overlapping = true;
                        break;
                    }
                }
            } while (overlapping);

            block.add(newBlock);
        }
    }

    /**
     * Cleans bullets that go off-screen.
     */
    public void cleanBullets(GameScreen gameScreen) {
        Set<Bullet> recyclable = new HashSet<>();
        for (Bullet bullet : this.bullets) {
            bullet.update();
            if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
                    || bullet.getPositionY() > gameScreen.getHeight())
                recyclable.add(bullet);
        }
        this.bullets.removeAll(recyclable);
        BulletPool.recycle(recyclable);
    }

    /**
     * Checks if two entities are colliding.
     *
     * @param a
     *            First entity, the bullet.
     * @param b
     *            Second entity, the ship.
     * @return Result of the collision test.
     */
    public boolean checkCollision(final Entity a, final Entity b) {
        if (a == null || b == null) return false;
        // Calculate center point of the entities in both axis.
        int centerAX = a.getPositionX() + a.getWidth() / 2;
        int centerAY = a.getPositionY() + a.getHeight() / 2;
        int centerBX = b.getPositionX() + b.getWidth() / 2;
        int centerBY = b.getPositionY() + b.getHeight() / 2;
        // Calculate maximum distance without collision.
        int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
        int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
        // Calculates distance.
        int distanceX = Math.abs(centerAX - centerBX);
        int distanceY = Math.abs(centerAY - centerBY);

        return distanceX < maxDistanceX && distanceY < maxDistanceY;
    }

    public void manageCollisions() {
        for (EnemyShip diver : getEnemyShipFormation().getDivingShips()) {
            if(checkCollision(diver, this.ship) && !this.levelFinished && !this.ship.isDestroyed()) {
                this.ship.destroy(balance);
                this.lives--;
                this.logger.info("Hit on player ship, " + this.lives
                        + " lives remaining.");
            }
        }

        Set<Bullet> recyclable = new HashSet<>();

        if (!isExecuted){
            isExecuted = true;
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                public void run() {
                    combo = 0;
                }
            };
            timer.schedule(timerTask, 3000);
        }

        int topEnemyY = Integer.MAX_VALUE;
        for (EnemyShip enemyShip : getEnemyShipFormation()) {
            if (enemyShip != null && !enemyShip.isDestroyed() && enemyShip.getPositionY() < topEnemyY) {
                topEnemyY = enemyShip.getPositionY();
            }
        }
        if (this.enemyShipSpecial != null && !this.enemyShipSpecial.isDestroyed() && this.enemyShipSpecial.getPositionY() < topEnemyY) {
            topEnemyY = this.enemyShipSpecial.getPositionY();
        }

        for (Bullet bullet : this.bullets) {
            // Enemy ship's bullets
            if (bullet.getSpeed() > 0) {
                if (checkCollision(bullet, this.ship) && !this.levelFinished && !itemManager.isGhostActive()) {
                    recyclable.add(bullet);
                    if (!this.ship.isDestroyed()) {
                        this.ship.destroy(balance);
                        levelDamage();
                        this.logger.info("Hit on player ship, " + this.lives + " lives remaining.");
                    }
                }

                if (this.barriers != null) {
                    Iterator<Barrier> barrierIterator = this.barriers.iterator();
                    while (barrierIterator.hasNext()) {
                        Barrier barrier = barrierIterator.next();
                        if (checkCollision(bullet, barrier)) {
                            recyclable.add(bullet);
                            barrier.reduceHealth(balance);
                            if (barrier.isDestroyed()) {
                                barrierIterator.remove();
                            }
                        }
                    }
                }

            } else {	// Player ship's bullets
                for (EnemyShip enemyShip : getEnemyShipFormation())
                    if (enemyShip != null && !enemyShip.isDestroyed()
                            && checkCollision(bullet, enemyShip)) {
                        // Decide whether to destroy according to physical strength
                        getEnemyShipFormation().healthManageDestroy(enemyShip, balance);
                        // if the enemy dies, both the combo and score increase.
                        this.score += Score.comboScore(getEnemyShipFormation().getPoint(), this.combo);
                        this.shipsDestroyed += getEnemyShipFormation().getDestroyedShip();
                        this.combo++;
                        this.hitBullets++;
                        if (this.combo > this.maxCombo) this.maxCombo = this.combo;
                        timer.cancel();
                        isExecuted = false;
                        recyclable.add(bullet);

                        if (enemyShip.getHealth() < 0 && !getEnemyShipFormation().getEnemyDivers().contains(enemyShip) && itemManager.dropItem()) {
                            this.itemBoxes.add(new ItemBox(enemyShip.getPositionX() + 6, enemyShip.getPositionY() + 1, balance));
                            logger.info("Item box dropped");
                        }
                    }

                if (this.enemyShipSpecial != null
                        && !this.enemyShipSpecial.isDestroyed()
                        && checkCollision(bullet, this.enemyShipSpecial)) {
                    this.score += Score.comboScore(this.enemyShipSpecial.getPointValue(), this.combo);
                    this.shipsDestroyed++;
                    this.combo++;
                    this.hitBullets++;
                    if (this.combo > this.maxCombo) this.maxCombo = this.combo;
                    this.enemyShipSpecial.destroy(balance);
                    this.enemyShipSpecialExplosionCooldown.reset();
                    timer.cancel();
                    isExecuted = false;

                    recyclable.add(bullet);
                }

                if (this.itemManager.getShootNum() == 1 && bullet.getPositionY() < topEnemyY) {
                    this.combo = 0;
                    isExecuted = true;
                }

                Iterator<ItemBox> itemBoxIterator = this.itemBoxes.iterator();
                while (itemBoxIterator.hasNext()) {
                    ItemBox itemBox = itemBoxIterator.next();
                    if (checkCollision(bullet, itemBox) && !itemBox.isDroppedRightNow()) {
                        this.hitBullets++;
                        itemBoxIterator.remove();
                        recyclable.add(bullet);
                        Map.Entry<Integer, Integer> itemResult = this.itemManager.useItem();

                        if (itemResult != null) {
                            this.score += itemResult.getKey();
                            this.shipsDestroyed += itemResult.getValue();
                        }
                    }
                }

                //check the collision between the obstacle and the bullet
                for (Block b : getBlock()) {
                    if (checkCollision(bullet, b)) {
                        recyclable.add(bullet);
                        soundManager.playSound(Sound.BULLET_BLOCKING, balance);
                        break;
                    }
                }
            }
        }

        //check the collision between the obstacle and the enemy ship
        Set<Block> removableBlocks = new HashSet<>();
        for (EnemyShip enemyShip : getEnemyShipFormation()) {
            if (enemyShip != null && !enemyShip.isDestroyed()) {
                for (Block b : block) {
                    if (checkCollision(enemyShip, b)) {
                        removableBlocks.add(b);
                    }
                }
            }
        }
        // remove crashed obstacle
        block.removeAll(removableBlocks);
        this.bullets.removeAll(recyclable);
        BulletPool.recycle(recyclable);
    }

    //Enemy bullet damage increases depending on stage level
    public void levelDamage(){
        for(int i = 0; i<= level /3; i++){
            this.lives--;
        }
        if(this.lives < 0){
            this.lives = 0;
        }
    }

}
