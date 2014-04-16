package ui.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.FontBuilder;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import ui.presenter.IPresenterGeneral;

public class ViewGeneral implements IViewGeneral {

	private final IPresenterGeneral presenter;
	private final ChartXY liveChart;
	private final ChartXY meanChart;
	private final ChartXY sigmaChart;
	private final ObservableList<String> rankings;
	private final ObservableList<String> games;
	private final Set<String> rankingIncreased = new HashSet<>();
	private final Set<String> rankingDecreased = new HashSet<>();
	private Button start;
	private Button play100;
	private Button stopReset;
	private ComboBox<String> gamesBox;
	
	private enum ButtonsState{
		Initial,
		Playing,
		Finished
	}
	
	public ViewGeneral(IPresenterGeneral presenter, Stage stage) {
		this.presenter = presenter;
		liveChart = new ChartXY("TrueSkill scores", "games played", "score");
		meanChart = new ChartXY("TrueSkill mean scores", "games played with sigma acceptable", "score");
		sigmaChart = new ChartXY("TrueSkill Sigma", "games played", "sigma");
		rankings = FXCollections.observableArrayList();
		games = FXCollections.observableArrayList();
		buildUI(stage);
	}

	private static class ChartXY{
		private final LineChart<Number,Number> chart;
		private int currentUpdate = 1;
		private final Map<String, XYChart.Series<Number, Number>> series = new HashMap<>();
		
		public ChartXY(String title, String xAxisLabel, String yAxisLabel){
			NumberAxis xAxis = new NumberAxis();
	        NumberAxis yAxis = new NumberAxis();
	        xAxis.setLabel(xAxisLabel);
	        xAxis.setLowerBound(0);
	        xAxis.setUpperBound(20);
	        yAxis.setLabel(yAxisLabel);
	        yAxis.setLowerBound(-5);
	        yAxis.setUpperBound(15);
	        chart = new LineChart<Number,Number>(xAxis,yAxis);
	        chart.setTitle(title);
		}
		public void updateChart(Map<String, Double> newValues) {
			if (series.isEmpty()){
				List<String> orderedNames = new ArrayList<>(newValues.keySet());
				Collections.sort(orderedNames);
				for (String serieName : orderedNames){
					XYChart.Series<Number, Number> serie = new XYChart.Series<>();
			        serie.setName(serieName);
			        series.put(serieName, serie);
			        chart.getData().add(serie);
				}
			}
			for (String serieName : newValues.keySet()){
				XYChart.Series<Number, Number> serie = series.get(serieName);
				serie.getData().add(new XYChart.Data<Number, Number>(currentUpdate, newValues.get(serieName)));
			}
			currentUpdate++;
		}
		
		public LineChart<Number, Number> getChart(){
			return chart;
		}
		
		public void clear() {
			for (XYChart.Series<Number, Number> serie : series.values()){
				serie.getData().clear();
			}
			chart.getData().clear();
			series.clear();
			currentUpdate=1;
		}
	}
	
	private void buildUI(Stage stage) {
        stage.setTitle("TrueSkill rankings");
        
        BorderPane border = new BorderPane();
        border.setCenter(buildCharts());
        border.setTop(buildHeader());
        Node leaderBoard = buildLeaderBoard();
        border.setRight(leaderBoard);
        
        Scene scene  = new Scene(border,1200,600);
        
        stage.setScene(scene);
        stage.show();
    }

