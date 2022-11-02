package org.luke.mex.app.utils.fileutils;

import java.io.File;
import java.util.function.Consumer;

import org.luke.mex.app.overlays.FileRename;
import org.luke.mex.app.overlays.FolderRename;
import org.luke.mex.gui.window.Window;

public class RenameUtils {
	private RenameUtils() {
	}

	private static FileRename fileRename;
	private static FolderRename folderRename;

	public static void rename(Window window, File file, Consumer<File> onSucc) {
		if (file.isFile()) {
			if (fileRename == null) {
				fileRename = new FileRename(window);
			}
		} else {
			if (folderRename == null) {
				folderRename = new FolderRename(window);
			}
		}

		(file.isFile() ? fileRename : folderRename).setFile(file, onSucc).show();
	}
}
