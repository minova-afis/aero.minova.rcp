package aero.minova.rcp.xml.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;

public class GsonTest {

	private Table t;

	@Before
	public void setup() {
		t = new Table();
		t.setName("OrderReceiver");
		t.addColumn(new Column("KeyLong", DataType.INTEGER));
		t.addColumn(new Column("KeyText", DataType.STRING));
		t.addColumn(new Column("Description", DataType.STRING));
		t.addColumn(new Column("LastDate", DataType.ZONED));
		t.addColumn(new Column("ValidUntil", DataType.INSTANT));
		t.addColumn(new Column("Married", DataType.BOOLEAN));
		// Wenn in dieser Row ein & steht, bedeutet es, dass die Row inklusiver der vorherigen zusammengeführt werden. Es bildet ein selektionskriterium mit UND
		t.addColumn(new Column("&", DataType.BOOLEAN)); // Verunden

		Row r;
		r = new Row();
		r.addValue(new Value(1));
		r.addValue(null);
//		r.addValue(new Value(23.5));
		r.addValue(new Value("Wilfried Saak"));
		r.addValue(new Value(Instant.now()));
		r.addValue(new Value(ZonedDateTime.now()));
		r.addValue(new Value(true));
		r.addValue(new Value(false));
		t.addRow(r);
		r = new Row();
		r.addValue(new Value(123.45));
		r.addValue(new Value("THEUERERG"));
		r.addValue(new Value("Gudrun Theuerer"));
		r.addValue(new Value(Instant.now()));
		r.addValue(new Value(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin"))));
		r.addValue(new Value(true));
		r.addValue(new Value(false));
		t.addRow(r);
		r = new Row();
		r.addValue(null);
		r.addValue(new Value("T"));
		r.addValue(null);
		r.addValue(new Value("=01.01.2020"));
		r.addValue(null);
		r.addValue(null);
		r.addValue(new Value(false));
		t.addRow(r);
	}

	@Test
	public void ensureTableIsInitialized() {
		assertTrue(t!=null);
	}

//	@Test
	public void blabla() {
		Instant now = Instant.parse("2020-08-04T22:00:00Z");
		System.out.println(now);

		String s = null;
		String userDir = System.getProperty("user.home");
		String p = userDir
				+ "/git/aero.minova.rcp/tests/aero.minova.rcp.xml.tests/src/aero/minova/rcp/xml/tests/examplejson.json";
		try {
			// FileReader fr = new FileReader(f);
			s = new String(Files.readAllBytes(Paths.get(p)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Gson gson = new Gson();
		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.setPrettyPrinting() //
				.create();

		SqlProcedureResult sql = gson.fromJson(s, SqlProcedureResult.class);
		assertTrue(sql.getOutputParameters().getRows() != null);
	}

	@Test
	public void tableCanBeConvertedToGsonAndBackDate() {
		Gson gson = new Gson();
		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.setPrettyPrinting() //
				.create();
		assertEquals("=01.01.2020", t.getRows().get(2).getValue(3).getStringValue());
		String s = gson.toJson(t);
		assertTrue(s.length() > 0);
		Table newTable = gson.fromJson(s, Table.class);
		assertNotNull(newTable);
		assertEquals("=01.01.2020", newTable.getRows().get(2).getValue(3).getStringValue());
	}

	@Test
	public void tableCanBeConvertedToGsonAndBack() {
		Gson gson = new Gson();
		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.setPrettyPrinting() //
				.create();
		String s = gson.toJson(t);
		assertTrue(s.length() > 0);
		Table newTable = gson.fromJson(s, Table.class);
		assertNotNull(newTable);
	}

}
