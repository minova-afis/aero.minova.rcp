package aero.minova.rcp.rcp.accessor;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.util.NumberFormatUtil;
import aero.minova.rcp.model.util.NumberFormatUtil.Result;

class NumberValueAccessorTests {

	// ================================================================================
	// Testfall aus Issue #1166, verbessert auf komplettes Leeren des Feldes
	// ================================================================================

	@Test
	void testCompleteBSIntegerField() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				0, // start
				1, // end
				8, // keyCode
				0, // decimals
				Locale.GERMANY, // locale
				"1", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("", result.text);
	}

	// ================================================================================
	// Testfälle aus Issue #1132
	// ================================================================================

	@Test
	void testDELAfterKomma() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				2, // start
				3, // end
				127, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,34", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("0,40", result.text);
	}

	// ================================================================================
	// Testfälle aus Issue #1084
	// ================================================================================

	@Test
	void testCompleteSelectionWindows() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1", // insertion
				0, // start
				9, // end
				49, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"12.345,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("1,00", result.text);
	}

	@Test
	void testEntfZeroFive() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				0, // start
				1, // end
				127, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,50", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("0,50", result.text);
	}

	@Test
	void testEntfZeroFiveDeleteFive() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				2, // start
				3, // end
				127, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,50", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("0,00", result.text);
	}

	@Test
	void testBSZeroFive() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				0, // start
				1, // end
				8, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,50", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("0,50", result.text);
	}

	@Test
	void testEntfZeroZero() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				0, // start
				1, // end
				127, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("0,00", result.text);
	}

	@Test
	void testIntCompleteSelection() {
		MNumberField field = new MNumberField(0);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1", // insertion
				0, // start
				5, // end
				49, // keyCode
				0, // decimals
				Locale.GERMANY, // locale
				"1.234", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("1", result.text);
	}

	@Test
	void testReplaceNumber() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"2", // insertion
				3, // start
				5, // end
				50, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1.111,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("112,00", result.text);
	}

	@Test
	void testEnterKommaZero() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				",", // insertion
				1, // start
				1, // end
				44, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("0,00", result.text);
	}

	@Test
	void testNumberAfterKomma() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"2", // insertion
				2, // start
				2, // end
				50, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,10", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("0,21", result.text);
	}

	// ================================================================================
	// "Normale" Testfälle
	// ================================================================================

	@Test
	void testDecimalSeparatorGerman() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"9.000", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("9.000,00", result.text, "Text");
	}

	@Test
	void testFrom99_95To9999_95() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"99", // insertion
				2, // start
				2, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"99,95", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("9.999,95", result.text, "Text");
	}

	@Test
	void testFrom0_00To1_00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1", // insertion
				1, // start
				1, // end
				0, // keyCode
				3, // decimals
				Locale.GERMANY, // locale
				"0,000", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("1,000", result.text, "Text");
		assertEquals(new Value(1.0), result.value, "Value");
	}

	@Test
	void testFrom1_000To12_000() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"2", // insertion
				1, // start
				1, // end
				0, // keyCode
				3, // decimals
				Locale.GERMANY, // locale
				"1,000", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("12,000", result.text, "Text");
		assertEquals(new Value(12.0), result.value, "Value");
	}

	@Test
	void testFrom99_95To999_95() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"9", // insertion
				2, // start
				2, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"99,95", // textBefore
				decimalFormatSymbols, //
				false//
		);

		assertEquals("999,95", result.text, "get text");
	}

	@Test
	void testDecimalSeparatorEnglish() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"0", // insertion
				3, // start
				3, // end
				0, // keyCode
				2, // decimals
				Locale.US, // locale
				"900.00", // textBefore
				decimalFormatSymbols, //
				false//
		);

		assertEquals("9,000.00", result.text);
	}

	@Test
	void testGetNewValue() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"0", // insertion
				3, // start
				3, // end
				0, // keyCode
				3, // decimals
				Locale.US, // locale
				"900.00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals(new Value(9000.00), result.value);
	}

	@Test
	void testFrom100_00To1002_00GER() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMANY);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"2", // insertion
				3, // start
				3, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"100,00", // textBefore
				decimalFormatSymbols, //
				false//
		);

		assertEquals("1.002,00", result.text, "get text");
	}

	@Test
	void testFrom100_00To1_002_00US() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"2", // insertion
				3, // start
				3, // end
				0, // keyCode
				2, // decimals
				Locale.US, // locale
				"100.00", // textBefore
				decimalFormatSymbols, //
				false//
		);

		assertEquals("1,002.00", result.text, "get text");
	}


	//
	//
	// Weitere Tests
	//
	//

	@Test
	void test0_1c000To0_12c00() {
		MNumberField field = new MNumberField(4);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"2", // insertion
				3, // start
				3, // end
				0, // keyCode
				4, // decimals
				Locale.GERMANY, // locale
				"0,1000", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("0,1200", result.text, "Text");
		assertEquals(new Value(0.12), result.value, "Value");
	}

	@Test
	void test89c_234_1To897c_234_1() {
		MNumberField field = new MNumberField(1);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"7", // insertion
				2, // start
				2, // end
				0, // keyCode
				1, // decimals
				Locale.GERMANY, // locale
				"89.234,1", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("897.234,1", result.text, "Text");
		assertEquals(new Value(897234.1), result.value, "Value");
	}

	@Test
	void test1c0_00To17c0_00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"7", // insertion
				1, // start
				1, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"10,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("170,00", result.text, "Text");
		assertEquals(new Value(170.0), result.value, "Value");
	}

	@Test
	void testc234_00To1c_234_00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"234,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("1.234,00", result.text, "Text");
		assertEquals(new Value(1234.0), result.value, "Value");
	}

	@Test
	void test1_00cTo1_46c() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"456", // insertion
				2, // start
				4, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("1,45", result.text, "Text");
		assertEquals(new Value(1.45), result.value, "Value");
	}

	@Test
	void testc234_00To11c_234_00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"11", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"234,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("11.234,00", result.text, "Text");
		assertEquals(new Value(11234.0), result.value, "Value");
	}

	@Test
	void testc234_00To111c_234_00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"111", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"234,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("111.234,00", result.text, "Text");
		assertEquals(new Value(111234.0), result.value, "Value");
	}

	@Test
	void testc234_00To1_111c_234_00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1111", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"234,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("1.111.234,00", result.text, "Text");
		assertEquals(new Value(1111234.0), result.value, "Value");
	}

	@Test
	void testc234_00To1_111_111c_234_00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1111111", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"234,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("1.111.111.234,00", result.text, "Text");
		assertEquals(new Value(1111111234.0), result.value, "Value");
	}

	@Test
	void testc234_00To2_341_111c_00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1111", // insertion
				3, // start
				3, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"234,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("2.341.111,00", result.text, "Text");
		assertEquals(new Value(2341111.0), result.value, "Value");
	}

	@Test
	void testcTo5c_00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"5", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("5,00", result.text, "Text");
		assertEquals(new Value(5.0), result.value);
	}

	@Test
	void testInsert1_111_111() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1.111.111", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"234,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("1.111.111.234,00", result.text, "Text");
		assertEquals(new Value(1111111234.0), result.value, "Value");
	}

	@Test
	void test_cse0k00_1_1csek00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("10,00", result.text, "Text");
		assertEquals(new Value(10.0), result.value, "Value");
	}

	@Test
	void test_cs0k00e_1_1csek00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1", // insertion
				0, // start
				4, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("1,00", result.text, "Text");
		assertEquals(new Value(1.0), result.value, "Value");
	}

	@Test
	void test_cs0k00e_1_100csek00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"100", // insertion
				0, // start
				4, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("100,00", result.text);
		assertEquals(new Value(100.0), result.value);
	}

	@Test
	void test_0csk00e_1_100csek00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"100", // insertion
				1, // start
				4, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"0,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("100,00", result.text);
		assertEquals(new Value(100.0), result.value);
	}

	@Test
	void test_12pcse345k00_BS_12csep345k00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				3, // start
				3, // end
				8, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"12.345,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("12.345,00", result.text);
		assertEquals(new Value(12345.0), result.value);
	}

	@Test
	void test_12pcse345k00_DEL_12pcse45k00() {
		MNumberField field = new MNumberField(2);

		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				2, // start
				3, // end
				127, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"12.345,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("12.345,00", result.text);
		assertEquals(new Value(12345.0), result.value);
	}

	@Test
	void test_1k234cse_8_1k234cse() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"8", // insertion
				5, // start
				5, // end
				0, // keyCode
				3, // decimals
				Locale.GERMANY, // locale
				"1,234", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("1,234", result.text);
		assertEquals(new Value(1.234), result.value);
	}

	@Test
	void test_1k23cse4_8_1k23cse8() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"8", // insertion
				4, // start
				5, // end
				0, // keyCode
				3, // decimals
				Locale.GERMANY, // locale
				"1,234", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("1,238", result.text);
		assertEquals(new Value(1.238), result.value);
	}

	@Test
	void test1_565csek00_1000_1_000csek00() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1000", // insertion
				0, // start
				8, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1.565,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("1.000,00", result.text);
		assertEquals(new Value(1000.00), result.value);
	}

	@Test
	void test1_565csek00_100_100csek00() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"100", // insertion
				0, // start
				8, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1.565,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("100,00", result.text);
		assertEquals(new Value(100.00), result.value);
	}

	@Test
	void testcs1_565k00e_1000000_1_000_000csek00() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1000000", // insertion
				0, // start
				8, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1.565,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("1.000.000,00", result.text);
		assertEquals(new Value(1000000.00), result.value);
	}

	@Test
	void testcs1_565k00e_1000k65_1_000csek65() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"1000,65", // insertion
				0, // start
				8, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1.565,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("1.000,65", result.text);
		assertEquals(new Value(1000.65), result.value);
	}

	@Test
	void test1_cs565ek00_100_100csek00() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"100", // insertion
				2, // start
				5, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1.565,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("1.100,00", result.text);
		assertEquals(new Value(1100.00), result.value);
	}

	@Test
	void test1_5cs65ek00_100_15_100csek00() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"100", // insertion
				3, // start
				5, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1.565,00", // textBefore
				decimalFormatSymbols, //
				true//
		);
		assertEquals("15.100,00", result.text);
		assertEquals(new Value(15100.00), result.value);
	}


	@Test
	void test12_3cse56k78_to_12csek78() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				3, // start
				6, // end
				127, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"12.356,78", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("12,78", result.text);
		assertEquals(new Value(12.78), result.value);
	}

	@Test
	void test1_234_56cse7k89_to_123_456csek89() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				8, // start
				9, // end
				127, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1.234.567,89", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("123.456,89", result.text);
		assertEquals(new Value(123456.89), result.value);
	}

	@Test
	void test_cs1ek00_DEL_0csek00() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				0, // start
				1, // end
				127, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("0,00", result.text);
		assertEquals(new Value(0.00), result.value);
	}

	@Test
	void test_s1cek00_BSP_0csek00() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"", // insertion
				0, // start
				1, // end
				8, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				"1,00", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("0,00", result.text);
		assertEquals(new Value(0.00), result.value);
	}

	@Test
	void test_s0ce_s10ec() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"10", // insertion
				0, // start
				1, // end
				0, // keyCode
				0, // decimals
				Locale.GERMANY, // locale
				"0", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("10", result.text);
		assertEquals(new Value(10), result.value);
	}

	@Test
	void test_s0ce_s54Point321ec() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"54321", // insertion
				0, // start
				1, // end
				0, // keyCode
				0, // decimals
				Locale.GERMANY, // locale
				"0", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("54.321", result.text);
		assertEquals(new Value(54321), result.value);
	}

	@Test
	void test_s0ce_s87Point654Point321ec() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"87654321", // insertion
				0, // start
				1, // end
				0, // keyCode
				0, // decimals
				Locale.GERMANY, // locale
				"0", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("87.654.321", result.text);
		assertEquals(new Value(87654321), result.value);
	}

	@Test
	void test_123sce_1234sec() {
		MNumberField field = new MNumberField(2);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = NumberFormatUtil.processInput(//
				field, //
				"4", // insertion
				3, // start
				3, // end
				0, // keyCode
				0, // decimals
				Locale.GERMANY, // locale
				"123", // textBefore
				decimalFormatSymbols, //
				false//
		);
		assertEquals("1.234", result.text);
		assertEquals(new Value(1234), result.value);
	}

}
