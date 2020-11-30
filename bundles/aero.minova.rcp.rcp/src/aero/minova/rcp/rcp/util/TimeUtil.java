package aero.minova.rcp.rcp.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeUtil {

	private static String hour = "h";
	private static String minute = "m";
	private static String shortcuts = hour + minute;

	/**
	 * Mit dieser Methode kann man die sprachspezifischen Kürzel einstellen. Es
	 * dürfen keine doppelten Kürzel verwendet werden. Alle Kürzel müssen als
	 * Kleinbuchstaben angegeben werden. Die Kürzel müssen aus genau einem Zeichen
	 * bestehen. Sie dürfen weder aus einer Zahl oder dem "+" oder "-" Symbol
	 * bestehen.
	 * 
	 * @param hour   das Kürzel für Stunde. Default (englisch) ist "h"
	 * @param minute das Kürzel für Minute. Default (englisch) ist "m"
	 * 
	 * 
	 * @exception IllegalArgumentException wird geworfen, wenn eine der obigen
	 *                                     Bedingungen nicht erfüllt ist
	 */
	public static void setShortcuts(String hour, String minute) {
		// Es muss immer genau ein Zeichen übergeben werden
		if (hour.length() != 1)
			throw new IllegalArgumentException("Shortcut for hour must have length of 1!");
		if (minute.length() != 1)
			throw new IllegalArgumentException("Shortcut for minute must have length of 1!");

		// nur Kleinbuchstaben zulassen
		hour = hour.toLowerCase();
		minute = minute.toLowerCase();

		// keine Symbol darf doppelt verwendet werden
		if (hour.equals(minute))
			throw new IllegalArgumentException("Shortcut for day and month must be different!");

		// jetzt können wir uns die Werte merken
		TimeUtil.hour = hour;
		TimeUtil.minute = minute;
		TimeUtil.shortcuts = hour + minute;
	}

	static public Instant getTime(String input, String timezone) {

		return getTime(Instant.now(), input, timezone);
	}

	static public Instant getTime(Instant today, String input, String timezone) {

		if (input.contains("-") || input.contains("+")) {
			String[] split = splitInput(input);
			today = changeHours(today, split, timezone);
		} else if (input.equals("0")) {
			LocalDateTime lt = LocalDateTime.ofInstant(today, ZoneId.of(timezone)).truncatedTo(ChronoUnit.MINUTES);
			lt = lt.withYear(1900).withMonth(1).withDayOfMonth(1);
			today = lt.toInstant(ZoneId.of(timezone).getRules().getOffset(lt));
			return today;
		} else {

			today = getTimeFromNumbers(today, input, timezone);
		}
		return today;
	}

	static String[] splitInput(String input) {
		ArrayList<String> splits = new ArrayList<>();
		String regex;
		Pattern pattern;
		Matcher matcher;

		input = input.toLowerCase();
		regex = "[0-9]+|[+]+|[-]+";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			// Zeichenfolge, die mit Ziffern beginnt nur Ziffern nehmen
			String result = matcher.group(0);
			splits.add(checkMissingHour(result));
			input = input.substring(result.length());
		} else {
			// nix gefunden
			return new String[0];
		}
		regex = "[+-][0-9]*[" + shortcuts + "]|([+]+|[-]+)[" + shortcuts + "]?";
		pattern = Pattern.compile(regex);
		while (input.length() > 0) {
			matcher = pattern.matcher(input);
			boolean b = matcher.find();
			if (!b)
				break;
			String result = matcher.group(0);
			splits.add(checkMissingHour(result));
			input = input.substring(result.length());
		}

		if (input.length() > 0) {
			// Fehler bei der Auswertung
			return new String[0];
		}

		return splits.toArray(new String[0]);
	}

	static String checkMissingHour(String result) {
		if ((result.endsWith("+") || result.endsWith("-")))
			return result + hour;
		else
			return result;
	}

	public static String getTimeString(Instant instant, Locale locale, String timezone) {
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of(timezone));
		return localDateTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale));
	}

	private static Instant changeHours(Instant instant, String[] splits, String timezone) {

		boolean correctInput = true;
		boolean skipFirst = false;
		LocalDateTime lt = LocalDateTime.ofInstant(instant, ZoneId.of(timezone));
		String regex = "([+-]+)([0-9]*)([" + shortcuts + "])";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(splits[0]);

		if (!matcher.find()) {
			if (splits[0].equals("0")) {
				lt = LocalDateTime.ofInstant(instant, ZoneId.of(timezone));
			} else {
				Instant givenInstant = getTimeFromNumbers(instant, splits[0], timezone);
				lt = LocalDateTime.ofInstant(givenInstant, ZoneId.of(timezone));
			}
			skipFirst = true;
		}
		for (int i = 0; i < splits.length; i++) {
			if (correctInput == true) {
				if (i == 0 && skipFirst == true) {
				} else {
					lt = addRelativeDate(lt, splits[i]);
					if (lt == null) {
						correctInput = false;
					}
				}
			}
		}
		lt = lt.truncatedTo(ChronoUnit.MINUTES);
		lt = lt.withYear(1900).withMonth(1).withDayOfMonth(1);
		if (correctInput == true) {
			instant = lt.toInstant(ZoneId.of(timezone).getRules().getOffset(lt));
		} else {
			instant = null;
		}
		return instant;
	}

	static LocalDateTime addRelativeDate(LocalDateTime time, String input) {
		String regex = "([+-]+)([0-9]*)([" + shortcuts + "])";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			String sign = matcher.group(1);
			String number = matcher.group(2);
			number = number.length() == 0 ? "1" : number;
			String period = matcher.group(3);
			int count = 0;
			if (sign.length() > 0) {
				count = sign.length() - 1;
			}

			if (matcher.group(1).startsWith("-")) {
				count *= -1;
				count -= Integer.parseInt(number);
			} else {
				count += Integer.parseInt(number);
			}
			if (period.equals(hour)) {
				time = time.plusHours(count);
			} else if (period.equals(minute)) {
				time = time.plusMinutes(count);
			} else {
				time = null;
			}
			return time;
		} else {
			return null;
		}
	}

	private static Instant getTimeFromNumbers(Instant givenInstant, String subString, String timezone) {

		Integer hours = 0;
		Integer minutes = 0;
		String[] subStrings = subString.split(":");
		if (subStrings.length == 2) {
			hours = Integer.valueOf(subStrings[0]);
			minutes = Integer.valueOf(subStrings[1]);
		}

		if (subStrings.length < 2) {
			int[] timeList = checkNumbersForTime(subString);
			if (timeList != null) {
				hours = timeList[0];
				minutes = timeList[1];
			}
		}
		if (hours > 23) {
			return null;
		}
		if (minutes > 59) {
			return null;
		}
		LocalTime localTime = LocalTime.of(hours, minutes);
		LocalDate localDate = LocalDate.ofInstant(givenInstant, ZoneId.of(timezone));
		localDate = localDate.withYear(1900).withMonth(1).withDayOfMonth(1);
		LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
		Instant instant = localDateTime.toInstant(ZoneId.of(timezone).getRules().getOffset(localDateTime));
		return instant;
	}

	private static int[] checkNumbersForTime(String subString) {

		String hour = "";
		String minutesString = "";
		int[] time = null;
		switch (subString.length()) {
		case 1:
			time = new int[2];
			time[0] = Integer.valueOf(subString);
			time[1] = 0;
			return time;
		case 2:
			time = new int[2];
			String hoursString = String.valueOf(subString.charAt(0)) + String.valueOf(subString.charAt(1));
			time[0] = Integer.valueOf(hoursString);
			time[1] = 0;
			return time;
		case 3:
			time = new int[2];
			hour = String.valueOf(subString.charAt(0)) + String.valueOf(subString.charAt(1));
			time[0] = Integer.valueOf(hour);
			minutesString = String.valueOf(subString.charAt(2)) + "0";
			time[1] = Integer.valueOf(minutesString);
			return time;
		case 4:
			time = new int[2];
			hour = String.valueOf(subString.charAt(0)) + String.valueOf(subString.charAt(1));
			time[0] = Integer.valueOf(hour);
			minutesString = String.valueOf(subString.charAt(2)) + String.valueOf(subString.charAt(3));
			time[1] = Integer.valueOf(minutesString);
			return time;
		}

		return null;
	}
}
