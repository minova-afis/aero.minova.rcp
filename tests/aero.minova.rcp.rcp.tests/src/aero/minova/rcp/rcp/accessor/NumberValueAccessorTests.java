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
				"0,00", // textBefore
				decimalFormatSymbols//
		);
		assertEquals("Text", "1,000", result.text);
		assertEquals("CaretPosition", 1, result.caretPosition);
		assertEquals("Value", new Value(1.0), result.value);
	}

	@Test
	public void testFrom1_00To12_00() {
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
				"1,00", // textBefore
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

//	@Test
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
				"900.00", // textBefore
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
				"900,00", // textBefore
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
				8, // keyCode
				2, // decimals
				Locale.GERMAN, // locale
				3, // caretPosition
				"100,00", // textBefore
				decimalFormatSymbols//
		);

		assertEquals("get caret Position", 3, result.caretPosition);
	}

}
