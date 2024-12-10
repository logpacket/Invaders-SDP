package engine;

import entity.Barrier;
import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Ship;

import java.awt.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Manages item drop and use.
 *
 * @author Seochan Moon
 * @author noturavrigk
 * @author specture258
 * @author javadocq
 * @author bamcasa
 * @author D0hunLee
 *
 */
public class ItemManager {
    /** Width of game screen. */
    private final int width;
    /** Height of game screen. */
    private final int height;
    /** Item drop probability, (1 ~ 100). */
    private static final int ITEM_DROP_PROBABILITY = 30;
    /** Cooldown of Ghost */
    private static final int GHOST_COOLDOWN = 3000;
    /** Cooldown of Time-stop */
    private static final int TIME_STOP_COOLDOWN = 4000;

    /** Random generator. */
    private final Random rand;
    /** Player's ship. */
    private final Ship ship;
    /** Formation of enemy ships. */
    private final EnemyShipFormation enemyShipFormation;
    /** Set of Barriers in game screen. */
    private final Set<Barrier> barriers;
    /** Application logger. */
    private final Logger logger;
    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();
    /** Cooldown variable for Ghost */
    private Cooldown ghostCooldown = Core.getCooldown(0);
    /** Cooldown variable for Time-stop */
    private Cooldown timeStopCooldown = Core.getCooldown(0);

    /** Check if the number of shoot is max, (maximum 3). */
    private boolean isMaxShootNum;
    /** Number of bullets that player's ship shoot. */
    private int shootNum;
    /** Sound balance for each player*/
    private final float balance;

    /** Types of item */
    public enum ItemType {
        BOMB,
        LINE_BOMB,
        BARRIER,
        GHOST,
        TIME_STOP,
        MULTI_SHOT
    }

    /**
     * Constructor, sets the initial conditions.
     *
     * @param ship Player's ship.
     * @param enemyShipFormation Formation of enemy ships.
     * @param barriers Set of barriers in game screen.
     * @param balance 1p -1.0, 2p 1.0, both 0.0
     *
     */
    public ItemManager(Ship ship, EnemyShipFormation enemyShipFormation, Set<Barrier> barriers, int width, int height, float balance) {
        this.shootNum = 1;
        this.rand = new Random();
        this.ship = ship;
        this.enemyShipFormation = enemyShipFormation;
        this.barriers = barriers;
        this.logger = Core.getLogger();
        this.width = width;
        this.height = height;
        this.balance = balance;
    }

    /**
     * Drop the item.
     *
     * @return Checks if the item was dropped.
     */
    public boolean dropItem() {
        return (rand.nextInt(101)) <= ITEM_DROP_PROBABILITY;
    }

    /**
     * Select item randomly.
     *
     * @return Item type.
     */
    private ItemType selectItemType() {
        ItemType[] itemTypes = ItemType.values();

        if (isMaxShootNum)
            return itemTypes[rand.nextInt(5)];

        return itemTypes[rand.nextInt(6)];
    }

    /**
     * Uses a randomly selected item.
     *
     * @return If the item is offensive, returns the score to add and the number of ships destroyed.
     *         If the item is non-offensive, returns null.
     */
    public Entry<Integer, Integer> useItem() {
        ItemType itemType = selectItemType();
        logger.info(itemType + " used");

        return switch (itemType) {
            case BOMB -> operateBomb();
            case LINE_BOMB -> operateLineBomb();
            case BARRIER -> operateBarrier();
            case GHOST -> operateGhost();
            case TIME_STOP -> operateTimeStop();
            case MULTI_SHOT -> operateMultiShoot();
        };
    }

