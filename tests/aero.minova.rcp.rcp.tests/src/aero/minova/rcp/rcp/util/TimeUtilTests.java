package aero.minova.rcp.rcp.util;

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
}
