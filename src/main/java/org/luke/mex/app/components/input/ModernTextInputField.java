package org.luke.mex.app.components.input;

import org.luke.mex.gui.controls.input.styles.ModernInputStyle;
import org.luke.mex.gui.window.Window;

public class ModernTextInputField extends TextInputField {

	public ModernTextInputField(Window window, String key, double width, boolean hidden) {
		super(window, key, width, hidden);
		
		input.setInputStyle(new ModernInputStyle(input));

		applyStyle(window.getStyl());
	}

	public ModernTextInputField(Window window, String key, boolean hidden) {
		this(window, key, 200, hidden);
	}

	public ModernTextInputField(Window window, String key, double width) {
		this(window, key, width, false);
	}

	public ModernTextInputField(Window window, String key) {
		this(window, key, 200, false);
	}
	
}
