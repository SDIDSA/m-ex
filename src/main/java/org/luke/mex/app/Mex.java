package org.luke.mex.app;

import org.luke.mex.app.pages.explorer.Explorer;
import org.luke.mex.app.utils.fileutils.ApplicationsUtils;
import org.luke.mex.gui.locale.Locale;
import org.luke.mex.gui.style.Style;
import org.luke.mex.gui.window.Window;

import javafx.application.Application;
import javafx.stage.Stage;

public class Mex extends Application {
	public void start(Stage discard) {
//		System.setProperty("prism.lcdtext", "false");
		
		ApplicationsUtils.init();
		
		Window main = new Window(this, Style.LIGHT, Locale.EN_US);
		main.setTitle("M-Ex");

		main.show();

		main.loadPage(new Explorer(main));

		main.centerOnScreen();
	}
}
