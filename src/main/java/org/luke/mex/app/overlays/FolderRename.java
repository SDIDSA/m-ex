package org.luke.mex.app.overlays;

import java.io.File;
import java.util.function.Consumer;

import org.luke.mex.app.components.input.ModernTextInputField;
import org.luke.mex.gui.window.Window;

public class FolderRename extends Rename {

	private ModernTextInputField name;
	
	public FolderRename(Window window) {
		super(window);

		setTop("Rename Folder");
		
		name = new ModernTextInputField(getWindow(), "Name", 408);
		
		addOnShown(name::requestFocus);
		
		form.addAll(name);
		newNameVal.bind(name.valueProperty());
		newName.getChildren().addAll(name);
	}
	
	@Override
	public Rename setFile(File file, Consumer<File> onSuccess) {
		String nameString = file.getName();
		
		name.setValue(nameString);
		
		return super.setFile(file, onSuccess);
	}

}
