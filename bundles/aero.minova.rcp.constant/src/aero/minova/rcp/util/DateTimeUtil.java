package aero.minova.rcp.rcp.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateTimeUtil {

	private DateTimeUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static Instant getDateTime(String input) {
		return getDateTime(Instant.now(), input);
	}

	public static Instant getDateTime(Instant now, String input) {
		return getDateTime(now, input, "UTC");
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
	public static Instant getDateTime(Instant todayNow, String input, String zoneId) {

		String[] splitInput = input.split(" ");
		Instant dateIn;
		Instant timeIn;
		Instant dateTime;
		LocalDate dateLocal;
		LocalTime timeLocal;

		if (splitInput.length > 1) {
			if (!splitInput[0].isEmpty()) {
				dateIn = DateUtil.getDate(todayNow, splitInput[0]);
			} else {
				dateIn = DateUtil.getDate(todayNow, "0");
			}

			if (!splitInput[1].isEmpty()) {
				timeIn = TimeUtil.getTime(todayNow, splitInput[1]);
			} else {
				timeIn = TimeUtil.getTime(todayNow, "0");
			}
		} else {
			dateIn = DateUtil.getDate(todayNow, splitInput[0]);
			timeIn = TimeUtil.getTime(todayNow, "0");
		}

		if (null != dateIn && null != timeIn) {
			dateLocal = LocalDate.ofInstant(dateIn, ZoneOffset.UTC);
			timeLocal = LocalTime.ofInstant(timeIn, ZoneOffset.UTC);
		} else {
			return null;
		}
		

		try {
			ZoneId zI = ZoneId.of(zoneId);
			dateTime = ZonedDateTime.of(LocalDateTime.of(dateLocal, timeLocal), zI).toInstant();
		} catch (Exception e) {
			//Invalid ZoneId;
			return null;
		}

		return dateTime;
	}

}
