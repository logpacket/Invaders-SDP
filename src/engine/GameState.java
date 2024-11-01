package engine;

import entity.Ship;
import entity.Wallet;

/**
 * Implements an object that stores the state of the game between levels.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameState {
	/** Current game level. */
	public final int level;
	/** Current score. */
	public final int score;
	/** Lives currently remaining. */
	public final int livesRemaining;
	/** Bullets shot until now. */
	public final int bulletsShot;
	/** Ships destroyed until now. */
	public final int shipsDestroyed;
	/** Elapsed time */
	public final int elapsedTime;
	/** Bonus life */
	public final boolean bonusLife;
	/** Width of the level's enemy formation. */
	private int formationWidth;
	/** Height of the level's enemy formation. */
	private int formationHeight;
	/** Speed of the enemies, function of the remaining number. */
	private int baseSpeed;
	/** Frequency of enemy shotings, +/- 30%. */
	private int shotFreq;
	/** Intermediate aggregation variables
	 * max combo, elapsed time and total score
	 * you get from previous level */
	public final int maxCombo;
	public final int prevTime;
	public final int prevScore;
	public final int hitBullets;
	/** Levels between extra life. */
	public static final int EXTRA_LIFE_FREQUENCY = 3;

	private static final int DEFAULT_FORMATION_SIZE = 4;
	private static final int DEFAULT_SPEED = 60;
	private static final int DEFAULT_SHOOT_FREQ = 2500;

	@FunctionalInterface
	interface CheckLambda {
		boolean check (int inc, int threshold);
	}

	public GameState() {
		this.level = 1;
		this.score = 0;
		this.livesRemaining = Wallet.getWallet().getLivesLevel() + 2 ;
		this.bulletsShot = 0;
		this.shipsDestroyed = 0;
		this.elapsedTime = 0;
		this.formationWidth = DEFAULT_FORMATION_SIZE;
		this.formationHeight = DEFAULT_FORMATION_SIZE;
		this.baseSpeed = DEFAULT_SPEED;
		this.shotFreq = DEFAULT_SHOOT_FREQ;
		this.maxCombo = 0;
		this.prevTime = 0;
		this.prevScore = 0;
		this.hitBullets = 0;
		this.bonusLife = false;
	}

	/**
	 * Constructor.
	 * 
	 * @param level
	 *            Current game level.
	 * @param score
	 *            Current score.
	 * @param livesRemaining
	 *            Lives currently remaining.
	 * @param bulletsShot
	 *            Bullets shot until now.
	 * @param shipsDestroyed
	 *            Ships destroyed until now.
	 * @param elapsedTime
	 * 			  Elapsed time.
	 * @param formationWidth
	 *            Width of the level's enemy formation.
	 * @param formationHeight
	 *            Height of the level's enemy formation.
	 * @param baseSpeed
	 *            Speed of the enemies.
	 * @param shotFreq
	 *            Frequency of enemy shootings, +/- 30%.
	 * @param maxCombo
	 * 			  Previous level's max combo
	 * @param prevTime
	 * 			  Previous time
	 * @param prevScore
	 * 			  Previous score
	 * @param hitBullets
	 * 			  Count of bullets that hit
	 */
	public GameState(final int level, final int score,
			final int livesRemaining, final int bulletsShot,
			final int shipsDestroyed, final int elapsedTime, final int formationWidth,
			 final int formationHeight, final int baseSpeed, final int shotFreq,
			 final int maxCombo, final int prevTime, final int prevScore, final int hitBullets) {
				
		this.level = level;
		this.score = score;
		this.livesRemaining = livesRemaining;
		this.bulletsShot = bulletsShot;
		this.shipsDestroyed = shipsDestroyed;
		this.elapsedTime = elapsedTime;
		this.formationWidth = formationWidth;
		this.formationHeight = formationHeight;
		this.baseSpeed = baseSpeed;
		this.shotFreq = shotFreq;
		this.maxCombo = maxCombo;
		this.prevTime = prevTime;
		this.prevScore = prevScore;
		this.hitBullets = hitBullets;
		this.bonusLife = false;
	}

	public GameState(GameState gameState) {
		this.level = gameState.level;
		this.score = gameState.score;
		this.livesRemaining = gameState.livesRemaining;
		this.bulletsShot = gameState.bulletsShot;
		this.shipsDestroyed = gameState.shipsDestroyed;
		this.elapsedTime = gameState.elapsedTime;
		this.formationWidth = gameState.formationWidth;
		this.formationHeight = gameState.formationHeight;
		this.baseSpeed = gameState.baseSpeed;
		this.shotFreq = gameState.shotFreq;
		this.maxCombo = gameState.maxCombo;
		this.prevTime = gameState.prevTime;
		this.prevScore = gameState.prevScore;
		this.hitBullets = gameState.hitBullets;
		this.bonusLife = gameState.bonusLife;
	}


	public GameState(GameState origin, GameSettings gameSettings) {
		this.level = origin.level + 1;
		this.score = origin.score;
		this.bulletsShot = origin.bulletsShot;
		this.shipsDestroyed = origin.shipsDestroyed;
		this.elapsedTime = origin.elapsedTime;
		this.maxCombo = origin.maxCombo;
		this.prevTime = origin.prevTime;
		this.prevScore = origin.prevScore;
		this.hitBullets = origin.hitBullets;
		this.bonusLife = level
				% EXTRA_LIFE_FREQUENCY == 0
				&& origin.livesRemaining < gameSettings.maxLives;
		this.livesRemaining = bonusLife ? origin.livesRemaining + 1 : origin.livesRemaining;

		boolean isUpgradeLevel = false;
		boolean checkFormWidth = origin.formationWidth < 14;
		boolean checkFormHeight = origin.formationHeight < 10;
		boolean checkFormation = origin.formationWidth == origin.formationHeight && checkFormWidth;
		CheckLambda checkSpeed = (int inc, int threshold) -> origin.baseSpeed - inc > threshold;
		CheckLambda checkShotFreq = (int inc, int threshold) -> origin.shotFreq - inc > threshold;

		int tempFormationWidth = origin.formationWidth;
		int tempFormationHeight = origin.formationHeight;
		int tempBaseSpeed = origin.baseSpeed;
		int tempShotFreq = origin.shotFreq;

		int speedInc = 10;
		int speedThreshold = -150;
		int shotFreqInc = 100;
		int shotFreqThreshold = 100;
		int baseShotFreq = 100;
		int formInc = 1;

		switch (gameSettings.difficulty) {
			case 0 -> {
				if ((level%3 == 0 && level < 5) || (level % 2 == 0 && level >= 5) ) isUpgradeLevel = true;
			}
			case 1 -> {
				shotFreqInc = 200;
				shotFreqThreshold = 200;
				if(level % 2 == 0)  isUpgradeLevel = true;
				else if (level >= 5) {
					isUpgradeLevel = true;
					speedInc = 20;
				}
			}
			case 2 -> {
				speedInc = 20;
				shotFreqInc = 300;
				if (level%2 == 0 && level < 5) isUpgradeLevel = true;
				else if (level >= 5) {
					isUpgradeLevel = true;
					shotFreqInc = 400;
					formInc = 2;
				}
			}
		}

		if (isUpgradeLevel) {
			if (checkFormation) tempFormationWidth += formInc;
			else if (checkFormHeight) tempFormationHeight += formInc;

			tempBaseSpeed = checkSpeed.check(speedInc, speedThreshold) ?
					origin.baseSpeed - speedInc : speedThreshold;

			tempShotFreq = checkShotFreq.check(shotFreqInc, shotFreqThreshold) ?
					origin.shotFreq - shotFreqInc : baseShotFreq;
		}

		this.formationWidth = tempFormationWidth;
		this.formationHeight = tempFormationHeight;
		this.baseSpeed = tempBaseSpeed;
		this.shotFreq = tempShotFreq;
	}

	public double getAccuracy() {
		if (bulletsShot == 0){
			return 0;
		}
		return ((double) hitBullets / bulletsShot) * 100;
	}

	public int getFormationWidth() { return formationWidth; }
	public int getFormationHeight() { return formationHeight; }
	public int getBaseSpeed() { return baseSpeed; }
	public int getShotFreq() { return shotFreq; }
}

