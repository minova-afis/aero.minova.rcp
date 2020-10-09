package aero.minova.rcp.dataservice.internal;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.concurrent.CompletableFuture;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.OutputType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public class DataServiceTest implements IDataService {


	@Override
	public CompletableFuture<Table> getIndexDataAsync(String tableName, Table seachTable) {
		Table t = new Table();
		t.setName("OrderReceiver");
		t.addColumn(new Column("KeyLong", DataType.INTEGER, OutputType.OUTPUT));
		t.addColumn(new Column("KeyText", DataType.STRING, OutputType.OUTPUT));
		t.addColumn(new Column("Description", DataType.STRING, OutputType.OUTPUT));
		t.addColumn(new Column("LastDate", DataType.ZONED, OutputType.OUTPUT));
		t.addColumn(new Column("ValidUntil", DataType.INSTANT, OutputType.OUTPUT));
		t.addColumn(new Column("Married", DataType.BOOLEAN, OutputType.OUTPUT));
		// Wenn in dieser Row ein & steht, bedeutet es, dass die Row inklusiver der
		// vorherigen zusammengeführt werden. Es bildet ein selektionskriterium mit UND
		t.addColumn(new Column("&", DataType.BOOLEAN, OutputType.OUTPUT)); // Verunden

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

		return CompletableFuture.supplyAsync(() -> t);
	}

	@Override
	public CompletableFuture<SqlProcedureResult> getDetailDataAsync(String tableName, Table detailTable) {
  return null;
}


	@Override
	public CompletableFuture<Integer> getReturnCodeAsync(String tableName, Table detailTable) {
		// TODO Auto-generated method stub
		return null;
	}
}
