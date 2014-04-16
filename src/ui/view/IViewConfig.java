package ui.view;

import ui.presenter.IPresenterConfig;

public interface IViewConfig {
	void setPresenter(IPresenterConfig presenter);
	void buildUI();
	void setSigmaLimit(double sigmaLimit);
	void setNumberOfStabilityMatchs(int numberOfMatchs);
	void close();
}
