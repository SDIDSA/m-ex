package org.luke.mex.gui.controls.space;

import org.luke.mex.gui.factory.Backgrounds;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Separator extends Region implements Styleable {
	private DropShadow ds;
	
	public Separator(Window window, Orientation or) {
		switch (or) {
		case HORIZONTAL:
			setMaxHeight(1);
			setMinHeight(1);
			break;
		case VERTICAL:
			setMinWidth(1);
			setMaxWidth(1);
			break;
		default:
			break;
		}
		
		setFill(Color.GRAY);
		
		ds = new DropShadow();
		ds.setRadius(4);
		setEffect(ds);
		
		setViewOrder(-1);
		
		applyStyle(window.getStyl());
	}
	
	public void setFill(Paint fill) {
		setBackground(Backgrounds.make(fill));
	}

	@Override
	public void applyStyle(Style style) {
		setFill(style.getBackgroundModifierAccent());
		ds.setColor(style.getBackgroundTertiary());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}