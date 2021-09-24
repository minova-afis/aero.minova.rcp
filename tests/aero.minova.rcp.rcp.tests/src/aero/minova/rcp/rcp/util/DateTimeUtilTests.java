package aero.minova.rcp.rcp.util;

import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.SEPTEMBER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.DateUtil;

public class DateTimeUtilTests {

	@BeforeClass
	static public void setGermanShortcuts() {
		DateUtil.setShortcuts("t", "m", "j", "w");
	}

	@Test
	public void testGetDateTime0() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "0"));
	}

	@Test
	public void testGetDateTime_0() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
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
		assertNull(null, DateTimeUtil.getDateTime(bithday, "1"));
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
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 1"));
	}

	@Test
	public void testGetDateTime2() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "2"));
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
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(2).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 2"));
	}

	@Test
	public void testGetDateTime11() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "11"));
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
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(11).toInstant(ZoneOffset.UTC);
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
	public void testGetDate89_89() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDateTime(bithday, "89 89"));
	}

	@Test
	public void testGetDateTime312() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "312 "));
	}

	@Test
	public void testGetDateTime312_312() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, DECEMBER, 3).atStartOfDay().plusHours(3).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "312 312"));
	}

	@Test
	public void testGetDateTime_312() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(3).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 312"));
	}

	@Test
	public void testGetDateTime119() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "119"));
	}

	@Test
	public void testGetDateTime911() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "911"));
	}

	@Test
	public void testGetDateTime911_911() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, NOVEMBER, 9).atStartOfDay().plusHours(9).plusMinutes(11).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "911 911"));
	}

	@Test
	public void testGetDateTime_911() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(9).plusMinutes(11).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 911"));
	}

	@Test
	public void testGetDateTime1109() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "1109"));
	}

	@Test
	public void testGetDateTime1109_0() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1109 0"));
	}

	@Test
	public void testGetDateTime1109_Plus() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(19).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1109 +"));
	}

	@Test
	public void testGetDateTime1109_PlusPlus() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1109 ++"));
	}

	@Test
	public void testGetDateTime1109_PlusPlusPlus() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(21).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1109 +++"));
	}

	@Test
	public void testGetDateTime1109_1109() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(11).plusMinutes(9).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1109 1109"));
	}

	@Test
	public void testGetDateTime_1109() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(11).plusMinutes(9).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 1109"));
	}

	@Test
	public void testGetDateTime1109_2() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(2).plusMinutes(0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1109 2"));
	}

	@Test
	public void testGetDateTime90412() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "90412"));
	}

	@Test
	public void testGetDateTime90412_904() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 9).atStartOfDay().plusHours(9).plusMinutes(4).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "90412 904"));
	}

	@Test
	public void testGetDateTime90412_5() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 9).atStartOfDay().plusHours(5).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "90412 5"));
	}

	@Test
	public void testGetDateTime190412() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "190412"));
	}

	@Test
	public void testGetDateTime190412_10() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 19).atStartOfDay().plusHours(10).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "190412 10"));
	}

	@Test
	public void testGetDateTime3092002() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "3092002"));
	}

	@Test
	public void testGetDateTime_3092002() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(3).plusMinutes(9).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 309"));
	}

	@Test
	public void testGetDateTime13121865() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "13121865"));
	}

	@Test
	public void testGetDateTime13121865_153() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1865, DECEMBER, 13).atStartOfDay().plusHours(1).plusMinutes(53).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "13121865 153"));
	}

	@Test
	public void testGetDateTime13121865_153_3h() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1865, DECEMBER, 13).atStartOfDay().plusHours(4).plusMinutes(53).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "13121865 153+3h"));
	}

	@Test
	public void testGetDateTimeA() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "A"));
	}

	@Test
	public void testGetDateTimeLKW() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDateTime(bithday, "LKW"));
	}

	@Test
	public void testGetDateTimePlus_Plus() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 24).atStartOfDay().plusHours(19).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "+ +"));
	}

	@Test
	public void testGetDateTimePlusPlus_PlusPlus() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 25).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "++ ++"));
	}

	@Test
	public void testGetDateTime1109PlusPlus_PlusPlus() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 13).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1109++ ++"));
	}

	@Test
	public void testGetDateTime2Plus3t_PlusPlus() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 5).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "2+3t ++"));
	}

	@Test
	public void testGetDateTime2Plus3m_PlusPlus() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, Month.AUGUST, 2).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "2+3m ++"));
	}

	@Test
	public void testGetDateTimeEmptyString() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDateTime(bithday, ""));
	}

	@Test
	public void testGetDatePlusPlusMinusOneWeekPlus() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "++-w+"));
	}

	@Test
	public void testGetDateLastOfYear() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, DECEMBER, 31).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "11+1j-"));
	}

	@Test
	public void testGetDate07_01_1967() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, JANUARY, 7).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull(DateUtil.getDate(bithday, "07.01.1967", Locale.GERMANY, "MEDIUM"));
	}

	@Test
	public void testGetDate07Point01Point20() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "07.01.20"));
	}

	@Test
	public void testGetDateTime0minus1() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDateTime(bithday, "0-1 "));
	}

	@Test
	public void testGetDateTime0minus1_0minus1() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDateTime(bithday, "0-1 0-1"));
	}

	@Test
	public void testGetDateTime1Plus1Wplus1J_6Plus1hPlus1m() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2021, MAY, 8).atStartOfDay().plusHours(7).plusMinutes(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1+1W+1j 6+1h+1M"));
	}

	@Test
	public void testGetDateTime01012020PlusPlusPlusM() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, APRIL, 01).atStartOfDay().plusHours(7).plusMinutes(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "01012020+++M 701"));
	}

	@Test
	public void testGetDateTime_825PlusPLusPlusM() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(8).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 825+++M"));
	}

	@Test
	public void testGetDate02Point05() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "02.05"));
	}

	@Test
	public void testGetDate02Point05Point() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "02.05."));
	}

	@Test
	public void testGetDateParse() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		Date date;
		try {
			date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("1.1.21");
			System.out.println(date.toString());
		} catch (ParseException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
		assertNull(null, DateTimeUtil.getDateTime(bithday, "1.1 1"));
	}

	@Test
	public void testGetDate1Point1() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "1.1"));
	}

	@Test
	public void testGetDate1Point1Point() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "1.1."));
	}

	@Test
	public void testGetDate1Point1PointPoint() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "1.1"));
	}

	@Test
	public void testGetDate02Point05Point1988() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "02.05.1988"));
	}

	@Test
	public void testGetDate02Point05Point20() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull(null, DateTimeUtil.getDateTime(bithday, "02.05.20"));
	}

	@Test
	public void testGetDate02Point05_205() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDateTime(bithday, "02.05 205"));
	}

	@Test
	public void testGetDate070120GERMANY() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "070120 1", Locale.GERMANY, "", ""));
	}

	@Test
	public void testGetDate070120US() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "070120 1", Locale.US, "dd-MM-yy", "hh:mm"));
	}

	@Test
	public void testGetDate07Point01Point20GERMANY() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "07.01.20 1", Locale.GERMANY));
	}

	@Test
	public void testGetDate07Slash01Slash20US() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, Month.JULY, 1).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "07/01/20 1", Locale.US));
	}

	@Test
	public void testGetDate07Slash01SlashUS() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDateTime(bithday, "07/01 1", Locale.US));
	}

	@Test
	public void testGetDate070120Stern1GERMANY() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "070120*1", Locale.GERMANY));
	}

	@Test
	public void testGetDate02Slash05Slash1988Stern1530() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2088, Month.FEBRUARY, 5).atStartOfDay().plusHours(15).plusMinutes(30).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "02/05/88*1530", Locale.US));
	}

	@Test
	public void testGetDateTimeStern825PlusPLusPlusM() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(8).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "*825+++M"));
	}

	@Test
	public void testGetDateTimeZoneIDEuropeMonaco() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("Europe/Monaco")).toInstant();
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(8).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "13052020 1028", "Europe/Monaco"));
	}

	@Test
	public void testGetDateTimeZoneIDEuropeLondon() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("Europe/London")).toInstant();
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(9).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "13052020 1028", "Europe/London"));
	}

	@Test
	public void testGetDateTimeZoneIDAmericaJuneau() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("America/Juneau")).toInstant();
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "13052020 1028", "America/Juneau"));
	}

	@Test
	public void testGetDateTimeZoneIDAmericaParis() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("America/Juneau")).toInstant();
		assertNull(null, DateTimeUtil.getDateTime(bithday, "13052020 1028", "America/Paris"));
	}

	@Test
	public void testGetDateTimeString() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals("13.05.20 18:12", DateTimeUtil.getDateTimeString(bithday, Locale.GERMANY, "dd.MM.yy", "HH:mm"));
	}
}
