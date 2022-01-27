package aero.minova.rcp.util;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse enthält alle Methoden, zum Konvertieren von Zeitangaben in Instant
 *
 * @author Wilfried Saak
 */
public class DateUtil {
	private static String day = "d";
	private static String month = "m";
	private static String year = "y";
	private static String week = "w";
	private static String shortcuts = day + month + year + week;
	private static String defaultPattern = "";

	private DateUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * Mit dieser Methode kann man die sprachspezifischen Kürzel einstellen. Es dürfen keine doppelten Kürzel verwendet werden. Alle Kürzel müssen als
	 * Kleinbuchstaben angegeben werden. Die Kürzel müssen aus genau einem Zeichen bestehen. Sie dürfen weder aus einer Zahl oder dem "+" oder "-" Symbol
	 * bestehen.
	 *
	 * @param day
	 *            das Kürzel für Tag. Default (englisch) ist "d"
	 * @param month
	 *            das Kürzel für Monat. Default (englisch) ist "m"
	 * @param year
	 *            das Kürzel für Jahr. Default (englisch) ist "y"
	 * @param week
	 *            das Kürzel für Woche. Default (englisch) ist "w"
	 * @exception IllegalArgumentException
	 *                wird geworfen, wenn eine der obigen Bedingungen nicht erfüllt ist
	 */
	public static void setShortcuts(String day, String month, String year, String week) {
		// Es muss immer genau ein Zeichen übergeben werden
		if (day.length() != 1) {
			throw new IllegalArgumentException("Shortcut for day must have length of 1!");
		}
		if (month.length() != 1) {
			throw new IllegalArgumentException("Shortcut for month must have length of 1!");
		}
		if (year.length() != 1) {
			throw new IllegalArgumentException("Shortcut for year must have length of 1!");
		}
		if (week.length() != 1) {
			throw new IllegalArgumentException("Shortcut for week must have length of 1!");
		}

		// nur Kleinbuchstaben zulassen
		day = day.toLowerCase();
		month = month.toLowerCase();
		year = year.toLowerCase();
		week = week.toLowerCase();

		// keine Symbol darf doppelt verwendet werden
		if (day.equals(month)) {
			throw new IllegalArgumentException("Shortcut for day and month must be different!");
		}
		if (day.equals(year)) {
			throw new IllegalArgumentException("Shortcut for day and year must be different!");
		}
		if (day.equals(week)) {
			throw new IllegalArgumentException("Shortcut for day and week must be different!");
		}
		if (month.equals(year)) {
			throw new IllegalArgumentException("Shortcut for month and year must be different!");
		}
		if (month.equals(week)) {
			throw new IllegalArgumentException("Shortcut for month and week must be different!");
		}
		if (year.equals(week)) {
			throw new IllegalArgumentException("Shortcut for year and week must be different!");
		}

		// jetzt können wir uns die Werte merken
		DateUtil.day = day;
		DateUtil.month = month;
		DateUtil.year = year;
		DateUtil.week = week;
		DateUtil.shortcuts = day + month + year + week;
	}

