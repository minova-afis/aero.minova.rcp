package aero.minova.rcp.xml.tests;

import static org.junit.Assert.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;

import aero.minova.workingtime.helper.WorkingTimeHelper;

public class CalculateDateTest {

	private WorkingTimeHelper w;

	@Before
	public void init() {
		w = new WorkingTimeHelper();
	}
	@Test
	public void testCalculateDuration2Hours() {
		Instant now = Instant.now();
		Instant nowPlus2Hours = now.plusSeconds(7200);
		long min = ChronoUnit.MINUTES.between(now, nowPlus2Hours);
		Float f = w.getFloatFromMinutes(min);
		assertEquals(2.00, f, 0.00);
	}

	@Test
	public void testCalculateDuration1_15Hours() {
		Instant now = Instant.now();
		Instant nowPlus2Hours = now.plusSeconds(4500);
		long min = ChronoUnit.MINUTES.between(now, nowPlus2Hours);
		Float f = w.getFloatFromMinutes(min);
		Float chargedQunatity = w.getChargedQuantity(f);
		assertEquals(1.5, chargedQunatity, 0.0);
	}

	@Test
	public void testCalculateDuration1_14Hours() {
		Instant now = Instant.now();
		Instant nowPlus2Hours = now.plusSeconds(4440);
		long min = ChronoUnit.MINUTES.between(now, nowPlus2Hours);
		Float f = w.getFloatFromMinutes(min);
		Float chargedQunatity = w.getChargedQuantity(f);
		assertEquals(1.0, chargedQunatity, 0.0);
	}

	@Test
	public void testCalculateDuration1_44Hours() {
		Instant now = Instant.now();
		Instant nowPlus2Hours = now.plusSeconds(6240);
		long min = ChronoUnit.MINUTES.between(now, nowPlus2Hours);
		Float f = w.getFloatFromMinutes(min);
		Float chargedQunatity = w.getChargedQuantity(f);
		assertEquals(1.5, chargedQunatity, 0.0);
	}

	@Test
	public void testCalculateDuration1_45Hours() {
		Instant now = Instant.now();
		Instant nowPlus2Hours = now.plusSeconds(6300);
		long min = ChronoUnit.MINUTES.between(now, nowPlus2Hours);
		Float f = w.getFloatFromMinutes(min);
		Float chargedQunatity = w.getChargedQuantity(f);
		assertEquals(2.0, chargedQunatity, 0.0);
	}

}
