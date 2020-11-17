package aero.minova.rcp.rcp.fields;

import static org.junit.Assert.assertEquals;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.Test;

public class NumberFieldVerifierTests {
	
	@Test
	public void testDecimalSeparatorGerman() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
		
		assertEquals("9.000,00", nfv.getNewText(2, Locale.GERMANY, "", 0, 0, 0, "9000,00", dfs));
		assertEquals(0, nfv.getNewCaretPosition(2, Locale.GERMANY, "", 0, 0, 0, "9", "9,00"));
	}
	
	@Test
	public void testDecimalSeparatorEnglish() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
		
		assertEquals("9,000.00", nfv.getNewText(2, Locale.US, "", 0, 0, 0, "9000.00", dfs));
		assertEquals(0, nfv.getNewCaretPosition(2, Locale.US, "", 0, 0, 0, "9", "9,00"));
	}
	
	@Test
	public void testGetNewValue() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.GERMANY);
		
		assertEquals(Double.valueOf(9000.0), nfv.getNewValue("9000,0", dfs));
	}

}
