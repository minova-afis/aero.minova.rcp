package aero.minova.rcp.xml.tests;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

import aero.minova.rcp.rcp.util.TextfieldVerifier;

class TextfieldVerifierTest {

	String heute;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Test
	void test0() {
		String verifyTime = TextfieldVerifier.verifyDate("0", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().format(formatter), verifyTime);
	}

	@Test
	public void testPlusPlusPlus() {
		String verifyTime = TextfieldVerifier.verifyDate("+++", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().plusDays(3).format(formatter), verifyTime);
	}

	@Test
	public void testMinus() {
		String verifyTime = TextfieldVerifier.verifyDate("-", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().minusDays(1).format(formatter), verifyTime);
	}

	@Test
	void testMinusMinusMinusMinus() {
		String verifyTime = TextfieldVerifier.verifyDate("----", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().minusDays(4).format(formatter), verifyTime);
	}

//	@Test
	public void testDezember() {
		String verifyTime = TextfieldVerifier.verifyDate("Dezember", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().withMonth(12).format(formatter), verifyTime);
	}

//	@Test
	public void testDez() {
		String verifyTime = TextfieldVerifier.verifyDate("Dez", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().withMonth(12).format(formatter), verifyTime);
	}

}
