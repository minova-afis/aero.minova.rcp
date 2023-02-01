package aero.minova.rcp.rcp.util;

import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.SEPTEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import aero.minova.rcp.util.DateUtil;

class DateUtilTests {

	@BeforeAll
	static void setGermanShortcuts() {
		DateUtil.setShortcuts("t", "m", "j", "w");
	}

	@Test
	void testEmptyString() {
		Instant today = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant result = DateUtil.getDate(today, "");
		assertNull(result);
	}

	@Test
	void testGetDate2w() {
		Instant bithday = LocalDate.of(1967, MAY, 1).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 15).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "+2w"));
	}

	@Test
	void testGetDate0() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "0"));
	}

	@Test
	void testGetDate1() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "1"));
	}

	@Test
	void testGetDate2() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "2"));
	}

	@Test
	void testGetDate11() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, JANUARY, 1).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "11"));
	}

	@Test
	void testGetDate89() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 8).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "89"));
	}

	@Test
	void testGetDate312() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, DECEMBER, 3).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "312"));
	}

	@Test
	void testGetDate119() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(null, DateUtil.getDate(bithday, "119"));
	}

	@Test
	void testGetDate911() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, NOVEMBER, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "911"));
	}

	@Test
	void testGetDateString911() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals("23.05.1967", DateUtil.getDateString(bithday, Locale.GERMANY, "dd.MM.yyyy"));
	}

	@Test
	void testGetDate1109() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "1109"));
	}

	@Test
	void testGetDate90412() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "90412"));
	}

	@Test
	void testGetDate190412() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1912, APRIL, 19).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "190412"));
	}

	@Test
	void testGetDate3092002() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2002, SEPTEMBER, 3).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "3092002"));
	}

	@Test
	void testGetDate13121865() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1865, DECEMBER, 13).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "13121865"));
	}

	@Test
	void testGetDate23051967Plus() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, MAY, 24).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "+"));
	}

	@Test
	void testGetDate29022020() {
		Instant bithday = LocalDate.of(2020, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, FEBRUARY, 29).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "11+2m-"));
	}

	@Test
	void testGetDatePlus2Weeks() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 27).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "0+2w"));
	}

	@Test
	void testGetDatePlusPlusPlus() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 16).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "+++"));
	}

	@Test
	void testGetDatePlusPlusMinusPlus() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 15).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "++-+"));
	}

	@Test
	void testGetDatePlusPlusMinus2WeeksPlus() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 2).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "++-2w+"));
	}

	@Test
	void testGetDatePlusPlusMinusOneWeekPlus() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 9).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "++-w+"));
	}

	@Test
	void testGetDateLastOfYear() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, DECEMBER, 31).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "11+1j-"));
	}

	@Test
	void testGetDate07_01_1967() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(1967, JANUARY, 7).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "07.01.1967", Locale.GERMANY, "dd.MM.yyyy"));
	}

	@Test
	void testGetDate7_1_1967() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateUtil.getDate(bithday, "7.1.1967", Locale.GERMANY, "dd.MM.yyyy"));
	}

	@Test
	void testGetDate07_01_20() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(bithday, "07.01.20", Locale.GERMANY, ""));
	}

	@Test
	void testGetDate0minus1() {
		assertNull(DateUtil.getDate("0-1"));
	}

	@Test
	void testSplitInputPlus4t() {
		String splits[] = DateUtil.splitInput("123+4t");
		assertEquals("123", splits[0]);
		assertEquals("+4t", splits[1]);
	}

	@Test
	void testSplitInput29February() {
		String splits[] = DateUtil.splitInput("01012020+2M-1T");
		assertEquals("01012020", splits[0]);
		assertEquals("+2m", splits[1]);
		assertEquals("-1t", splits[2]);
	}

	@Test
	void testSplitInputPlusPlus() {
		String splits[] = DateUtil.splitInput("01012020+++M");
		assertEquals("01012020", splits[0]);
		assertEquals("+++m", splits[1]);
	}

	@Test
	void testSplitInputPlusPlusPlus() {
		String splits[] = DateUtil.splitInput("+++");
		assertEquals("+++t", splits[0]);
	}

	@ParameterizedTest
	@ValueSource(strings = { "+TT", "T", "+0-3t", "3t", "0+-3tt" })
	void testWrongStringTT(String arg) {
		String splits[] = DateUtil.splitInput(arg);
		assertEquals(0, splits.length);
	}

	@Test
	void testSplitInputTodayPlus1DayMinus3Day() {
		String splits[] = DateUtil.splitInput("0+-3t");
		assertEquals(3, splits.length);
		assertEquals("0", splits[0]);
		assertEquals("+t", splits[1]);
		assertEquals("-3t", splits[2]);
	}

	@Test
	void testShortcutWrongDay() {
		assertThrows(IllegalArgumentException.class, () -> DateUtil.setShortcuts("T", "t", "y", "w"));
	}

	@Test
	void testShortcutDoubleMonth() {
		assertThrows(IllegalArgumentException.class, () -> DateUtil.setShortcuts("t", "m", "m", "w"));
	}

	@Test
	void testShortcutDoubleYear() {
		assertThrows(IllegalArgumentException.class, () -> DateUtil.setShortcuts("t", "m", "y", "y"));
	}

	@Test
	void testShortcut2DayCahrs() {
		assertThrows(IllegalArgumentException.class, () -> DateUtil.setShortcuts("tt", "m", "y", "w"));
	}

	@Test
	void testShortcut2MonthCahrs() {
		assertThrows(IllegalArgumentException.class, () -> DateUtil.setShortcuts("t", "nm", "y", "w"));
	}

	@Test
	void testShortcut2YearCahrs() {
		assertThrows(IllegalArgumentException.class, () -> DateUtil.setShortcuts("t", "m", "ay", "w"));
	}

	@Test
	void testShortcut2WeekCahrs() {
		assertThrows(IllegalArgumentException.class, () -> DateUtil.setShortcuts("t", "m", "y", "ww"));
	}
}
