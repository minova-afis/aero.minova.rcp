package aero.minova.plugininformation.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

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

		pI1 = new PluginInformation("test-12.3.1.jar");
		pI2 = new PluginInformation("test-12.1.0.jar");
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

		pI1 = new PluginInformation("test-12.3.1.jar");
		pI2 = new PluginInformation("test-12.1.0.jar");
		assertTrue(!pI2.isNewerAs(pI1));

		pI1 = new PluginInformation("test-12.0.1.jar");
		pI2 = new PluginInformation("test-12.0.1.jar");
		assertTrue(!pI2.isNewerAs(pI1));
	}

}
