package aero.minova.rcp.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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

	private TimeUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Mit dieser Methode kann man die sprachspezifischen Kürzel einstellen. Es dürfen keine doppelten Kürzel verwendet werden. Alle Kürzel müssen als
	 * Kleinbuchstaben angegeben werden. Die Kürzel müssen aus genau einem Zeichen bestehen. Sie dürfen weder aus einer Zahl oder dem "+" oder "-" Symbol
	 * bestehen.
	 * 
	 * @param hour
	 *            das Kürzel für Stunde. Default (englisch) ist "h"
	 * @param minute
	 *            das Kürzel für Minute. Default (englisch) ist "m"
	 * @exception IllegalArgumentException
	 *                wird geworfen, wenn eine der obigen Bedingungen nicht erfüllt ist
	 */
	public static void setShortcuts(String hour, String minute) {
		// Es muss immer genau ein Zeichen übergeben werden
		if (hour.length() != 1) {
			throw new IllegalArgumentException("Shortcut for hour must have length of 1!");
		}
		if (minute.length() != 1) {
			throw new IllegalArgumentException("Shortcut for minute must have length of 1!");
		}

		// nur Kleinbuchstaben zulassen
		hour = hour.toLowerCase();
		minute = minute.toLowerCase();

		// keine Symbol darf doppelt verwendet werden
		if (hour.equals(minute)) {
			throw new IllegalArgumentException("Shortcut for day and month must be different!");
		}

		// jetzt können wir uns die Werte merken
		TimeUtil.hour = hour;
		TimeUtil.minute = minute;
		TimeUtil.shortcuts = hour + minute;
	}

	public static Instant getTime(String input) {
		return getTime(input, "");
	}

	public static Instant getTime(String input, String timeUtilPref) {
		return getTime(LocalDateTime.now().toInstant(ZoneOffset.UTC), input, timeUtilPref, Locale.getDefault());
	}

	public static Instant getTime(String input, String timeUtilPref, Locale locale) {
		return getTime(LocalDateTime.now().toInstant(ZoneOffset.UTC), input, timeUtilPref, locale);
	}

	public static Instant getTime(Instant now, String input) {
		return getTime(now, input, "", Locale.getDefault());
	}

	public static Instant getTime(Instant now, String input, String timeUtilPref) {
		return getTime(now, input, timeUtilPref, Locale.getDefault());
	}

	public static Instant getTime(Instant now, String input, String timeUtilPref, Locale locale) {
		try {
			if (!timeUtilPref.isBlank()) {
				// Wir versuchen den Input String direkt zu parsen mit dem übergebenen Pattern. Das parsen funktioniert nur, wenn der Input String exakt dem
				// Pattern
				// entspricht. Z.B. für die Uhrzeit 08:00 pm muss der Input String "08:00 PM" lauten mit dem Locale.US (oder anderer englischer Locale). Die
				// korrekte Schreibweise des Merdiem ist hier sehr wichtig.
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern(timeUtilPref, locale);
				LocalTime lt = LocalTime.parse(input, dtf);
				LocalDateTime ldt = LocalDateTime.of(LocalDate.of(1900, 1, 1), lt);
				now = ldt.toInstant(ZoneOffset.UTC);
			} else {
				// Wir probieren das Instant zu formatieren mit dem Pattern, das für das Locale als default gilt.
				DateTimeFormatter dtf = DateTimeFormatter
						.ofPattern(DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, FormatStyle.SHORT, Chronology.ofLocale(locale), locale), locale);
				LocalTime lt = LocalTime.parse(input, dtf);
				LocalDateTime ldt = LocalDateTime.of(LocalDate.of(1900, 1, 1), lt);
				now = ldt.toInstant(ZoneOffset.UTC);
			}
		} catch (Exception e) {
			// Klappt das Parsen nicht, wird der alternative Code ausgeführt, der den Input String splitted und so zu einem Instant kommt.
			now = getAlternativeTime(now, input, timeUtilPref, locale);
		}

		return now;
	}

	public static Instant getAlternativeTime(Instant now, String input, String timeUtilPref, Locale locale) {
		// das m in am/pm kolidiert mit unseren Shortcuts und muss rausgeschnitten werden. Dieser Fehler tritt nur bei am/pm auf.Bei Meridiems in anderen
		// Sprache tritt er nicht auf.
		if (input.contains("am")) {
			input = input.substring(0, input.indexOf("a")) + input.substring(input.indexOf("a") + 2);
		} else if (input.contains("pm")) {
			input = input.substring(0, input.indexOf("p")) + input.substring(input.indexOf("p") + 2);
		}

		if (input.equals("0")) {
			LocalDateTime lt = LocalDateTime.ofInstant(now, ZoneId.of("UTC")).truncatedTo(ChronoUnit.MINUTES);
			lt = lt.withYear(1900).withMonth(1).withDayOfMonth(1);
			now = lt.toInstant(ZoneId.of("UTC").getRules().getOffset(lt));
		} else if (input.contains("-") || input.contains("+")) {
			String[] split = splitInput(input);
			now = changeHours(now, split);
		} else if (input.equals("")) {
			return null;
		} else {
			now = getTimeFromNumbers(input);
		}

		if (!timeUtilPref.equals("")) {
			try {
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern(timeUtilPref, locale);
				LocalTime lt = LocalTime.ofInstant(now, ZoneId.of("UTC"));
				String formatted = lt.format(dtf);
				now = Instant.parse(formatted);
			} catch (Exception e) {
				// Mit dem Pattern konnte nicht geparsed werden
			}
		} else {
			// Testen verschiedener Format Styles, um eine passende Formatierung zu finden.
			FormatStyle[] styles = new FormatStyle[] { FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL };
			for (FormatStyle formatStyle : styles) {
				try {
					DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedTime(formatStyle).withLocale(locale);
					LocalTime lt = LocalTime.ofInstant(now, ZoneId.of("UTC"));
					String formatted = lt.format(dtf);
					now = Instant.parse(formatted);
					break;
				} catch (Exception ex) {
					// dann war is nicht in diesem Format
				}
			}
		}

		return now;
	}

	private static String[] splitInput(String input) {
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
			if (!b) {
				break;
			}
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

	private static String checkMissingHour(String result) {
		if ((result.endsWith("+") || result.endsWith("-"))) {
			return result + hour;
		} else {
			return result;
		}
	}

	public static String getTimeString(Instant instant, Locale locale, String timeUtilPref) {
		DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale);
		if (timeUtilPref != null && !timeUtilPref.isBlank()) {
			dtf = DateTimeFormatter.ofPattern(timeUtilPref, locale);
		}
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
		return localDateTime.format(dtf);
	}

	private static Instant changeHours(Instant instant, String[] splits) {
		if (splits.length != 0) {
			boolean correctInput = true;
			boolean skipFirst = false;
			LocalDateTime lt = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
			String regex = "([+-]+)([0-9]*)([" + shortcuts + "])";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(splits[0]);

			if (!matcher.find()) {
				if (splits[0].equals("0")) {
					lt = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
				} else {
					Instant givenInstant = getTimeFromNumbers(splits[0]);
					lt = LocalDateTime.ofInstant(givenInstant, ZoneId.of("UTC"));
				}
				skipFirst = true;
			}
			for (int i = 0; i < splits.length; i++) {
				if (correctInput) {
					if (i == 0 && skipFirst) {
						// Ersten überspringen
					} else {
						lt = addRelativeDate(lt, splits[i]);
						if (lt == null) {
							correctInput = false;
						}
					}
				}
			}
			if (correctInput) {
				lt = lt.truncatedTo(ChronoUnit.MINUTES);
				lt = lt.withYear(1900).withMonth(1).withDayOfMonth(1);
				instant = lt.toInstant(ZoneId.of("UTC").getRules().getOffset(lt));
			} else {
				instant = null;
			}
		} else {
			return null;
		}
		return instant;
	}

	private static LocalDateTime addRelativeDate(LocalDateTime time, String input) {
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

	private static Instant getTimeFromNumbers(String input) {
		Integer hours = 0;
		Integer minutes = 0;
		StringBuilder clearInputBuilder = new StringBuilder();
		String regex = "([" + shortcuts + "])";

		for (char c : input.toCharArray()) {
			if (Character.isLetter(c) && !String.valueOf(c).matches(regex)) {
				continue;
			}
			clearInputBuilder.append(String.valueOf(c));
		}

		String clearInput = clearInputBuilder.toString();
		String[] subStrings = clearInput.split("[\\:/\\s]");

		if (subStrings.length == 2) {
			if (!subStrings[0].isBlank() && !subStrings[1].isBlank()) {
				hours = Integer.valueOf(subStrings[0]);
				minutes = Integer.valueOf(subStrings[1]);
			} else {
				return null;
			}
		}

		if (subStrings.length < 2) {
			clearInput = clearInput.replaceAll("[\\:/\\s]", "");
			int[] timeList = checkNumbersForTime(clearInput);
			if (timeList.length == 2) {
				hours = timeList[0];
				minutes = timeList[1];
			} else {
				return null;
			}
		}
		if (hours > 23) {
			return null;
		}
		if (minutes > 59) {
			return null;
		}
		LocalDateTime localDateTime = LocalDateTime.of(1900, 1, 1, hours, minutes);
		return localDateTime.toInstant(ZoneOffset.UTC);
	}

	private static int[] checkNumbersForTime(String input) {
		String hour = "";
		String minutesString = "";
		int[] time = new int[2];

		try {
			switch (input.length()) {
			case 1:
			case 2:
				time[0] = Integer.valueOf(input);
				time[1] = 0;
				return time;
			case 3:
				hour = String.valueOf(input.charAt(0));
				time[0] = Integer.valueOf(hour);
				minutesString = input.substring(1);
				time[1] = Integer.valueOf(minutesString);
				return time;
			case 4:
				hour = input.substring(0, 2);
				time[0] = Integer.valueOf(hour);
				minutesString = input.substring(2);
				time[1] = Integer.valueOf(minutesString);
				return time;
			default:
				return new int[0];
			}
		} catch (Exception e) {
			return new int[0];
		}
	}
}
