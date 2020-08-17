package aero.minova.rcp.plugin1.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Table {

	String name;
	// vDriverIndex
	// opReadDriverRights
	// spReadDriver
	// opReadContact
	//
	List<Column> columns = new ArrayList();
	List<Row> rows = new ArrayList();

	public void setName(String name) {
		this.name = name;
	}

	public String getColumnName(int index) {
		return columns.get(index).name;
	}

	public int getColumnCount() {
		return columns.size();
	}

	public void addColumn(Column c) {
		if (rows.size() != 0) {
			throw new RuntimeException("ss");
		}
		columns.add(c);
	}

	public int getColumnIndex(String columnName) {
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).name.equals(columnName)) {
				return i;
			}
		}
		return -1;
	}

	public void addRow(Row r) {
		if (columns.size() != r.values.size()) {
			throw new RuntimeException("Mehr Columns definiert, als Wert übergeben!");
		}
		rows.add(r);
	}

	public String getName() {
		return name;
	}

	public List<Row> getRows() {
		return rows;
	}
}
