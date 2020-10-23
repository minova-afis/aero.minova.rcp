package aero.minova.rcp.preferencewindow.control;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author bauer
 *
 */
public class CustomTimeZone {

	/**
	 * Liefert alle Zeitzonen zurück
	 * 
	 * @param locale
	 * @return
	 */
	public static Map<String, ZoneId> getZones(Locale locale) {
		Map<String, ZoneId> map = new HashMap<>();
		for (String zone : ZoneId.getAvailableZoneIds()) {
			ZoneId zoneId = ZoneId.of(zone);
			map.put(zoneId.getDisplayName(TextStyle.FULL, locale), zoneId);
		}
		return map;
	}

	/**
	 * Liefert eine ZoneId anhand der Zeitzone zurück.
	 * 
	 * @param map
	 * @param id
	 * @param l
	 * @return
	 */
	public static ZoneId getId(Map<String, ZoneId> map, String id, Locale l) {
		return map.get(id);
	}

	/**
	 * Liefert eine sortierte Liste mit alle Zeitzonen mit entsprechenden GMT
	 * wieder. Zeitzonen werden nach aktuellem Locale dargestellt.
	 * 
	 * @return
	 */
	public static List<String> getTimeZones(Locale locale) {
		Map<String, ZoneId> map = getZones(locale);
		List<String> zones = new ArrayList<>();
		ZoneId[] zoneIds = map.values().toArray(new ZoneId[0]);
		Arrays.sort(zoneIds, (o1, o2) -> {
			if (TimeZone.getTimeZone(o1).getRawOffset() - TimeZone.getTimeZone(o2).getRawOffset() == 0) {
				return o1.getDisplayName(TextStyle.FULL, locale).compareTo(o2.getDisplayName(TextStyle.FULL, locale));
			} else {
				return TimeZone.getTimeZone(o1).getRawOffset() - TimeZone.getTimeZone(o2).getRawOffset();
			}
		});
		for (ZoneId zoneId : zoneIds) {
			zones.add(displayTimeZone(zoneId.getId(), locale));
		}
		return zones;
	}

	/**
	 * Liefert übergebene Zeitzone mit GMT wieder.
	 * 
	 * @param timeZone
	 * @return
	 */
	public static String displayTimeZone(String timeZone, Locale activeLocale) {
		TimeZone tz = TimeZone.getTimeZone(timeZone);
		long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
		long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);
		// avoid -4:-30 issue
		minutes = Math.abs(minutes);

		String result = "";
		if (hours >= 0) {
			result = String.format("(GMT+%d:%02d) %s", hours, minutes,
					tz.toZoneId().getDisplayName(TextStyle.FULL, activeLocale));
		} else {
			result = String.format("(GMT%d:%02d) %s", hours, minutes,
					tz.toZoneId().getDisplayName(TextStyle.FULL, activeLocale));
		}

		return result;

	}
}
