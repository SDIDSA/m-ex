package org.luke.mex.gui.controls.alert;

import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.image.ImageProxy;
import org.luke.mex.gui.controls.label.keyed.Label;
import org.luke.mex.gui.controls.label.unkeyed.Link;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class Credits extends Alert {
	private Label mostIcons;
	private Link icons8;
	
	public Credits(Pane owner, Window window) {
		super(owner, window, AlertType.INFO);
		setHead("about");
		addLabel("code_hosted");
		addLink("SDIDSA/m-ex");
		setBodyAction(1, ()-> window.openLink("https://github.com/SDIDSA/m-ex"));
		
		mostIcons = new Label(window, "icons8_credits", new Font(14));
		icons8 = new Link(window, "icons8", new Font(14));
		icons8.setAction(()-> window.openLink("https://icons8.com"));
		
		TextFlow icons = new TextFlow(mostIcons, icons8);
		VBox.setMargin(icons, new Insets(0, 0, 16, 0));
		
		addToBody(icons);
		addToBody(new StackPane(new ImageView(ImageProxy.load("icon", 128))));
		
		applyStyle(window.getStyl());
	}
	
	@Override
	public void applyStyle(Style style) {
		mostIcons.setFill(style.getTextNormal());
		
		super.applyStyle(style);
	}
	
	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		if(mostIcons == null) {
			return;
		}
		Styleable.bindStyle(this, style);
	}

}
