package entity;

import java.util.ArrayList;
import java.util.List;

public class RankingService {

    /**
     * Fatches rankings from the server.
     *
     * @return List of RankingEntry objects.
     * @throws Exception If the server communication fails.
     */
    public List<RankingEntry> fetchRankingsFromServer() throws Exception{
        //Replace this with actual HTTP request Logic

        // Simulating server response
        List<RankingEntry> rankings = new ArrayList<>();
        rankings.add(new RankingEntry("ServerUser1", 3000));
        rankings.add(new RankingEntry("ServerUser2", 2950));
        rankings.add(new RankingEntry("ServerUser3", 2900));
        rankings.add(new RankingEntry("ServerUser4", 2850));
        rankings.add(new RankingEntry("ServerUser5", 2800));
        rankings.add(new RankingEntry("ServerUser6", 2750));
        rankings.add(new RankingEntry("ServerUser7", 2700));
        rankings.add(new RankingEntry("ServerUser8", 2650));
        rankings.add(new RankingEntry("ServerUser9", 2600));
        rankings.add(new RankingEntry("ServerUser10", 2550));
        rankings.add(new RankingEntry("ServerUser11", 2500));
        rankings.add(new RankingEntry("ServerUser12", 2450));
        rankings.add(new RankingEntry("ServerUser13", 2400));
        rankings.add(new RankingEntry("ServerUser14", 2350));
        rankings.add(new RankingEntry("ServerUser15", 2300));
        rankings.add(new RankingEntry("ServerUser16", 2250));
        rankings.add(new RankingEntry("ServerUser17", 2200));
        rankings.add(new RankingEntry("ServerUser18", 2150));
        rankings.add(new RankingEntry("ServerUser19", 2100));
        rankings.add(new RankingEntry("ServerUser20", 2050));
        rankings.add(new RankingEntry("ServerUser21", 2000));
        rankings.add(new RankingEntry("ServerUser22", 1950));
        rankings.add(new RankingEntry("ServerUser23", 1900));
        rankings.add(new RankingEntry("ServerUser24", 1850));
        rankings.add(new RankingEntry("ServerUser25", 1800));
        rankings.add(new RankingEntry("ServerUser26", 1750));
        rankings.add(new RankingEntry("ServerUser27", 1700));
        rankings.add(new RankingEntry("ServerUser28", 1650));
        rankings.add(new RankingEntry("ServerUser29", 1600));
        rankings.add(new RankingEntry("ServerUser30", 1550));


        return rankings;
    }
}
