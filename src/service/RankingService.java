package service;

import engine.network.EventHandler;
import engine.network.ErrorHandler;
import engine.network.NetworkManager;
import message.Ranking;
import message.RankingListResponse;

import java.util.List;

public class RankingService {
    private final NetworkManager networkManager = NetworkManager.getInstance();

    /**
     * Fetches rankings from the server.
     *
     * @param successHandler Handler for successful responses.
     * @param errorHandler   Handler for errors.
     */
    public void fetchRankings(EventHandler successHandler, ErrorHandler errorHandler) {
        networkManager.registerEventHandler("fetchRankings", event -> {
            if (event.body() instanceof RankingListResponse response) {
                successHandler.handle(event); // 성공적인 결과 처리
            } else {
                errorHandler.handle(new message.Error("Invalid data format."));
            }
        });

        networkManager.sendEvent("fetchRankings", null);
    }

    /**
     * Saves a ranking to the server.
     *
     * @param ranking       The ranking to save.
     * @param successHandler Handler for successful responses.
     * @param errorHandler   Handler for errors.
     */
    public void saveRanking(Ranking ranking, EventHandler successHandler, ErrorHandler errorHandler) {
        networkManager.registerEventHandler("saveRanking", eventContext -> successHandler.handle(eventContext));
        networkManager.sendEvent("saveRanking", ranking);
    }
}
