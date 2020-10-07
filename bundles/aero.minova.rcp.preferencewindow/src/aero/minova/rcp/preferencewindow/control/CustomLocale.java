package aero.minova.rcp.preferencewindow.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomLocale {

	public static Locale[] getLocales() {
		return SimpleDateFormat.getAvailableLocales();
	}

}
