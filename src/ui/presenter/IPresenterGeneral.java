package ui.presenter;

public interface IPresenterGeneral {
	void playUntilRankStabilized();
	void stop();
	void reset();
	void selectGame(String gameName);
	void play100Matchs();
	void editConfiguration();
}
