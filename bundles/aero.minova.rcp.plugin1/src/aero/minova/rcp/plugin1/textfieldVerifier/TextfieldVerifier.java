package aero.minova.rcp.plugin1.textfieldVerifier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.form.model.xsd.Field;

public class TextfieldVerifier implements FocusListener{

	public static boolean verifyDouble(String newString) {
		boolean correctDouble = true;
		try {
			Float.parseFloat(newString);
		} catch (NumberFormatException ex) {
			correctDouble = false;
		}

		return correctDouble;
	}

	public static boolean verifyText(String newString, int limit) {
		if (newString.length() > limit) {
			return false;
		} else {
			return true;
		}
	}

	public static String verifyDate(String newString) {
		String date = newString;
		String allowedCharacters = "1234567890.";
		boolean normalDateInput = true;
		for (int i = 0; i < newString.length(); i++) {
			char c = newString.charAt(i);
			if (allowedCharacters.indexOf(c) == -1) {
				normalDateInput = false;
			}
		}
		if (!normalDateInput || newString.length() != 10) {
			date = translateCommandIntoDate(newString);
		} else {
			if (newString.length() == 10) {
				date = checkForRegularDate(newString);
			}
		}
		return date;
	}

	public static String checkForRegularDate(String newString) {
		String date = newString;

		for (int index = 0; index < newString.length(); index++) {
			if (newString.charAt(index) == '.' && (index != 2 && index != 5)) {
				date = "";
				return date;
			}
			if ((index == 2 || index == 5) && newString.charAt(index) != '.') {
				date = "";
				return date;
			}
		}
		String day = String.valueOf(newString.charAt(0)) + String.valueOf(newString.charAt(1));
		int dayNumber = Integer.valueOf(day);
		String month = String.valueOf(newString.charAt(3)) + String.valueOf(newString.charAt(4));
		int monthNumber = Integer.valueOf(month);
		if (dayNumber > 31) {
			date = "";
		}
		if (monthNumber > 12) {
			date = "";
		}
		// Beachten der Monatswechsel
		if (monthNumber == 2 && dayNumber > 28) {
			String year = String.valueOf(newString.charAt(6)) + String.valueOf(newString.charAt(7))
					+ String.valueOf(newString.charAt(8)) + String.valueOf(newString.charAt(9));
			int yearNumber = Integer.valueOf(year);
			if (yearNumber % 4 != 0) {
				date = "";
			} else {
				if (dayNumber != 29) {
					date = "";
				}
			}
		} else {
			if (monthNumber % 2 == 0 && dayNumber == 31) {
				date = "";
			}
		}

		return date;
	}

	public static String translateCommandIntoDate(String newString) {

		String allowedCharacters = "1234567890.";
		String subString = newString;
		String date = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		LocalDate localDate = null;
		Boolean unallowedFormat = false;
		if (subString.contains("heute")) {
			localDate = localDate.now();
			date = df.format(localDate);
			subString.replaceFirst("heute", "");
		}
		if (subString.contains("h")) {
			localDate = localDate.now();
			date = df.format(localDate);
			subString.replaceFirst("h", "");
		}
		if (subString.contains("morgen")) {
			localDate = localDate.now().plusDays(1);
			date = df.format(localDate);
			subString.replaceFirst("morgen", "");
		}
		if (subString.contains("m")) {
			localDate = localDate.now().plusDays(1);
			date = df.format(localDate);
			subString.replaceFirst("m", "");
		}
		if (subString.contains("gestern")) {
			localDate = localDate.now().minusDays(1);
			date = df.format(localDate);
			subString.replaceFirst("gestern", "");
		}
		if (subString.contains("g")) {
			localDate = localDate.now().minusDays(1);
			date = df.format(localDate);
			subString.replaceFirst("g", "");
		}

		if (subString.contains("+") || subString.contains("-")) {
			if (localDate == null) {
				localDate = localDate.now();
			}
			for (int i = 0; i < subString.length(); i++) {
				if (subString.charAt(i) == '+') {
					if (i + 1 < subString.length()) {
						if (subString.charAt(i + 1) == 'M') {
							localDate = localDate.plusMonths(1);
						} else if (subString.charAt(i + 1) == 'Y') {
							localDate = localDate.plusYears(1);
						} else {
							localDate = localDate.plusDays(1);
						}
					} else {
						localDate = localDate.plusDays(1);
					}
				}
				if (subString.charAt(i) == '-') {
					if (i + 1 < subString.length()) {
						if (subString.charAt(i + 1) == 'M') {
							localDate = localDate.minusMonths(1);
						} else if (subString.charAt(i + 1) == 'Y') {
							localDate = localDate.minusYears(1);
						} else {
							localDate = localDate.minusDays(1);
						}
					} else {
						localDate = localDate.minusDays(1);
					}

				}
			}
			date = df.format(localDate);
		} else if (localDate == null) {
			int points = 0;
			for (int i = 0; i < subString.length(); i++) {
				if (subString.charAt(i) == '.') {
					points++;
				}
				if (allowedCharacters.indexOf(subString.charAt(i)) == -1) {
					unallowedFormat = true;
				}
			}
			if (points > 2) {
				unallowedFormat = true;
			}
			if (unallowedFormat == true) {
				date = "";
				return date;
			} else {
				date = getDateFromNumbers(subString, df);
			}
		}

		return date;
	}

