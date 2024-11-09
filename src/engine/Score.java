package engine;

/**
 * Implements a high score record.
 *
 * @param name  Player's name.
 * @param score Score points.
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 */
public record Score(String name, int score) implements Comparable<Score> {
	/**
	 * Orders the scores descending by score.
	 *
	 * @param score Score to compare the current one with.
	 * @return Comparison between the two scores. Positive if the current one is
	 * smaller, positive if its bigger, zero if it is the same.
	 */
	@Override
	public int compareTo(final Score score) {
		return Integer.compare(score.score(), this.score);
	}

	public static int comboScore(int baseScore, int combo) {
		if (combo >= 5)
			return baseScore * (combo / 5 + 1);
		else
			return baseScore;
	}

}
