package org.luke.mex.gui.controls.input;

import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.input.styles.ModernInputStyle;
import org.luke.mex.gui.window.Window;

public class ModernTextInput extends TextInput {

	public ModernTextInput(Window window, Font font, String key, boolean hidden) {
		super(window, font, key, hidden);

		inputStyle = new ModernInputStyle(this);

		applyStyle(window.getStyl());
	}
}
