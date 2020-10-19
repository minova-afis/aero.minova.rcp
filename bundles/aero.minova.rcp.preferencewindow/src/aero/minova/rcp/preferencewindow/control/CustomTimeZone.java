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

public class CustomTimeZone {
	
	public static Map<String, ZoneId> getZones(Locale locale) {
		Map<String, ZoneId> map = new HashMap<>();
		for (String zone : ZoneId.getAvailableZoneIds()) {
			ZoneId zoneId = ZoneId.of(zone);
			map.put(zoneId.getDisplayName(TextStyle.FULL, locale), zoneId);
		}
		return map;
	}

	public static ZoneId getId(Map<String, ZoneId> map, String id, Locale l) {
		return map.get(id);
	}

	public static List<String> getTimeZones() {
		Locale locale = CustomLocale.getLocale();
		Map<String, ZoneId> map = getZones(locale);
		List<String> zones = new ArrayList<>();
		ZoneId[]  zoneIds = map.values().toArray(new ZoneId[0]);
		Arrays.sort(zoneIds, (o1, o2) -> {
			if (TimeZone.getTimeZone(o1).getRawOffset()-TimeZone.getTimeZone(o2).getRawOffset() == 0) {
				return o1.getDisplayName(TextStyle.FULL, locale).compareTo(o2.getDisplayName(TextStyle.FULL, locale));
			} else {
				return TimeZone.getTimeZone(o1).getRawOffset()-TimeZone.getTimeZone(o2).getRawOffset();
			}
		});
		for (ZoneId zoneId : zoneIds) {
			zones.add(displayTimeZone(zoneId.getId()));
		}
		return zones;
	}

	public static String displayTimeZone(String timeZone) {
		Locale locale = CustomLocale.getLocale();
		TimeZone tz = TimeZone.getTimeZone(timeZone);
		long hours = TimeUnit.MILLISECONDS.toHours(tz.getRawOffset());
		long minutes = TimeUnit.MILLISECONDS.toMinutes(tz.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);
		// avoid -4:-30 issue
		minutes = Math.abs(minutes);

		String result = "";
		if (hours >= 0) {
			result = String.format("(GMT+%d:%02d) %s", hours, minutes,
					tz.toZoneId().getDisplayName(TextStyle.FULL, locale));
		} else {
			result = String.format("(GMT%d:%02d) %s", hours, minutes,
					tz.toZoneId().getDisplayName(TextStyle.FULL, locale));
		}

		return result;

	}
}
