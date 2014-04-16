package ui.view;

import java.util.Collection;
import java.util.Map;

public interface IViewGeneral {
	void updateLeaderBoard(Map<String, Double> seriesNewData);
	void updateMeanLeaderBoard(Map<String, Double> seriesNewData);
	void updateSigma(double sigmaMean, double sigmaMax, double sigmaLimit);
	void clear();
	void computationsFinished();
	void availableGames(Collection<String> gamesNames);
}
