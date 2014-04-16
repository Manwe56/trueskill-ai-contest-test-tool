package engine;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import trueskill.ITrueSkill;
import trueskill.Rating;

public class Leaderboard<Player extends Rating> {

	private final Configuration configuration;
	private final IGame<Player> game;
	private final ITrueSkill trueSkill;
	private final Map<MatchType, Map<Player, Integer>> matchPlayed;
	private final String historyFileName;
	private final Map<Player, List<Double>> scoresHistory = new HashMap<>();
	private final Map<Player, List<Double>> scoresMeanHistory = new HashMap<>();
	private int leaderBoardRefreshRate = 1;
	private int leaderBoardNextRefresh = leaderBoardRefreshRate;
	private int matchPlayedCount = 0;
	private boolean stop=false;
	private List<LeaderBoardHistoryListener> listeners = new ArrayList<>();
	
	public interface LeaderBoardHistoryListener{
		void update(Map<String, Double> scores, double sigmaMax, double sigmaMean, double sigmaLimit);
		void updateMeans(Map<String, Double> scoresMeans);
		void computationsFinished();
	}
	
	public Leaderboard(IGame<Player> game, ITrueSkill trueSkill, String historyFileName, Configuration configuration) {
		this.game = game;
		this.trueSkill = trueSkill;
		this.configuration = configuration;
		this.matchPlayed = new HashMap<>();
		this.historyFileName = historyFileName;
		initMatchPlayed();
		initScoresHistory();
		writeHistoryHeader();
	}

	private void initScoresHistory() {
		for (Player player : game.getPlayers()){
			scoresHistory.put(player, new ArrayList<Double>());
			scoresMeanHistory.put(player, new ArrayList<Double>());
		}
	}

	private void initMatchPlayed() {
		for (MatchType type : game.getMatchTypes()){
			Map<Player, Integer> typeMatchs = new HashMap<>();
			for (Player rating : game.getPlayers()){
				typeMatchs.put(rating, 0);
			}
			matchPlayed.put(type, typeMatchs);
			if (game.getMatchTypes().size()>1)
				writeHistoryHeader(type);
		}
	}

	private void writeHistoryHeader() {
		if (historySavedToFile()){
			writeHeaderToFile(historyFileName);
		}
	}

	private void writeHistoryHeader(MatchType type) {
		if (historySavedToFile()){
			String fileName = historyFileName(type);
			writeHeaderToFile(fileName);
		}
	}

	private void writeHeaderToFile(String fileName) {
		writeToCSV(fileName, historyHeader(), false);
	}

	private void writeToCSV(String fileName, String historyHeader, boolean append) {
		writeToFile(fileName+".csv", historyHeader, append);
	}

