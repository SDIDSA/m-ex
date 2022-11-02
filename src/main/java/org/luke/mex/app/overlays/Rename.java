package org.luke.mex.app.overlays;

import java.io.File;
import java.util.function.Consumer;

import org.luke.mex.app.utils.Command;
import org.luke.mex.gui.controls.alert.AbstractOverlay;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.style.Styleable;
import org.luke.mex.gui.window.Window;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.HBox;

public class Rename extends AbstractOverlay {

	protected HBox newName;
	protected StringProperty newNameVal;
	
	protected File file;
	
	protected Consumer<File> onSuccess;
	
	public Rename(Window window) {
		super(window.getLoadedPage());
		setButtonText("Rename");

		newName = new HBox(10);

		newNameVal = new SimpleStringProperty();
		
		done.setAction(() -> {
			File moveTo = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().lastIndexOf("\\") + 1)
					.concat(newNameVal.get()));

			startLoading();
			new Thread() {
				@Override
				public void run() {
					try {
						new Command("cmd.exe", "/C", "ren \"" + file.getName() + "\" \"" + moveTo.getName()+"\"")
								.execute(file.getParentFile()).waitFor();
						Platform.runLater(() -> onSuccess.accept(moveTo));
						Platform.runLater(Rename.this::hide);
					} catch (InterruptedException e) {
						e.printStackTrace();
						Thread.currentThread().interrupt();
					}

					Platform.runLater(Rename.this::stopLoading);
				}
			}.start();

		});

		center.getChildren().addAll(newName);

		applyStyle(window.getStyl());
	}

	public Rename setFile(File file, Consumer<File> onSuccess) {
		this.file = file;
		this.onSuccess = onSuccess;

		doneDisabled().unbind();
		doneDisabled().bind(newNameVal.isEqualTo(file.getName()));
		
		return this;
	}
	
	@Override
	public void applyStyle(ObjectProperty<Style> style) {
		Styleable.bindStyle(this, style);
	}

}
