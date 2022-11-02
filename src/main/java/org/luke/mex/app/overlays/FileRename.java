package org.luke.mex.app.overlays;

import java.io.File;
import java.util.function.Consumer;

import org.luke.mex.app.components.input.ModernTextInputField;
import org.luke.mex.gui.window.Window;

public class FileRename extends Rename {
	private ModernTextInputField base;
	private ModernTextInputField extension;

	public FileRename(Window window) {
		super(window);

		setTop("Rename File");

		base = new ModernTextInputField(getWindow(), "Base", 296);
		extension = new ModernTextInputField(window, "Extension", 102);
		
		base.positionCaret(0);
		
		addOnShown(base::requestFocus);

		form.addAll(base, extension);
		newNameVal.bind(base.valueProperty().concat(".").concat(extension.valueProperty()));
		newName.getChildren().addAll(base, extension);
	}

	@Override
	public Rename setFile(File file, Consumer<File> onSuccess) {
		String name = file.getName();
		String baseString = name.substring(0, name.lastIndexOf("."));
		String extensionString = name.replace(baseString + ".", "");

		base.setValue(baseString);
		extension.setValue(extensionString);

		return super.setFile(file, onSuccess);
	}

}
