package aero.minova.rcp.plugin1.textfieldVerifier;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

		localDate = checkDateForCommands(subString, df);
		localDate = checkForReductionAndAddition(localDate, subString);
		if (localDate == null) {
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
		else {
			date = df.format(localDate);
		}

		return date;
	}

	private static LocalDate checkDateForCommands(String subString, DateTimeFormatter df) {
		LocalDate localDate = null;
		// Überprüfen, ob das Datum als Worz/in der Kurzform übergeben wurde
		localDate = getWeekdayFromCommand(subString);
		if (localDate == null) {
			if (subString.contains("[Jj]anuar")) {
				localDate = localDate.now().withMonth(1);
				subString.replaceFirst("[Jj]anuar", "");
			} else if (subString.contains("[Jj]an")) {
				localDate = localDate.now().withMonth(1);
				subString.replaceFirst("[Jj]an", "");
			} else if (subString.contains("[Ff]ebruar")) {
				localDate = localDate.now().withMonth(2);
				subString.replaceFirst("[Ff]ebruar", "");
			} else if (subString.contains("[Ff]eb")) {
				localDate = localDate.now().withMonth(2);
				subString.replaceFirst("[Ff]eb", "");
			} else if (subString.contains("[Mm][äa]rz")) {
				localDate = localDate.now().withMonth(3);
				subString.replaceFirst("Mm][äa]rz", "");
			} else if (subString.contains("[Mm][äa]r")) {
				localDate = localDate.now().withMonth(3);
				subString.replaceFirst("Mm][äa]r", "");
			} else if (subString.contains("[Aa]pril")) {
				localDate = localDate.now().withMonth(4);
				subString.replaceFirst("[Aa]pril", "");
			} else if (subString.contains("[Aa]pr")) {
				localDate = localDate.now().withMonth(4);
				subString.replaceFirst("[Aa]pr", "");
			} else if (subString.contains("[Mm]ai")) {
				localDate = localDate.now().withMonth(5);
				subString.replaceFirst("[Mm]ai", "");
			} else if (subString.contains("[Jj]uni")) {
				localDate = localDate.now().withMonth(6);
				subString.replaceFirst("[Jj]uni", "");
			} else if (subString.contains("[Jj]un")) {
				localDate = localDate.now().withMonth(6);
				subString.replaceFirst("[Jj]un", "");
			} else if (subString.contains("[Jj]uli")) {
				localDate = localDate.now().withMonth(7);
				subString.replaceFirst("[Jj]uli", "");
			} else if (subString.contains("[Jj]ul")) {
				localDate = localDate.now().withMonth(7);
				subString.replaceFirst("[Jj]ul", "");
			} else if (subString.contains("[Aa]ugust")) {
				localDate = localDate.now().withMonth(8);
				subString.replaceFirst("[Aa]ugust", "");
			} else if (subString.contains("[Aa]ug")) {
				localDate = localDate.now().withMonth(8);
				subString.replaceFirst("[Aa]ug", "");
			} else if (subString.contains("[Ss]eptember")) {
				localDate = localDate.now().withMonth(9);
				subString.replaceFirst("[Ss]eptember", "");
			} else if (subString.contains("[Ss]ep")) {
				localDate = localDate.now().withMonth(9);
				subString.replaceFirst("[Ss]ep", "");
			} else if (subString.contains("[Oo]ktober")) {
				localDate = localDate.now().withMonth(10);
				subString.replaceFirst("[Oo]ktober", "");
			} else if (subString.contains("[Oo]kt")) {
				localDate = localDate.now().withMonth(10);
				subString.replaceFirst("[Oo]kt", "");
			} else if (subString.contains("[Nn]ovember")) {
				localDate = localDate.now().withMonth(11);
				subString.replaceFirst("[Nn]ovember", "");
			} else if (subString.contains("[Nn]ov")) {
				localDate = localDate.now().withMonth(11);
				subString.replaceFirst("[Nn]ov", "");
			} else if (subString.contains("[Dd]ezember")) {
				localDate = localDate.now().withMonth(12);
				subString.replaceFirst("[Dd]ezember", "");
			} else if (subString.contains("[Dd]ez")) {
				localDate = localDate.now().withMonth(12);
				subString.replaceFirst("[Dd]ez", "");
			}
			// Überprüfen, ob ein relativer Tag ausgewählt wurde
			else if (subString.contains("heute")) {
				localDate = localDate.now();
				subString.replaceFirst("heute", "");
			} else if (subString.contains("h")) {
				localDate = localDate.now();
				subString.replaceFirst("h", "");
			} else if (subString.contains("morgen")) {
				localDate = localDate.now().plusDays(1);
				subString.replaceFirst("morgen", "");
			} else if (subString.contains("m")) {
				localDate = localDate.now().plusDays(1);
				subString.replaceFirst("m", "");
			} else if (subString.contains("uebermorgen")) {
				localDate = localDate.now().plusDays(2);
				subString.replaceFirst("uebermorgen", "");
			} else if (subString.contains("u")) {
				localDate = localDate.now().plusDays(2);
				subString.replaceFirst("u", "");
			} else if (subString.contains("gestern")) {
				localDate = localDate.now().minusDays(1);
				subString.replaceFirst("gestern", "");
			} else if (subString.contains("g")) {
				localDate = localDate.now().minusDays(1);
				subString.replaceFirst("g", "");
			} else if (subString.contains("vorgestern")) {
				localDate = localDate.now().minusDays(2);
				subString.replaceFirst("vorgestern", "");
			} else if (subString.contains("v")) {
				localDate = localDate.now().minusDays(2);
				subString.replaceFirst("v", "");
			}
		}
		return localDate;
	}

	private static LocalDate getWeekdayFromCommand(String subString) {
		LocalDate calculationDate = LocalDate.now();
		LocalDate date = null;
		DayOfWeek weekday = calculationDate.getDayOfWeek();
		ArrayList<String[]> weekdays = new ArrayList();
		weekdays.add(new String[] { "MONDAY", "Montag" });
		weekdays.add(new String[] { "TUESDAY", "Dienstag" });
		weekdays.add(new String[] { "WEDNESDAY", "Mittwoch" });
		weekdays.add(new String[] { "THURSDAY", "Donnerstag" });
		weekdays.add(new String[] { "FRIDAY", "Freitag" });
		weekdays.add(new String[] { "SATURDAY", "Samstag" });
		weekdays.add(new String[] { "SUNDAY", "Sonntag" });

		for (int i = 0; i < 7; i++) {
			if (subString.contains(weekdays.get(i)[1])) {
				String requested = weekdays.get(i)[0];
				for (int j = 1; j <= 7; j++) {
					if (weekday.plus(j).toString() == requested) {
						date = calculationDate.plusDays(j);
					}
				}
			}
		}

		return date;
	}
	private static LocalDate checkForReductionAndAddition(LocalDate localDate, String subString) {

		if (subString.contains("+") || subString.contains("-")) {
			if (localDate == null) {
				localDate = localDate.now();
			}
			for (int i = 0; i < subString.length(); i++) {
				if (subString.charAt(i) == '+') {
					if (i + 1 < subString.length()) {
						if (subString.charAt(i + 1) == 'W') {
							localDate = localDate.plusDays(7);
						} else if (subString.charAt(i + 1) == 'M') {
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
						if (subString.charAt(i + 1) == 'W') {
							localDate = localDate.minusDays(7);
						} else if (subString.charAt(i + 1) == 'M') {
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
		}

		return localDate;
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
			int[] dateValues = translateNumbersIntoDate(newString);
			if (dateValues == null) {
				return date;
			}
			days = dateValues[0];
			months = dateValues[1];
			years = dateValues[2];
		}
		if ((years >= 10 && years < 100)) {
			String year = String.valueOf(localDate.now().getYear());
			year = String.valueOf(year.charAt(0)) + String.valueOf(year.charAt(1));
			year = year + String.valueOf(years);
			years = Integer.valueOf(year);
		}
		if (years == 0) {
			years = localDate.now().getYear();
		}
		int[] dateValues = checkForCorrectForm(date, days, months, years);
		days = dateValues[0];
		months = dateValues[1];
		years = dateValues[2];
		localDate = localDate.now();
		localDate = localDate.withDayOfMonth(days);
		localDate = localDate.withMonth(months);
		localDate = localDate.withYear(years);
		date = df.format(localDate);
		return date;
	}

	private static int[] checkForCorrectForm(String date, int days, int months, int years) {

		int[] dateValues = new int[3];
		dateValues[0] = days;
		dateValues[1] = months;
		dateValues[2] = years;
		if (years == 0) {
			LocalDate localDate = null;
			dateValues[2] = localDate.now().getYear();
		}
		if (days > 31) {
			dateValues[0] = 31;
		}
		if (months > 12) {
			dateValues[1] = 12;
		}
		// Beachten der Monatswechsel
		if (months == 2 && days > 28) {
			if (years % 4 != 0) {
				dateValues[0] = 28;
			} else {
				if (days != 29) {
					dateValues[0] = 29;
				}
			}
		} else {
			if (months % 2 == 0 && days == 31) {
				dateValues[0] = 30;
			}
		}
		if (days == 0) {
			dateValues[0] = 1;
		}
		if (months == 0) {
			dateValues[1] = 1;
		}
		return dateValues;
	}

	private static int[] translateNumbersIntoDate(String newString) {
		int[] dateValues = null;

		if (newString.length() == 3) {
			dateValues = new int[3];
			dateValues[0] = Integer.valueOf(newString.charAt(0));
			String monthsString = String.valueOf(newString.charAt(1)) + String.valueOf(newString.charAt(2));
			dateValues[1] = Integer.valueOf(monthsString);
			dateValues[2] = 0;
		}
		if (newString.length() == 4) {
			dateValues = new int[3];
			String daysString = String.valueOf(newString.charAt(0)) + String.valueOf(newString.charAt(1));
			dateValues[0] = Integer.valueOf(daysString);
			String monthsString = String.valueOf(newString.charAt(2)) + String.valueOf(newString.charAt(3));
			dateValues[1] = Integer.valueOf(monthsString);
			dateValues[2] = 0;
		}
		if (newString.length() == 5) {
			dateValues = new int[3];
			dateValues[0] = Integer.valueOf(newString.charAt(0));
			String monthsString = String.valueOf(newString.charAt(1)) + String.valueOf(newString.charAt(2));
			dateValues[1] = Integer.valueOf(monthsString);
			String yearsString = String.valueOf(newString.charAt(3)) + String.valueOf(newString.charAt(4));
			dateValues[2] = Integer.valueOf(yearsString);
		}
		if (newString.length() == 6 || newString.length() == 7) {
			dateValues = new int[3];
			String daysString = String.valueOf(newString.charAt(0)) + String.valueOf(newString.charAt(1));
			dateValues[0] = Integer.valueOf(daysString);
			String monthsString = String.valueOf(newString.charAt(2)) + String.valueOf(newString.charAt(3));
			dateValues[1] = Integer.valueOf(monthsString);
			String yearsString = String.valueOf(newString.charAt(4)) + String.valueOf(newString.charAt(5));
			dateValues[2] = Integer.valueOf(yearsString);
		}
		if (newString.length() == 8) {
			dateValues = new int[3];
			String daysString = String.valueOf(newString.charAt(0)) + String.valueOf(newString.charAt(1));
			dateValues[0] = Integer.valueOf(daysString);
			String monthsString = String.valueOf(newString.charAt(2)) + String.valueOf(newString.charAt(3));
			dateValues[1] = Integer.valueOf(monthsString);
			String yearsString = String.valueOf(newString.charAt(4)) + String.valueOf(newString.charAt(5))
					+ String.valueOf(newString.charAt(6)) + String.valueOf(newString.charAt(7));
			dateValues[2] = Integer.valueOf(yearsString);
		}

		return dateValues;
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
			int[] timeList = checkNumbersForTime(subString);
			if (timeList != null) {
				hours = timeList[0];
				minutes = timeList[1];
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

	private static int[] checkNumbersForTime(String subString) {
		int[] time = null;

		if (subString.length() == 2) {
			time = new int[2];
			String hoursString = String.valueOf(subString.charAt(0)) + String.valueOf(subString.charAt(1));
			time[0] = Integer.valueOf(hoursString);
			time[1] = 0;
		}
		if (subString.length() == 3) {
			time = new int[2];
			String hour = String.valueOf(subString.charAt(0)) + String.valueOf(subString.charAt(1));
			time[0] = Integer.valueOf(hour);
			String minutesString = String.valueOf(subString.charAt(2)) + "0";
			time[1] = Integer.valueOf(minutesString);
		}
		if (subString.length() == 4) {
			time = new int[2];
			String hour = String.valueOf(subString.charAt(0)) + String.valueOf(subString.charAt(1));
			time[0] = Integer.valueOf(hour);
			String minutesString = String.valueOf(subString.charAt(2)) + String.valueOf(subString.charAt(3));
			time[1] = Integer.valueOf(minutesString);
		}

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
