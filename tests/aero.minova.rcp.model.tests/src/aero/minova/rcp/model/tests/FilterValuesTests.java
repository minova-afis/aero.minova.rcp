package aero.minova.rcp.model.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Test;

import aero.minova.rcp.model.FilterValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;

public class FilterValuesTests {

	@Test
	public void testSerializeString() {
		FilterValue fv = new FilterValue("=", "Test", "");
		assertEquals("f-=-s-Test", ValueSerializer.serialize(fv).getAsString());
	}

	@Test
	public void testSerializeInt() {
		FilterValue fv = new FilterValue("=", 1234, "");
		assertEquals("f-=-n-1234", ValueSerializer.serialize(fv).getAsString());
	}

	@Test
	public void testSerializeDouble() {
		FilterValue fv = new FilterValue("=", 12.34, "");
		assertEquals("f-=-d-12.34", ValueSerializer.serialize(fv).getAsString());
	}

	@Test
	public void testSerializeInstant() {
		FilterValue fv = new FilterValue("=", Instant.ofEpochMilli(0), "");
		assertEquals("f-=-i-1970-01-01T00:00:00Z", ValueSerializer.serialize(fv).getAsString());
	}

	@Test
	public void testSerializeZoned() {
		FilterValue fv = new FilterValue("=", ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, ZoneId.of("UTC+1")), "");
		assertEquals("f-=-z-2015-11-30T23:45:59.000001234+01:00[UTC+01:00]", ValueSerializer.serialize(fv).getAsString());
	}

	@Test
	public void testSerializeBoolean() {
		FilterValue fv = new FilterValue("=", true, "");
		assertEquals("f-=-b-true", ValueSerializer.serialize(fv).getAsString());
	}

	@Test
	public void testDeserializerString() {
		Value v = ValueDeserializer.deserialize("f-=-s-Test");
		assertEquals(new FilterValue("=", "Test", ""), v);
		assertNotEquals(new FilterValue("<", "Test", ""), v);
		assertNotEquals(new FilterValue("=", "Test2", ""), v);
	}
}
