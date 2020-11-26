package aero.minova.rcp.rcp.fields;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.eclipse.swt.widgets.Event;
import org.junit.Test;

public class NumberFieldVerifierTests {

	@Test
	public void testDecimalSeparatorGerman() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);

		assertEquals("9.000,00", nfv.getNewText(2, Locale.GERMAN, "", 0, 0, 0, "9.000", dfs));
	}

	@Test
	public void testWasWeissIch() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);

		assertEquals("9,00", nfv.getNewText(2, Locale.GERMANY, "", 0, 0, 0, "9", dfs));
	}

	@Test
	public void testFrom99_95To9999_95() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);

		assertEquals("9.999,95", nfv.getNewText(2, Locale.GERMANY, "99,95", 2, 2, 2, "99", dfs));
	}

	@Test
	public void testFrom0_00To1_00() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);

		assertEquals("get text", "1,00", nfv.getNewText(2, Locale.GERMANY, "0,00", 1, 1, 1, "1", dfs));
		assertEquals("get caret", 1, nfv.getNewCaretPosition("0,00", "1", "1,00", dfs, 1, 1));
	}

	@Test
	public void testFrom1_00To12_00() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);

		assertEquals("get text", "12,00", nfv.getNewText(2, Locale.GERMANY, "1,00", 1, 1, 1, "2", dfs));
		assertEquals("get caret", 2, nfv.getNewCaretPosition("1,00", "2", "12,00", dfs, 1, 1));
	}

	@Test
	public void testFrom99_95To999_95() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);

		assertEquals("get text", "999,95", nfv.getNewText(2, Locale.GERMANY, "99,95", 2, 2, 2, "9", dfs));
	}

	@Test
	public void testFrom1_C00To1_1C0() {
		Event e = new Event();
		e.character = '\b';

		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);

		assertEquals("get caret", 3, nfv.getNewCaretPosition("1,00", "1", "1,10", dfs, 2, 1));
	}

	@Test
	public void testInsertComma() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMAN);

		assertEquals("999,95", nfv.getNewText(2, Locale.GERMANY, "999,95", 0, 0, 0, ",", dfs));
	}

	@Test
	public void testDecimalSeparatorEnglish() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

		assertEquals("9,000.00", nfv.getNewText(2, Locale.US, "", 0, 0, 0, "9000", dfs));
	}

	@Test
	public void testGetNewValue() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);

		assertEquals(Double.valueOf(9000.0), nfv.getNewValue("9000,0", dfs));
	}

	@Test
	public void testGetNewCaretPositionForLocaleUS() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

		assertEquals(4, nfv.getNewCaretPosition("0.00", "9000", "9,000.00", dfs, 1, 1));
	}

	@Test
	public void testGetNewCaretPositionForLocaleGERMANY() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);

		assertEquals(4, nfv.getNewCaretPosition("0,00", "9000", "9.000,00", dfs, 1, 1));
	}

	@Test
	public void testGetNewCaretPositionInsertPointUS() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

		assertEquals(2, nfv.getNewCaretPosition("0.00", ".", "0.00", dfs, 1, 1));
	}

	@Test
	public void testGetNewCaretPositionInsertCommaGER() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);

		assertEquals(2, nfv.getNewCaretPosition("0,00", ",", "0,00", dfs, 1, 1));
	}

	@Test
	public void testFrom100_00To1_002_00GER() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);

		assertEquals("get text", "1.002,00", nfv.getNewText(2, Locale.GERMANY, "100,00", 3, 3, 3, "2", dfs));
		assertEquals("get caret Position", 5, nfv.getNewCaretPosition("100,00", "2", "1.002,00", dfs, 3, 1));
	}

	@Test
	public void testFrom100_00To1_002_00US() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

		assertEquals("get text", "1,002.00", nfv.getNewText(2, Locale.US, "100.00", 3, 3, 3, "2", dfs));
		assertEquals("get caret Position", 5, nfv.getNewCaretPosition("100.00", "2", "1,002.00", dfs, 3, 1));
	}

	@Test
	public void testKeyCode8() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);

		assertEquals("get caret Position", 3, nfv.getNewCaretPosition("1.002,00", "", "100,00", dfs, 4, 8));
	}
	
	@Test
	public void testKeyCode127() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);

		assertEquals("get caret Position", 3, nfv.getNewCaretPosition("1.002,00", "", "100,00", dfs, 3, 127));
	}

}
