package org.luke.mex.app.pages.explorer.sidebar;

import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.image.ColorIcon;
import org.luke.mex.gui.factory.Backgrounds;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class SideBarSearch extends StackPane implements Styleable {

	private TextField field;
	private ColorIcon icon;
	
	public SideBarSearch(Window window) {
		setPadding(new Insets(1));
		setAlignment(Pos.CENTER_LEFT);

		field = new TextField();
		field.setBackground(Background.EMPTY);
		field.setBorder(Border.EMPTY);
		field.setPadding(new Insets(10, 15, 10, 58));
		field.setFont(new Font(14).getFont());
		
		field.setPromptText("Search...");

		icon = new ColorIcon("search", 24);
		icon.setTranslateX(14);

		getChildren().addAll(field, icon);
		
		applyStyle(window.getStyl());
	}
	
	@Override
	public void requestFocus() {
		field.requestFocus();
	}

	@Override
	public void applyStyle(Style style) {
		setBackground(Backgrounds.make(style.getBackgroundModifierActive(), 10));

		Color tx = style.getTextNormal();
		field.setStyle("-fx-text-fill: " + Styleable.colorToCss(tx) + ";-fx-prompt-text-fill: "
				+ Styleable.colorToCss(tx.deriveColor(0, 1, 1, .35))
				+ ";-fx-background-color:transparent;-fx-text-box-border: transparent;");

		icon.setFill(style.getAccent());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}

}
