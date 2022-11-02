package org.luke.mex.app.pages.explorer;

import java.awt.Dimension;

import org.luke.mex.app.pages.Page;
import org.luke.mex.app.pages.explorer.navigate.Navigate;
import org.luke.mex.app.pages.explorer.sidebar.SideBar;
import org.luke.mex.gui.controls.space.Separator;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.HBox;

public class Explorer extends Page {
	private double initMx, initW;
	
	private SideBar side;
	private Navigate nav;
	
	private HBox root;

	public Explorer(Window window) {
		super(window, new Dimension(825, 600));
		setAlignment(Pos.TOP_LEFT);

		side = new SideBar(window);
		
		Separator sep = new Separator(window, Orientation.VERTICAL);
		sep.setCursor(Cursor.H_RESIZE);

		sep.setOnMousePressed(e -> {
			initMx = e.getSceneX();
			initW = side.getW();
		});
		
		sep.setOnMouseDragged(e -> {
			double dx = e.getSceneX() - initMx;
			side.setW(Math.min(Math.max(150, initW + dx), 300));
		});
		
		nav = new Navigate(window);
		
		side.setLoad(nav::load);
		
		root = new HBox(side, sep, nav);

		nav.setMinHeight(0);
		nav.maxHeightProperty().bind(heightProperty());
		
		nav.wProperty().bind(window.widthProperty().subtract(side.widthProperty().add(Bindings.when(window.paddedProperty()).then(30).otherwise(0))));

		getChildren().add(root);
	}

	@Override
	public void applyStyle(Style style) {
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}

}