	private void writeToFile(String fileName, String content, boolean append) {
		try {
			FileOutputStream leaderBoardFile = new FileOutputStream(fileName, append);
			leaderBoardFile.write(content.getBytes());
			leaderBoardFile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String historyFileName(MatchType type) {
		return historyFileName+type.toString();
	}

	private boolean historySavedToFile() {
		return historyFileName!=null && !historyFileName.isEmpty();
	}

	private String historyHeader() {
		String history = "";
		boolean first = true;
		for (Player p : game.getPlayers()){
			if (!first)
				history+=";";
			history+=p.toString();
			first = false;
		}
		history+="\n";
		return history;
	}
	
	public void runMatchsUntilRankStabilized(){
		stop=false;
		while (!rankStabilized() && !stop){
			runMatchsForEachTypeWithSeed(1, null);
		}
		computationsFinished();
	}

	private void computationsFinished() {
		for (LeaderBoardHistoryListener listener : listeners){
			listener.computationsFinished();
		}
		stop=false;
	}
	
	private boolean rankStabilized() {
		if (scoresHistory.values().iterator().next().size()<configuration.getRankNumberOfStableLeaderBoard())
			return false;
		List<Player> ranks = extractRanksAt(0);
		for (int i=1; i<configuration.getRankNumberOfStableLeaderBoard(); i++){
			List<Player> ranksAtI = extractRanksAt(i);
			for (int player=0; player<ranks.size(); player++){
				if (ranks.get(player)!=ranksAtI.get(player))
					return false;
			}
		}
		return true;
	}

	private List<Player> extractRanksAt(final int numberOfIterationsFromEnd) {
		List<Player> players = new ArrayList<>(game.getPlayers());
		
		Collections.sort(players, new Comparator<Player>() {
			@Override
			public int compare(Player p1, Player p2) {
				double p1Score = scoreAt(p1, numberOfIterationsFromEnd);
				double p2Score = scoreAt(p2, numberOfIterationsFromEnd);
				if (p1Score>p2Score)
					return 1;
				if (p2Score>p1Score)
					return -1;
				return 0;
			}

			private double scoreAt(Player player, int numberOfIterationsFromEnd) {
				List<Double> scoresMeans = scoresMeanHistory.get(player);
				return scoresMeans.get(scoresMeans.size() - numberOfIterationsFromEnd -1);
			}
		});
		
		return players;
	}

	public void runMatchsForEachType(int numberOfMatchsPerPlayer) {
		runMatchsForEachTypeWithSeed(numberOfMatchsPerPlayer, null);
		computationsFinished();
	}
	
	public void runMatchsForEachTypeWithSeed(int numberOfMatchPerPlayer, Player seed){
		updateNextLeaderBoardRefresh();
		IMatchPicker<Player> picker = new MatchPicker<>();
		int nextMatchPlayedStop = numberOfMatchPerPlayer+minimumMatchPlayed();
		boolean leaderBoardNotUpdated = false;
		while (minimumMatchPlayed()<nextMatchPlayedStop && !stop){
			for (MatchType type : matchPlayed.keySet()){
				leaderBoardNotUpdated = true;
				Map<Player, Integer> typePlayedMatch = matchPlayed.get(type);
				List<List<Player>> teams = null;
				if (seed!=null)
					teams = picker.pickMatchWithSeed(type, typePlayedMatch, seed);
				else
					teams = picker.pickMatch(type, typePlayedMatch);
				try{
					List<Integer> ranks = game.runMatch(teams);
					System.out.println("Match selected:"+teams+" result:"+ranks);
					rate(teams, ranks, trueSkill, type);
					matchPlayedCount++;
					updateMatchPlayed(teams, typePlayedMatch);
				}
				catch (Exception e){
					System.out.println("Crash occured during match:"+teams);
					e.printStackTrace();
				}
			}
			if (seed==null){
				if (minimumMatchPlayed()==leaderBoardNextRefresh){
					updateLeaderBoard();
					leaderBoardNotUpdated = false;
					updateNextLeaderBoardRefresh();
				}
			}
			else{
				leaderBoardNotUpdated = false;
				updateLeaderBoard();
			}
		}
		if (leaderBoardNotUpdated)
			updateLeaderBoard();
	}

	private void updateNextLeaderBoardRefresh() {
		leaderBoardNextRefresh = minimumMatchPlayed()+leaderBoardRefreshRate;
	}

	private int minimumMatchPlayed() {
		Map<Player, Integer> playedMatchs = new HashMap<>();
		for (Player player : game.getPlayers()){
			playedMatchs.put(player, 0);
		}
		for (Map<Player, Integer> matchPlayedPerType : matchPlayed.values()){
			for (Player player : matchPlayedPerType.keySet()){
				playedMatchs.put(player, playedMatchs.get(player)+matchPlayedPerType.get(player));
			}
		}
		int min = Integer.MAX_VALUE;
		for (Player player : game.getPlayers()){
			if (playedMatchs.get(player)<min)
				min = playedMatchs.get(player);
		}
		return min;
	}

	private void rate(List<List<Player>> teams, List<Integer> ranks, ITrueSkill trueSkill, MatchType type) {
		//Rate general
		List<List<? extends Rating>> allTypesTeams = new ArrayList<>();
		for (List<Player> team : teams){
			allTypesTeams.add(new ArrayList<Rating>(team));
		}
		trueSkill.rate(allTypesTeams, ranks);
		List<List<? extends Rating>> typedTeams = new ArrayList<>();
		for (List<Player> team : teams){
			ArrayList<Rating> typedRatings = new ArrayList<Rating>();
			for (Player player : team){
				typedRatings.add(player.typedRating(type));
			}
			typedTeams.add(typedRatings);
		}
		trueSkill.rate(typedTeams, ranks);
	}
	
	private void updateLeaderBoard() {
		if (varianceIsStable()){
			for (Player player : game.getPlayers()){
				scoresHistory.get(player).add(player.getSkill());
				scoresMeanHistory.get(player).add(scoreMean(player));
			}
			notifyUpdateMeans();
		}
		if (historySavedToFile()){
			System.out.println("updating leaderboard. Match played:"+matchPlayedCount);
			updateLeaderBoardHistoryWithRatings(historyFileName, new ArrayList<Rating>(game.getPlayers()));
			if (game.getMatchTypes().size()>1){
				for (MatchType matchType : game.getMatchTypes()){
					updateLeaderBoardHistoryWithRatings(historyFileName(matchType), ratingsByType(matchType));
				}
			}
		}
		notifyUpdateLeaderBoard();
	}

	private void notifyUpdateMeans() {
		Map<String, Double> newMeans = new HashMap<>();
		for (Player player : game.getPlayers()){
			newMeans.put(player.toString(), scoresMeanHistory.get(player).get(scoresMeanHistory.get(player).size()-1));
		}
		for (LeaderBoardHistoryListener listener : listeners){
			listener.updateMeans(newMeans);
		}
	}

	private void notifyUpdateLeaderBoard() {
		double maxSigma = 0;
		double meanSigma = 0;
		Map<String, Double> scores = new HashMap<>();
		for (Player player : game.getPlayers()){
			scores.put(player.toString(), player.getSkill());
			double playerSigma = player.getSigma();
			meanSigma+=playerSigma;
			if (playerSigma>maxSigma)
				maxSigma = playerSigma;
		}
		meanSigma = meanSigma/game.getPlayers().size();
		for (LeaderBoardHistoryListener listener : listeners){
			listener.update(scores, maxSigma, meanSigma, configuration.getSigmaStabilization());
		}
	}

	private boolean varianceIsStable() {
		for (Player player : game.getPlayers()){
			if (player.getSigma()>configuration.getSigmaStabilization()){
				System.out.println("Sigma not enough low:"+player.getSigma());
				return false;
			}
		}
		return true;
	}

	private double scoreMean(Player player) {
		if (scoresHistory.get(player).size()==0)
			return 0;
		double total = 0;
		for (double score : scoresHistory.get(player)){
			total+=score;
		}
		
		return total/scoresHistory.get(player).size();
	}

	private List<Rating> ratingsByType(MatchType matchType) {
		List<Rating> ratings = new ArrayList<>();
		for (Player player : game.getPlayers()){
			ratings.add(player.typedRating(matchType));
		}
		return ratings;
	}

	private void updateLeaderBoardHistoryWithRatings(String fileName, List<Rating> players) {
		boolean first = true;
		String history = "";
		for (Rating p : players){
			if (!first)
				history+=";";
			history+=p.getSkill();
			first = false;
		}
		history+="\n";
		writeToCSV(fileName, history, true);
	}

	private void updateMatchPlayed(List<List<Player>> teams, Map<Player, Integer> typePlayedMatch) {
		for (List<Player> team : teams){
			for (Player player : team){
				typePlayedMatch.put(player, typePlayedMatch.get(player)+1);
			}
		}
	}

	public List<Player> ranking() {
		List<Player> ranking = new ArrayList<>(game.getPlayers());
		Collections.sort(ranking);
		return ranking;
	}

	public void addLeaderBoardListener(LeaderBoardHistoryListener listener) {
		listeners.add(listener);
	}
	
	public void removeLeaderBoardListener(LeaderBoardHistoryListener listener) {
		listeners.remove(listener);
	}

	public void reset() {
		stop=false;
		game.reset();
		matchPlayedCount = 0;
		leaderBoardNextRefresh = leaderBoardRefreshRate;
		initMatchPlayed();
		initScoresHistory();
	}

	public synchronized void stop() {
		stop=true;
	}
}
