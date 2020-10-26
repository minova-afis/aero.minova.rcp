package aero.minova.rcp.rcp.util;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.form.model.xsd.Field;

public class TextfieldVerifier implements FocusListener {

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
		String date = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());
		Instant instant = DateTimeUtil.getDate(newString);
		if (instant != null) {
			date = df.format(instant);
		} else {
			date = "";
		}
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
		if (subString.length() == 1) {
			time = new int[2];
			time[0] = Integer.valueOf(subString);
			time[1] = 0;
		}
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
