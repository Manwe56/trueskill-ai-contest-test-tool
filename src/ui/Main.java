package ui;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Application;
import javafx.stage.Stage;
import trueskill.Rating;
import trueskill.TrueSkillFactory;
import ui.presenter.IPresenterGeneral;
import ui.presenter.PresenterGeneral;
import ui.threading.ThreadingUtilities;
import ui.view.IViewGeneral;
import ui.view.ViewGeneral;
import engine.Configuration;
import engine.Leaderboard;
import games.ExactSkilledGame;
import games.GaussianProbabilityProvider;
import games.ProbabilisticGame;
import games.SkilledPlayer;
import games.StaticMatrixProbabilityProvider;

public class Main  extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Configuration configuration = new Configuration();
		Map<String, Leaderboard<? extends Rating>> games = new HashMap<>();
		StaticMatrixProbabilityProvider provider = new StaticMatrixProbabilityProvider("PlayersMatchProbabilities.csv");
		games.put("Probabilities from file game", new Leaderboard<>(new ProbabilisticGame<>(provider.getPlayers(), provider), TrueSkillFactory.instance(), "", configuration));
		games.put("Gaussian simulated game", new Leaderboard<>(
				new ProbabilisticGame<SkilledPlayer>(Arrays.asList(
						new SkilledPlayer(0),
						new SkilledPlayer(1),
						new SkilledPlayer(2),
						new SkilledPlayer(3),
						new SkilledPlayer(4),
						new SkilledPlayer(5),
						new SkilledPlayer(6),
						new SkilledPlayer(7),
						new SkilledPlayer(8),
						new SkilledPlayer(9)
						), new GaussianProbabilityProvider()), TrueSkillFactory.instance(), "", configuration));
		games.put("Exact simulated game", new Leaderboard<>(new ExactSkilledGame(10), TrueSkillFactory.instance(), "", configuration));
		PresenterGeneral presenter = new PresenterGeneral(games, configuration);
		ViewGeneral view = new ViewGeneral(ThreadingUtilities.runInEventQueue(IPresenterGeneral.class, presenter), stage);
		presenter.setView(ThreadingUtilities.runInFX(IViewGeneral.class, view));
	}
}
