package engine;

import static org.junit.Assert.assertTrue;
import games.ExactSkilledGame;
import games.GaussianProbabilityProvider;
import games.SkilledPlayer;
import games.ProbabilisticGame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

import trueskill.TrueSkillFactory;

public class LeaderBoardTest {
	
	@Test
	public void tournamentFFA234_OnPredictableGame(){
		ExactSkilledGame game = new ExactSkilledGame(10);
		
		Leaderboard<SkilledPlayer> leaderBoard = new Leaderboard<>(game, TrueSkillFactory.instance(), "TestExactSkilledLeaderBoard", new Configuration());
		
		leaderBoard.runMatchsUntilRankStabilized();
		
		List<SkilledPlayer> expected = new ArrayList<>(game.getPlayers());
		Collections.sort(expected, new Comparator<SkilledPlayer>(){
			@Override
			public int compare(SkilledPlayer o1, SkilledPlayer o2) {
				if (o1.getRealSkill()>o2.getRealSkill())
					return -1;
				return 1;
			}
		});
		
		List<SkilledPlayer> rankings = leaderBoard.ranking();
		
		System.out.println("Ranking="+rankings);
		for (SkilledPlayer player : rankings){
			assertTrue("Player"+player+" incorrectly ranked to:"+rankings.indexOf(player)+" instead of:"+expected.indexOf(player), Math.abs(rankings.indexOf(player) - expected.indexOf(player))<2);
		}
	}
	
	@Test
	public void tournamentFFA2_OnPredictableWithRandomGame(){
		List<SkilledPlayer> players = new ArrayList<>();
		for (int i=0; i<10; i++){
			players.add(new SkilledPlayer(i));
		}
		ProbabilisticGame<SkilledPlayer> game = new ProbabilisticGame<SkilledPlayer>(players, new GaussianProbabilityProvider());
		
		Leaderboard<SkilledPlayer> leaderBoard = new Leaderboard<>(game, TrueSkillFactory.instance(), "TestRandomSkilledLeaderBoard", new Configuration());
		
		leaderBoard.runMatchsUntilRankStabilized();
		
		List<SkilledPlayer> expected = new ArrayList<>(game.getPlayers());
		Collections.sort(expected, new Comparator<SkilledPlayer>(){
			@Override
			public int compare(SkilledPlayer o1, SkilledPlayer o2) {
				if (o1.getRealSkill()<o2.getRealSkill())
					return 1;
				return -1;
			}
		});
		
		List<SkilledPlayer> rankings = leaderBoard.ranking();
		
		System.out.println("Ranking="+rankings);
		for (SkilledPlayer player : rankings){
			assertTrue("Player"+player+" incorrectly ranked to:"+rankings.indexOf(player)+" instead of:"+expected.indexOf(player), Math.abs(rankings.indexOf(player) - expected.indexOf(player))<2);
		}
	}
}
