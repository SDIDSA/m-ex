package org.luke.mex.app.pages.explorer.navigate.top;

import java.io.File;
import java.util.function.Consumer;

import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.image.ColorIcon;
import org.luke.mex.gui.controls.label.unkeyed.Text;
import org.luke.mex.gui.factory.Backgrounds;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;

public class PathDispEntry extends HBox implements Styleable {

	private Text text;
	private ColorIcon goIn;

	public PathDispEntry(Window window, File file, String text, Consumer<File> load) {
		super(10);
		setPadding(new Insets(0,5,0,5));
		setAlignment(Pos.CENTER);
		
		setMinHeight(0);
		setMaxHeight(24);
		
		String txt = text.isBlank() ? "Local Disk (" + file.getAbsolutePath().replace("\\", "") + ")" : text;
		
		this.text = new Text(txt, new Font(12));

		goIn = new ColorIcon("go-in", 8);

		getChildren().addAll(this.text, goIn);

		setCursor(Cursor.HAND);
		
		setOnMouseClicked(e -> {
			if(load != null) {
				load.accept(file);
			}
		});
		
		applyStyle(window.getStyl());
	}

	@Override
	public void applyStyle(Style style) {
		backgroundProperty()
		.bind(Bindings.when(pressedProperty()).then(Backgrounds.make(style.getBackgroundModifierActive(), 5))
				.otherwise(Bindings.when(hoverProperty())
						.then(Backgrounds.make(style.getBackgroundModifierHover(), 5))
						.otherwise(Background.EMPTY)));
		
		text.setFill(style.getTextNormal());
		goIn.setFill(style.getTextNormal());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}

}
