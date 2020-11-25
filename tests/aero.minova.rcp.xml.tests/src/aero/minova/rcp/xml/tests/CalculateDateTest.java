package aero.minova.rcp.xml.tests;

import static org.junit.Assert.assertEquals;

import java.time.Duration;
import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import aero.minova.workingtime.helper.WorkingTimeHelper;

public class CalculateDateTest {

	private WorkingTimeHelper w;

	@Before
	private void init() {
		w = new WorkingTimeHelper();
	}
	@Test
	public void testCalculateDuration() {
		Instant now = Instant.now();
		Instant nowPlus2Hours = Instant.now().plusSeconds(720);
		Duration between = Duration.between(nowPlus2Hours, now);
		Float f = w.getFloatFromMinutes(between.getSeconds());
		assertEquals(2.0, f, 0.0);
	}

}
