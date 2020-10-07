package aero.minova.rcp.preferencewindow.control;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CustomLocale {

	public static Locale[] getLocales() {
		return SimpleDateFormat.getAvailableLocales();
	}

	public static List<String> getCountrys() {
		Locale locales[] = getLocales();
		List<String> countrys = new ArrayList<String>();
		for (Locale country : locales) {
			if (!country.getDisplayCountry().equals("") && !countrys.contains(country.getDisplayCountry()))
				countrys.add(country.getDisplayCountry());
		}
		Collections.sort(countrys);
		return countrys;
	}
	
}