	private static String getDateFromNumbers(String newString, DateTimeFormatter df) {
		String date = "";
		LocalDate localDate = null;
		Integer days = 0;
		Integer months = 0;
		Integer years = 0;
		if (newString.length() == 2) {
			return date;
		}
		String[] subStrings = newString.split(".");
		if (subStrings.length == 3) {

			years = Integer.valueOf(subStrings[2]);
		}
		if (subStrings.length == 2) {
			days = Integer.valueOf(subStrings[0]);
			months = Integer.valueOf(subStrings[1]);
		}

		if (subStrings.length < 2) {
			if (newString.length() == 3) {
				days = Integer.valueOf(newString.charAt(0));
				String monthsString = String.valueOf(newString.charAt(1)) + String.valueOf(newString.charAt(2));
				months = Integer.valueOf(monthsString);
			}
			if (newString.length() == 4) {
				String daysString = String.valueOf(newString.charAt(0)) + String.valueOf(newString.charAt(1));
				days = Integer.valueOf(daysString);
				String monthsString = String.valueOf(newString.charAt(2)) + String.valueOf(newString.charAt(3));
				months = Integer.valueOf(monthsString);
			}
			if (newString.length() == 5) {
				days = Integer.valueOf(newString.charAt(0));
				String monthsString = String.valueOf(newString.charAt(1)) + String.valueOf(newString.charAt(2));
				months = Integer.valueOf(monthsString);
				String yearsString = String.valueOf(newString.charAt(3)) + String.valueOf(newString.charAt(4));
				years = Integer.valueOf(yearsString);
			}
			if (newString.length() == 6 || newString.length() == 7) {
				String daysString = String.valueOf(newString.charAt(0)) + String.valueOf(newString.charAt(1));
				days = Integer.valueOf(daysString);
				String monthsString = String.valueOf(newString.charAt(2)) + String.valueOf(newString.charAt(3));
				months = Integer.valueOf(monthsString);
				String yearsString = String.valueOf(newString.charAt(4)) + String.valueOf(newString.charAt(5));
				years = Integer.valueOf(yearsString);
			}
			if (newString.length() == 8) {
				String daysString = String.valueOf(newString.charAt(0)) + String.valueOf(newString.charAt(1));
				days = Integer.valueOf(daysString);
				String monthsString = String.valueOf(newString.charAt(2)) + String.valueOf(newString.charAt(3));
				months = Integer.valueOf(monthsString);
				String yearsString = String.valueOf(newString.charAt(4)) + String.valueOf(newString.charAt(5))
						+ String.valueOf(newString.charAt(6)) + String.valueOf(newString.charAt(7));
				years = Integer.valueOf(yearsString);
			}
		}
		if (years >= 10 && years < 100) {
			String year = String.valueOf(localDate.now().getYear());
			year = String.valueOf(year.charAt(0)) + String.valueOf(year.charAt(1));
			year = year + String.valueOf(years);
			years = Integer.valueOf(year);
		}
		if (years == 0) {
			years = localDate.now().getYear();
		}
		if (days > 31) {
			date = "";
			return date;
		}
		if (months > 12) {
			date = "";
			return date;
		}
		// Beachten der Monatswechsel
		if (months == 2 && days > 28) {
			if (years % 4 != 0) {
				date = "";
				return date;
			} else {
				if (days != 29) {
					date = "";
					return date;
				}
			}
		} else {
			if (months % 2 == 0 && days == 31) {
				date = "";
				return date;
			}
		}
		if (days == 0 || months == 0 || years == 0) {
			date = "";
			return date;
		}
		localDate = localDate.now();
		localDate = localDate.withDayOfMonth(days);
		localDate = localDate.withMonth(months);
		localDate = localDate.withYear(years);
		date = df.format(localDate);
		return date;
	}

