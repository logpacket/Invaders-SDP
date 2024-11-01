package entity;

public class Achievement {

    private int totalPlayTime;
    private int totalScore;
    public int maxCombo;
    public int currentPerfectStage;
    public boolean flawlessFailure;

    public Achievement(int totalPlayTime, int totalScore, int maxCombo, int currentPerfectStage,
                       boolean flawlessFailure) {
        this.totalPlayTime = totalPlayTime;
        this.totalScore = totalScore;
        this.maxCombo = maxCombo;
        this.currentPerfectStage = currentPerfectStage;
        this.flawlessFailure = flawlessFailure;
    }

    // Functions to get the status of each achievement.
    public int getTotalPlayTime() { return totalPlayTime; }
    public int getTotalScore() { return totalScore; }

    // Functions to store the status of each achievement.
    public void setTotalPlayTime(int totalPlayTime) {
        this.totalPlayTime += totalPlayTime;
    }
    public void setTotalScore(int totalScore) {
        this.totalScore += totalScore;
    }
}