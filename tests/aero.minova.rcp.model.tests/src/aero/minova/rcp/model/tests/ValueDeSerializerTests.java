package aero.minova.rcp.model.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.PeriodValue;
import aero.minova.rcp.model.ReferenceValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;

/**
 * Klasse zum Testen der ValueDe-/Serializer. FilterValues speziell haben eigene Tests in FilterValueTests
 * 
 * @author janiak
 */
class ValueDeSerializerTests {

	//////////////////
	// Deserialize ///
	//////////////////

	@Test
	void deserializeInt() {
		assertEquals(new Value(10, DataType.INTEGER), ValueDeserializer.deserialize("n-10"));
	}

	@Test
	void deserializeDouble() {
		assertEquals(new Value(11.11, DataType.DOUBLE), ValueDeserializer.deserialize("d-11.11"));
	}

	@Test
	void deserializeMoney() {
		assertEquals(new Value(11.11, DataType.BIGDECIMAL), ValueDeserializer.deserialize("m-11.11"));
	}

	@Test
	void deserializeInstant() {
		assertEquals(new Value(Instant.ofEpochMilli(0), DataType.INSTANT), ValueDeserializer.deserialize("i-1970-01-01T00:00:00Z"));
	}

	@Test
	void deserializeZoned() {
		assertEquals(new Value(ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, ZoneId.of("UTC+1")), DataType.ZONED),
				ValueDeserializer.deserialize("z-2015-11-30T23:45:59.000001234+01:00[UTC+01:00]"));
	}

	@Test
	void deserializeBoolean() {
		assertEquals(new Value(true, DataType.BOOLEAN), ValueDeserializer.deserialize("b-true"));
	}

	@Test
	void deserializeString() {
		assertEquals(new Value("TEST", DataType.STRING), ValueDeserializer.deserialize("s-TEST"));
	}

	@Test
	void deserializePeriod() {
		Instant base = LocalDate.of(2022, 7, 26).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant due = LocalDate.of(2028, 7, 26).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals(new PeriodValue(base, "+6y", due),
				ValueDeserializer.deserialize("s-{\"base\":\"2022-07-26T00:00:00Z\",\"userInput\":\"+6y\",\"due\":\"2028-07-26T00:00:00Z\"}"));
	}

	@Test
	void deserializePeriodNull() {
		assertEquals(new PeriodValue(null, null, null), ValueDeserializer.deserialize("s-{\"base\":null,\"userInput\":null,\"due\":null}"));
	}

	@Test
	void deserializeReference() {
		assertEquals(new ReferenceValue("parent", 5, "Column-Name"), ValueDeserializer.deserialize("r-parent-5-Column-Name"));
	}

	//////////////////
	// Serialize /////
	//////////////////

	@Test
	void serializeInt() {
		assertEquals("n-10", ValueSerializer.serialize(new Value(10, DataType.INTEGER)).getAsString());
	}

	@Test
	void serializeDouble() {
		assertEquals("d-11.11", ValueSerializer.serialize(new Value(11.11, DataType.DOUBLE)).getAsString());
	}

	@Test
	void serializeMoney() {
		assertEquals("m-11.11", ValueSerializer.serialize(new Value(11.11, DataType.BIGDECIMAL)).getAsString());
	}

	@Test
	void serializeInstant() {
		assertEquals("i-1970-01-01T00:00:00Z", ValueSerializer.serialize(new Value(Instant.ofEpochMilli(0), DataType.INSTANT)).getAsString());
	}

	@Test
	void serializeZoned() {
		assertEquals("z-2015-11-30T23:45:59.000001234+01:00[UTC+01:00]",
				ValueSerializer.serialize(new Value(ZonedDateTime.of(2015, 11, 30, 23, 45, 59, 1234, ZoneId.of("UTC+1")), DataType.ZONED)).getAsString());
	}

	@Test
	void serializeBoolean() {
		assertEquals("b-true", ValueSerializer.serialize(new Value(true, DataType.BOOLEAN)).getAsString());
	}

	@Test
	void serializeString() {
		assertEquals("s-TEST", ValueSerializer.serialize(new Value("TEST", DataType.STRING)).getAsString());
	}

	@Test
	void serializePeriod() {
		Instant base = LocalDate.of(2022, 7, 26).atStartOfDay().toInstant(ZoneOffset.UTC);
		Instant due = LocalDate.of(2028, 7, 26).atStartOfDay().toInstant(ZoneOffset.UTC);
		assertEquals("s-{\"base\":\"2022-07-26T00:00:00Z\",\"userInput\":\"+6y\",\"due\":\"2028-07-26T00:00:00Z\"}",
				ValueSerializer.serialize(new PeriodValue(base, "+6y", due)).getAsString());
	}

	@Test
	void serializePeriodNull() {
		assertEquals("s-{\"base\":null,\"userInput\":null,\"due\":null}", ValueSerializer.serialize(new PeriodValue(null, null, null)).getAsString());
	}

	@Test
	void serializeReference() {
		assertEquals("r-parent-6-Column-Name", ValueSerializer.serialize(new ReferenceValue("parent", 6, "Column-Name")).getAsString());
	}

}
