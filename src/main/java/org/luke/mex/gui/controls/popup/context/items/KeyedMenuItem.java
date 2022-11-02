package org.luke.mex.gui.controls.popup.context.items;

import org.luke.mex.gui.controls.popup.context.ContextMenu;

import javafx.scene.paint.Color;

public class KeyedMenuItem extends MenuItem {

	public KeyedMenuItem(ContextMenu menu, String key, Color fill) {
		super(menu, key, fill, true);
	}

}
