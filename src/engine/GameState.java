package engine;

import engine.network.NetworkManager;
import entity.*;
import screen.GameScreen;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * Represents the overall state of the game, containing all entities and game information.
 */
public class GameState {

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

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();
    /** Singleton instance of ItemManager. */
    private ItemManager itemManager;
    private GameScreen gameScreen;

    /** Formation of enemy ships. */
    private EnemyShipFormation enemyShipFormation;
    /** Player's ship. */
    private Ship ship;
    /** Current ship type. */
    private final Ship.ShipType shipType;
    /** Bonus enemy ship that appears sometimes. */
    private EnemyShip enemyShipSpecial;

    /*Entities*/
    /** Set of all bullets fired by on-screen ships. */
    private final Set<Bullet> bullets;
    /** Item boxes that dropped when kill enemy ships. */
    private Set<ItemBox> itemBoxes;
    private List<Block> block;
    private List<Blocker> blockers;
    private List<Web> webList;
    /** Barriers appear in game screen. */
    private Set<Barrier> barriers;

    /** Checks if a bonus life is received. */
    private final boolean bonusLife;
    /** Checks if the level is finished. */
    private boolean levelFinished;
    /** checks if it's executed. */
    private boolean isExecuted = false;
    /** Minimum time between bonus ship appearances. */
    private final Cooldown enemyShipSpecialCooldown;
    /** Time until bonus ship explosion disappears. */
    private final Cooldown enemyShipSpecialExplosionCooldown;
    /** Time from finishing the level to screen change. */
    private final Cooldown screenFinishedCooldown;
    /** Timer */
    private Timer timer;
    private int maxBlockers = 0;
    /** Blocker appearance cooldown */
    private final Cooldown blockerCooldown;
    private final Random random;
    private String alertMessage = "";
    /** Sound balance for each player*/
    private float balance = 0.0f;

    /** Sources from GameLevelState */
    private int level;
    private int score;
    private int lives;
    private int bulletsShoot;
    private int shipsDestroyed;
    private int combo;
    private int maxCombo;
    /** Keep previous timestamp. */
    private Integer prevTime;
    private int elapsedTime;
    private int lapTime;
    private int hitBullets;
    private int tempScore;
    private long ping = 0;


    public GameState(final GameLevelState gameLevelState, final GameSettings gameSettings) {
        this.bullets = new HashSet<>();
        this.itemBoxes = new HashSet<>();
        this.block = new ArrayList<>();
        this.blockers = new ArrayList<>();
        this.webList = new ArrayList<>();
        this.barriers = new HashSet<>();
        this.level = gameLevelState.level();
        this.score = gameLevelState.score();
        this.lives = gameLevelState.livesRemaining();
        this.bonusLife = gameLevelState.bonusLife();
        this.maxCombo = gameLevelState.maxCombo();
        this.bulletsShoot = gameLevelState.bulletsShoot();
        this.shipsDestroyed = gameLevelState.shipsDestroyed();
        this.hitBullets = gameLevelState.hitBullets();
        this.shipType = gameSettings.shipType();
        this.logger = Core.getLogger();
        this.enemyShipSpecialCooldown = Core.getVariableCooldown(
                BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
        this.enemyShipSpecialCooldown.reset();
        this.enemyShipSpecialExplosionCooldown = Core
                .getCooldown(BONUS_SHIP_EXPLOSION);
        this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);

        this.random = new Random();
        this.blockerCooldown = Core.getVariableCooldown(10000, 14000);
        this.blockerCooldown.reset();
    }

    public int getLives() { return lives;}

    public boolean getBonusLife() {
        return bonusLife;
    }
    public int getLevel() { return level; }

    public Ship getShip() {
        return this.ship;
    }

    public EnemyShip getEnemyShipSpecial() {
        return enemyShipSpecial;
    }
    public int getCombo() { return combo;}
    public int getMaxCombo() { return maxCombo;}
    public int getTempScore() { return tempScore;}
    public int getLapTime() { return lapTime;}
    public void initTempScore() {
            tempScore = this.score;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }
    public void initLapTime() {
        this.lapTime = this.elapsedTime;
    }

    public void initMaxCombo() {maxCombo = 0;}
    public int getBulletsShoot() {return bulletsShoot;}

    public int getShipsDestroyed() {return shipsDestroyed;}
    public Cooldown setScreenFinishedCooldown() {
        return screenFinishedCooldown;
    }