	private Node buildLeaderBoard() {
		BorderPane border = new BorderPane();
		
		Label title = new Label("Mean Leaderboard");
		title.setAlignment(Pos.CENTER);
		BorderPane.setAlignment(title, Pos.CENTER);
		title.setFont(FontBuilder.create().size(16).build());
		
		border.setTop(title);
		
		ListView<String> listView = new ListView<String>(rankings);
        listView.setPrefWidth(300);
        listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> listView) {
				return new ListCell<String>() {
					private final Timeline ti = new Timeline();
					private final double animDuration = 600;
					
					@Override
				    protected void updateItem(String t, boolean bln) {
				        super.updateItem(t, bln);
				        if (t == null) {
				            return;
				        }
				        setText(t);
				        if (rankingDecreased.contains(t)){
				        	animate(t, animTop(), rankingDecreased);
				        }
				    }
				    private void animate(String text, KeyFrame[] keyFrames, Set<String> rankingSet) {
				        if ((ti.getStatus() == Timeline.Status.STOPPED
				                || ti.getStatus() == Timeline.Status.PAUSED)) {
				            if (ti.getKeyFrames().isEmpty())
				            	ti.getKeyFrames().addAll(keyFrames);
				        	rankingSet.remove(text);
				            ti.playFromStart();
				        }
				    }
					private KeyFrame[] animTop(){
						return new KeyFrame[]{
					            new KeyFrame(Duration.millis(0),
					            new KeyValue(translateYProperty(), Math.max(getHeight(), 50))),
					            new KeyFrame(Duration.millis(animDuration), new KeyValue(translateYProperty(), 0)),};
					}
				};
			}
		});
        border.setCenter(listView);
		return border;
	}

	private Node buildHeader() {
		GridPane grid= new GridPane();
		start = new Button("Play until rank stabilized");
		start.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updateButtons(ButtonsState.Playing);
				presenter.playUntilRankStabilized();
			}
		});
		play100 = new Button("Play 100 matchs for each player");
		play100.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				updateButtons(ButtonsState.Playing);
				presenter.play100Matchs();
			}
		});
		stopReset = new Button("Stop");
		stopReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (stopReset.getText().equals("Stop")){
					updateButtons(ButtonsState.Finished);
					presenter.stop();
				}
				else{
					updateButtons(ButtonsState.Initial);
					presenter.reset();
				}
			}
		});
		stopReset.setDisable(true);

		final Hyperlink editConfig = new Hyperlink("Edit configuration");
		editConfig.setUnderline(true);
		editConfig.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.editConfiguration();
				editConfig.setVisited(false);
			}
		});
		gamesBox = new ComboBox<>(games);
		
		gamesBox.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				presenter.selectGame(gamesBox.getSelectionModel().getSelectedItem());
			}
		});
		
		int index=0;
		Insets insets = new Insets(5);
		
		Label gameLabel = new Label("Game:");
		grid.add(gameLabel, index++, 0);
		GridPane.setMargin(gameLabel, insets);
		
		grid.add(gamesBox, index++, 0);
		GridPane.setMargin(gamesBox, insets);
		
		Label spacerLabel = new Label("");
		grid.add(spacerLabel, index++, 0);
		GridPane.setHgrow(spacerLabel, Priority.ALWAYS);
		
		grid.add(start, index++, 0);
		GridPane.setMargin(start, insets);
		
		grid.add(play100, index++, 0);
		GridPane.setMargin(play100, insets);
		
		grid.add(stopReset, index++, 0);
		GridPane.setMargin(stopReset, insets);
		
		grid.add(editConfig, index++, 0);
		GridPane.setMargin(editConfig, insets);
		
		return grid;
	}

	private TabPane buildCharts() {
		TabPane tabs = new TabPane();
		
		tabs.getTabs().add(createTab(liveChart.getChart(), "Scores"));
		tabs.getTabs().add(createTab(meanChart.getChart(), "Means"));
		tabs.getTabs().add(createTab(sigmaChart.getChart(), "Sigma"));

		return tabs;
	}
	
	private Tab createTab(LineChart<Number, Number> chart, String text) {
		Tab tab = new Tab(text);
		tab.setClosable(false);
		tab.setContent(chart);
		return tab;
	}

	@Override
	public void updateLeaderBoard(Map<String, Double> newValues) {
		liveChart.updateChart(newValues);
	}

	@Override
	public void updateMeanLeaderBoard(final Map<String, Double> seriesNewData) {
		List<String> rankingsSorted = new ArrayList<>(seriesNewData.keySet());
		Collections.sort(rankingsSorted, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				double value1 = seriesNewData.get(o1);
				double value2 = seriesNewData.get(o2);
				if (value1>value2)
					return -1;
				else if (value1<value2)
					return 1;
				return 0;
			}
		});
		if (rankings.isEmpty()){
			rankings.addAll(rankingsSorted);
		}
		else{
			for (int i=0; i<rankings.size(); i++){
				String value = rankingsSorted.get(i);
				if (!rankings.get(i).equals(value)){
					int previousRank = rankings.indexOf(value);
					if (previousRank>i)
						rankingDecreased.add(value);
					if (previousRank<i)
						rankingIncreased.add(value);
					
					rankings.set(i, value);
				}
			}
		}
			
		meanChart.updateChart(seriesNewData);
	}

	@Override
	public void updateSigma(double sigmaMean, double sigmaMax, double sigmaLimit) {
		Map<String, Double> newValues = new HashMap<>();
		newValues.put("Sigma mean", sigmaMean);
		newValues.put("Sigma max", sigmaMax);
		newValues.put("Sigma limit", sigmaLimit);
		sigmaChart.updateChart(newValues);
	}

	@Override
	public void clear() {
		liveChart.clear();
		sigmaChart.clear();
		meanChart.clear();
		rankings.clear();
	}

	@Override
	public void availableGames(Collection<String> gamesNames) {
		games.clear();
		games.addAll(gamesNames);
		gamesBox.getSelectionModel().selectFirst();
	}

	@Override
	public void computationsFinished() {
		updateButtons(ButtonsState.Finished);
	}

	private void updateButtons(ButtonsState state) {
		switch(state){
		case Finished:
			stopReset.setText("Reset");
			stopReset.setDisable(false);
			start.setDisable(false);
			play100.setDisable(false);
			gamesBox.setDisable(true);
			break;
		case Initial:
			stopReset.setText("Stop");
			stopReset.setDisable(true);
			start.setDisable(false);
			play100.setDisable(false);
			gamesBox.setDisable(false);
			break;
		case Playing:
			stopReset.setText("Stop");
			stopReset.setDisable(false);
			start.setDisable(true);
			play100.setDisable(true);
			gamesBox.setDisable(true);
			break;
		default:
			break;
		}
	}
}
