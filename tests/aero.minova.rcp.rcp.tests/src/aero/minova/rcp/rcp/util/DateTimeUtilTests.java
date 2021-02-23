package aero.minova.rcp.rcp.util;

import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.SEPTEMBER;
import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.BeforeClass;
import org.junit.Test;

public class DateTimeUtilTests {

	@BeforeClass
	static public void setGermanShortcuts() {
		DateUtil.setShortcuts("t", "m", "j", "w");
	}

	@Test
	public void testGetDateTime0() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "0"));
	}

	@Test
	public void testGetDateTime_0() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.now().atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 0"));
	}

	@Test
	public void testGetDateTime0_0() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "0 0"));
	}

	@Test
	public void testGetDateTime0_00() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "0 00"));
	}

	@Test
	public void testGetDateTime1() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 1).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1"));
	}

	@Test
	public void testGetDateTime1_1() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 1).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1 1"));
	}

	@Test
	public void testGetDateTime_1() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.now().atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 1"));
	}

	@Test
	public void testGetDateTime2() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 2).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "2"));
	}

	@Test
	public void testGetDateTime2_2() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 2).atStartOfDay().plusHours(2).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "2 2"));
	}

	@Test
	public void testGetDateTime_2() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.now().atStartOfDay().plusHours(2).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 2"));
	}

	@Test
	public void testGetDateTime11() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, JANUARY, 1).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "11"));
	}
	
	@Test
	public void testGetDateTime11_11() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, JANUARY, 1).atStartOfDay().plusHours(11).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "11 11"));
	}

	@Test
	public void testGetDateTime_11() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.now().atStartOfDay().plusHours(11).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 11"));
	}
	
	@Test
	public void testGetDate89() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 8).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "89"));
	}
	
	@Test
	public void testGetDate89_809() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 8).atStartOfDay().plusHours(8).plusMinutes(9).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "89 809"));
	}

	@Test
	public void testGetDate312() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, DECEMBER, 3).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "312"));
	}

	@Test
	public void testGetDate119() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(null, DateUtil.getDate(bithday, "119"));
	}

	@Test
	public void testGetDate911() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, NOVEMBER, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "911"));
	}

	@Test
	public void testGetDate1109() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "1109"));
	}

	@Test
	public void testGetDate90412() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "90412"));
	}

	@Test
	public void testGetDate190412() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 19).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "190412"));
	}

	@Test
	public void testGetDate3092002() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2002, SEPTEMBER, 3).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "3092002"));
	}

	@Test
	public void testGetDate13121865() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1865, DECEMBER, 13).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "13121865"));
	}

}
