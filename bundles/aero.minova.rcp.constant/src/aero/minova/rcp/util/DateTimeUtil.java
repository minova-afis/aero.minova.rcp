package aero.minova.rcp.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Locale;

public class DateTimeUtil {

	private DateTimeUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static Instant getDateTime(String input, String timezone) {
		return getDateTime(LocalDateTime.now(ZoneId.of(timezone)).toInstant(ZoneOffset.UTC), input);
	}

	public static Instant getDateTime(Instant todayNow, String input) {
		return getDateTime(todayNow, input, Locale.getDefault());
	}

	public static Instant getDateTime(Instant todayNow, String input, Locale locale) {
		return getDateTime(todayNow, input, locale, "", "", "UTC");
	}

	public static Instant getDateTime(Instant todayNow, String input, Locale locale, String dateUtilPattern, String timeUtilPattern) {
		return getDateTime(todayNow, input, locale, dateUtilPattern, timeUtilPattern, "UTC");
	}

	public static Instant getDateTime(Instant todayNow, String input, Locale locale, String zoneId) {
		return getDateTime(todayNow, input, locale, "", "", zoneId);
	}

	public static Instant getDateTime(Instant todayNow, String input, String zoneId) {
		return getDateTime(todayNow, input, Locale.getDefault(), "", "", zoneId);
	}

	public static String getDateTimeString(Instant instant, Locale locale, String datePattern, String timePattern, String timezone) {

		instant = LocalDateTime.ofInstant(instant, ZoneId.of(timezone)).toInstant(ZoneOffset.UTC);
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
	public static Instant getDateTime(Instant todayNow, String input, Locale locale, String dateUtilPattern, String timeUtilPattern, String zoneId) {
		String[] splitInput = null;
		Instant dateIn;
		Instant timeIn;
		Instant dateTime;
		LocalDate dateLocal;
		LocalTime timeLocal;

		if (input.contains("*")) {
			String first = input.substring(0, input.indexOf("*"));
			String last = input.substring(input.lastIndexOf("*") + 1);
			String firstLast = first + " " + last;
			splitInput = firstLast.split(" ");
		} else {
			splitInput = input.split(" ");
		}

		// Datum aus dem ersten Teil
		if (!splitInput[0].isEmpty()) {
			dateIn = DateUtil.getDate(todayNow, splitInput[0], locale, dateUtilPattern);
		} else {
			dateIn = DateUtil.getDate(todayNow, "0", locale, dateUtilPattern);
		}

		// Zeit aus dem zweiten Teil
		if (splitInput.length > 1) {
			timeIn = TimeUtil.getTime(todayNow, splitInput[1], timeUtilPattern);
		} else {
			// Mitternacht, wenn nichts angegeben ist
			timeIn = TimeUtil.getTime(todayNow, "00", timeUtilPattern);
		}

		if (null != dateIn && null != timeIn) {
			dateLocal = LocalDate.ofInstant(dateIn, ZoneOffset.UTC);
			timeLocal = LocalTime.ofInstant(timeIn, ZoneOffset.UTC);
		} else {
			return null;
		}

		try {
			ZonedDateTime zdt = ZonedDateTime.of(dateLocal, timeLocal, ZoneId.of(zoneId));
			dateTime = LocalDateTime.ofInstant(zdt.toInstant(), ZoneId.of("UTC")).toInstant(ZoneOffset.UTC);
		} catch (Exception e) {
			// Invalid ZoneId
			return null;
		}

		return dateTime;
	}

}
