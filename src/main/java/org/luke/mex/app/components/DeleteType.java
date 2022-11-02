package org.luke.mex.app.components;

import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.check.LabeledRadio;
import org.luke.mex.gui.controls.image.ColorIcon;
import org.luke.mex.gui.controls.space.ExpandingHSpace;
import org.luke.mex.gui.factory.Backgrounds;
import org.luke.mex.gui.factory.Borders;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;

public class DeleteType extends LabeledRadio implements Styleable {

	private ColorIcon icon;

	public DeleteType(Window window, String text, String iconName) {
		super(window, text, 14);
		setPadding(new Insets(14));
		setFont(new Font(14));

		parentProperty().addListener(new ChangeListener<Parent>() {
			@Override
			public void changed(ObservableValue<? extends Parent> obs, Parent ov, Parent nv) {
				if (nv != null) {
					minWidthProperty().unbind();
					minWidthProperty().bind(((HBox) nv).widthProperty().subtract(10).divide(2));
					parentProperty().removeListener(this);
				}
			}
		});

		icon = new ColorIcon(iconName, 24, 18);

		getChildren().addAll(new ExpandingHSpace(), icon);

		setFocusTraversable(true);
		
		setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.SPACE)) {
				getRadio().flip();
			}
		});

		applyStyle(window.getStyl());
	}

	@Override
	public void applyStyle(Style style) {
		setTextFill(style.getTextNormal());
		icon.setFill(style.getTextNormal());

		borderProperty().bind(Bindings.when(focusedProperty()).then(Borders.make(style.getTextLink(), 10))
				.otherwise(Borders.make(style.getBackgroundSecondary(), 10)));
		backgroundProperty().bind(Bindings.when(pressedProperty())
				.then(Backgrounds.make(style.getBackgroundModifierActive(), 10))
				.otherwise(Bindings.when(hoverProperty()).then(Backgrounds.make(style.getBackgroundModifierHover(), 10))
						.otherwise(Bindings.when(checkedProperty())
								.then(Backgrounds.make(style.getBackgroundModifierSelected(), 10))
								.otherwise(Background.EMPTY))));
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}

}
