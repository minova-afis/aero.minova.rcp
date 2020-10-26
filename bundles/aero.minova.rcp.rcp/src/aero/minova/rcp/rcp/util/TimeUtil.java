package aero.minova.rcp.rcp.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtil {

	static public Instant getTime(String input) {

		return getTime(Instant.now(), input);
	}

	static public Instant getTime(Instant today, String input) {

		if (input.contains("-") || input.contains("+")) {
			today = changeHours(today, input);
		} else {
			today = getTimeFromNumbers(input);
		}
		return today;
	}

	private static Instant changeHours(Instant instant, String input) {

		boolean correctInput = true;
		LocalDateTime lt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		for (int i = 0; i < input.length(); i++) {
			if (correctInput = true) {
				if (input.charAt(i) == '+') {
					lt = lt.plusHours(1);
				} else if (input.charAt(i) == '-') {
					lt = lt.minusHours(1);
				} else {
					correctInput = false;
				}
			}
		}
		if (correctInput == true) {
			instant = lt.toInstant(ZoneId.systemDefault().getRules().getOffset(lt));
		} else {
			instant = null;
		}
		return instant;
	}

	private static Instant getTimeFromNumbers(String subString) {

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

		LocalDateTime localDateTime = LocalDateTime.now().withHour(hours).withMinute(minutes).withSecond(0);
		Instant instant = localDateTime.toInstant(ZoneId.systemDefault().getRules().getOffset(localDateTime));
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
