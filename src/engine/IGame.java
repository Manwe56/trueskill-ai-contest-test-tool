package engine;

import java.util.List;

import trueskill.Rating;

public interface IGame<Player extends Rating> {
	List<MatchType> getMatchTypes();
	List<Player> getPlayers();
	List<Integer> runMatch(List<List<Player>> teams);//Return ranks for each team
	void reset();
}
