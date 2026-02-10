package aero.minova.rcp.rcp.util;

import static java.time.Month.APRIL;
import static java.time.Month.DECEMBER;
import static java.time.Month.JANUARY;
import static java.time.Month.MAY;
import static java.time.Month.SEPTEMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
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
	void setup() {
		initialDay = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
	}

	@Test
	void ensureInput0WithoutHourInput() {
		assertEquals(LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC), DateTimeUtil.getDateTime(initialDay, "0"));
	}

	@Test
	void ensureNotNullInput_0WithoutDateInput() {
		assertEquals(initialDay, DateTimeUtil.getDateTime(initialDay, " 0"));
	}

	@Test
	void ensureGivenDateTimeInput0_0() {
		assertEquals(initialDay, DateTimeUtil.getDateTime(initialDay, "0 0"));
	}

	@Test
	void ensureStartOfDayWithGivenDateInput0_00() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "0 00"));
	}

	@Test
	void ensureInput1WithoutHourInput() {
		assertEquals(LocalDate.of(1967, MAY, 1).atStartOfDay().toInstant(ZoneOffset.UTC), DateTimeUtil.getDateTime(initialDay, "1"));
	}

	@Test
	void ensureFirstHourOfFirstDayOfMonthInput1_1() {
		Instant expected = LocalDate.of(1967, MAY, 1).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1 1"));
	}

	@Test
	void ensureFirstHourOfGivenDateInput_1() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 1"));
	}

	@Test
	void ensureInput11WithoutHourInput() {
		assertEquals(LocalDate.of(1967, JANUARY, 1).atStartOfDay().toInstant(ZoneOffset.UTC), DateTimeUtil.getDateTime(initialDay, "11"));
	}

	@Test
	void ensureOutput01January1967_11AMInput11_11() {
		Instant expected = LocalDate.of(1967, JANUARY, 1).atStartOfDay().plusHours(11).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "11 11"));
	}

	@Test
	void ensureOutput11AMOnGivenDateInput_11() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(11).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 11"));
	}

	@Test
	void ensureOutput11SeptemberPlus1HourOfGivenTime() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(19).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 +"));
	}

	@Test
	void ensureOutput11SeptemberPlus2HourOfGivenTime() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 ++"));
	}

	@Test
	void ensureOutput11SeptemberPlus3HourOfGivenTime() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(21).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 +++"));
	}

	@Test
	void ensureOutputOf11September1967At9Past11() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(11).plusMinutes(9).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 1109"));
	}

	@Test
	void ensureOutputOfGivenDateAt9Past11() {
		Instant expected = LocalDate.of(1967, MAY, 23).atStartOfDay().plusHours(11).plusMinutes(9).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 1109"));
	}

	@Test
	void ensureOutputOf11September1967At2() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 11).atStartOfDay().plusHours(2).plusMinutes(0).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109 2"));
	}

	@Test
	void ensureInput90412WithoutHourInput() {
		assertEquals(LocalDate.of(1912, APRIL, 9).atStartOfDay().toInstant(ZoneOffset.UTC), DateTimeUtil.getDateTime(initialDay, "90412"));
	}

	@Test
	void ensureOutputOf9April1912At4Past9() {
		Instant expected = LocalDate.of(1912, APRIL, 9).atStartOfDay().plusHours(9).plusMinutes(4).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "90412 904"));
	}

	@Test
	void ensureOutputOf9April1912At5() {
		Instant expected = LocalDate.of(1912, APRIL, 9).atStartOfDay().plusHours(5).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "90412 5"));
	}

	@Test
	void ensureWithInput13121865WithoutHourInput() {
		assertEquals(LocalDate.of(1865, DECEMBER, 13).atStartOfDay().toInstant(ZoneOffset.UTC), DateTimeUtil.getDateTime(initialDay, "13121865"));
	}

	@Test
	void ensureOutputOf13Dezember1865At7To5() {
		Instant expected = LocalDate.of(1865, DECEMBER, 13).atStartOfDay().plusHours(4).plusMinutes(53).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "13121865 153+3h"));
	}

	@Test
	void ensureNullWithInputA() {
		assertNull(DateTimeUtil.getDateTime(initialDay, "A"));
	}

	@Test
	void ensureNullWithInputLKW() {
		assertNull(DateTimeUtil.getDateTime(initialDay, "LKW"));
	}

	@Test
	void ensureOutputOfGivenDatePlus1DayAndGivenHourPlus1Hour() {
		Instant expected = LocalDate.of(1967, MAY, 24).atStartOfDay().plusHours(19).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "+ +"));
	}

	@Test
	void ensureOutputOfGivenDatePlus2DaysAndGivenHourPlus2Hours() {
		Instant expected = LocalDate.of(1967, MAY, 25).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "++ ++"));
	}

	@Test
	void ensureOutputOf11SeptemberGivenYearPlus2DayAndGivenHourPlus2Hours() {
		Instant expected = LocalDate.of(1967, SEPTEMBER, 13).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1109++ ++"));
	}

	@Test
	void ensureOutputOf5MayGivenYearAndGivenHourPlus2Hours() {
		Instant expected = LocalDate.of(1967, MAY, 5).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "2+3t ++"));
	}

	@Test
	void ensureOutputOf2AugustGivenYearAndGivenHourPlus2Hours() {
		Instant expected = LocalDate.of(1967, Month.AUGUST, 2).atStartOfDay().plusHours(20).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "2+3m ++"));
	}

	@Test
	void ensureWithInputEmptyString() {
		assertEquals(LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC), DateTimeUtil.getDateTime(initialDay, ""));
	}

	@Test
	void ensureNullWithInput0Minus1_0Minus1() {
		assertNull(DateTimeUtil.getDateTime(initialDay, "0-1 0-1"));
	}

	@Test
	void ensureOutputOfGivenDatePlus1WeekPlus1YearAnd6OclockPLus1HourPlus1Minute() {
		Instant initialDay = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2021, MAY, 8).atStartOfDay().plusHours(7).plusMinutes(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "1+1W+1j 6+1h+1M"));
	}

	@Test
	void ensureOutputOfGivenDateAT8_28() {
		Instant initialDay = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(8).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, " 825+++M"));
	}

	@Test
	void ensureWithInput1Point1() {
		Instant initialDay = LocalDate.of(1967, MAY, 23).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(LocalDate.of(1967, JANUARY, 1).atStartOfDay().toInstant(ZoneOffset.UTC), DateTimeUtil.getDateTime(initialDay, "1.1"));
	}

	@Test
	void ensureOuputOf07January2020At1WithLocaleGERMANY() {
		Instant initialDay = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "070120 1", Locale.GERMANY, "", ""));
	}

	@Test
	void ensureOuputOf07January2020At1WithLocaleUSAndPattern_ddMinusMMMinusyyAndHH_mm() {
		Instant initialDay = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "070120 1", Locale.US, "dd-MM-yy", "HH:mm"));
	}

	@Test
	void ensureOuputOf07January2020At1WithLocaleGERMANYWithStarAsSeperator() {
		Instant initialDay = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, JANUARY, 7).atStartOfDay().plusHours(1).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "070120*1", Locale.GERMANY));
	}

	@Test
	void ensureOuputOfGivenDateAt8_28WithStarAsSeperator() {
		Instant initialDay = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(8).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "*825+++M"));
	}

	@Test
	void ensureOuputOfGivenDateAt10_28WithZoneIdEurope_Monaco() {
		Instant initialDay = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("Europe/Monaco")).toInstant();
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(8).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "13052020 1028", "Europe/Monaco"));
	}

	@Test
	void ensureOuputOfGivenDateAt10_28WithZoneIdEurope_London() {
		Instant initialDay = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("Europe/London")).toInstant();
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(9).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "13052020 1028", "Europe/London"));
	}

	@Test
	void ensureOuputOfGivenDateAt10_28WithZoneIdAmerica_Juneau() {
		Instant initialDay = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).atZone(ZoneId.of("America/Juneau")).toInstant();
		Instant expected = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(28).toInstant(ZoneOffset.UTC);
		assertEquals(expected, DateTimeUtil.getDateTime(initialDay, "13052020 1028", "America/Juneau"));
	}

	@Test
	void ensureOuputOfGivenDateTimeWithLocaleGERMANYAndPattern_ddMinusMMMinusyyAndHH_mm() {
		Instant initialDay = LocalDate.of(2020, MAY, 13).atStartOfDay().plusHours(18).plusMinutes(12).toInstant(ZoneOffset.UTC);
		assertEquals("13.05.20 20:12", DateTimeUtil.getDateTimeString(initialDay, Locale.GERMANY, "dd.MM.yy", "HH:mm", "Europe/Berlin"));
	}
}