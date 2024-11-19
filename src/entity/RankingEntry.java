package entity;

public class RankingEntry {
    private final String userId;
    private final int highScore;

    public RankingEntry(String userId, int highScore){
        this.userId = userId;
        this.highScore = highScore;
    }

    public String getUserId() {
        return userId;
    }

    public int getHighScore() {
        return highScore;
    }
}
