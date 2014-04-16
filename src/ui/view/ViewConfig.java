package ui.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ui.presenter.IPresenterConfig;

public class ViewConfig implements IViewConfig{

	private IPresenterConfig presenter;
	private TextField sigma;
	private Stage stage;
	private TextField matchs;

	@Override
	public void buildUI(){
		stage = new Stage();
		stage.setTitle("Configuration");
		stage.setScene(new Scene(createUIContent()));
		stage.show();
	}
	
	private Parent createUIContent() {
		GridPane grid = new GridPane();
		grid.add(new Label("Maximum trueskill sigma for contributing to scores means:"), 0, 0);
		sigma = new TextField();
		sigma.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
	         public void handle( KeyEvent t ) {
	             char ar[] = t.getCharacter().toCharArray();
	             char ch = ar[t.getCharacter().toCharArray().length - 1];
	             if (!(ch >= '0' && ch <= '9') && ch!='.') {
	                 t.consume();
	             }
	          }
	       });
		sigma.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (!arg2.isEmpty())
					presenter.changeSigmaLimit(Double.parseDouble(arg2));
			}
		});
		grid.add(sigma, 1, 0, 2, 1);
		grid.add(new Label("Number of matchs with a stable leaderboard to consider rankings are definitive:"), 0, 1);
		matchs = new TextField();
		matchs.addEventFilter(KeyEvent.KEY_TYPED, new EventHandler<KeyEvent>() {
	         public void handle( KeyEvent t ) {
	             char ar[] = t.getCharacter().toCharArray();
	             char ch = ar[t.getCharacter().toCharArray().length - 1];
	             if (!(ch >= '0' && ch <= '9')) {
		             t.consume();
	             }
	          }
	       });
		matchs.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				if (!arg2.isEmpty())
					presenter.changeNumberOfStabilityMatchs(Integer.parseInt(arg2));
			}
		});
		grid.add(matchs, 1, 1, 2, 1);
		Button okButton = new Button("OK");
		okButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.ok();
			}
		});
		grid.add(okButton, 1, 2);
		final Hyperlink cancelLink = new Hyperlink("Cancel");
		cancelLink.setUnderline(true);
		cancelLink.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				presenter.cancel();
				cancelLink.setVisited(false);
			}
		});
		GridPane.setHalignment(cancelLink, HPos.RIGHT);
		grid.add(cancelLink, 2, 2);
		return grid;
	}

	@Override
	public void setSigmaLimit(double sigmaLimit) {
		sigma.setText(Double.toString(sigmaLimit));
	}

	@Override
	public void setNumberOfStabilityMatchs(int numberOfMatchs) {
		matchs.setText(Integer.toString(numberOfMatchs));
	}

	@Override
	public void setPresenter(IPresenterConfig presenter) {
		this.presenter = presenter;
	}

	@Override
	public void close() {
		stage.close();
	}

}