	public static String verifyTime(String newString) {
		String time = newString;
		String allowedCharacters = "1234567890:";
		boolean normalDateInput = true;
		for (int i = 0; i < newString.length(); i++) {
			char c = newString.charAt(i);
			if (allowedCharacters.indexOf(c) == -1) {
				normalDateInput = false;
			}
		}
		if (!normalDateInput || newString.length() != 5) {
			time = translateCommandIntoTime(newString);
		} else {
			if (newString.length() == 5) {
				time = checkForRegularTime(newString);
			}
		}
		return time;
	}

	private static String translateCommandIntoTime(String newString) {
		String allowedCharacters = "1234567890.";
		String subString = newString;
		String time = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime localTime = null;
		Boolean unallowedFormat = false;
		if (subString.contains("jetzt")) {
			localTime = LocalTime.now();
			time = df.format(localTime);
			subString.replaceFirst("jetzt", "");
		}
		if (subString.contains("j")) {
			localTime = LocalTime.now();
			time = df.format(localTime);
			subString.replaceFirst("j", "");
		}

		if (subString.contains("+") || subString.contains("-")) {
			for (int i = 0; i < subString.length(); i++) {
				if (subString.charAt(i) == '+') {
					localTime = localTime.plusHours(1);
				}
				if (subString.charAt(i) == '-') {
					localTime = localTime.minusHours(1);
				}
			}
			time = df.format(localTime);
		} else if (time == "") {
			int points = 0;
			for (int i = 0; i < subString.length(); i++) {
				if (subString.charAt(i) == ':') {
					points++;
				}
				if (allowedCharacters.indexOf(subString.charAt(i)) == -1) {
					unallowedFormat = true;
				}
			}
			if (points > 1) {
				unallowedFormat = true;
			}
			if (unallowedFormat == true) {
				time = "";
				return time;
			} else {
				time = getTimeFromNumbers(subString, df);
			}
		}

		return time;
	}

	private static String getTimeFromNumbers(String subString, DateTimeFormatter df) {
		String time = "";

		LocalTime localTime = null;
		Integer hours = 0;
		Integer minutes = 0;
		String[] subStrings = subString.split(".");
		if (subStrings.length == 2) {
			hours = Integer.valueOf(subStrings[0]);
			minutes = Integer.valueOf(subStrings[1]);
		}

		if (subStrings.length < 2) {
			if (subString.length() == 2) {
				String hoursString = String.valueOf(subString.charAt(0)) + String.valueOf(subString.charAt(1));
				hours = Integer.valueOf(hoursString);
			}
			if (subString.length() == 3) {
				String hour = String.valueOf(subString.charAt(0)) + String.valueOf(subString.charAt(1));
				hours = Integer.valueOf(hour);
				String minutesString = String.valueOf(subString.charAt(2)) + "0";
				minutes = Integer.valueOf(minutesString);
			}
			if (subString.length() == 4) {
				String hour = String.valueOf(subString.charAt(0)) + String.valueOf(subString.charAt(1));
				hours = Integer.valueOf(hour);
				String minutesString = String.valueOf(subString.charAt(2)) + String.valueOf(subString.charAt(3));
				minutes = Integer.valueOf(minutesString);
			}
		}
		if (hours > 23) {
			time = "";
			return time;
		}
		if (minutes > 59) {
			time = "";
			return time;
		}

		localTime = LocalTime.of(hours, minutes);
		time = df.format(localTime);

		return time;
	}

	private static String checkForRegularTime(String newString) {
		String time = newString;

		for (int index = 0; index < newString.length(); index++) {
			if (newString.charAt(index) == ':' && index != 2) {
				time = "";
				return time;
			}
			if (index == 2 && newString.charAt(index) != ':') {
				time = "";
				return time;
			}
		}

		String hour = String.valueOf(newString.charAt(0)) + String.valueOf(newString.charAt(1));
		int hourNumber = Integer.valueOf(hour);
		String minute = String.valueOf(newString.charAt(3)) + String.valueOf(newString.charAt(4));
		int minuteNumber = Integer.valueOf(minute);

		if (hourNumber > 23) {
			time = "";
			return time;
		} else {
			if (minuteNumber > 59) {
				time = "";
				return time;
			}

		}

		return time;
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusLost(FocusEvent e) {
		final String newString = ((Text) e.getSource()).getText();
		if (newString != "") {
			Text t = (Text) e.getSource();
			Field field = (Field) t.getData("field");
			if (field.getShortDate() != null || field.getLongDate() != null) {
				t.setText(verifyDate(newString));
			} else {
				t.setText(verifyTime(newString));
			}
		}

	}
}
