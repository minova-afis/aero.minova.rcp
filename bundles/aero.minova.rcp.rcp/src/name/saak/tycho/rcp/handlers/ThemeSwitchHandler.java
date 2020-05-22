package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;

@SuppressWarnings("restriction")
public class ThemeSwitchHandler {
	
	private static final String DEFAULT_THEME = "aero.minova.rcp.css.default";
	private static final String RAINBOW_THEME = "aero.minova.rcp.css.rainbow";
	
	@Execute
	public void switchTheme(IThemeEngine engine) {
		if(!engine.getActiveTheme().getId().equals(DEFAULT_THEME)) {
			engine.setTheme(DEFAULT_THEME, true);
		} else {
			engine.setTheme(RAINBOW_THEME, true);
		}
	}


}
