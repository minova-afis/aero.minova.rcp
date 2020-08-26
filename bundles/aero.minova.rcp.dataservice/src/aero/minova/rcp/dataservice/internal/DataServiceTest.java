package aero.minova.rcp.dataservice.internal;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.osgi.service.component.annotations.Component;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.plugin1.model.Column;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.Row;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.Value;


public class DataServiceTest implements IDataService {

	@Override
	public Table getData(String tableName, Table seachTable) {

		Table t = new Table();
		t.setName("OrderReceiver");
		t.addColumn(new Column("KeyLong", DataType.INTEGER));
		t.addColumn(new Column("KeyText", DataType.STRING));
		t.addColumn(new Column("Description", DataType.STRING));
		t.addColumn(new Column("LastDate", DataType.ZONED));
		t.addColumn(new Column("ValidUntil", DataType.INSTANT));
		t.addColumn(new Column("Married", DataType.BOOLEAN));
		// Wenn in dieser Row ein & steht, bedeutet es, dass die Row inklusiver der
		// vorherigen zusammengeführt werden. Es bildet ein selektionskriterium mit UND
		t.addColumn(new Column("&", DataType.BOOLEAN)); // Verunden

		Row r;
		r = new Row();
		r.addValue(new Value(1));
		r.addValue(null);
		r.addValue(new Value(23.5));
		r.addValue(new Value("Wilfried Saak"));
		r.addValue(new Value(Instant.now()));
		r.addValue(new Value(ZonedDateTime.now()));
		r.addValue(new Value(true));
		t.addRow(r);
		return t;
	}

	@Override
	public Table getData(String tableName) {

		Table t = new Table();
		t.setName("OrderReceiver");
		t.addColumn(new Column("KeyLong", DataType.INTEGER));
		t.addColumn(new Column("KeyText", DataType.STRING));
		t.addColumn(new Column("Description", DataType.STRING));
		t.addColumn(new Column("LastDate", DataType.ZONED));
		t.addColumn(new Column("ValidUntil", DataType.INSTANT));
		t.addColumn(new Column("Married", DataType.BOOLEAN));
		// Wenn in dieser Row ein & steht, bedeutet es, dass die Row inklusiver der
		// vorherigen zusammengeführt werden. Es bildet ein selektionskriterium mit UND
		t.addColumn(new Column("&", DataType.BOOLEAN)); // Verunden

		Row r;
		r = new Row();
		r.addValue(new Value(1));
		r.addValue(null);
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
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(new Value(false));
		t.addRow(r);
		return t;
	}

}
