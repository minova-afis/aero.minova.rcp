package aero.minova.plugininformation.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.osgi.framework.Version;

import aero.minova.rcp.dataservice.internal.PluginInformation;

class PluginInformationTest {

	@Test
	void testPluginInformationNewest() throws IOException {

		PluginInformation pI1 = new PluginInformation("test-12.0.0.jar");
		PluginInformation pI2 = new PluginInformation("test-11.0.0.jar");
		assertTrue(pI1.isNewerAs(pI2));

		pI1 = new PluginInformation("test-12.1.0.jar");
		pI2 = new PluginInformation("test-12.0.0.jar");
		assertTrue(pI1.isNewerAs(pI2));

		pI1 = new PluginInformation("test-12.0.1.jar");
		pI2 = new PluginInformation("test-12.0.0.jar");
		assertTrue(pI1.isNewerAs(pI2));

		pI1 = new PluginInformation("test-12.0.12.jar");
		pI2 = new PluginInformation("test-12.0.2.jar");
		assertTrue(pI1.isNewerAs(pI2));

		// Gleich -> P1 ist nicht jünger
		pI1 = new PluginInformation("test-12.0.1.jar");
		pI2 = new PluginInformation("test-12.0.1.jar");
		assertTrue(!pI1.isNewerAs(pI2));

		// Anderer Name -> false
		pI1 = new PluginInformation("test-12.0.1.jar");
		pI2 = new PluginInformation("test123-12.1.0.jar");
		assertTrue(!pI1.isNewerAs(pI2));

		// Andere Reihenfolge -> P2 ist älter
		pI1 = new PluginInformation("test-12.0.0.jar");
		pI2 = new PluginInformation("test-11.0.0.jar");
		assertTrue(!pI2.isNewerAs(pI1));

		pI1 = new PluginInformation("test-12.1.0.jar");
		pI2 = new PluginInformation("test-12.0.0.jar");
		assertTrue(!pI2.isNewerAs(pI1));

		pI1 = new PluginInformation("test-12.0.1.jar");
		pI2 = new PluginInformation("test-12.0.0.jar");
		assertTrue(!pI2.isNewerAs(pI1));

		pI1 = new PluginInformation("test-12.0.12.jar");
		pI2 = new PluginInformation("test-12.0.2.jar");
		assertTrue(!pI2.isNewerAs(pI1));

		pI1 = new PluginInformation("test-12.0.1.jar");
		pI2 = new PluginInformation("test-12.0.1.jar");
		assertTrue(!pI2.isNewerAs(pI1));
	}

	@Test
	void testDifferent() {
		PluginInformation pI1 = new PluginInformation("test-12.0.0.jar");
		Version v = new Version(12, 0, 0);
		assertTrue(!pI1.isDifferent(v));

		pI1 = new PluginInformation("test-13.0.0.jar");
		v = new Version(12, 0, 0);
		assertTrue(pI1.isDifferent(v));

		pI1 = new PluginInformation("test-12.1.0.jar");
		v = new Version(12, 0, 0);
		assertTrue(pI1.isDifferent(v));

		pI1 = new PluginInformation("test-12.0.1.jar");
		v = new Version(12, 0, 0);
		assertTrue(pI1.isDifferent(v));

		pI1 = new PluginInformation("test-12.0.0-SNAPSHOT.jar");
		v = new Version(12, 0, 0, "SNAPSHOT");
		assertTrue(!pI1.isDifferent(v));

		pI1 = new PluginInformation("test-12.0.0-SNAPSHOT.jar");
		v = new Version(12, 0, 0, "quatsch");
		assertTrue(pI1.isDifferent(v));
	}

	@Test
	void testNoVersion() {
		PluginInformation pI1 = new PluginInformation("blubblub.jar");
		PluginInformation pI2 = new PluginInformation("blubblub-11.0.0.jar");
		assertTrue(!pI1.isNewerAs(pI2));
	}

	@Test
	void testToString() {
		PluginInformation pI1 = new PluginInformation("blubblub-11.0.0.jar");
		assertEquals("PluginInformation blubblub-11.0.0", pI1.toString());

		pI1 = new PluginInformation("blubblub-11.0.0-SNAPSHOT.jar");
		assertEquals("PluginInformation blubblub-11.0.0-SNAPSHOT", pI1.toString());

	}

}
