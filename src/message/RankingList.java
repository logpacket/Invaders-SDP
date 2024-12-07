package message;

import engine.network.Body;

import java.util.List;

public record RankingList(List<Ranking> rankings) implements Body {}

