package org.luke.mex.app.pages.explorer.sidebar;

import java.io.File;
import java.util.ArrayList;
import java.util.function.Consumer;

import org.luke.mex.app.utils.fileutils.IconUtils;
import org.luke.mex.gui.controls.space.FixedVSpace;
import org.luke.mex.gui.controls.space.Separator;
import org.luke.mex.gui.window.Window;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.layout.VBox;

public class SideBar extends VBox {
	private DoubleProperty width;
	
	private ArrayList<SideBarEntry> entries;
	
	public SideBar(Window window) {
		setPadding(new Insets(10));
		width = new SimpleDoubleProperty(220);
		
		minWidthProperty().bind(width);
		maxWidthProperty().bind(width);
		
		entries = new ArrayList<>();
		
		entries.add(new SideBarEntry(window, IconUtils.THIS_PC));
		
		for(String sf : IconUtils.systemFolders) {
			entries.add(new SideBarEntry(window, sf));
		}
		
		getChildren().add(new SideBarSearch(window));
		
		for(int i = 0; i<entries.size(); i++) {
			if(i == 0) {
				getChildren().add(new FixedVSpace(20));
			}
			if(i == 1) {
				getChildren().add(new FixedVSpace(10));
				getChildren().add(new Separator(window, Orientation.HORIZONTAL));
				getChildren().add(new FixedVSpace(10));
			}
			SideBarEntry entry = entries.get(i);
			getChildren().add(entry);
		}
	}
	
	public void setLoad(Consumer<File> load) {
		entries.forEach(e -> e.setLoad(load));
	}
	
	public void setW(double w) {
		width.set(w);
	}
	
	public double getW() {
		return width.get();
	}
	
	public DoubleProperty wProperty() {
		return width;
	}
}
