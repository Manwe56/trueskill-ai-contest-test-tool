package games;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import trueskill.Rating;
import engine.IGame;
import engine.MatchType;

public class ProbabilisticGame<Player extends Rating> implements IGame<Player>{

	private final List<Player> players = new ArrayList<>();
	private final List<Integer> player1Wins = new ArrayList<>();
	private final List<Integer> player2Wins = new ArrayList<>();
	private final List<Integer> playerDraws = new ArrayList<>();
	private final ProbabilityProvider<Player> probabilityProvider;
	
	public interface ProbabilityProvider<Player>{
		double chancesToWinForPlayer1(Player player1, Player player2);
	}
	
	public ProbabilisticGame(List<Player> players, ProbabilityProvider<Player> probabilityProvider){
		this.probabilityProvider = probabilityProvider;
		this.players.addAll(players);
		player1Wins.add(0);
		player1Wins.add(1);
		player2Wins.add(1);
		player2Wins.add(0);
		playerDraws.add(0);
		playerDraws.add(0);
	}
//	private static void runScenarioOn1kMatchs() {
//		ProbabilisticGame game = new ProbabilisticGame(10, 1);
//		
//		Leaderboard<SkilledPlayer> leaderBoard = new Leaderboard<>(game, TrueSkillFactory.instance(), "leaderBoardWith1kMatchs");
//		
//		leaderBoard.runMatchsForEachType(1000);
//	}
//
//	private static void runScenarioOn500MatchsThenAugmentSkill() {
//		ProbabilisticGame game = new ProbabilisticGame(10, 1);
//		
//		Leaderboard<SkilledPlayer> leaderBoard = new Leaderboard<>(game, TrueSkillFactory.instance(), "leaderBoardWithSkillAugmentationNoReset");
//		
//		leaderBoard.runMatchsForEachType(500);
//		SkilledPlayer player7 = game.getPlayers().get(6);
//		player7.setRealSkill(11);
//		
//		leaderBoard.runMatchsForEachType(500);
//	}
//	
//	private static void runScenarioOn500MatchsThenExclusiveReinsertionOfSameAI() {
//		ProbabilisticGame game = new ProbabilisticGame(10, 1);
//		Leaderboard<SkilledPlayer> leaderBoard = new Leaderboard<>(game, TrueSkillFactory.instance(), "leaderBoardWithReinsertion");
//		
//		leaderBoard.runMatchsForEachType(500);
//		SkilledPlayer newPlayer = new SkilledPlayer(0);
//		SkilledPlayer player9 = game.getPlayers().get(8);
//		player9.setMu(newPlayer.getMu());
//		player9.setSigma(newPlayer.getSigma());
//		leaderBoard.runMatchsForEachTypeWithSeed(500, player9);
//	}
	
	@Override
	public List<MatchType> getMatchTypes() {
		return Arrays.asList(MatchType.FFA2);
	}

	@Override
	public List<Player> getPlayers() {
		return players;
	}

	@Override
	public List<Integer> runMatch(List<List<Player>> teams) {
		Player player1 = teams.get(0).get(0);
		Player player2 = teams.get(1).get(0);
		
		double random = Math.random();
		
		if (random>probabilityProvider.chancesToWinForPlayer1(player1, player2))
			return player2Wins;
		return player1Wins;
	}

	@Override
	public void reset() {
		for (Player player : players){
			player.resetSkill();
		}
	}
}
