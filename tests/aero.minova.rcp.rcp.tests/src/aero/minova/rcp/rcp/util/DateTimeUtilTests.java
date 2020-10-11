package aero.minova.rcp.rcp.util;

import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.SEPTEMBER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.Test;

public class DateTimeUtilTests {

	@Test
	public void testEmptyString() {
		Instant today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant result = DateTimeUtil.getDate(today, "");
		assertNull(result);
	}

	@Test
	public void testString0() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "0"));
	}

	@Test
	public void testString1() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "1"));
	}

	@Test
	public void testString2() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "2"));
	}

	@Test
	public void testString11() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, JANUARY, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "11"));
	}

	@Test
	public void testString89() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 8).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "89"));
	}

	@Test
	public void testString312() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, DECEMBER, 3).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "312"));
	}
	
	@Test
	public void testString119() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		assertEquals(null, DateTimeUtil.getDate(bithday, "119"));
	}

	@Test
	public void testString911() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, NOVEMBER, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "911"));
	}
	
	@Test
	public void testString1109() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "1109"));
	}
	
	@Test
	public void testString90412() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "90412"));
	}
	
	@Test
	public void testString190412() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 19).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "190412"));
	}
	
	@Test
	public void testString3092002() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2002, SEPTEMBER, 3).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "3092002"));
	}
	
	@Test
	public void testString13121865() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1865, DECEMBER, 13).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "13121865"));
	}
	
}
