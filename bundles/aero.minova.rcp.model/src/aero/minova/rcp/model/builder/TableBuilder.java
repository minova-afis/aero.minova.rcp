package aero.minova.rcp.model.builder;

import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public class TableBuilder {

	private Table table;

	private TableBuilder(String tableName) {
		table = new Table();
		table.setName(tableName);
	}

	public static TableBuilder newTable(String tableName) {
		return new TableBuilder(tableName);
	}

	public TableBuilder withColumn(String columnName, DataType dataType) {
		table.addColumn(new Column(columnName, dataType));
		return this;
	}

	public TableBuilder withKey(Integer key) {
		table.addRow();
		table.getRows().get(0).setValue(new Value(key), 0);
		return this;
	}

	public Table create() {
		return table;
	}
}
