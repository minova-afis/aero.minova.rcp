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

public class TimeUtilTests {
	@Test
	public void testGetTimeString1_59() {
		Instant instant = LocalDate.of(1900, JANUARY, 1).atTime(1, 59).toInstant(ZoneOffset.UTC);

		assertEquals("01:59", TimeUtil.getTimeString(instant, Locale.GERMANY, "UTC"));
	}

	@Test
	public void testGetTime1() {
		Instant bithday = LocalDate.of(1900, JANUARY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "1", "UTC"));
	}

	@Test
	public void testGetTime12() {
		Instant bithday = LocalDate.of(1900, JANUARY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "12", "UTC"));
	}

	@Test
	public void testGetTime123() {
		Instant bithday = LocalDate.of(1900, JANUARY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(12).plusMinutes(30).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "123", "UTC"));
	}

	@Test
	public void testGetTime1234() {
		Instant bithday = LocalDate.of(1900, JANUARY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(12).plusMinutes(34).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "1234", "UTC"));
	}

	@Test
	public void testGetTimePlus() {
		Instant bithday = LocalDate.of(1900, JANUARY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "+", "UTC"));
	}

	@Test
	public void testGetTime7Plus1Hour() {
		Instant bithday = LocalDate.of(1900, JANUARY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(8).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "7+1h", "UTC"));
	}

	@Test
	public void testGetTime7Plus8HourMinus2Minutes() {
		Instant bithday = LocalDate.of(1900, JANUARY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(15).minusMinutes(2).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "7+8h-2m", "UTC"));
	}

	@Test
	public void testGetTime0() {
		Instant now = Instant.now();
		Instant expected = LocalDateTime.ofInstant(now, ZoneId.of("UTC")).withYear(1900).withMonth(1).withDayOfMonth(1).truncatedTo(ChronoUnit.MINUTES)
				.toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(expected, "0", "UTC"));
	}

	@Test
	public void testGetTime0Plus() {
		Instant birthday = LocalDate.of(1900, JANUARY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1900, JANUARY, 1).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(birthday, "0+", "UTC"));
	}

	@Test
	public void testGetDateWithoutSeconds() {
		Instant now = Instant.now();
		Instant expected = LocalDateTime.ofInstant(now, ZoneId.of("UTC")).withYear(1900).withMonth(1).withDayOfMonth(1).truncatedTo(ChronoUnit.MINUTES)
				.toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(now, "0", "UTC"));
	}

	@Test
	public void testEmptyString() {
		Instant today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant result = TimeUtil.getTime(today, "", "UTC");
		assertNull(result);
	}

	@Test
	public void test2Plus2() {
		Instant today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant result = TimeUtil.getTime(today, "2+2", "UTC");
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
}
