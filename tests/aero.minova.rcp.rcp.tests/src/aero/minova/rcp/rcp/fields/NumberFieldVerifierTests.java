package aero.minova.rcp.rcp.fields;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

public class NumberFieldVerifierTests {
	
	@Test
	public void testDecimalSeparator() {
		NumberFieldVerifier nfv = new NumberFieldVerifier();
		
		assertEquals("9,00", nfv.getNewText(2, Locale.GERMANY, "", 0, 0, 0, "9"));
		assertEquals(1, nfv.getNewCaretPosition(2, Locale.GERMANY, "", 0, 0, 0, "9", "9,00"));
	}

}
