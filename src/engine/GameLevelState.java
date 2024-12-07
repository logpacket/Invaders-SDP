package engine;

import entity.Wallet;

/**
 * Implements an object that stores the state of the game between levels.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public record GameLevelState(
	int level,
	int score,
	int livesRemaining,
	int bulletsShoot,
	int shipsDestroyed,
	int elapsedTime,
	boolean bonusLife,
	int formationWidth,
	int formationHeight,
	int baseSpeed,
	int shootInterval,
	int maxCombo,
	int prevTime,
	int prevScore,
	int hitBullets
) {
	private static final int EXTRA_LIFE_FREQUENCY = 3;
	private static final int DEFAULT_FORMATION_SIZE = 4;
	private static final int DEFAULT_SPEED = 60;
	private static final int DEFAULT_SHOOT_INTERVAL = 2500;

	@FunctionalInterface
	interface CheckLambda {
		boolean check (int inc, int threshold);
	}

	public GameLevelState() {
		this(1, 0, Wallet.getWallet().getLivesLevel() + 2, 0, 0, 0,
				false, DEFAULT_FORMATION_SIZE, DEFAULT_FORMATION_SIZE, DEFAULT_SPEED,
				DEFAULT_SHOOT_INTERVAL, 0, 0, 0, 0);
	}

	public GameLevelState(GameLevelState gameLevelState) {
		this(gameLevelState.level(), gameLevelState.score(), gameLevelState.livesRemaining(), gameLevelState.bulletsShoot(),
				gameLevelState.shipsDestroyed(), gameLevelState.elapsedTime(), gameLevelState.bonusLife, gameLevelState.formationWidth(),
				gameLevelState.formationHeight, gameLevelState.baseSpeed, gameLevelState.shootInterval, gameLevelState.maxCombo(),
				gameLevelState.prevTime, gameLevelState.prevScore, gameLevelState.hitBullets);
	}

	public GameLevelState(GameLevelState origin, GameSettings gameSettings) {
		int level = origin.level + 1;
		boolean tempBonusLife = (level % EXTRA_LIFE_FREQUENCY == 0 && origin.livesRemaining < gameSettings.maxLives());
		boolean isUpgradeLevel = false;
		boolean checkFormWidth = origin.formationWidth < 14;
		boolean checkFormHeight = origin.formationHeight < 10;
		boolean checkFormation = origin.formationWidth == origin.formationHeight && checkFormWidth;
		CheckLambda checkSpeed = (int inc, int threshold) -> origin.baseSpeed - inc > threshold;
		CheckLambda checkShootInterval = (int inc, int threshold) -> origin.shootInterval - inc > threshold;

		int tempFormationWidth = origin.formationWidth;
		int tempFormationHeight = origin.formationHeight;
		int tempBaseSpeed = origin.baseSpeed;
		int tempShootInterval = origin.shootInterval;

		int speedInc = 10;
		int speedThreshold = -150;
		int shootIntervalInc = 100;
		int shootIntervalThreshold = 100;
		int baseShootInterval = 100;
		int formInc = 1;

		switch (gameSettings.difficulty()) {
			case 0 -> {
				if ((level%3 == 0 && level < 5) || (level % 2 == 0 && level >= 5) ) isUpgradeLevel = true;
			}
			case 1 -> {
				shootIntervalInc = 200;
				shootIntervalThreshold = 200;
				if(level % 2 == 0)  isUpgradeLevel = true;
				else if (level >= 5) {
					isUpgradeLevel = true;
					speedInc = 20;
				}
			}
			case 2 -> {
				speedInc = 20;
				shootIntervalInc = 300;
				if (level%2 == 0 && level < 5) isUpgradeLevel = true;
				else if (level >= 5) {
					isUpgradeLevel = true;
					shootIntervalInc = 400;
					formInc = 2;
				}
			}
		}

		if (isUpgradeLevel) {
			if (checkFormation) tempFormationWidth += formInc;
			else if (checkFormHeight) tempFormationHeight += formInc;

			tempBaseSpeed = checkSpeed.check(speedInc, speedThreshold) ?
					origin.baseSpeed - speedInc : speedThreshold;

			tempShootInterval = checkShootInterval.check(shootIntervalInc, shootIntervalThreshold) ?
					origin.shootInterval - shootIntervalInc : baseShootInterval;
		}
		this(origin.level + 1, origin.score, (tempBonusLife ? origin.livesRemaining + 1 : origin.livesRemaining),
				origin.bulletsShoot, origin.shipsDestroyed, origin.elapsedTime, tempBonusLife,
				tempFormationWidth, tempFormationHeight, tempBaseSpeed, tempShootInterval,
				origin.maxCombo, origin.prevTime, origin.prevScore, origin.hitBullets);
	}

	public double getAccuracy() {
		if (bulletsShoot == 0){
			return 0;
		}
		return ((double) hitBullets / bulletsShoot) * 100;
	}
}
