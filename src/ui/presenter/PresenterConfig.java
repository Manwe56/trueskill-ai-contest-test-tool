package ui.presenter;

import ui.view.IViewConfig;
import engine.Configuration;

public class PresenterConfig implements IPresenterConfig{
	private final Configuration originalConfig;
	private final Configuration config;
	private final IViewConfig view;

	public PresenterConfig(Configuration config, IViewConfig view) {
		this.originalConfig = config;
		this.view = view;
		this.config = new Configuration(originalConfig);
		view.buildUI();
		view.setSigmaLimit(originalConfig.getSigmaStabilization());
		view.setNumberOfStabilityMatchs(originalConfig.getRankNumberOfStableLeaderBoard());
	}

	@Override
	public void changeSigmaLimit(double sigma) {
		System.out.println("Sigma changed : "+sigma);
		config.setSigmaStabilization(sigma);
	}

	@Override
	public void changeNumberOfStabilityMatchs(int matchs) {
		System.out.println("matchs changed : "+matchs);
		config.setRankNumberOfStableLeaderBoard(matchs);
	}

	@Override
	public void cancel() {
		view.close();
	}

	@Override
	public void ok() {
		originalConfig.setRankNumberOfStableLeaderBoard(config.getRankNumberOfStableLeaderBoard());
		originalConfig.setSigmaStabilization(config.getSigmaStabilization());
		view.close();
	}
		
}
