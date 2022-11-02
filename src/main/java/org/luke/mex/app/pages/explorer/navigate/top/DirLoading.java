package org.luke.mex.app.pages.explorer.navigate.top;

import org.luke.mex.gui.factory.Backgrounds;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class DirLoading extends StackPane implements Styleable {

	private DoubleProperty progress;
	
	private Rectangle progressPane;
	
	public DirLoading(Window window) {
		setAlignment(Pos.CENTER_LEFT);
		setMinHeight(32);
		setMaxHeight(32);
		
		progressPane = new Rectangle();
		progressPane.setOpacity(.5);
		progressPane.setHeight(32);
		
		Rectangle progressClip = new Rectangle();
		progressClip.setHeight(32);
		progressClip.widthProperty().bind(widthProperty());
		progressClip.setArcHeight(20);
		progressClip.setArcWidth(20);
		
		progressPane.setClip(progressClip);
		
		progress = new SimpleDoubleProperty(0);

		progressPane.widthProperty().bind(progress.multiply(widthProperty()));
		
		getChildren().add(progressPane);
		
		applyStyle(window.getStyl());
	}
	
	public void setProgress(double progress) {
		this.progress.set(progress);
	}
	
	public double getProgress() {
		return progress.get();
	}
	
	public DoubleProperty progressProperty() {
		return progress;
	}
	
	@Override
	public void applyStyle(Style style) {
		setBackground(Backgrounds.make(style.getBackgroundFloating(), 10));
		
		progressPane.setFill(style.getAccent());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}

}