    public void setLevelFinished() {
        this.levelFinished = true;
    }

    public boolean isLevelFinished() { return levelFinished; }

    public int getHitBullets() {
        return hitBullets;
    }

    public Set<Bullet> getBullets() {
        return this.bullets;
    }

    public Set<ItemBox> getItemBoxes() {
        return itemBoxes;
    }

    public Set<Barrier> getBarriers() {
        return barriers;
    }

    public int getScore() {
        return this.score;
    }

    public void addScore(int lives) {
        this.score += LIFE_SCORE * (lives - 1);
        if(lives == 0) this.score += 100;
    }

    public List<Blocker> getBlockers() {
        return blockers;
    }

    public EnemyShipFormation getEnemyShipFormation() {
        return enemyShipFormation;
    }

    public List<Web> getWebList() {
        return webList;
    }

    public List<Block> getBlock() {
        if (block == null) {
            block = new ArrayList<>();
        }
        return block;
    }

    public long getPing() { return ping; }

    public String getAlertMessage() { return alertMessage; }

    public void initialize(GameSettings gameSettings, GameLevelState gameLevelState, GameScreen gameScreen, int formationHeight) {
        this.gameScreen = gameScreen;

        this.ship = ShipFactory.create(this.shipType, gameScreen.getWidth() / 2, gameScreen.getHeight() - 30);
        this.ship.applyItem();

        this.enemyShipFormation = new EnemyShipFormation(gameSettings, gameLevelState);
        this.enemyShipFormation.attach(gameScreen);

        this.itemManager = new ItemManager(this.ship, this.enemyShipFormation, this.barriers, gameScreen.getWidth(), gameScreen.getHeight(), this.balance);

        elapsedTime = gameLevelState.elapsedTime();

        // initialize webList
        if (this.webList == null) {
            this.webList = new ArrayList<>(); // webList 초기화
        }
        int webCount = 1 + level / 3;
        for (int i = 0; i < webCount; i++) {
            double randomValue = Math.random();
            int positionX = (int) Math.max(0, randomValue * gameScreen.getWidth() - 12 * 2);
            int positionY = gameScreen.getHeight() - 30;
            this.webList.add(new Web(positionX, positionY)); // Create a new Web
        }

        // initialize block
        if (this.block == null) {
            this.block = new ArrayList<>();
        }
        int blockCount = level / 2;
        int playerTopYContainBarrier = gameScreen.getHeight() - 40 - 150;
        int enemyBottomY = 100 + (formationHeight - 1) * 48;
        this.block.clear(); // Clear existing blocks

        for (int i = 0; i < blockCount; i++) {
            Block newBlock;
            boolean overlapping;

            do {
                newBlock = new Block(0, 0);
                int positionX = (int) (Math.random() * (gameScreen.getWidth() - newBlock.getWidth()));
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

    public void update(boolean playerAttacking, boolean moveRight, boolean moveLeft) {
        if (playerAttacking && ship.shoot(bullets, itemManager.getShootNum()))
            bulletsShoot += itemManager.getShootNum();

        long currentTime = System.currentTimeMillis();
        ping = NetworkManager.getLatency();

        if (prevTime != null)
            elapsedTime += (int) (currentTime - prevTime);

        prevTime = (int) currentTime;

        if(!itemManager.isGhostActive())
            ship.setColor(Color.GREEN);

        if (!ship.isDestroyed()) {
            boolean isRightBorder = ship.getPositionX()
                    + ship.getWidth() + ship.getSpeed() > gameScreen.getWidth() - 1;
            boolean isLeftBorder = ship.getPositionX()
                    - ship.getSpeed() < 1;

            if (moveRight && !isRightBorder) ship.moveRight();
            if (moveLeft && !isLeftBorder) ship.moveLeft();

            for (Web web : webList) {
                //escape Spider Web
                if (ship.getPositionX() + 6 <= web.getPositionX() - 6
                        || web.getPositionX() + 6 <= web.getPositionX() - 6) {
                    ship.setThreadWeb(false);
                }
                //get caught in a spider's webList
                else {
                    ship.setThreadWeb(true);
                    break;
                }
            }
        }
        if (this.enemyShipSpecial != null) {
            if (!this.enemyShipSpecial.isDestroyed())
                this.enemyShipSpecial.move(2, 0);
            else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
                this.enemyShipSpecial = null;

        }
        if (this.enemyShipSpecial == null
                && this.enemyShipSpecialCooldown.checkFinished()) {
            this.enemyShipSpecial = new EnemyShip();
            this.alertMessage = "";
            this.enemyShipSpecialCooldown.reset();
            soundManager.playSound(Sound.UFO_APPEAR, balance);
            this.logger.info("A special ship appears");
        }
        if(this.enemyShipSpecial == null
                && this.enemyShipSpecialCooldown.checkAlert()) {
            switch (this.enemyShipSpecialCooldown.checkAlertAnimation()){
                case 1: this.alertMessage = "--! ALERT !--";
                    break;

                case 2: this.alertMessage = "-!! ALERT !!-";
                    break;

                case 3: this.alertMessage = "!!! ALERT !!!";
                    break;

                default: this.alertMessage = "";
                    break;
            }
        }
        if (this.enemyShipSpecial != null
                && this.enemyShipSpecial.getPositionX() > gameScreen.getWidth()) {
            this.enemyShipSpecial = null;
            this.logger.info("The special ship has escaped");
        }

        ship.update();

        // If Time-stop is active, Stop updating enemy ships' move and their shoots.
        if (!itemManager.isTimeStopActive()) {
            enemyShipFormation.update();
            enemyShipFormation.shoot(bullets, level, balance);
        }

        if (level >= 3) { //Events where vision obstructions appear start from level 3 onwards.
            handleBlockerAppearance();
        }
    }

    public void updateEnemyShipFormation(boolean inputDelayFinished) {
        if(inputDelayFinished && !itemManager.isTimeStopActive()) {
            enemyShipFormation.updateSmooth();
        }

    }

    // Methods that handle the position, angle, sprite, etc. of the blocker (called repeatedly in update.)
    private void handleBlockerAppearance() {

        if (level >= 3 && level < 6) maxBlockers = 1;
        else if (level >= 6 && level < 11) maxBlockers = 2;
        else if (level >= 11) maxBlockers = 3;

        int kind = random.nextInt(2 - 1 + 1) + 1; // 1~2
        Renderer.SpriteType newSprite = switch (kind) {
            case 1 -> Renderer.SpriteType.BLOCKER_1; // artificial satellite
            case 2 -> Renderer.SpriteType.BLOCKER_2; // astronaut
            default -> Renderer.SpriteType.BLOCKER_1;
        };

        // Check number of blockers and cooldown
        if (blockers.size() < maxBlockers && blockerCooldown.checkFinished()) {
            boolean isLeftDirection = random.nextBoolean(); // Random movement direction
            int startY = random.nextInt(gameScreen.getHeight() - 90) + 25; // Random Y position
            int startX = isLeftDirection ? gameScreen.getWidth() + 300 : -300; // Start position based on direction

            // Add new Blocker
            blockers.add( new Blocker(startX, startY, newSprite, isLeftDirection));
            blockerCooldown.reset();
        }

        // Manage existing blockers
        for (int i = 0; i < blockers.size(); i++) {
            Blocker blocker = blockers.get(i);

            // Remove blockers that leave the screen
            if (blocker.getMoveLeft() && blocker.getPositionX() < -300
                    || !blocker.getMoveLeft() && blocker.getPositionX() > gameScreen.getWidth() + 300) {
                blockers.remove(blocker);
                i--;
                continue;
            }

            // Update blocker position and rotation
            if (blocker.getMoveLeft()) {
                blocker.move(-1.5, 0); // Move left
            } else {
                blocker.move(1.5, 0); // Move right
            }
            blocker.rotate(0.2); // Rotate blocker
        }
    }

    /**
     * Cleans bullets that go off-screen.
     */
    public void cleanBullets() {
        Set<Bullet> recyclable = new HashSet<>();
        for (Bullet bullet : bullets) {
            bullet.update();
            if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
                    || bullet.getPositionY() > gameScreen.getHeight())
                recyclable.add(bullet);
        }
        bullets.removeAll(recyclable);
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
    public boolean checkCollision(final SpriteEntity a, final SpriteEntity b) {
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
        for (EnemyShip diver : this.enemyShipFormation.getDivingShips()) {
            if(checkCollision(diver, ship) && !this.levelFinished && !ship.isDestroyed()) {
                ship.destroy(balance);
                lives--;
                logger.info("Hit on player ship, " + lives
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
                for (EnemyShip enemyShip : this.enemyShipFormation)
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
        if(this.lives< 0){
            this.lives = 0;
        }
    }
}
