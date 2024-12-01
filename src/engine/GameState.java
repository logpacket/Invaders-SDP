package engine;

import entity.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the overall state of the game, containing all entities and game information.
 */
public class GameState {

    // Core game data
    private Ship playerShip;
    private Set<EnemyShip> enemyShips;
    private Set<Bullet> bullets;
    private Set<ItemBox> itemBoxes;
    private Set<Block> blocks;
    private Set<Web> webs;
    private Set<Barrier> barriers;

    // Game-level information
    private int score;
    private int level;
    private int lives;
    private boolean isGameOver;

    /**
     * Constructor to initialize game state with default values.
     */
    public GameState() {
        this.enemyShips = ConcurrentHashMap.newKeySet();
        this.bullets = ConcurrentHashMap.newKeySet();
        this.itemBoxes = ConcurrentHashMap.newKeySet();
        this.blocks = ConcurrentHashMap.newKeySet();
        this.webs = ConcurrentHashMap.newKeySet();
        this.barriers = ConcurrentHashMap.newKeySet();

        this.score = 0;
        this.level = 1;
        this.lives = 3;
        this.isGameOver = false;
    }

    // Getters and setters for all fields
    public Ship getPlayerShip() {
        return playerShip;
    }

    public void setPlayerShip(Ship playerShip) {
        this.playerShip = playerShip;
    }

    public Set<EnemyShip> getEnemyShips() {
        return enemyShips;
    }

    public Set<Bullet> getBullets() {
        return bullets;
    }

    public Set<ItemBox> getItemBoxes() {
        return itemBoxes;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public void setGameOver(boolean isGameOver) {
        this.isGameOver = isGameOver;
    }

    // Add methods to manage entity sets
    public void addEnemyShip(EnemyShip enemyShip) {
        this.enemyShips.add(enemyShip);
    }

    public void removeEnemyShip(EnemyShip enemyShip) {
        this.enemyShips.remove(enemyShip);
    }

    public void addBullet(Bullet bullet) {
        this.bullets.add(bullet);
    }

    public void removeBullet(Bullet bullet) {
        this.bullets.remove(bullet);
    }

    // Similar methods for itemBoxes, blocks, webs, barriers, etc.
}
