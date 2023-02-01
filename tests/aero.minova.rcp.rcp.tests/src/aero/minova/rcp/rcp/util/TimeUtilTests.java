package aero.minova.rcp.rcp.util;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import aero.minova.rcp.util.TimeUtil;

class TimeUtilTests {

	@Test
	void testStartofDayGerman() {
		Instant instant = LocalDate.of(1900, JANUARY, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals("00:00", TimeUtil.getTimeString(instant, Locale.GERMANY, "HH:mm"));
	}

	@Test
	void testGetTimeString1_59() {
		Instant instant = LocalDateTime.of(1900, JANUARY, 1, 1, 59).toInstant(ZoneOffset.UTC);
		assertEquals("01:59", TimeUtil.getTimeString(instant, Locale.GERMANY, "hh:mm"));
	}

	@Test
	void testGetTime1() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 1, 0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "1"));
	}

	@Test
	void testGetTime12() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 12, 0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "12"));
	}

	@Test
	void testGetTime123() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 1, 23).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "123"));
	}

	@Test
	void testGetTime1234() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 12, 34).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "1234"));
	}

	@Test
	void testGetTime1234Formatted() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 12, 34).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "1234", "hh:mm:ss", Locale.GERMANY));
	}

	@Test
	void testGetTime123456Formatted() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		assertNull(TimeUtil.getTime(now, "123456", "hh:mm:ss", Locale.GERMANY));
	}

	@Test
	void testGetTimePlus() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 1, 0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "+"));
	}

	@Test
	void testGetTime7Plus1Hour() {
		Instant bithday = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(8).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "7+1h"));
	}

	@Test
	void testGetTime7Plus8HourMinus2Minutes() {
		Instant bithday = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(15).minusMinutes(2).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "7+8h-2m"));
	}

	@Test
	void testGetTime0() {
		Instant now = Instant.now();
		Instant expected = LocalDateTime.ofInstant(now, ZoneId.of("UTC")).withYear(1900).withMonth(1).withDayOfMonth(1).truncatedTo(ChronoUnit.MINUTES)
				.toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(expected, "0"));
	}

	@Test
	void testGetTime0Plus() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 1, 0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "0+"));
	}

	@Test
	void testGetDateWithoutSeconds() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 6, 7, 8, 9).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 6, 7).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "0"));
	}

	@Test
	void testEmptyString() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 6, 7, 8, 9).toInstant(ZoneOffset.UTC);
		Instant result = TimeUtil.getTime(now, "");
		assertNull(result);
	}

	@Test
	void test2Plus2() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 6, 7, 8, 9).toInstant(ZoneOffset.UTC);
		Instant result = TimeUtil.getTime(now, "2+2");
		assertNull(result);
	}

	@Test
	void testShortcutDoubleHour() {
		assertThrows(IllegalArgumentException.class, () -> TimeUtil.setShortcuts("H", "h"));
	}

	@Test
	void testShortcutDoubleMinute() {
		assertThrows(IllegalArgumentException.class, () -> TimeUtil.setShortcuts("M", "m"));
	}

	@Test
	void testShortcut2HourChars() {
		assertThrows(IllegalArgumentException.class, () -> TimeUtil.setShortcuts("hh", "m"));
	}

	@Test
	void testShortcut2MinuteChars() {
		assertThrows(IllegalArgumentException.class, () -> TimeUtil.setShortcuts("h", "mm"));
	}

	@Test
	void entryPM() {
		Instant now = Instant.now();
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(20).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "08:00 PM", "hh:mm a", Locale.US));
	}

}