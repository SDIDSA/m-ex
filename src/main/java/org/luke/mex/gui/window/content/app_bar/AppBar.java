package org.luke.mex.gui.window.content.app_bar;

import org.luke.mex.gui.controls.image.ColorIcon;
import org.luke.mex.gui.controls.space.ExpandingHSpace;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;
import org.luke.mex.gui.window.helpers.MoveResizeHelper;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class AppBar extends HBox implements Styleable {
	private AppBarButton info;
	private ColorIcon icon;

	public AppBar(Window window, MoveResizeHelper helper) {
		setPadding(new Insets(0, 8, 0, 10));
		setMinHeight(30);
		setAlignment(Pos.CENTER);

		icon = new ColorIcon("m-ex", 15);
		icon.setMouseTransparent(true);

		HBox buttons = new HBox(4);
		buttons.setAlignment(Pos.CENTER);

		AppBarButton minimize = new AppBarButton(window, "minimize");
		minimize.setAction(() -> window.setIconified(true));

		AppBarButton maxRest = new AppBarButton(window, "maximize");
		maxRest.setAction(window::maxRestore);

		AppBarButton exit = new AppBarButton(window, "close");
		exit.setAction(window::close);

		info = new AppBarButton(window, "info");
		HBox.setMargin(info, new Insets(0, 8, 0, 0));
		
		buttons.getChildren().addAll(info, minimize, maxRest, exit);

		getChildren().addAll(icon, new ExpandingHSpace(), buttons);

		helper.addOnTile(() -> maxRest.setIcon("restore"));

		helper.addOnUnTile(() -> maxRest.setIcon("maximize"));
		
		applyStyle(window.getStyl());
	}

	public void setOnInfo(Runnable action) {
		info.setAction(action);
	}
	
	@Override
	public void applyStyle(Style style) {
		icon.setFill(style.getAccent());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}
