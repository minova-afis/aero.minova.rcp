package aero.minova.rcp.rcp.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class DateTimeUtil {

	private DateTimeUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static Instant getDateTime(Instant todayNow, String input) {

		String[] splitInput = input.split(" ");
		Instant dateIn;
		Instant timeIn;

		if (splitInput.length > 1) {
			if (!splitInput[0].isEmpty()) {
				dateIn = DateUtil.getDate(todayNow, splitInput[0]);
			} else {
				dateIn = DateUtil.getDate("0");
			}

			if (!splitInput[1].isEmpty()) {
				timeIn = TimeUtil.getTime(todayNow, splitInput[1]);
			} else {
				timeIn = TimeUtil.getTime("0");
			}
		} else {
			dateIn = DateUtil.getDate(todayNow, splitInput[0]);
			timeIn = TimeUtil.getTime(todayNow,"0");
		}

		LocalDate dateLocal = LocalDate.ofInstant(dateIn, ZoneOffset.UTC);
		LocalTime timeLocal = LocalTime.ofInstant(timeIn, ZoneOffset.UTC);

		Instant dateTime = LocalDateTime.of(dateLocal, timeLocal).toInstant(ZoneOffset.UTC);

		return dateTime;
	}

}
