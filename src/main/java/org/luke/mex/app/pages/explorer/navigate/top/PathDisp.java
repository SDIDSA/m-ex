package org.luke.mex.app.pages.explorer.navigate.top;

import java.io.File;
import java.util.function.Consumer;

import org.luke.mex.app.utils.fileutils.IconUtils;
import org.luke.mex.gui.controls.image.ColorIcon;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.beans.property.ObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public class PathDisp extends HBox implements Styleable {
	private Window window;

	private ColorIcon goIn;

	private ImageView icon;

	private Consumer<File> load;

	public PathDisp(Window window) {
		setPadding(new Insets(0, 15, 0, 15));
		setAlignment(Pos.CENTER_LEFT);

		icon = new ImageView();
		goIn = new ColorIcon("go-in", 8);

		HBox.setMargin(icon, new Insets(0, 10, 0, 0));
		HBox.setMargin(goIn, new Insets(0, 5, 0, 0));

		this.window = window;

		Rectangle clip = new Rectangle();

		parentProperty().addListener((obs, ov, nv) -> {
			clip.widthProperty().bind(((Region) nv).widthProperty());
			clip.heightProperty().bind(((Region) nv).heightProperty());

			clip.xProperty().bind(widthProperty().subtract(((Region) nv).widthProperty()));

			Runnable update = () -> {
				double from = getWidth() > ((Region) nv).getWidth() ? 0 : 1;
				Stop[] stops = new Stop[] { new Stop(0, Color.gray(0, from)),
						new Stop((70 / clip.getWidth()), Color.gray(0, 1)), new Stop(1, Color.gray(0, 1)) };
				clip.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops));
			};

			getChildren().addListener((ListChangeListener<? super Node>) c -> update.run());

			widthProperty().addListener(e -> update.run());

			((Region) nv).widthProperty().addListener(e -> update.run());
		});

		setClip(clip);

		applyStyle(window.getStyl());
	}

	public void setLoad(Consumer<File> load) {
		this.load = load;
	}

	public void update(File dir) {
		getChildren().clear();

		File at = dir;

		icon.setImage(IconUtils.typeIcon(dir, 18));

		getChildren().addAll(icon, goIn);

		while (at != null) {
			if (at.equals(new File(System.getProperty("user.home")))) {
				if (at != dir) {
					getChildren().add(2, new PathDispEntry(window, new File(IconUtils.THIS_PC), "This PC", load));
				} else {
					getChildren().add(2, new PathDispEntry(window, at, at.getName(), load));
				}
				return;
			} else {
				getChildren().add(2, new PathDispEntry(window, at, at.getName(), load));
			}

			at = at.getParentFile();
		}

		if (dir != null && !dir.equals(new File(IconUtils.THIS_PC)))
			getChildren().add(2, new PathDispEntry(window, new File(IconUtils.THIS_PC), "This PC", load));
	}

	@Override
	public void applyStyle(Style style) {
		goIn.setFill(style.getTextNormal());
	}

	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}
}
