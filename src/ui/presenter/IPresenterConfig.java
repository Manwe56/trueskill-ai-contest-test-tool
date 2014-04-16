package ui.presenter;

public interface IPresenterConfig {
	void changeSigmaLimit(double sigma);
	void changeNumberOfStabilityMatchs(int matchs);
	void cancel();
	void ok();
}
