package org.luke.mex.gui.controls.label.keyed;

import org.luke.mex.gui.controls.Font;
import org.luke.mex.gui.controls.label.unkeyed.Link;
import org.luke.mex.gui.window.Window;

public class KeyedLink extends Link implements KeyedTextNode {
	public KeyedLink(Window window, String key, Font font) {
		super(window, null, font, true);
		setKey(key);
	}

	public KeyedLink(Window window, String key) {
		this(window, key, Font.DEFAULT);
	}

	@Override
	public void setKey(String key) {
		if (label instanceof KeyedTextNode keyed)
			keyed.setKey(key);
	}
}
