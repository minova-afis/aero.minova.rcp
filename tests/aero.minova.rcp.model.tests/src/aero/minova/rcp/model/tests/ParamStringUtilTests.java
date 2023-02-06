package aero.minova.rcp.model.tests;

import static java.time.Month.JANUARY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.util.ParamStringUtil;

class ParamStringUtilTests {

	private List<Value> values = new ArrayList<>();

	@BeforeEach
	void emptyList() {
		values.clear();
	}

	////////////////////////////////
	// Values to String ////////////
	////////////////////////////////

	@Test
	void convertEmptyValue() {
		values.add(null);
		assertEquals("{0-0-0}", ParamStringUtil.convertValuesToStringParameter(values, Locale.GERMAN));
	}

	@Test
	void convertBigDecimalValue() {
		Value v = new Value(123.456, DataType.BIGDECIMAL);
		values.add(v);
		assertEquals("{0-5-7}123.456", ParamStringUtil.convertValuesToStringParameter(values, Locale.GERMAN));
	}

	@Test
	void convertBooleanValue() {
		Value v = new Value(true, DataType.BOOLEAN);
		values.add(v);
		assertEquals("{0-11-1}1", ParamStringUtil.convertValuesToStringParameter(values, Locale.GERMAN));
	}

	@Test
	void convertDoubleValue() {
		Value v = new Value(123.456, DataType.DOUBLE);
		values.add(v);
		assertEquals("{0-5-7}123.456", ParamStringUtil.convertValuesToStringParameter(values, Locale.GERMAN));
	}

	@Test
	void convertShortDateValue() {
		Value v = new Value(LocalDate.of(2020, JANUARY, 7).atStartOfDay().toInstant(ZoneOffset.UTC), DataType.INSTANT);
		values.add(v);
		assertEquals("{0-7-14}20200107000000", ParamStringUtil.convertValuesToStringParameter(values, Locale.GERMAN));
	}

	@Test
	void convertShortTimeValue() {
		Value v = new Value(LocalDateTime.of(1900, JANUARY, 1, 14, 34).toInstant(ZoneOffset.UTC), DataType.INSTANT);
		values.add(v);
		assertEquals("{0-7-14}19000101143400", ParamStringUtil.convertValuesToStringParameter(values, Locale.GERMAN));
	}

	@Test
	void convertDateTimeValue() {
		Value v = new Value(LocalDateTime.of(2022, JANUARY, 17, 12, 34).toInstant(ZoneOffset.UTC), DataType.INSTANT);
		values.add(v);
		assertEquals("{0-7-14}20220117123400", ParamStringUtil.convertValuesToStringParameter(values, Locale.GERMAN));
	}

	@Test
	void convertIntegerValue() {
		Value v = new Value(123, DataType.INTEGER);
		values.add(v);
		assertEquals("{0-3-3}123", ParamStringUtil.convertValuesToStringParameter(values, Locale.GERMAN));
	}

	@Test
	void convertStringValue() {
		Value v = new Value("TestString", DataType.STRING);
		values.add(v);
		assertEquals("{0-8-10}TestString", ParamStringUtil.convertValuesToStringParameter(values, Locale.GERMAN));
	}

	@Test
	void convertMultipleValues() {
		Value v = new Value("TestString", DataType.STRING);
		values.add(v);
		v = new Value(LocalDateTime.of(2022, JANUARY, 17, 12, 34).toInstant(ZoneOffset.UTC), DataType.INSTANT);
		values.add(v);
		v = new Value(123, DataType.INTEGER);
		values.add(v);
		v = new Value(false, DataType.BOOLEAN);
		values.add(v);
		assertEquals("{0-8-10}TestString{1-7-14}20220117123400{2-3-3}123{3-11-1}0", ParamStringUtil.convertValuesToStringParameter(values, Locale.GERMAN));
	}

	////////////////////////////////
	// String to Values ////////////
	////////////////////////////////

	@Test
	void convertEmptyString() {
		String s = "";
		assertEquals(values, ParamStringUtil.convertStringParameterToValues(s, Locale.GERMAN));
	}

	@Test
	void convertStringString() {
		String s = "{0-8-4}Test";
		values.add(new Value("Test"));
		List<Value> res = ParamStringUtil.convertStringParameterToValues(s, Locale.GERMAN);
		assertEquals(values, res);
		assertEquals(DataType.STRING, res.get(0).getType());
	}

	@Test
	void convertIntegerString() {
		String s = "{0-3-4}1234";
		values.add(new Value(1234));
		List<Value> res = ParamStringUtil.convertStringParameterToValues(s, Locale.GERMAN);
		assertEquals(values, res);
		assertEquals(DataType.INTEGER, res.get(0).getType());
	}

	@Test
	void convertDoubleString() {
		String s = "{0-5-8}1234.567";
		values.add(new Value(1234.567));
		List<Value> res = ParamStringUtil.convertStringParameterToValues(s, Locale.GERMAN);
		assertEquals(values, res);
		assertEquals(DataType.DOUBLE, res.get(0).getType());
	}

	@Test
	void convertBooleanString() {
		String s = "{0-11-1}1";
		values.add(new Value(true));
		List<Value> res = ParamStringUtil.convertStringParameterToValues(s, Locale.GERMAN);
		assertEquals(values, res);
		assertEquals(DataType.BOOLEAN, res.get(0).getType());
	}

	@Test
	void convertDateString() {
		String s = "{0-7-14}20220117234500";
		values.add(new Value(LocalDateTime.of(2022, JANUARY, 17, 23, 45).toInstant(ZoneOffset.UTC)));
		List<Value> res = ParamStringUtil.convertStringParameterToValues(s, Locale.GERMAN);
		assertEquals(values, res);
		assertEquals(DataType.INSTANT, res.get(0).getType());
	}

	@Test
	void convertMultipleString() {
		String s = "{0-7-14}20220117234500{1-11-1}1{2-5-8}1234.567{3-8-4}Test";
		values.add(new Value(LocalDateTime.of(2022, JANUARY, 17, 23, 45).toInstant(ZoneOffset.UTC)));
		values.add(new Value(true));
		values.add(new Value(1234.567));
		values.add(new Value("Test"));
		List<Value> res = ParamStringUtil.convertStringParameterToValues(s, Locale.GERMAN);
		assertEquals(values, res);
	}

}
