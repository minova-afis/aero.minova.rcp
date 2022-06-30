package aero.minova.rcp.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Locale;

public class DateTimeUtil {

	private DateTimeUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static Instant getDateTime(String input) {
		return getDateTime(LocalDateTime.now().toInstant(ZoneOffset.UTC), input);
	}

	public static Instant getDateTime(String input, Locale locale, String dateUtilPattern, String timeUtilPattern) {
		return getDateTime(LocalDateTime.now().toInstant(ZoneOffset.UTC), input, locale, dateUtilPattern, timeUtilPattern);
	}

	public static Instant getDateTime(Instant todayNow, String input, Locale locale) {
		return getDateTime(todayNow, input, locale, "", "");
	}

	public static Instant getDateTime(Instant todayNow, String input) {
		return getDateTime(todayNow, input, Locale.getDefault(), "", "");
	}

	public static String getDateTimeString(Instant instant, Locale locale, String datePattern, String timePattern) {

		String part1 = DateUtil.getDateString(instant, locale, datePattern);
		String part2 = TimeUtil.getTimeString(instant, locale, timePattern);
		return part1 + " " + part2;
	}

	/**
	 * Diese Methode erstellt ein Instant aus DateUtil.getDate() und TimeUtil.getTime(). Das Datum und die Zeit werden bei der Eingabe mit einer Leerstelle
	 * getrennt. Wenn die Eingabe vom Datum oder der Zeit unzulässig ist, wird null zurückgegeben. Was einer zulässigen Eingabe entspricht, wird in DateUtil und
	 * TimeUtil festgelegt.
	 * 
	 * @param todayNow
	 * @param input
	 * @return dateTime oder null wenn die Eingabe unzulässig ist
	 */
	public static Instant getDateTime(Instant todayNow, String input, Locale locale, String dateUtilPattern, String timeUtilPattern) {
		String[] splitInput = null;
		Instant dateIn;
		Instant timeIn;
		LocalDate dateLocal;
		LocalTime timeLocal;

		if (input.contains(" ")) {
			splitInput = input.split(" ");
		} else if (input.contains("*")) {
			String first = input.substring(0, input.indexOf("*"));
			String last = input.substring(input.lastIndexOf("*") + 1);
			String firstLast = first + " " + last;
			splitInput = firstLast.split(" ");
		} else {
			return null;
		}

		if (splitInput.length > 1) {
			if (!splitInput[0].isEmpty()) {
				dateIn = DateUtil.getDate(todayNow, splitInput[0], locale, dateUtilPattern);
				timeIn = TimeUtil.getTime(todayNow, splitInput[1], timeUtilPattern);
			} else {
				dateIn = DateUtil.getDate(todayNow, "0", locale, dateUtilPattern);
				timeIn = TimeUtil.getTime(todayNow, splitInput[1], timeUtilPattern);
			}
		} else {
			return null;
		}

		if (null != dateIn && null != timeIn) {
			dateLocal = LocalDate.ofInstant(dateIn, ZoneOffset.UTC);
			timeLocal = LocalTime.ofInstant(timeIn, ZoneOffset.UTC);
		} else {
			return null;
		}

		return LocalDateTime.of(dateLocal, timeLocal).toInstant(ZoneOffset.UTC);
	}

}
