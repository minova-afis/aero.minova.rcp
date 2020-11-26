package aero.minova.rcp.rcp.util;

import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;

import org.junit.Test;

public class TimeUtilTests {
	@Test
	public void testGetTimeString1_59() {
		Instant instant = LocalDate.of(2020, JANUARY, 1).atTime(1, 59).toInstant(ZoneOffset.UTC);

		assertEquals("01:59", TimeUtil.getTimeString(instant, Locale.GERMANY, "UTC"));
	}

	@Test
	public void testGetTime1() {
		Instant bithday = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "1", "UTC"));
	}

	@Test
	public void testGetTime12() {
		Instant bithday = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().plusHours(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "12", "UTC"));
	}

	@Test
	public void testGetTime123() {
		Instant bithday = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().plusHours(12).plusMinutes(30)
				.toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "123", "UTC"));
	}

	@Test
	public void testGetTime1234() {
		Instant bithday = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().plusHours(12).plusMinutes(34)
				.toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "1234", "UTC"));
	}

	@Test
	public void testGetTimePlus() {
		Instant bithday = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "+", "UTC"));
	}

	@Test
	public void testGetTime7Plus1Hour() {
		Instant bithday = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().plusHours(8).toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "7+1h", "UTC"));
	}

	@Test
	public void testGetTime7Plus8HourMinus2Minutes() {
		Instant bithday = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, DECEMBER, 24).atStartOfDay().plusHours(15).minusMinutes(2)
				.toInstant(ZoneOffset.UTC);
		assertEquals(expected, TimeUtil.getTime(bithday, "7+8h-2m", "UTC"));
	}
}
