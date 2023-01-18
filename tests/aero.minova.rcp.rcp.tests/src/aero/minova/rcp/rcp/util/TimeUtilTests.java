package aero.minova.rcp.rcp.util;

import static java.time.Month.JANUARY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import org.junit.Test;

import aero.minova.rcp.util.TimeUtil;

public class TimeUtilTests {

	@Test
	public void testStartofDayGerman() {
		Instant instant = LocalDate.of(1900, JANUARY, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals("00:00", TimeUtil.getTimeString(instant, Locale.GERMANY, "HH:mm"));
	}

	@Test
	public void testGetTimeString1_59() {
		Instant instant = LocalDateTime.of(1900, JANUARY, 1, 1, 59).toInstant(ZoneOffset.UTC);
		assertEquals("01:59", TimeUtil.getTimeString(instant, Locale.GERMANY, "hh:mm"));
	}

	@Test
	public void testGetTime1() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 1, 0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "1"));
	}

	@Test
	public void testGetTime12() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 12, 0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "12"));
	}

	@Test
	public void testGetTime123() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 1, 23).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "123"));
	}

	@Test
	public void testGetTime1234() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 12, 34).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "1234"));
	}

	@Test
	public void testGetTime1234Formatted() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 12, 34).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "1234", "hh:mm:ss", Locale.GERMANY));
	}

	@Test
	public void testGetTime123456Formatted() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		assertNull(TimeUtil.getTime(now, "123456", "hh:mm:ss", Locale.GERMANY));
	}

	@Test
	public void testGetTimePlus() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 1, 0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "+"));
	}

	@Test
	public void testGetTime7Plus1Hour() {
		Instant bithday = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(8).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "7+1h"));
	}

	@Test
	public void testGetTime7Plus8HourMinus2Minutes() {
		Instant bithday = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(15).minusMinutes(2).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "7+8h-2m"));
	}

	@Test
	public void testGetTime0() {
		Instant now = Instant.now();
		Instant expected = LocalDateTime.ofInstant(now, ZoneId.of("UTC")).withYear(1900).withMonth(1).withDayOfMonth(1).truncatedTo(ChronoUnit.MINUTES)
				.toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(expected, "0"));
	}

	@Test
	public void testGetTime0Plus() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 0, 0).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 1, 0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "0+"));
	}

	@Test
	public void testGetDateWithoutSeconds() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 6, 7, 8, 9).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDateTime.of(1900, JANUARY, 1, 6, 7).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "0"));
	}

	@Test
	public void testEmptyString() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 6, 7, 8, 9).toInstant(ZoneOffset.UTC);
		Instant result = TimeUtil.getTime(now, "");
		assertNull(result);
	}

	@Test
	public void test2Plus2() {
		Instant now = LocalDateTime.of(1900, JANUARY, 2, 6, 7, 8, 9).toInstant(ZoneOffset.UTC);
		Instant result = TimeUtil.getTime(now, "2+2");
		assertNull(result);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcutDoubleHour() {
		TimeUtil.setShortcuts("H", "h");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcutDoubleMinute() {
		TimeUtil.setShortcuts("M", "m");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcut2HourChars() {
		TimeUtil.setShortcuts("hh", "m");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcut2MinuteChars() {
		TimeUtil.setShortcuts("h", "mm");
	}

	@Test
	public void entryPM() {
		Instant now = Instant.now();
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(20).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "08:00 PM", "hh:mm a", Locale.US));
	}

}