package org.luke.mex.app.pages.explorer.navigate.top;

import java.io.File;
import java.util.function.Consumer;

import org.luke.mex.gui.controls.space.Separator;
import org.luke.mex.gui.window.Window;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

public class TopNav extends HBox {
	private HBox left;
	
	private NavigateButton goBack;
	private NavigateButton goForward;
	private NavigateButton goUp;
	private NavigateButton refresh;
	
	private StackPane right;
	
	private DirLoading dirLoading;
	private PathDisp pathDisp;

	private Runnable back;
	private Runnable forward;
	private Runnable up;
	private Runnable refresher;

	public TopNav(Window window) {
		super(10);
		setAlignment(Pos.CENTER_LEFT);
		setPadding(new Insets(12));

		goBack = new NavigateButton(window, "go-back", "Go Back");
		goForward = new NavigateButton(window, "go-forward", "Go Forward");
		goUp = new NavigateButton(window, "go-up", "Go Up");
		refresh = new NavigateButton(window, "refresh", "Refresh");
		refresh.setEnabled(true);

		goBack.setOnMouseClicked(e -> {
			if (back != null) {
				back.run();
			}
		});

		goForward.setOnMouseClicked(e -> {
			if (forward != null) {
				forward.run();
			}
		});

		goUp.setOnMouseClicked(e -> {
			if (up != null) {
				up.run();
			}
		});
		
		refresh.setOnMouseClicked(e -> {
			if(refresher != null) {
				refresher.run();
			}
		});

		dirLoading = new DirLoading(window);
		
		pathDisp = new PathDisp(window);

		right = new StackPane(dirLoading, pathDisp);
		HBox.setHgrow(right, Priority.ALWAYS);
		
		left = new HBox(5);
		
		left.getChildren().add(goBack);
		left.getChildren().add(goForward);
		left.getChildren().add(new Separator(window, Orientation.VERTICAL));
		left.getChildren().add(goUp);
		left.getChildren().add(refresh);
		
		right.setMinWidth(0);
		right.maxWidthProperty().bind(widthProperty().subtract(34).subtract(left.widthProperty()));
		
		right.setAlignment(Pos.CENTER_RIGHT);
		
		getChildren().addAll(left, right);
	}
	
	public void setLoad(Consumer<File> load) {
		pathDisp.setLoad(load);
	}
	
	public void updatePathDisp(File file) {
		pathDisp.update(file);
	}
	
	public void setProgress(double progress) {
		dirLoading.setProgress(progress);
	}

	public void setBack(Runnable back) {
		this.back = back;
	}

	public void setForward(Runnable forward) {
		this.forward = forward;
	}

	public void setUp(Runnable up) {
		this.up = up;
	}
	
	public void setRefresher(Runnable refresher) {
		this.refresher = refresher;
	}

	public void enableBack() {
		goBack.setEnabled(true);
	}

	public void disableBack() {
		goBack.setEnabled(false);
	}

	public void enableForward() {
		goForward.setEnabled(true);
	}

	public void disableForward() {
		goForward.setEnabled(false);
	}

	public void enableUp() {
		goUp.setEnabled(true);
	}

	public void disableUp() {
		goUp.setEnabled(false);
	}
	
	public boolean isGoBackEnabled() {
		return goBack.isEnabled();
	}
	
	public boolean isGoForwardEnabled() {
		return goForward.isEnabled();
	}
}
