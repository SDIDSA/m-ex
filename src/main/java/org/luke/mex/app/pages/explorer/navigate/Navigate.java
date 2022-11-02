package org.luke.mex.app.pages.explorer.navigate;

import java.io.File;
import java.util.ArrayList;

import org.luke.mex.app.pages.explorer.navigate.top.TopNav;
import org.luke.mex.app.utils.fileutils.IconUtils;
import org.luke.mex.gui.window.Window;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class Navigate extends VBox {
	private DoubleProperty w;

	private MainNav mainNav;
	private TopNav topNav;

	private ArrayList<File> history;
	private int index = -1;

	public Navigate(Window window) {
		w = new SimpleDoubleProperty();

		topNav = new TopNav(window);
		mainNav = new MainNav(window);

		mainNav.setMinHeight(0);
		mainNav.maxHeightProperty().bind(heightProperty().subtract(topNav.heightProperty()));

		mainNav.minWidthProperty().bind(w);
		mainNav.maxWidthProperty().bind(w);
		topNav.minWidthProperty().bind(w);
		topNav.maxWidthProperty().bind(w);

		history = new ArrayList<>();

		mainNav.setLoad(this::load);

		topNav.setBack(this::goBack);
		topNav.setForward(this::goForward);
		topNav.setUp(this::goUp);
		topNav.setRefresher(this::refresh);
		
		mainNav.setSetProgress(topNav::setProgress);
		mainNav.setUpdatePathDisp(topNav::updatePathDisp);
		
		topNav.setLoad(this::load);

		load(new File(IconUtils.THIS_PC));
		
		addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
			if(e.getButton() == MouseButton.BACK && topNav.isGoBackEnabled()) {
				goBack();
			}
			if(e.getButton() == MouseButton.FORWARD && topNav.isGoForwardEnabled()) {
				goForward();
			}
		});
		
		getChildren().addAll(topNav, mainNav);
	}

	public void setW(double w) {
		this.w.set(w);
	}

	public double getW() {
		return w.get();
	}

	public DoubleProperty wProperty() {
		return w;
	}

	public void load(File dir) {
		if (dir.isFile()) {
			throw new IllegalArgumentException("passed parameter is not a directory");
		}

		mainNav.load(dir);

		history.add(dir);
		index++;

		if (index > 0) {
			topNav.enableBack();
		}

		while (index < history.size() - 1) {
			history.remove(history.size() - 1);
		}

		topNav.disableForward();

		checkUp();

		printHist();
	}

	public void goBack() {
		index--;
		mainNav.load(history.get(index));

		if (index <= 0) {
			topNav.disableBack();
		}

		topNav.enableForward();

		checkUp();

		printHist();
	}

	public void goForward() {
		index++;
		mainNav.load(history.get(index));

		if (index >= history.size() - 1) {
			topNav.disableForward();
		}

		topNav.enableBack();

		checkUp();

		printHist();
	}

	public void goUp() {
		File loaded = mainNav.getLoaded();
		
		boolean isSystemFolder = false;
		for(String sf : IconUtils.systemFolders) {
			if(loaded.equals(new File(sf))) {
				isSystemFolder = true;
				break;
			}
		}
		
		if(isSystemFolder || loaded.getParent() == null) {
			load(new File(IconUtils.THIS_PC));
		}else {
			load(loaded.getParentFile());
		}
		
	}

	private void checkUp() {
		File loaded = mainNav.getLoaded();
		if (loaded.getParent() == null && loaded.equals(new File(IconUtils.THIS_PC))) {
			topNav.disableUp();
		} else {
			topNav.enableUp();
		}
	}
	
	private void refresh() {
		mainNav.refresh();
	}

	public void printHist() {
//		for (int i = 0; i < history.size(); i++) {
//			System.out.print(i + " ");
//		}
//		System.out.println();
//		for (int i = 0; i < history.size(); i++) {
//			System.out.print((index == i ? "^" : " ") + " ");
//		}
//		System.out.println();
	}
}
