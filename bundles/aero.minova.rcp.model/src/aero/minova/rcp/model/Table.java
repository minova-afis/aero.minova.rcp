package aero.minova.rcp.model;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Table {

	String name;
	// vDriverIndex
	// opReadDriverRights
	// spReadDriver
	// opReadContact
	//
	List<Column> columns = new ArrayList<>();
	List<Row> rows = new ArrayList<>();

	public void setName(String name) {
		this.name = name;
	}

	public String getColumnName(int index) {
		return columns.get(index).getName();
	}

	public int getColumnCount() {
		return columns.size();
	}

	public void addColumn(Column c) {
		if (rows.size() != 0) {
			throw new RuntimeException("Tabelle mit existierenden Zeilen kann nicht erweitert werden!");
		}
		columns.add(c);
	}

	public int getColumnIndex(String columnName) {
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getName().equals(columnName)) {
				return i;
			}
		}
		return -1;
	}

	public List<Column> getColumns() {
		return columns;
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

	public Row addRow() {
		Row row = new Row();
		for (Column c : columns) {
			if (c.getType().equals(DataType.STRING)) {
				row.addValue(new Value((String) null));
			} else if (c.getType().equals(DataType.INTEGER)) {
				row.addValue(new Value((Integer) null));
			} else if (c.getType().equals(DataType.INSTANT)) {
				row.addValue(new Value((Instant) null));
			} else if (c.getType().equals(DataType.ZONED)) {
				row.addValue(new Value((ZonedDateTime) null));
			} else if (c.getType().equals(DataType.DOUBLE)) {
				row.addValue(new Value((Double) null));
			} else if (c.getType().equals(DataType.BOOLEAN)) {
				row.addValue(new Value((Boolean) null));
			} else if (c.getType().equals(DataType.BIGDECIMAL)) {
				row.addValue(new Value(null, DataType.BIGDECIMAL));
			} else {
				System.err.println("Typ " + c.getType() + " noch nicht definiert! (aero.minova.rcp.model.Table");
			}
		}
		rows.add(row);
		return row;
	}

	public void deleteRow(Row row) {
		rows.remove(row);
	}
}
