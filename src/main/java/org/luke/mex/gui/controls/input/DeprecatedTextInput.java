package org.luke.mex.gui.controls.input;

import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.input.styles.DeprecatedInputStyle;
import org.luke.mex.gui.window.Window;

public class DeprecatedTextInput extends TextInput {

	public DeprecatedTextInput(Window window, Font font, String key, boolean hidden) {
		super(window, font, key, hidden);

		inputStyle = new DeprecatedInputStyle(this);

		applyStyle(window.getStyl());
	}

	public DeprecatedTextInput(Window window, Font f, String key) {
		this(window, f, key, false);
	}
}
