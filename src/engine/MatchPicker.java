package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import trueskill.Rating;

public class MatchPicker<Player extends Rating> implements IMatchPicker<Player>{

	@Override
	public List<List<Player>> pickMatch(MatchType type, final Map<Player, Integer> playedMatchs) {
		List<Player> players = new ArrayList<>(playedMatchs.keySet());
		Collections.sort(players, new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				return playedMatchs.get(o1)- playedMatchs.get(o2);
			}
		});
		Player seed = players.get(0);
		return selectPlayers(type, players, seed);
	}

	private List<List<Player>> selectPlayers(MatchType type, List<Player> candidates, Player seed) {
		Collections.sort(candidates, new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				if (o1.getSkill()> o2.getSkill())
					return 1;
				if (o2.getSkill()> o1.getSkill())
					return -1;
				return 0;
			}
		});
		
		int seedIndex = candidates.indexOf(seed);
		
		List<Player> possibleCandidates = candidates.subList(Math.max(0, seedIndex-5), Math.min(seedIndex+5, candidates.size()));
		
		possibleCandidates.remove(seed);
		
		Collections.shuffle(possibleCandidates);
		
		int playersToSelect = 0;
		switch (type){
		case FFA2:
			playersToSelect=1;
			break;
		case FFA3:
			playersToSelect=2;
			break;
		case FFA4:
			playersToSelect=3;
			break;
		}
		
		List<Player> players = possibleCandidates.subList(0, playersToSelect);
		
		List<List<Player>> teams = new ArrayList<>();
		teams.add(Arrays.asList(seed));
		for (Player player : players){
			teams.add(Arrays.asList(player));
		}
		
		return teams;
	}

	@Override
	public List<List<Player>> pickMatchWithSeed(MatchType type, Map<Player, Integer> playedMatchs, Player seed) {
		return selectPlayers(type, new ArrayList<>(playedMatchs.keySet()), seed);
	}
}