	public static Instant getDate(String input) {
		return getDate(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC), input);
	}

	public static Instant getDate(Instant today, String input) {
		return getDate(today, input, Locale.getDefault(Category.FORMAT), defaultPattern);
	}

	public static Instant getDate(String input, Locale locale, String datePattern) {
		return getDate(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC), input, locale, datePattern);
	}

	public static Instant getDate(Instant today, String input, Locale locale, String datePattern) {
		String[] formulars = splitInput(input);
		LocalDateTime startOfToday = null;

		if (formulars.length > 0) {
			startOfToday = LocalDate.ofInstant(today, ZoneId.of("UTC")).atStartOfDay();
		}

		int pos = 0;
		try {
			if (formulars.length > 0) {
				if (formulars[0].matches("\\d*")) {
					// Es beginnt mit eine Tagesangabe
					startOfToday = LocalDate.ofInstant(getNumericDate(today, formulars[pos++], datePattern), ZoneId.of("UTC")).atStartOfDay();
				}
				while (pos < formulars.length && startOfToday != null) {
					startOfToday = addRelativeDate(startOfToday, formulars[pos++]);
				}
			}
		} catch (DateTimeException | NullPointerException e ) {
			// Es ließ sich wohl nicht korrekt konvertieren
			startOfToday = null;
		}

		if (!input.isEmpty() && startOfToday == null) {
			if (!datePattern.equals("")) {
				try {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern(datePattern, locale);
					LocalDate ld = LocalDate.parse(input, dtf);
					startOfToday = ld.atStartOfDay();
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				DateTimeFormatter dtf;
				FormatStyle[] styles = new FormatStyle[] { FormatStyle.SHORT, FormatStyle.MEDIUM, FormatStyle.LONG, FormatStyle.FULL };
				for (FormatStyle formatStyle : styles) {
					try {
						dtf = DateTimeFormatter.ofLocalizedDate(formatStyle).withLocale(locale);
						LocalDate ld = LocalDate.parse(input, dtf);
						startOfToday = ld.atStartOfDay();
						break;
					} catch (Exception e) {
						// dann war is nicht in diesem Format
					}
				}
			}
		}

		return startOfToday == null ? null : startOfToday.toInstant(ZoneOffset.UTC);

	}

	public static String getDateString(Instant instant, Locale locale, String dateUtilPref) {
		DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale);
		if (dateUtilPref != null && !dateUtilPref.isBlank()) {
			dtf = DateTimeFormatter.ofPattern(dateUtilPref);
		}
		return LocalDate.ofInstant(instant, ZoneId.of("UTC")).format(dtf);
	}

	public static String[] splitInput(String input) {
		ArrayList<String> splits = new ArrayList<>();
		String regex;
		Pattern pattern;
		Matcher matcher;
		input = input.replaceAll("[\\.,/\\s]", "");

		input = input.toLowerCase();
		regex = "[0-9]+|[+]+|[-]+";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			// Zeichenfolge, die mit Ziffern beginnt nur Ziffern nehmen
			String result = matcher.group(0);
			splits.add(checkMissingDay(result));
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
			splits.add(checkMissingDay(result));
			input = input.substring(result.length());
		}

		if (input.length() > 0) {
			// Fehler bei der Auswertung
			return new String[0];
		}

		return splits.toArray(new String[0]);
	}

	static String checkMissingDay(String result) {
		if ((result.endsWith("+") || result.endsWith("-"))) {
			return result + day;
		} else {
			return result;
		}
	}

	static Instant getNumericDate(Instant now, String input, String datePattern) {
		int day;
		int month;
		int year;
		LocalDateTime startOfToday = LocalDate.ofInstant(now, ZoneId.of("UTC")).atStartOfDay();

		if ("0".equals(input)) {
			return startOfToday.toInstant(ZoneOffset.UTC);
		}
		switch (input.length()) {
		case 1: // nur der Tag im aktuellen Monat
			return startOfToday.withDayOfMonth(Integer.parseInt(input)).toInstant(ZoneOffset.UTC);
		case 2: // 1. Stelle Tag im Monat, 2. Stelle Monat
		case 3: // 1. Stelle Tag im Monat, Stelle 3-4 Monat
			day = Integer.parseInt(input.substring(0, 1));
			month = Integer.parseInt(input.substring(1));
			return startOfToday.withMonth(month).withDayOfMonth(day).toInstant(ZoneOffset.UTC);
		case 4: // Stelle 1-2 = Tag im Monat, Stelle 3-4 Monat
			day = Integer.parseInt(input.substring(0, 2));
			month = Integer.parseInt(input.substring(2));
			return startOfToday.withMonth(month).withDayOfMonth(day).toInstant(ZoneOffset.UTC);
		case 5: // Stelle 1 = Tag im Monat, Stelle 2-3 Monat, Stelle 4-5 das Jahr, Jahrhundet
				// von today
			day = Integer.parseInt(input.substring(0, 1));
			month = Integer.parseInt(input.substring(1, 3));
			year = Integer.parseInt(input.substring(3));
			year += startOfToday.getYear() / 100 * 100;
			return LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC);
		case 6: // Stelle 1-2 = Tag im Monat, Stelle 3-4 Monat, Stelle 5-6 das Jahr, Jahrhundet
			// von today
			day = Integer.parseInt(input.substring(0, 2));
			month = Integer.parseInt(input.substring(2, 4));
			year = Integer.parseInt(input.substring(4));
			year += startOfToday.getYear() / 100 * 100;
			return LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC);
		case 7: // Stelle 1 = Tag im Monat, Stelle 2-3 Monat, Stelle 4-7 das Jahr
			day = Integer.parseInt(input.substring(0, 1));
			month = Integer.parseInt(input.substring(1, 3));
			year = Integer.parseInt(input.substring(3));
			return LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC);
		case 8: // Stelle 1-2 = Tag im Monat, Stelle 3-4 Monat, Stelle 5-8 das Jahr
			day = Integer.parseInt(input.substring(0, 2));
			month = Integer.parseInt(input.substring(2, 4));
			year = Integer.parseInt(input.substring(4));
			return LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC);
		default:
			return null;
		}
	}

	static LocalDateTime addRelativeDate(LocalDateTime startOfDay, String input) {
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
			if (period.equals(day)) {
				startOfDay = startOfDay.plusDays(count);
			} else if (period.equals(month)) {
				startOfDay = startOfDay.plusMonths(count);
			} else if (period.equals(year)) {
				startOfDay = startOfDay.plusYears(count);
			} else if (period.equals(week)) {
				startOfDay = startOfDay.plusWeeks(count);
			} else {
				startOfDay = null;
			}
			return startOfDay;
		} else {
			return null;
		}
	}
}
