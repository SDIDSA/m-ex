package org.luke.mex.app.utils.fileutils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

import org.luke.mex.app.components.DeleteType;
import org.luke.mex.gui.controls.alert.Alert;
import org.luke.mex.gui.controls.alert.AlertType;
import org.luke.mex.gui.controls.alert.ButtonType;
import org.luke.mex.gui.controls.check.RadioGroup;
import org.luke.mex.gui.controls.space.FixedVSpace;
import org.luke.mex.gui.window.Window;

import javafx.application.Platform;
import javafx.scene.layout.HBox;

public class DeleteUtils {
	private DeleteUtils() {}

	private static Alert deleteAlert;
	private static Alert failedToDelete;

	private static RadioGroup dt;
	private static DeleteType trash;

	public static void delete(Window window, File file, Runnable onSuccess) {
		createAlert(window);

		deleteAlert.setHead("Delete  " + file.getName());
		deleteAlert.clearBody();
		deleteAlert.addUnkeyedLabel(
				"Are you sure you want to delete this " + (file.isDirectory() ? "folder" : "file") + " ?");

		deleteAlert.addAction(ButtonType.DELETE, () -> deleteFile(window, file, onSuccess));

		deleteAlert.show();
	}

	private static void deleteDir(File dir) throws IOException {
		Stream<Path> stream = Files.walk(dir.toPath());
		stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		stream.close();
	}
	
	private static void createAlert(Window window) {
		if (deleteAlert == null) {

			deleteAlert = new Alert(window.getLoadedPage(), AlertType.DELETE);

			HBox deleteType = new HBox(10);

			trash = new DeleteType(window, "Move to trash", "trash");
			DeleteType def = new DeleteType(window, "Delete", "delete");

			dt = new RadioGroup(def.getRadio(), trash.getRadio());

			deleteType.getChildren().addAll(trash, def);

			deleteAlert.addToBody(new FixedVSpace(16), deleteType);
			deleteAlert.disableProperty(ButtonType.DELETE).bind(dt.valueProperty().isNull());
		}
	}
	
	private static void deleteFile(Window window, File file, Runnable onSuccess) {
		deleteAlert.startLoading(ButtonType.DELETE);
		new Thread() {
			@Override
			public void run() {
				try {
					if (dt.getValue().equals(trash.getRadio())) {
						if (!Desktop.getDesktop().moveToTrash(file)) {
							throw new IllegalStateException("File wasn't deleted");
						}
					} else {
						if (file.isFile()) {
							Files.delete(file.toPath());
						} else {
							deleteDir(file);
						}
					}
					Platform.runLater(() -> {
						deleteAlert.stopLoading(ButtonType.DELETE);
						deleteAlert.hide();
						onSuccess.run();
					});
				} catch (Exception e) {
					e.printStackTrace();

					if (failedToDelete == null) {
						failedToDelete = new Alert(window.getLoadedPage(), AlertType.INFO);
					}

					failedToDelete.setHead("Failed to Delete  " + file.getName());
					failedToDelete.clearBody();
					failedToDelete.addUnkeyedLabel(
							e.getCause().getClass().getSimpleName() + " > " + e.getClass().getSimpleName());

					failedToDelete.show();
				}
			}
		}.start();
	}
}
