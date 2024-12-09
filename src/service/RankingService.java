package service;

import engine.network.EventHandler;
import engine.network.ErrorHandler;
import message.HighScore;
import message.Ranking;

public class RankingService extends Service {

    public RankingService() {
        super("ranking");
    }

    /**
     * Fetches rankings from the server.
     *
     * @param successHandler Handler for successful responses.
     * @param errorHandler   Handler for errors.
     */
    public void fetchRankings(EventHandler successHandler, ErrorHandler errorHandler) {
        request(null, successHandler, errorHandler);
    }

    /**
     * Saves a ranking to the server.
     *
     * @param score          The new high score to save.
     */
    public void saveRanking(int score, EventHandler callback, ErrorHandler errorHandler) {

        HighScore highScore = new HighScore(score);
        request(highScore, callback, errorHandler);
    }
}