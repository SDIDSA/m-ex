package org.luke.mex.gui.controls.alert;

import java.util.Arrays;
import java.util.List;

public enum AlertType {
	INFO(ButtonType.CLOSE),
	DELETE(ButtonType.CANCEL, ButtonType.DELETE);
	
	private List<ButtonType> buttons;
	
	private AlertType(ButtonType...buttonTypes) {
		buttons = Arrays.asList(buttonTypes);
	}
	
	public List<ButtonType> getButtons() {
		return buttons;
	}
}
