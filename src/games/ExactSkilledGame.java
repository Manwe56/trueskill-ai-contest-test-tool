package games;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import trueskill.TrueSkillFactory;
import engine.Configuration;
import engine.IGame;
import engine.Leaderboard;
import engine.MatchType;

public class ExactSkilledGame implements IGame<SkilledPlayer>{

	private final List<SkilledPlayer> players = new ArrayList<>();
	public ExactSkilledGame(int playerNumber){
		for (int i=0; i<playerNumber; i++){
			players.add(new SkilledPlayer(i));
		}
	}
	
	@Override
	public List<MatchType> getMatchTypes() {
		return Arrays.asList(MatchType.FFA2/*, MatchType.FFA3, MatchType.FFA4*/);
	}

	public static void main(String[] args) {
		ExactSkilledGame game = new ExactSkilledGame(10);
		Leaderboard<SkilledPlayer> leaderBoard = new Leaderboard<>(game, TrueSkillFactory.instance(), "ExactSkilledLeaderBoard", new Configuration());
		
		leaderBoard.runMatchsForEachType(1000);
	}
	
	@Override
	public void reset() {
		for (SkilledPlayer player : players){
			player.resetSkill();
		}
	}
	
	@Override
	public List<SkilledPlayer> getPlayers() {
		return players;
	}

	@Override
	public List<Integer> runMatch(List<List<SkilledPlayer>> teams) {
		List<Integer> ranks = new ArrayList<>();
		List<SkilledPlayer> orderedPlayers = new ArrayList<>();
		for (List<SkilledPlayer> team : teams){
			orderedPlayers.addAll(team);
		}
		Collections.sort(orderedPlayers, new Comparator<SkilledPlayer>(){
			@Override
			public int compare(SkilledPlayer o1, SkilledPlayer o2) {
				if (o1.getRealSkill()>o2.getRealSkill())
					return -1;
				return 1;
			}
		});
		for (List<SkilledPlayer> team : teams){
			ranks.add(orderedPlayers.indexOf(team.get(0)));
		}
		return ranks;
	}
}
