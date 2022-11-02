package org.luke.mex.gui.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.luke.mex.gui.exception.ErrorHandler;
import org.luke.mex.gui.window.Window;

import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class FileUtils {
	private FileUtils() {

	}

	public static String readFile(String path) {
		StringBuilder sb = new StringBuilder();

		try (BufferedReader br = new BufferedReader(new InputStreamReader(FileUtils.class.getResourceAsStream(path)))) {
			String line;
			boolean first = true;
			while ((line = br.readLine()) != null) {
				if (first) {
					first = false;
				} else {
					sb.append("\n");
				}
				sb.append(line);
			}
		} catch (Exception x) {
			ErrorHandler.handle(x, "reading file " + path);
		}

		return sb.toString();
	}

	public static File selectImage(Window window) {
		try {
			return selectFile(window, "Image", "*.png", "*.jpg");
		} catch (Exception e) {
			return null;
		}
	}

	private static File selectFile(Window window, String type, String... extensions) {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter(type, extensions));
		return fc.showOpenDialog(window);
	}
}
