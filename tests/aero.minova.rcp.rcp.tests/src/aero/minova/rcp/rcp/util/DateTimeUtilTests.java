package aero.minova.rcp.rcp.util;

import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.Month.MAY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.SEPTEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.DateUtil;

class DateTimeUtilTests {

	private Instant initialDay;

	@BeforeAll
	static void setGermanShortcuts() {
		DateUtil.setShortcuts("t", "m", "j", "w");
	}

	@BeforeEach
	void setup () {
		initialDay = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
	}
	@Test
	void testGetDateTime0() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "0"));
	}

	@Test
	void testGetDateTime_0() {
		assertEquals(initialDay, DateTimeUtil.getDateTime(initialDay, " 0"));
	}

	@Test
	void testGetDateTime0_0() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "0 0"));
	}

	@Test
	void testGetDateTime0_00() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "0 00"));
	}

	@Test
	void testGetDateTime1() {
		assertNull(DateTimeUtil.getDateTime(initialDay, "1"));
	}

	@Test
	void testGetDateTime1_1() {
		Instant expected = LocalDate.of(1967, MAY, 1).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1 1"));
	}

	@Test
	void testGetDateTime_1() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 1"));
	}

	@Test
	void testGetDateTime2() {
		assertNull(DateTimeUtil.getDateTime(initialDay, "2"));
	}

	@Test
	void testGetDateTime2_2() {
		Instant expected = LocalDate.of(1967, MAY, 2).atStartOfDay().plusHours(2).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "2 2"));
	}

	@Test
	void testGetDateTime_2() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(2).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 2"));
	}

	@Test
	void testGetDateTime11() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "11"));
	}

	@Test
	void testGetDateTime11_11() {
		Instant expected = LocalDate.of(1967, JANUARY, 1).atStartOfDay().plusHours(11).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "11 11"));
	}

	@Test
	void testGetDateTime_11() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(11).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 11"));
	}

	@Test
	void testGetDate89() {
		
		Instant expected = LocalDate.of(1967, SEPTEMBER, 8).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateUtil.getDate(initialDay, "89"));
	}

	@Test
	void testGetDate89_809() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 8).atStartOfDay().plusHours(8).plusMinutes(9).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "89 809"));
	}

	@Test
	void testGetDate89_89() {
		
		assertNull(DateTimeUtil.getDateTime(initialDay, "89 89"));
	}

	@Test
	void testGetDateTime312() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "312 "));
	}

	@Test
	void testGetDateTime312_312() {
		Instant expected = LocalDate.of(1967, DECEMBER, 3).atStartOfDay().plusHours(3).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "312 312"));
	}

	@Test
	void testGetDateTime_312() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(3).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 312"));
	}

	@Test
	void testGetDateTime119() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "119"));
	}

	@Test
	void testGetDateTime911() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "911"));
	}

	@Test
	void testGetDateTime911_911() {
		Instant expected = LocalDate.of(1967, NOVEMBER, 9).atStartOfDay().plusHours(9).plusMinutes(11).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "911 911"));
	}

	@Test
	void testGetDateTime_911() {
		
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(9).plusMinutes(11).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 911"));
	}

	@Test
	void testGetDateTime1109() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "1109"));
	}

	@Test
	void testGetDateTime1109_0() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 0"));
	}

	@Test
	void testGetDateTime1109_Plus() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(19).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 +"));
	}

	@Test
	void testGetDateTime1109_PlusPlus() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 ++"));
	}

	@Test
	void testGetDateTime1109_PlusPlusPlus() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(21).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 +++"));
	}

	@Test
	void testGetDateTime1109_1109() {
		
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(11).plusMinutes(9).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 1109"));
	}

	@Test
	void testGetDateTime_1109() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(11).plusMinutes(9).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 1109"));
	}

	@Test
	void testGetDateTime1109_2() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(2).plusMinutes(0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 2"));
	}

	@Test
	void testGetDateTime90412() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "90412"));
	}

	@Test
	void testGetDateTime90412_904() {
		Instant expected = LocalDate.of(1912, APRIL, 9).atStartOfDay().plusHours(9).plusMinutes(4).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "90412 904"));
	}

	@Test
	void testGetDateTime90412_5() {
		Instant expected = LocalDate.of(1912, APRIL, 9).atStartOfDay().plusHours(5).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "90412 5"));
	}

	@Test
	void testGetDateTime190412() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "190412"));
	}

	@Test
	void testGetDateTime190412_10() {
		Instant expected = LocalDate.of(1912, APRIL, 19).atStartOfDay().plusHours(10).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "190412 10"));
	}

	@Test
	void testGetDateTime3092002() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "3092002"));
	}

	@Test
	void testGetDateTime_3092002() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(3).plusMinutes(9).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 309"));
	}

	@Test
	void testGetDateTime13121865() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "13121865"));
	}

	@Test
	void testGetDateTime13121865_153() {
		Instant expected = LocalDate.of(1865, DECEMBER, 13).atStartOfDay().plusHours(1).plusMinutes(53).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "13121865 153"));
	}

	@Test
	void testGetDateTime13121865_153_3h() {
		Instant expected = LocalDate.of(1865, DECEMBER, 13).atStartOfDay().plusHours(4).plusMinutes(53).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "13121865 153+3h"));
	}

	@Test
	void testGetDateTimeA() {
		assertNull( DateTimeUtil.getDateTime(initialDay, "A"));
	}

	@Test
	void testGetDateTimeLKW() {
		
		assertNull(DateTimeUtil.getDateTime(initialDay, "LKW"));
	}

	@Test
	void testGetDateTimePlus_Plus() {
		
		Instant expected = LocalDate.of(1967, MAY, 24).atStartOfDay().plusHours(19).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "+ +"));
	}

	@Test
	void testGetDateTimePlusPlus_PlusPlus() {
		Instant expected = LocalDate.of(1967, MAY, 25).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "++ ++"));
	}

	@Test
	void testGetDateTime1109PlusPlus_PlusPlus() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 13).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109++ ++"));
	}

	@Test
	void testGetDateTime2Plus3t_PlusPlus() {
		Instant expected = LocalDate.of(1967, MAY, 5).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "2+3t ++"));
	}

	@Test
	void testGetDateTime2Plus3m_PlusPlus() {
		Instant expected = LocalDate.of(1967, Month.AUGUST, 2).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "2+3m ++"));
	}

	@Test
	void testGetDateTimeEmptyString() {
		assertNull(DateTimeUtil.getDateTime(initialDay, ""));
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
		assertNull(DateUtil.getDate(bithday, "07.01.1967", Locale.GERMANY, "MEDIUM"));
	}

	@Test
	void testGetDate07Point01Point20() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull( DateTimeUtil.getDateTime(bithday, "07.01.20"));
	}

	@Test
	void testGetDateTime0minus1() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDateTime(bithday, "0-1 "));
	}

	@Test
	void testGetDateTime0minus1_0minus1() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDateTime(bithday, "0-1 0-1"));
	}

	@Test
	void testGetDateTime1Plus1Wplus1J_6Plus1hPlus1m() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2021, MAY, 8).atStartOfDay().plusHours(7).plusMinutes(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "1+1W+1j 6+1h+1M"));
	}

	@Test
	void testGetDateTime01012020PlusPlusPlusM() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, APRIL, 01).atStartOfDay().plusHours(7).plusMinutes(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "01012020+++M 701"));
	}

	@Test
	void testGetDateTime_825PlusPLusPlusM() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(8).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, " 825+++M"));
	}

	@Test
	void testGetDate02Point05() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull( DateTimeUtil.getDateTime(bithday, "02.05"));
	}

	@Test
	void testGetDate02Point05Point() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull( DateTimeUtil.getDateTime(bithday, "02.05."));
	}

	@Test
	void testGetDateParse() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		Date date;
		try {
			date = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMANY).parse("1.1.21");
			System.out.println(date.toString());
		} catch (ParseException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
		assertNull( DateTimeUtil.getDateTime(bithday, "1.1 1"));
	}

	@Test
	void testGetDate1Point1() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull( DateTimeUtil.getDateTime(bithday, "1.1"));
	}

	@Test
	void testGetDate1Point1Point() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull( DateTimeUtil.getDateTime(bithday, "1.1."));
	}

	@Test
	void testGetDate1Point1PointPoint() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull( DateTimeUtil.getDateTime(bithday, "1.1"));
	}

	@Test
	void testGetDate02Point05Point1988() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull( DateTimeUtil.getDateTime(bithday, "02.05.1988"));
	}

	@Test
	void testGetDate02Point05Point20() {
		Instant bithday = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertNull( DateTimeUtil.getDateTime(bithday, "02.05.20"));
	}

	@Test
	void testGetDate02Point05_205() {
		
		assertNull(DateTimeUtil.getDateTime(initialDay, "02.05 205"));
	}

	@Test
	void testGetDate070120GERMANY() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "070120 1", Locale.GERMANY, "", ""));
	}

	@Test
	void testGetDate070120US() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "070120 1", Locale.US, "dd-MM-yy", "HH:mm"));
	}

	@Test
	void testGetDate07Point01Point20GERMANY() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "07.01.20 1", Locale.GERMANY));
	}

	@Test
	void testGetDate07Slash01Slash20US() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, Month.JULY, 1).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "07/01/20 1", Locale.US));
	}

	@Test
	void testGetDate07Slash01SlashUS() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertNull(DateTimeUtil.getDateTime(bithday, "07/01 1", Locale.US));
	}

	@Test
	void testGetDate070120Stern1GERMANY() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "070120*1", Locale.GERMANY));
	}

	@Test
	void testGetDate02Slash05Slash1988Stern1530() {
		
		Instant expected = LocalDate.of(2088, Month.FEBRUARY, 5).atStartOfDay().plusHours(15).plusMinutes(30).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "02/05/88*1530", Locale.US));
	}

	@Test
	void testGetDateTimeStern825PlusPLusPlusM() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(8).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "*825+++M"));
	}

	@Test
	void testGetDateTimeZoneIDEuropeMonaco() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("Europe/Monaco")).toInstant();
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(8).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "13052020 1028", "Europe/Monaco"));
	}

	@Test
	void testGetDateTimeZoneIDEuropeLondon() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("Europe/London")).toInstant();
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(9).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "13052020 1028", "Europe/London"));
	}

	@Test
	void testGetDateTimeZoneIDAmericaJuneau() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("America/Juneau")).toInstant();
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(bithday, "13052020 1028", "America/Juneau"));
	}

	@Test
	void testGetDateTimeZoneIDAmericaParis() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("America/Juneau")).toInstant();
		assertNull( DateTimeUtil.getDateTime(bithday, "13052020 1028", "America/Paris"));
	}

	@Test
	void testGetDateTimeString() {
		Instant bithday = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals("13.05.20 18:12", DateTimeUtil.getDateTimeString(bithday, Locale.GERMANY, "dd.MM.yy", "HH:mm"));
	}
}
