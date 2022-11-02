package org.luke.mex.app.pages.explorer.sidebar;

import java.io.File;
import java.util.function.Consumer;

import org.luke.mex.app.utils.fileutils.IconUtils;
import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.factory.Backgrounds;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;

public class SideBarEntry extends HBox implements Styleable {
	private ImageView icon;
	private Label text;

	private Consumer<File> load;

	public SideBarEntry(Window window, String path) {
		super(20);
		setAlignment(Pos.CENTER_LEFT);

		setPadding(new Insets(10, 15, 10, 15));

		File f = new File(path);
		
		this.icon = new ImageView(IconUtils.typeIcon(f, 24));
		this.text = new Label(f.getName());
		text.setFont(new Font(14).getFont());
		this.text.setOpacity(.8);

		getChildren().addAll(this.icon, this.text);

		setCursor(Cursor.HAND);

		setOnMouseClicked(e -> {
			if (load != null) {
				load.accept(new File(path));
			}
		});

		setMinWidth(0);
		parentProperty().addListener((obs, ov, nv) -> {
			if(nv != null && nv instanceof SideBar sideBar) {
				maxWidthProperty().unbind();
				maxWidthProperty().bind(sideBar.widthProperty());
			}
		});
		
		applyStyle(window.getStyl());
	}

	public void setLoad(Consumer<File> load) {
		this.load = load;
	}

	@Override
	public void applyStyle(Style style) {
		text.setTextFill(style.getTextNormal());

		backgroundProperty()
				.bind(Bindings.when(pressedProperty()).then(Backgrounds.make(style.getBackgroundModifierActive(), 10))
						.otherwise(Bindings.when(hoverProperty())
								.then(Backgrounds.make(style.getBackgroundModifierHover(), 10))
								.otherwise(Background.EMPTY)));
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}
