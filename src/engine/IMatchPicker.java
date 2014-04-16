package engine;

import java.util.List;
import java.util.Map;

import trueskill.Rating;

public interface IMatchPicker<Player extends Rating> {
	List<List<Player>> pickMatch(MatchType type, Map<Player, Integer> playedMatchs);
	List<List<Player>> pickMatchWithSeed(MatchType type, Map<Player, Integer> playedMatchs, Player seed);
}
