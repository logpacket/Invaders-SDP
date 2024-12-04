package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;


import engine.*;
import engine.Menu;
import entity.*;


/**
 * Implements the game screen, where the action happens.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameScreen extends Screen implements Callable<GameState> {

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

	/** Current game difficulty settings. */
	private final GameSettings gameSettings;
	/** Current level number. */
	private final int level;
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
	/** Current ship type. */
	private final Ship.ShipType shipType;
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
	private final boolean bonusLife;
	/** Player number for two player mode **/
	private int playerNumber;
	/** list of highScores for find recode. */
	private List<Score>highScores;
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
	/** Blocker appearance cooldown */
	private final Cooldown blockerCooldown;
	private final Random random;
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

	private final GameState gameState;

	private int hitBullets;


    /**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param gameState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState,
			final GameSettings gameSettings, final int width, final int height, final int fps) {
		super(width, height, fps);

		this.gameSettings = gameSettings;
		this.gameState = gameState;
		this.bonusLife = gameState.bonusLife();
		this.level = gameState.level();
		this.score = gameState.score();
		this.elapsedTime = gameState.elapsedTime();
		this.lives = gameState.livesRemaining();
		this.bulletsShoot = gameState.bulletsShoot();
		this.shipsDestroyed = gameState.shipsDestroyed();
		this.playerNumber = -1;
		this.maxCombo = gameState.maxCombo();
		this.lapTime = gameState.prevTime();
		this.tempScore = gameState.prevScore();
		this.hitBullets = gameState.hitBullets();
		this.shipType = gameSettings.shipType();

		try {
			this.highScores = FileManager.getInstance().loadHighScores();
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}

		this.random = new Random();
		this.blockerCooldown = Core.getVariableCooldown(10000, 14000);
		this.blockerCooldown.reset();
		this.alertMessage = "";

	}

	/**
	 * Constructor, establishes the properties of the screen for two player mode.
	 *
	 * @param gameState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 * @param playerNumber
	 *            Player number for two player mode
	 */
	public GameScreen(final GameState gameState,
					  final GameSettings gameSettings,
					  final int width, final int height, final int fps,
					  final int playerNumber) {
		this(gameState, gameSettings, width, height, fps);
		this.playerNumber = playerNumber;
		this.balance = switch (playerNumber) {
			case 0: yield -1.0f; // 1P
			case 1: yield 1.0f;  // 2P
			default: yield 0.0f; // default
		};
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	@Override
	public final void initialize() {
		super.initialize();

		enemyShipFormation = new EnemyShipFormation(this.gameSettings, this.gameState);
		enemyShipFormation.attach(this);
        // Appears each 10-30 seconds.
        this.ship = ShipFactory.create(this.shipType, this.width / 2, this.height - 30);
		logger.info("Player ship created " + this.shipType + " at " + this.ship.getPositionX() + ", " + this.ship.getPositionY());
        ship.applyItem();
		//Create random Spider Web.
		int webCount = 1 + level / 3;
		web = new ArrayList<>();
		for(int i = 0; i < webCount; i++) {
			double randomValue = Math.random();
			this.web.add(new Web((int) Math.max(0, randomValue * width - 12 * 2), this.height - 30));
			this.logger.info("Spider web creation location : " + web.get(i).getPositionX());
		}
		//Create random Block.
		int blockCount = level / 2;
		int playerTopYContainBarrier = this.height - 40 - 150;
		int enemyBottomY = 100 + (gameState.formationHeight() - 1) * 48;
		this.block = new ArrayList<>();
		for (int i = 0; i < blockCount; i++) {
			Block newBlock;
			boolean overlapping;
			do {
				newBlock = new Block(0,0);
				int positionX = (int) (Math.random() * (this.width - newBlock.getWidth()));
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

		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<>();
		this.barriers = new HashSet<>();
        this.itemBoxes = new HashSet<>();
		this.itemManager = new ItemManager(this.ship, this.enemyShipFormation, this.barriers, this.width, this.height, this.balance);

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
		if (soundManager.isSoundPlaying(Sound.BGM_MAIN))
			soundManager.stopSound(Sound.BGM_MAIN);
		soundManager.playSound(Sound.COUNTDOWN);

		switch (this.level) {
			case 1: soundManager.loopSound(Sound.BGM_LV1); break;
			case 2: soundManager.loopSound(Sound.BGM_LV2); break;
			case 3: soundManager.loopSound(Sound.BGM_LV3); break;
			case 4: soundManager.loopSound(Sound.BGM_LV4); break;
			case 5: soundManager.loopSound(Sound.BGM_LV5); break;
			case 6: soundManager.loopSound(Sound.BGM_LV6); break;
            case 7:
				// From level 7 and above, it continues to play at BGM_LV7.
            default: soundManager.loopSound(Sound.BGM_LV7); break;
		}
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	@Override
	public final Menu run() {
		super.run();
		this.score += LIFE_SCORE * (this.lives - 1);
		if(this.lives == 0) this.score += 100;
		this.logger.info("Screen cleared with a score of " + this.score);

		return this.menu;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	@Override
	protected final void update() {
		if (playerNumber == -1)
			super.update();
		else{

		}

		if (this.inputDelay.checkFinished() && !this.levelFinished) {
			boolean player1Attacking = inputManager.isKeyDown(KeyEvent.VK_SPACE);
			boolean player2Attacking = inputManager.isKeyDown(KeyEvent.VK_SHIFT);

			if (player1Attacking && player2Attacking) {
				// Both players are attacking
				if (this.ship.shoot(this.bullets, this.itemManager.getShootNum()))
					this.bulletsShoot += this.itemManager.getShootNum();
			} else {
				switch (playerNumber) {
					case 1:
						if (player2Attacking && this.ship.shoot(this.bullets, this.itemManager.getShootNum(), 1.0f)) {
							this.bulletsShoot += this.itemManager.getShootNum();
						}
						break;
					case 0:
						if (player1Attacking && this.ship.shoot(this.bullets, this.itemManager.getShootNum(), -1.0f)) {
							this.bulletsShoot += this.itemManager.getShootNum();
						}
						break;
					default: //playerNumber = -1
						if (player1Attacking && this.ship.shoot(this.bullets, this.itemManager.getShootNum(), 0.0f)) {
							this.bulletsShoot += this.itemManager.getShootNum();
						}
						break;
				}
			}
			/*Elapsed Time Update*/
			long currentTime = System.currentTimeMillis();

			if (this.prevTime != null)
				this.elapsedTime += (int) (currentTime - this.prevTime);

			this.prevTime = (int) currentTime;

			if(!itemManager.isGhostActive())
				this.ship.setColor(Color.GREEN);

			if (!this.ship.isDestroyed()) {
				boolean moveRight;
				boolean moveLeft;
				switch (playerNumber) {
					case 0:
						moveRight = inputManager.isKeyDown(KeyEvent.VK_D);
						moveLeft = inputManager.isKeyDown(KeyEvent.VK_A);
						break;
					case 1:
						moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT);
						moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT);
						break;
					default:
						moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
								|| inputManager.isKeyDown(KeyEvent.VK_D);
						moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
								|| inputManager.isKeyDown(KeyEvent.VK_A);
				}

				boolean isRightBorder = this.ship.getPositionX()
						+ this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
				boolean isLeftBorder = this.ship.getPositionX()
						- this.ship.getSpeed() < 1;

				if (moveRight && !isRightBorder) {
					if (playerNumber == -1)
						this.ship.moveRight();
					else
						this.ship.moveRight(balance);
				}
				if (moveLeft && !isLeftBorder) {
					if (playerNumber == -1)
						this.ship.moveLeft();
					else
						this.ship.moveLeft(balance);
				}
                for (Web value : web) {
                    //escape Spider Web
                    if (ship.getPositionX() + 6 <= value.getPositionX() - 6
                            || value.getPositionX() + 6 <= ship.getPositionX() - 6) {
                        this.ship.setThreadWeb(false);
                    }
                    //get caught in a spider's web
                    else {
                        this.ship.setThreadWeb(true);
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
					&& this.enemyShipSpecial.getPositionX() > this.width) {
				this.enemyShipSpecial = null;
				this.logger.info("The special ship has escaped");
			}

			this.ship.update();

			// If Time-stop is active, Stop updating enemy ships' move and their shoots.
			if (!itemManager.isTimeStopActive()) {
				this.enemyShipFormation.update();
				this.enemyShipFormation.shoot(this.bullets, this.level, balance);
			}

			if (level >= 3) { //Events where vision obstructions appear start from level 3 onwards.
				handleBlockerAppearance();
			}
		}

		if(this.inputDelay.checkFinished() && !itemManager.isTimeStopActive()) {
			this.enemyShipFormation.updateSmooth();
		}

		manageCollisions();
		cleanBullets();


		if ((this.enemyShipFormation.isEmpty() || this.lives <= 0)
				&& !this.levelFinished) {
			this.levelFinished = true;

			soundManager.stopSound(soundManager.getCurrentBGM());
			if (this.lives == 0)
				soundManager.playSound(Sound.GAME_END);
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished()) {
			//Reset alert message when level is finished
			this.alertMessage = "";
			this.menu = Menu.SCORE;
			this.isRunning = false;
		}
	}


	protected void updateEntity(){
		entityList.add(EntityFactory.createGameTitle(this));

		entityList.addAll(EntityFactory.createLaunchTrajectory(this, this.ship.getPositionX()));

		entityList.add(this.ship);

		//create Spider Web
		if (web != null)
        	entityList.addAll(web);

		//create Blocks
		if (block != null)
        	entityList.addAll(block);

		if (this.enemyShipSpecial != null)
			entityList.add(this.enemyShipSpecial);


		//create enemyShip
		for (List<EnemyShip> column : enemyShipFormation.getEnemyShips())
			for (EnemyShip enemyShip : column)
				if (enemyShip != null)
					entityList.add(enemyShip);

		if(enemyShipFormation != null)
        	entityList.addAll(enemyShipFormation.getEnemyDivers());

		if (itemBoxes != null)
			entityList.addAll(this.itemBoxes);

		if(barriers != null)
			entityList.addAll(this.barriers);

		if(bullets != null)
			entityList.addAll(this.bullets);

		// Interface.
		entityList.add(EntityFactory.createScore(this, this.score));
		entityList.add(EntityFactory.createElapseTime(this, this.elapsedTime));
		entityList.add(EntityFactory.createAlertMessage(this, this.alertMessage));
		entityList.add(EntityFactory.createLivesString(this, this.lives));
		entityList.addAll(EntityFactory.createLivesSprites(this, this.lives, this.shipType));
		entityList.add(EntityFactory.createLevel(this, this.level));
		entityList.addAll(EntityFactory.createHorizontalLines(this, SEPARATION_LINE_HEIGHT - 1));
		entityList.add(EntityFactory.createReloadTimer(this, this.ship, ship.getRemainingReloadTime(), this.shipType));
		entityList.add(EntityFactory.createCombo(this, this.combo));


		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY - (System.currentTimeMillis() - this.gameStartTime)) / 1000);
			entityList.addAll(EntityFactory.createCountDown(this, this.level, countdown, this.bonusLife));
			entityList.addAll(EntityFactory.createHorizontalLines(this, this.height / 2 - this.height / 12));
			entityList.addAll(EntityFactory.createHorizontalLines(this, this.height / 2 + this.height / 12));

			//Intermediate aggregation
			if (this.level > 1){
				if (countdown == 0) {
					//Reset mac combo and edit temporary values
					this.lapTime = this.elapsedTime;
					this.tempScore = this.score;
					this.maxCombo = 0;
				} else {
					// Don't show it just before the game starts, i.e. when the countdown is zero.
					entityList.addAll(EntityFactory.createAggre(this, this.level - 1, this.maxCombo, this.elapsedTime - this.lapTime, this.score, this.tempScore));
				}
			}
		}

		entityList.add(EntityFactory.createRecord(this, highScores));

		// Blocker drawing part
		if (!blockers.isEmpty())
			entityList.addAll(blockers);

	}


	// Methods that handle the position, angle, sprite, etc. of the blocker (called repeatedly in update.)
	private void handleBlockerAppearance() {

		if (level >= 3 && level < 6) maxBlockers = 1;
		else if (level >= 6 && level < 11) maxBlockers = 2;
		else if (level >= 11) maxBlockers = 3;

		int kind = random.nextInt(2-1 + 1) +1; // 1~2
		Renderer.SpriteType newSprite = switch (kind) {
            case 1 -> Renderer.SpriteType.BLOCKER_1; // artificial satellite
            case 2 -> Renderer.SpriteType.BLOCKER_2; // astronaut
            default -> Renderer.SpriteType.BLOCKER_1;
        };

        // Check number of blockers, check timing of exit
		if (blockers.size() < maxBlockers && blockerCooldown.checkFinished()) {
			boolean isLeftDirection = random.nextBoolean(); // Randomly sets the movement direction of the current blocker
			int startY = random.nextInt(this.height - 90) + 25; // Random Y position with margins at the top and bottom of the screen
			int startX = isLeftDirection ? this.width + 300 : -300; // If you want to move left, outside the right side of the screen, if you want to move right, outside the left side of the screen.
			// Add new Blocker
            blockers.add(new Blocker(startX, startY, newSprite, isLeftDirection));
            blockerCooldown.reset();
		}

		// Items in the blocker list that will disappear after leaving the screen
		for (int i = 0; i < blockers.size(); i++) {
			Blocker blocker = blockers.get(i);

			// If the blocker leaves the screen, remove it directly from the list.
			if (blocker.getMoveLeft() && blocker.getPositionX() < -300 || !blocker.getMoveLeft() && blocker.getPositionX() > this.width + 300) {
				blockers.remove(i);
				i--; // When an element is removed from the list, the index must be decreased by one place.
				continue;
			}

			// Blocker movement and rotation (positionX, Y value change)
			if (blocker.getMoveLeft()) {
				blocker.move(-1.5, 0); // move left
			} else {
				blocker.move(1.5, 0); // move right
			}
			blocker.rotate(0.2); // Blocker rotation
		}
	}


	/**
	 * Cleans bullets that go off-screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
					|| bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Manages collisions between bullets and ships.
	 * Also manages collisions between diver enemies and ships.
	 */
	private void manageCollisions() {
		for (EnemyShip diver : this.enemyShipFormation.getDivingShips()) {
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
		for (EnemyShip enemyShip : this.enemyShipFormation) {
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
						this.enemyShipFormation.healthManageDestroy(enemyShip, balance);
						// if the enemy dies, both the combo and score increase.
						this.score += Score.comboScore(this.enemyShipFormation.getPoint(), this.combo);
						this.shipsDestroyed += this.enemyShipFormation.getDestroyedShip();
						this.combo++;
						this.hitBullets++;
						if (this.combo > this.maxCombo) this.maxCombo = this.combo;
						timer.cancel();
						isExecuted = false;
						recyclable.add(bullet);

						if (enemyShip.getHealth() < 0 && !this.enemyShipFormation.getEnemyDivers().contains(enemyShip) && itemManager.dropItem()) {
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
						Entry<Integer, Integer> itemResult = this.itemManager.useItem();

						if (itemResult != null) {
							this.score += itemResult.getKey();
							this.shipsDestroyed += itemResult.getValue();
						}
					}
				}

				//check the collision between the obstacle and the bullet
				for (Block b : this.block) {
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
		for (EnemyShip enemyShip : this.enemyShipFormation) {
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

	/**
	 * Checks if two entities are colliding.
	 * 
	 * @param a
	 *            First entity, the bullet.
	 * @param b
	 *            Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final SpriteEntity a, final SpriteEntity b) {
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

	/**
	 * Returns a GameState object representing the status of the game.
	 * 
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score, this.lives,
				this.bulletsShoot, this.shipsDestroyed, this.elapsedTime, this.bonusLife,
				this.gameState.formationWidth(), this.gameState.formationHeight(),
				this.gameState.baseSpeed(), this.gameState.shootInterval(),
				this.maxCombo, this.lapTime, this.tempScore, this.hitBullets);
	}


	/**
	 * Start the action for two player mode
	 *
	 * @return Current game state.
	 */
	@Override
	public final GameState call() {
		run();
		return getGameState();
	}

	//Enemy bullet damage increases depending on stage level
	public void levelDamage() {
		for(int i = 0; i<= level /3; i++){
			this.lives--;
		}
		if(this.lives < 0){
			this.lives = 0;
		}
	}

	public List<Entity> getEntities() {
		updateEntity();
		List<Entity> tmpEntities = (List<Entity>) ((ArrayList<Entity>) entityList).clone();
		entityList.clear();
		return tmpEntities;
	}
}
