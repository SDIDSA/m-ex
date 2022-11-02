package org.luke.mex.app.pages.explorer.navigate.top;

import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.label.unkeyed.Text;
import org.luke.mex.gui.controls.space.Separator;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class CategoryHeader extends HBox implements Styleable {
	private Text name;

	public CategoryHeader(Window window, String text) {
		super(10);
		setPadding(new Insets(8));
		setAlignment(Pos.CENTER);

		name = new Text(text, new Font(Font.DEFAULT_FAMILY_MEDIUM, 14));

		getChildren().addAll(separate(window), name, separate(window));
		
		parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> obs, Parent ov, Parent nv) {
				if (nv != null) {
					minWidthProperty().unbind();
					minWidthProperty().bind(((FlowPane) nv).widthProperty().subtract(10));
					parentProperty().removeListener(this);
				}
			}
		});

		applyStyle(window.getStyl());
	}

	private Separator separate(Window window) {
		Separator sep = new Separator(window, Orientation.HORIZONTAL);
		HBox.setHgrow(sep, Priority.ALWAYS);
		return sep;
	}

	@Override
	public void applyStyle(Style style) {
		name.setFill(style.getTextNormal());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}
