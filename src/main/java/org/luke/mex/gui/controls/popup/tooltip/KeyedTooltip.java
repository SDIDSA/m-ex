package org.luke.mex.gui.controls.popup.tooltip;

import org.luke.mex.gui.controls.popup.Direction;
import org.luke.mex.gui.locale.Locale;
import org.luke.mex.gui.locale.Localized;
import org.luke.mex.gui.window.Window;

import javafx.beans.property.ObjectProperty;

public class KeyedTooltip extends Tooltip implements Localized {

	private String key;

	public KeyedTooltip(Window window, String key, Direction direction, double offset) {
		super(window, "", direction, offset);

		this.key = key;
		applyLocale(window.getLocale());
	}

	public KeyedTooltip(Window window, String key, Direction direction) {
		this(window, key, direction, 0);
	}

	@Override
	public void applyLocale(Locale locale) {
		setText(locale.get(key));
	}
	
	@Override
	public void applyLocale(ObjectProperty<Locale> locale) {
		Localized.bindLocale(this, locale);	
	}

}
