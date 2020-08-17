package aero.minova.rcp.plugin1.model;

import java.util.List;
import java.util.Vector;

public class Table {

	String name;
	//vDriverIndex
	//opReadDriverRights
	//spReadDriver
	//opReadContact
	//
	List<Column> columns = new Vector<>();
	List<Row> rows = new Vector<>();

	public void setName(String name) {
		this.name = name;
	}

	public void addColumn(Column c) {
		if (rows.size() != 0) {
			throw new RuntimeException("ss");
		}
		columns.add(c);
	}
	
	public void addRow(Row r) {
		if (columns.size() != r.values.size()) {
			throw new RuntimeException();
		}
		rows.add(r);
	}

	public String getName() {
		return name;
	}
}
