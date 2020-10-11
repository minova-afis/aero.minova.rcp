package aero.minova.rcp.rcp.util;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

/**
 * Diese Klasse enthält alle Methoden, zum Konvertieren von Zeitangaben in
 * Instant
 * 
 * @author Wilfried Saak
 *
 */
public class DateTimeUtil {

	static public Instant getDate(String input) {
		return getDate(LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC), input);
	}

	static Instant getDate(Instant today, String input) {
		String formulars[] = input.split("[-+]");
		Instant date = null;
		try {
			if (formulars.length > 0 && formulars[0].matches("\\d*")) {
				// Es beginnt mit eine Tagesabgabe
				date = getNumericDate(today, formulars[0]);
			}
		} catch (DateTimeException e) {
			// Es ließ sich wohl nicht korrekt konvertieren
			date = null;
		}
		return date;
	}

	private static Instant getNumericDate(Instant now, String input) {
		int day, month, year;
		LocalDateTime today = LocalDate.ofInstant(now, ZoneId.of("UTC")).atStartOfDay();

		if ("0".equals(input)) {
			return today.toInstant(ZoneOffset.UTC);
		}
		switch (input.length()) {
		case 1: // nur der Tag im aktuellen Monat
			return today.withDayOfMonth(Integer.parseInt(input)).toInstant(ZoneOffset.UTC);
		case 2: // 1. Stelle Tag im Monat, 2. Stelle Monat
			day = Integer.parseInt(input.substring(0, 1));
			month = Integer.parseInt(input.substring(1));
			return today.withMonth(month).withDayOfMonth(day).toInstant(ZoneOffset.UTC);
		case 3: // 1. Stelle Tag im Monat, Stelle 3-4 Monat
			day = Integer.parseInt(input.substring(0, 1));
			month = Integer.parseInt(input.substring(1));
			return today.withMonth(month).withDayOfMonth(day).toInstant(ZoneOffset.UTC);
		case 4: // Stelle 1-2 = Tag im Monat, Stelle 3-4 Monat
			day = Integer.parseInt(input.substring(0, 2));
			month = Integer.parseInt(input.substring(2));
			return today.withMonth(month).withDayOfMonth(day).toInstant(ZoneOffset.UTC);
		case 5: // Stelle 1 = Tag im Monat, Stelle 2-3 Monat, Stelle 4-5 das Jahr, Jahrhundet
				// von today
			day = Integer.parseInt(input.substring(0, 1));
			month = Integer.parseInt(input.substring(1, 3));
			year = Integer.parseInt(input.substring(3));
			year += today.getYear() / 100 * 100;
			return LocalDate.of(year, month, day).atStartOfDay().toInstant(ZoneOffset.UTC);
		case 6: // Stelle 1-2 = Tag im Monat, Stelle 3-4 Monat, Stelle 5-6 das Jahr, Jahrhundet
			// von today
			day = Integer.parseInt(input.substring(0, 2));
			month = Integer.parseInt(input.substring(2, 4));
			year = Integer.parseInt(input.substring(4));
			year += today.getYear() / 100 * 100;
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
		}
		return null;
	}
}
