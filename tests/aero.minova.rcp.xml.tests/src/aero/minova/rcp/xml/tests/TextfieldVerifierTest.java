package aero.minova.rcp.xml.tests;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import aero.minova.rcp.rcp.util.TextfieldVerifier;

public class TextfieldVerifierTest {

	String heute;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Test
	public void test0() {
		String verifyTime = TextfieldVerifier.verifyDate("0");
		assertEquals(LocalDate.now().format(formatter), verifyTime);
	}

	@Test
	public void testh() {
		String verifyTime = TextfieldVerifier.verifyDate("h");
		assertEquals(LocalDate.now().format(formatter), verifyTime);
	}

	@Test
	public void testH() {
		String verifyTime = TextfieldVerifier.verifyDate("H");
		assertEquals(LocalDate.now().format(formatter), verifyTime);
	}

	@Test
	public void testHeute() {
		String verifyTime = TextfieldVerifier.verifyDate("heute");
		assertEquals(LocalDate.now().format(formatter), verifyTime);
	}

	@Test
	public void testm() {
		String verifyTime = TextfieldVerifier.verifyDate("m");
		assertEquals(LocalDate.now().plusDays(1).format(formatter), verifyTime);
	}

	@Test
	public void testPlusPlusPlus() {
		String verifyTime = TextfieldVerifier.verifyDate("+++");
		assertEquals(LocalDate.now().plusDays(3).format(formatter), verifyTime);
	}

	@Test
	public void testMinus() {
		String verifyTime = TextfieldVerifier.verifyDate("-");
		assertEquals(LocalDate.now().minusDays(1).format(formatter), verifyTime);
	}

	@Test
	public void testMinusMinusMinusMinus() {
		String verifyTime = TextfieldVerifier.verifyDate("----");
		assertEquals(LocalDate.now().minusDays(4).format(formatter), verifyTime);
	}

//	@Test
	public void testDezember() {
		String verifyTime = TextfieldVerifier.verifyDate("Dezember");
		assertEquals(LocalDate.now().withMonth(12).format(formatter), verifyTime);
	}

//	@Test
	public void testDez() {
		String verifyTime = TextfieldVerifier.verifyDate("Dez");
		assertEquals(LocalDate.now().withMonth(12).format(formatter), verifyTime);
	}

}
