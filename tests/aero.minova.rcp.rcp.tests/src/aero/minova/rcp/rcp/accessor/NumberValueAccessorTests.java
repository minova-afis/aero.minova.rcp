package aero.minova.rcp.rcp.accessor;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.Test;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.rcp.accessor.NumberValueAccessor.Result;

public class NumberValueAccessorTests {

	@Test
	public void testDecimalSeparatorGerman() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"9.000", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "9.000,00", result.text);
	}

	@Test
	public void testWasWeissIch() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"9", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "9,00", result.text);
	}

	@Test
	public void testFrom99_95To9999_95() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"99", // insertion
				2, // start
				2, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				2, // caretPosition
				"99,95", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "9.999,95", result.text);
		assertEquals("CaretPosition", 5, result.caretPosition);
	}

	@Test
	public void testFrom0_00To1_00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"1", // insertion
				1, // start
				1, // end
				0, // keyCode
				3, // decimals
				Locale.GERMANY, // locale
				1, // caretPosition
				"0,000", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1,000", result.text);
		assertEquals("CaretPosition", 1, result.caretPosition);
		assertEquals("Value", new Value(1.0), result.value);
	}

	@Test
	public void testFrom1_000To12_000() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"2", // insertion
				1, // start
				1, // end
				0, // keyCode
				3, // decimals
				Locale.GERMANY, // locale
				1, // caretPosition
				"1,000", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "12,000", result.text);
		assertEquals("CaretPosition", 2, result.caretPosition);
		assertEquals("Value", new Value(12.0), result.value);
	}

	@Test
	public void testFrom99_95To999_95() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"9", // insertion
				2, // start
				2, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				2, // caretPosition
				"99,95", // textBefore
				decimalFormatSymbols//
		);

		assertEquals("get text", "999,95", result.text);
	}

	@Test
	public void testFrom1_C00To1_1C0() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"1", // insertion
				2, // start
				2, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				2, // caretPosition
				"1,00", // textBefore
				decimalFormatSymbols//
		);

		assertEquals("get caret", 3, result.caretPosition);
	}

	@Test
	public void testInsertComma() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				",", // insertion
				3, // start
				3, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				3, // caretPosition
				"999,95", // textBefore
				decimalFormatSymbols//
		);

		assertEquals("get Text", "999,95", result.text);
		assertEquals("get Caret", 4, result.caretPosition);
	}

	@Test
	public void testDecimalSeparatorEnglish() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);

		Result result = numberValueAccessor.processInput(//
				"0", // insertion
				3, // start
				3, // end
				0, // keyCode
				2, // decimals
				Locale.US, // locale
				3, // caretPosition
				"900.00", // textBefore
				decimalFormatSymbols//
		);

		assertEquals("9,000.00", result.text);
	}

	@Test
	public void testGetNewValue() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);

		Result result = numberValueAccessor.processInput(//
				"0", // insertion
				3, // start
				3, // end
				0, // keyCode
				3, // decimals
				Locale.US, // locale
				3, // caretPosition
				"900.00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals(new Value(9000.00), result.value);
	}

	@Test
	public void testGetNewCaretPositionForLocaleUS() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);

		Result result = numberValueAccessor.processInput(//
				"0", // insertion
				3, // start
				3, // end
				0, // keyCode
				3, // decimals
				Locale.US, // locale
				3, // caretPosition
				"900.000", // textBefore
				decimalFormatSymbols//
		);
		assertEquals(5, result.caretPosition);
	}

	@Test
	public void testGetNewCaretPositionForLocaleGERMANY() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMANY);

		Result result = numberValueAccessor.processInput(//
				"0", // insertion
				3, // start
				3, // end
				0, // keyCode
				3, // decimals
				Locale.GERMANY, // locale
				3, // caretPosition
				"900,000", // textBefore
				decimalFormatSymbols//
		);

		assertEquals(5, result.caretPosition);
	}

	@Test
	public void testGetNewCaretPositionInsertPointUS() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);

		Result result = numberValueAccessor.processInput(//
				".", // insertion
				1, // start
				1, // end
				0, // keyCode
				3, // decimals
				Locale.US, // locale
				1, // caretPosition
				"0.00", // textBefore
				decimalFormatSymbols//
		);

		assertEquals(2, result.caretPosition);
	}

	@Test
	public void testGetNewCaretPositionInsertCommaGER() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMANY);

		Result result = numberValueAccessor.processInput(//
				",", // insertion
				1, // start
				1, // end
				0, // keyCode
				3, // decimals
				Locale.GERMANY, // locale
				3, // caretPosition
				"0,00", // textBefore
				decimalFormatSymbols//
		);

		assertEquals(2, result.caretPosition);
	}

	@Test
	public void testFrom100_00To1002_00GER() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMANY);

		Result result = numberValueAccessor.processInput(//
				"2", // insertion
				3, // start
				3, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				3, // caretPosition
				"100,00", // textBefore
				decimalFormatSymbols//
		);

		assertEquals("get text", "1.002,00", result.text);
		assertEquals("get caret Position", 5, result.caretPosition);
	}

	@Test
	public void testFrom100_00To1_002_00US() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);

		Result result = numberValueAccessor.processInput(//
				"2", // insertion
				3, // start
				3, // end
				0, // keyCode
				2, // decimals
				Locale.US, // locale
				3, // caretPosition
				"100.00", // textBefore
				decimalFormatSymbols//
		);

		assertEquals("get text", "1,002.00", result.text);
		assertEquals("get caret Position", 5, result.caretPosition);
	}

	@Test
	public void testKeyCode8() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"", // insertion
				3, // start
				3, // end
				8, // keyCode
				2, // decimals
				Locale.GERMAN, // locale
				3, // caretPosition
				"100,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals(3, result.caretPosition);
	}

	@Test
	public void testKeyCode127() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"", // insertion
				3, // start
				3, // end
				127, // keyCode
				2, // decimals
				Locale.GERMAN, // locale
				3, // caretPosition
				"100,00", // textBefore
				decimalFormatSymbols//
		);

		assertEquals("get caret Position", 3, result.caretPosition);
	}

	//
	//
	// Weitere Tests
	//
	//

	@Test
	public void test0_1c000To0_12c00() {
		MNumberField field = new MNumberField(4);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"2", // insertion
				3, // start
				3, // end
				0, // keyCode
				4, // decimals
				Locale.GERMANY, // locale
				3, // caretPosition
				"0,1000", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "0,1200", result.text);
		assertEquals("CaretPosition", 4, result.caretPosition);
		assertEquals("Value", new Value(0.12), result.value);
	}

	@Test
	public void test89c_234_1To897c_234_1() {
		MNumberField field = new MNumberField(1);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"7", // insertion
				2, // start
				2, // end
				0, // keyCode
				1, // decimals
				Locale.GERMANY, // locale
				2, // caretPosition
				"89.234,1", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "897.234,1", result.text);
		assertEquals("CaretPosition", 3, result.caretPosition);
		assertEquals("Value", new Value(897234.1), result.value);
	}

	@Test
	public void test1c0_00To17c0_00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"7", // insertion
				1, // start
				1, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				1, // caretPosition
				"10,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "170,00", result.text);
		assertEquals("CaretPosition", 2, result.caretPosition);
		assertEquals("Value", new Value(170.0), result.value);
	}

	@Test
	public void testc234_00To1c_234_00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"1", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"234,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1.234,00", result.text);
		assertEquals("CaretPosition", 1, result.caretPosition);
		assertEquals("Value", new Value(1234.0), result.value);
	}

	@Test
	public void test1_c00To1_46c() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"456", // insertion
				2, // start
				4, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				2, // caretPosition
				"1,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1,45", result.text);
		assertEquals("CaretPosition", 4, result.caretPosition);
		assertEquals("Value", new Value(1.45), result.value);
	}

	@Test
	public void test1_00cTo1_46c() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"456", // insertion
				2, // start
				4, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				4, // caretPosition
				"1,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1,45", result.text);
		assertEquals("CaretPosition", 4, result.caretPosition);
		assertEquals("Value", new Value(1.45), result.value);
	}
	
	@Test
	public void testc234_00To11c_234_00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"11", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"234,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "11.234,00", result.text);
		assertEquals("CaretPosition", 2, result.caretPosition);
		assertEquals("Value", new Value(11234.0), result.value);
	}
	
	@Test
	public void testc234_00To111c_234_00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"111", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"234,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "111.234,00", result.text);
		assertEquals("CaretPosition", 3, result.caretPosition);
		assertEquals("Value", new Value(111234.0), result.value);
	}
	
	@Test
	public void testc234_00To1_111c_234_00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"1111", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"234,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1.111.234,00", result.text);
		assertEquals("CaretPosition", 5, result.caretPosition);
		assertEquals("Value", new Value(1111234.0), result.value);
	}
	
	@Test
	public void testc234_00To1_111_111c_234_00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"1111111", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"234,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1.111.111.234,00", result.text);
		assertEquals("CaretPosition", 9, result.caretPosition);
		assertEquals("Value", new Value(1111111234.0), result.value);
	}
	
	@Test
	public void testc234_00To2_341_111c_00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"1111", // insertion
				3, // start
				3, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				3, // caretPosition
				"234,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "2.341.111,00", result.text);
		assertEquals("CaretPosition", 9, result.caretPosition);
		assertEquals("Value", new Value(2341111.0), result.value);
	}
	
	@Test
	public void testcTo5c_00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"5", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "5,00", result.text);
		assertEquals("CaretPosition", 1, result.caretPosition);
		assertEquals("Value", new Value(5.0), result.value);
	}
	
	@Test
	public void testInsert1_111_111() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"1.111.111", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"234,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1.111.111.234,00", result.text);
		assertEquals("CaretPosition", 9, result.caretPosition);
		assertEquals("Value", new Value(1111111234.0), result.value);
	}
	
	@Test
	public void test1_5c0To1_c00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"", // insertion
				3, // start
				3, // end
				8, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				3, // caretPosition
				"1,50", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("CaretPosition", 2, result.caretPosition);
	}
	
	@Test
	public void test1_52cTo1_50c() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"", // insertion
				3, // start
				3, // end
				127, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				3, // caretPosition
				"1,52", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("CaretPosition", 4, result.caretPosition);
	}
	
	@Test
	public void test_cse0k00_1_1csek00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"1", // insertion
				0, // start
				0, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"0,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "10,00", result.text);
		assertEquals("CaretPosition", 1, result.caretPosition);
		assertEquals("Value", new Value(10.0), result.value);
	}

	@Test
	public void test_cs0k00e_1_1csek00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"1", // insertion
				0, // start
				4, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"0,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1,00", result.text);
		assertEquals("CaretPosition", 1, result.caretPosition);
		assertEquals("Value", new Value(1.0), result.value);
	}
	
	@Test
	public void test_cs0k00e_1_100csek00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"100", // insertion
				0, // start
				3, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				0, // caretPosition
				"0,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "100,00", result.text);
		assertEquals("CaretPosition", 3, result.caretPosition);
		assertEquals("Value", new Value(100.0), result.value);
	}
	
	@Test
	public void test_0csk00e_1_100csek00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"100", // insertion
				1, // start
				4, // end
				0, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				1, // caretPosition
				"0,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "100,00", result.text);
		assertEquals("CaretPosition", 3, result.caretPosition);
		assertEquals("Value", new Value(100.0), result.value);
	}

	@Test
	public void test_1pcse234k00_BS_1csep234k00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"", // insertion
				2, // start
				2, // end
				8, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				2, // caretPosition
				"1.234,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1.234,00", result.text);
		assertEquals("CaretPosition", 1, result.caretPosition);
		assertEquals("Value", new Value(1234.0), result.value);
	}

	@Test
	public void test_12pcse345k00_BS_12csep345k00() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"", // insertion
				3, // start
				3, // end
				8, // keyCode
				2, // decimals
				Locale.GERMANY, // locale
				3, // caretPosition
				"12.345,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "12.345,00", result.text);
		assertEquals("CaretPosition", 2, result.caretPosition);
		assertEquals("Value", new Value(12345.0), result.value);
	}

	@Test
	public void test_1k234cse_8_1k234cse() {
		MNumberField field = new MNumberField(2);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, null);
		DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.GERMAN);

		Result result = numberValueAccessor.processInput(//
				"8", // insertion
				5, // start
				5, // end
				0, // keyCode
				3, // decimals
				Locale.GERMANY, // locale
				5, // caretPosition
				"1,234", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1,234", result.text);
		assertEquals("CaretPosition", 5, result.caretPosition);
		assertEquals("Value", new Value(1.234), result.value);
	}
}
