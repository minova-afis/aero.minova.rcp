package aero.minova.rcp.rcp.util;

import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.SEPTEMBER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;

import org.junit.BeforeClass;
import org.junit.Test;

public class DateTimeUtilTests {

	@BeforeClass
	static public void setGermanShortcuts() {
		DateTimeUtil.setShortcuts("t", "m", "j", "w");
	}

	@Test
	public void testEmptyString() {
		Instant today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant result = DateTimeUtil.getDate(today, "");
		assertNull(result);
	}

	@Test
	public void testGetDate0() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "0"));
	}

	@Test
	public void testGetDate1() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "1"));
	}

	@Test
	public void testGetDate2() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "2"));
	}

	@Test
	public void testGetDate11() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, JANUARY, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "11"));
	}

	@Test
	public void testGetDate89() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 8).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "89"));
	}

	@Test
	public void testGetDate312() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, DECEMBER, 3).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "312"));
	}

	@Test
	public void testGetDate119() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		assertEquals(null, DateTimeUtil.getDate(bithday, "119"));
	}

	@Test
	public void testGetDate911() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, NOVEMBER, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "911"));
	}

	@Test
	public void testGetDate1109() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "1109"));
	}

	@Test
	public void testGetDate90412() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "90412"));
	}

	@Test
	public void testGetDate190412() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 19).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "190412"));
	}

	@Test
	public void testGetDate3092002() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2002, SEPTEMBER, 3).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "3092002"));
	}

	@Test
	public void testGetDate13121865() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1865, DECEMBER, 13).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "13121865"));
	}

	@Test
	public void testGetDate23051967Plus() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 24).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "+"));
	}

	@Test
	public void testGetDate29022020() {
		Instant bithday = LocalDate.of(2020, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, FEBRUARY, 29).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "11+2m-"));
	}

	@Test
	public void testGetDatePlus2Weeks() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 27).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "0+2w"));
	}

	@Test
	public void testGetDatePlusPlusPlus() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 16).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "+++"));
	}

	@Test
	public void testGetDatePlusPlusMinusPlus() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 15).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "++-+"));
	}

	@Test
	public void testGetDatePlusPlusMinus2WeeksPlus() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "++-2w+"));
	}

	@Test
	public void testGetDatePlusPlusMinusOneWeekPlus() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "++-w+"));
	}

	@Test
	public void testGetDateLastOfYear() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, DECEMBER, 31).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "11+1j-"));
	}

	@Test
	public void testGetDate07_01_1967() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, JANUARY, 7).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "07.01.1967", Locale.GERMANY));
	}

	@Test
	public void testGetDate7_1_1967() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDate(bithday, "7.1.1967", Locale.GERMANY));
	}

	@Test
	public void testGetDate07_01_20() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12)
				.toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(20, JANUARY, 7).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDate(bithday, "07.01.20", Locale.GERMANY));
	}

	@Test
	public void testGetDate0minus1() {
		assertNull(DateTimeUtil.getDate("0-1", Locale.GERMANY));
	}

	@Test
	public void testGetDateString01_01_2020() {
		Instant instant = LocalDate.of(2020, JANUARY, 1).atStartOfDay().toInstant(ZoneOffset.UTC);

		assertEquals("01.01.2020", DateTimeUtil.getDateString(instant, Locale.GERMANY));
	}

	@Test
	public void testSplitInputPlus4t() {
		String splits[] = DateTimeUtil.splitInput("123+4t");
		assertEquals("123", splits[0]);
		assertEquals("+4t", splits[1]);
	}

	@Test
	public void testSplitInput29February() {
		String splits[] = DateTimeUtil.splitInput("01012020+2M-1T");
		assertEquals("01012020", splits[0]);
		assertEquals("+2m", splits[1]);
		assertEquals("-1t", splits[2]);
	}

	@Test
	public void testSplitInputPlusPlus() {
		String splits[] = DateTimeUtil.splitInput("01012020+++M");
		assertEquals("01012020", splits[0]);
		assertEquals("+++m", splits[1]);
	}

	@Test
	public void testSplitInputPlusPlusPlus() {
		String splits[] = DateTimeUtil.splitInput("+++");
		assertEquals("+++t", splits[0]);
	}

	@Test
	public void testWrongStringTT() {
		String splits[] = DateTimeUtil.splitInput("+TT");
		assertEquals(0, splits.length);
	}

	@Test
	public void testWrongStringT() {
		String splits[] = DateTimeUtil.splitInput("T");
		assertEquals(0, splits.length);
	}

	@Test
	public void testWrongStringPlusMinus() {
		String splits[] = DateTimeUtil.splitInput("+0-3t");
		assertEquals(0, splits.length);
	}

	@Test
	public void testSplitInputTodayPlus1DayMinus3Day() {
		String splits[] = DateTimeUtil.splitInput("0+-3t");
		assertEquals(3, splits.length);
		assertEquals("0", splits[0]);
		assertEquals("+t", splits[1]);
		assertEquals("-3t", splits[2]);
	}

	@Test
	public void testSplitInputWrong31Day() {
		String splits[] = DateTimeUtil.splitInput("3t");
		assertEquals(0, splits.length);
	}

	@Test
	public void testSplitInputWrongTodayPlus1DayMinus3DayDay() {
		String splits[] = DateTimeUtil.splitInput("0+-3tt");
		assertEquals(0, splits.length);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcutWrongDay() {
		DateTimeUtil.setShortcuts("T", "t", "y", "w");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcutDoubleMonth() {
		DateTimeUtil.setShortcuts("t", "m", "m", "w");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcutDoubleYear() {
		DateTimeUtil.setShortcuts("t", "m", "y", "y");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcut2DayCahrs() {
		DateTimeUtil.setShortcuts("tt", "m", "y", "w");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcut2MonthCahrs() {
		DateTimeUtil.setShortcuts("t", "nm", "y", "w");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcut2YearCahrs() {
		DateTimeUtil.setShortcuts("t", "m", "ay", "w");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testShortcut2WeekCahrs() {
		DateTimeUtil.setShortcuts("t", "m", "y", "ww");
	}
}
