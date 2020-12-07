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

//	@Test
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
		assertEquals("Value", new Value(1.0d), result.value);
	}

//	@Test
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
		assertEquals("Value", new Value(12.0d), result.value);
	}

//	@Test
//	public void testFrom99_95To999_95() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);
//
//		assertEquals("get text", "999,95", nfv.getNewText(2, Locale.GERMANY, "99,95", 2, 2, 2, "9", dfs));
//	}
//
////	@Test
////	public void testFrom1_C00To1_1C0() {
////		Event e = new Event();
////		e.character = '\b';
////
////		NumberFieldVerifier nfv = new NumberFieldVerifier();
////		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);
////
////		assertEquals("get caret", 3, nfv.getNewCaretPosition("1,00", "1", "1,10", dfs, 2, 1));
////	}
//
//	@Test
//	public void testInsertComma() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);
//
//		assertEquals("999,95", nfv.getNewText(2, Locale.GERMANY, "999,95", 0, 0, 0, ",", dfs));
//	}
//
//	@Test
//	public void testDecimalSeparatorEnglish() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
//
//		assertEquals("9,000.00", nfv.getNewText(2, Locale.US, "", 0, 0, 0, "9000", dfs));
//	}
//
//	@Test
//	public void testGetNewValue() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
//
//		assertEquals(Double.valueOf(9000.0), nfv.getNewValue("9000,0", dfs));
//	}
//
//	@Test
//	public void testGetNewCaretPositionForLocaleUS() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
//
//		assertEquals(4, nfv.getNewCaretPosition("0.00", "9000", "9,000.00", dfs, 1, 1));
//	}
//
//	@Test
//	public void testGetNewCaretPositionForLocaleGERMANY() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
//
//		assertEquals(4, nfv.getNewCaretPosition("0,00", "9000", "9.000,00", dfs, 1, 1));
//	}
//
//	@Test
//	public void testGetNewCaretPositionInsertPointUS() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
//
//		assertEquals(2, nfv.getNewCaretPosition("0.00", ".", "0.00", dfs, 1, 1));
//	}
//
//	@Test
//	public void testGetNewCaretPositionInsertCommaGER() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
//
//		assertEquals(2, nfv.getNewCaretPosition("0,00", ",", "0,00", dfs, 1, 1));
//	}
//
//	@Test
//	public void testFrom100_00To1_002_00GER() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
//
//		assertEquals("get text", "1.002,00", nfv.getNewText(2, Locale.GERMANY, "100,00", 3, 3, 3, "2", dfs));
//		assertEquals("get caret Position", 5, nfv.getNewCaretPosition("100,00", "2", "1.002,00", dfs, 3, 1));
//	}
//
//	@Test
//	public void testFrom100_00To1_002_00US() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
//
//		assertEquals("get text", "1,002.00", nfv.getNewText(2, Locale.US, "100.00", 3, 3, 3, "2", dfs));
//		assertEquals("get caret Position", 5, nfv.getNewCaretPosition("100.00", "2", "1,002.00", dfs, 3, 1));
//	}
//
//	@Test
//	public void testKeyCode8() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
//
//		assertEquals("get caret Position", 3, nfv.getNewCaretPosition("1.002,00", "", "100,00", dfs, 4, 8));
//	}
//	
//	@Test
//	public void testKeyCode127() {
//		NumberFieldVerifier nfv = new NumberFieldVerifier();
//		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
//
//		assertEquals("get caret Position", 3, nfv.getNewCaretPosition("1.002,00", "", "100,00", dfs, 3, 127));
//	}

}
