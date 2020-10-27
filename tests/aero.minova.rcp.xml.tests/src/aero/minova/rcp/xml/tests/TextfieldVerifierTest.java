package aero.minova.rcp.xml.tests;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import aero.minova.rcp.rcp.util.TextfieldVerifier;

public class TextfieldVerifierTest {

	String heute;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Test
	public void test0() {
		String verifyTime = TextfieldVerifier.verifyDate("0", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().format(formatter), verifyTime);
	}

	@Test
	public void testh() {
		String verifyTime = TextfieldVerifier.verifyDate("h", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().format(formatter), verifyTime);
	}

	@Test
	public void testH() {
		String verifyTime = TextfieldVerifier.verifyDate("H", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().format(formatter), verifyTime);
	}

	@Test
	public void testHeute() {
		String verifyTime = TextfieldVerifier.verifyDate("heute", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().format(formatter), verifyTime);
	}

	@Test
	public void testm() {
		String verifyTime = TextfieldVerifier.verifyDate("m", ZoneId.systemDefault().getId());
		assertEquals(LocalDate.now().plusDays(1).format(formatter), verifyTime);
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
	public void testMinusMinusMinusMinus() {
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
