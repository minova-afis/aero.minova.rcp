package aero.minova.rcp.plugin1.model.builder;

import aero.minova.rcp.plugin1.model.Column;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.OutputType;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.Value;

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
		table.addColumn(new Column(columnName, dataType, OutputType.OUTPUT));
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
