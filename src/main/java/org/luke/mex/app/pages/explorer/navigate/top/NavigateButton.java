package org.luke.mex.app.pages.explorer.navigate.top;

import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.image.ColorIcon;
import org.luke.mex.gui.controls.popup.Direction;
import org.luke.mex.gui.controls.popup.tooltip.Tooltip;
import org.luke.mex.gui.factory.Backgrounds;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;

public class NavigateButton extends StackPane implements Styleable {

	private BooleanProperty enabled;
	
	private ColorIcon icon;

	public NavigateButton(Window window, String icon, String tooltip) {
		this.icon = new ColorIcon(icon, 16);
		setPadding(new Insets(8));
		getChildren().add(this.icon);
		
		setCursor(Cursor.HAND);
		
		Tooltip ttip = new Tooltip(window, tooltip, Direction.UP);
		ttip.setOffset(-10);
		ttip.setFont(new Font(13));
		
		Tooltip.install(this, ttip);
		
		enabled = new SimpleBooleanProperty(false);
		
		disableProperty().bind(enabled.not());
		opacityProperty().bind(Bindings.when(enabled).then(1).otherwise(.5));
		
		applyStyle(window.getStyl());
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled.set(enabled);
	}
	
	public boolean isEnabled() {
		return enabled.get();
	}

	@Override
	public void applyStyle(Style style) {
		backgroundProperty()
				.bind(Bindings.when(pressedProperty()).then(Backgrounds.make(style.getBackgroundModifierActive(), 10))
						.otherwise(Bindings.when(hoverProperty())
								.then(Backgrounds.make(style.getBackgroundModifierHover(), 10))
								.otherwise(Background.EMPTY)));

		icon.setFill(style.getAccent());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}

}