    /**
     * Operate Bomb item.
     *
     * @return The score to add and the number of ships destroyed.
     */
    private Entry<Integer, Integer> operateBomb() {
        this.soundManager.playSound(Sound.ITEM_BOMB, balance);

        int addScore = 0;
        int addShipsDestroyed = 0;

        List<List<EnemyShip>> enemyships = this.enemyShipFormation.getEnemyShipList();
        int enemyShipsSize = enemyships.size();

        int maxCnt = -1;
        int maxRow = 0, maxCol = 0;

        for (int i = 0; i <= enemyShipsSize - 3; i++) {

            List<EnemyShip> rowShips = enemyships.get(i);
            int rowSize = rowShips.size();

            for (int j = 0; j <= rowSize - 3; j++) {

                int currentCnt = 0;

                for (int x = i; x < i + 3; x++) {

                    List<EnemyShip> subRowShips = enemyships.get(x);

                    for (int y = j; y < j + 3 && y < subRowShips.size(); y++) {
                        EnemyShip ship = subRowShips.get(y);

                        if (ship != null && !ship.isDestroyed())
                            currentCnt++;
                    }
                }

                if (currentCnt > maxCnt) {
                    maxCnt = currentCnt;
                    maxRow = i;
                    maxCol = j;
                }
            }
        }

        List<EnemyShip> targetEnemyShips = new ArrayList<>();
        for (int i = maxRow; i < maxRow + 3; i++) {
            List<EnemyShip> subRowShips = enemyships.get(i);
            for (int j = maxCol; j < maxCol + 3 && j < subRowShips.size(); j++) {
                EnemyShip ship = subRowShips.get(j);

                if (ship != null && !ship.isDestroyed())
                    targetEnemyShips.add(ship);
            }
        }

        if (!targetEnemyShips.isEmpty()) {
            for (EnemyShip destroyedShip : targetEnemyShips) {
                addScore += destroyedShip.getPointValue();
                addShipsDestroyed++;
                enemyShipFormation.destroy(destroyedShip, balance);
            }
        }

        return new SimpleEntry<>(addScore, addShipsDestroyed);
    }

    /**
     * Operate Line-bomb item.
     *
     * @return The score to add and the number of ships destroyed.
     */
    private Entry<Integer, Integer> operateLineBomb() {
        this.soundManager.playSound(Sound.ITEM_BOMB, balance);

        int addScore = 0;
        int addShipsDestroyed = 0;

        List<List<EnemyShip>> enemyShips = this.enemyShipFormation.getEnemyShipList();

        int destroyRow = -1;

        for (List<EnemyShip> column : enemyShips) {
            for (int i = 0; i < column.size(); i++) {
                if (column.get(i) != null && !column.get(i).isDestroyed())
                    destroyRow = Math.max(destroyRow, i);
            }
        }

        if (destroyRow != -1) {
            for (List<EnemyShip> column : enemyShips) {
                if (destroyRow < column.size() &&
                        column.get(destroyRow) != null &&
                        !column.get(destroyRow).isDestroyed()) {
                    addScore += column.get(destroyRow).getPointValue();
                    addShipsDestroyed++;
                    enemyShipFormation.destroy(column.get(destroyRow), balance);
                }
            }
        }

        return new SimpleEntry<>(addScore, addShipsDestroyed);
    }

    /**
     * Operate Barrier item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateBarrier() {
        this.soundManager.playSound(Sound.ITEM_BARRIER_ON, balance);

        int barrierY = height - 70;
        int middle = width / 2 - 39;
        int range = 200;
        this.barriers.clear();

        this.barriers.add(new Barrier(middle, barrierY));
        this.barriers.add(new Barrier(middle - range, barrierY));
        this.barriers.add(new Barrier(middle + range, barrierY));
        logger.info("Barrier created at positions: (" + middle + ", " + (barrierY) + "), ("
                + (middle - range) + ", " + (barrierY) + "), ("
                + (middle + range) + ", " + (barrierY) + ")");
        return null;
    }

    /**
     * Operate Ghost item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateGhost() {
        this.soundManager.playSound(Sound.ITEM_GHOST, balance);

        this.ship.setColor(Color.DARK_GRAY);
        this.ghostCooldown = Core.getCooldown(GHOST_COOLDOWN);
        this.ghostCooldown.reset();

        return null;
    }

    /**
     * Operate Time-stop item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateTimeStop() {
        this.soundManager.playSound(Sound.ITEM_TIME_STOP_ON, balance);

        this.timeStopCooldown = Core.getCooldown(TIME_STOP_COOLDOWN);
        this.timeStopCooldown.reset();

        return null;
    }

    /**
     * Operate Multi-shoot item.
     *
     * @return null
     */
    private Entry<Integer, Integer> operateMultiShoot() {
        if (this.shootNum < 3) {
            this.shootNum++;
            if (this.shootNum == 3) {
                this.isMaxShootNum = true;
            }
        }

        return null;
    }

    /**
     * Checks if Ghost is active.
     *
     * @return True when Ghost is active.
     */
    public boolean isGhostActive() {
        return !this.ghostCooldown.checkFinished();
    }

    /**
     * Checks if Time-stop is active.
     *
     * @return True when Time-stop is active.
     */
    public boolean isTimeStopActive() {
        return !this.timeStopCooldown.checkFinished();
    }

    /**
     * Returns the number of bullets that player's ship shoot.
     * @return Number of bullets that player's ship shoot.
     */
    public int getShootNum() {
        return this.shootNum;
    }
}