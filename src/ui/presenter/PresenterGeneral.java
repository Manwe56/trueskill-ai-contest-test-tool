package ui.presenter;

import java.util.Map;

import trueskill.Rating;
import ui.threading.ThreadingUtilities;
import ui.view.IViewConfig;
import ui.view.IViewGeneral;
import ui.view.ViewConfig;
import engine.Configuration;
import engine.Leaderboard;
import engine.Leaderboard.LeaderBoardHistoryListener;

public class PresenterGeneral implements IPresenterGeneral{

	private IViewGeneral view;
	private final Map<String, Leaderboard<? extends Rating>> games;
	private Leaderboard<? extends Rating> leaderBoard;
	private final LeaderBoardHistoryListener listener;
	private final Configuration config;
	
	public PresenterGeneral(Map<String, Leaderboard<? extends Rating>> games, Configuration config) {
		this.games = games;
		this.config = config;
		String firstGame = games.keySet().iterator().next();
		leaderBoard = games.get(firstGame);
		listener = new LeaderBoardHistoryListener() {
			@Override
			public void update(Map<String, Double> scores, double sigmaMax, double sigmaMean, double sigmaLimit) {
				view.updateLeaderBoard(scores);
				view.updateSigma(sigmaMean, sigmaMax, sigmaLimit);
			}

			@Override
			public void updateMeans(Map<String, Double> scoresMeans) {
				view.updateMeanLeaderBoard(scoresMeans);
			}

			@Override
			public void computationsFinished() {
				view.computationsFinished();
			}
		};
		for (Leaderboard<? extends Rating> leaderboard : games.values()){
			leaderboard.addLeaderBoardListener(ThreadingUtilities.runInEventQueue(LeaderBoardHistoryListener.class, listener));
		}
	}

	@Override
	public void playUntilRankStabilized() {
		Thread rankingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				leaderBoard.runMatchsUntilRankStabilized();
			}
		});
		rankingThread.start();
	}

	@Override
	public void play100Matchs() {
		Thread rankingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				leaderBoard.runMatchsForEachType(100);
			}
		});
		rankingThread.start();
	}

	@Override
	public void stop() {
		leaderBoard.stop();
	}

	public void setView(IViewGeneral view) {
		this.view = view;
		view.availableGames(games.keySet());
	}

	@Override
	public void reset() {
		leaderBoard.reset();
		view.clear();
	}

	@Override
	public void selectGame(String gameName) {
		Leaderboard<? extends Rating> selected = games.get(gameName);
		if (selected!=leaderBoard){
			leaderBoard.stop();
			reset();
			leaderBoard = selected;
		}
	}

	@Override
	public void editConfiguration() {
		ViewConfig configView = new ViewConfig();
		PresenterConfig configPresenter = new PresenterConfig(config, ThreadingUtilities.runInFX(IViewConfig.class, configView));
		configView.setPresenter(ThreadingUtilities.runInEventQueue(IPresenterConfig.class, configPresenter));
	}

}
