package aero.minova.rcp.model;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Table {

	String name;
	private TableMetaData metaData;
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
		if (!rows.isEmpty()) {
			throw new RuntimeException("Tabelle mit existierenden Zeilen kann nicht erweitert werden!");
		}
		columns.add(c);
	}

	public int getColumnIndex(String columnName) {
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getName().equalsIgnoreCase(columnName)) {
				return i;
			}
		}
		return -1;
	}

	public Column getColumn(String columnName) {
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).getName().equalsIgnoreCase(columnName)) {
				return columns.get(i);
			}
		}
		return null;
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

	public void setRows(List<Row> newRows) {
		rows.clear();
		rows.addAll(newRows);
	}

	public void clearRows() {
		rows.clear();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columns == null) ? 0 : columns.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((rows == null) ? 0 : rows.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		Table other = (Table) obj;

		// Namen vergleichen
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}

		// Spalten vergleichen
		if (columns == null) {
			if (other.columns != null) {
				return false;
			}
		} else if (columns.size() != other.columns.size()) {
			return false;
		} else {
			for (int i = 0; i < columns.size(); i++) {
				if (!columns.get(i).equals(other.columns.get(i))) {
					return false;
				}
			}
		}

		// Zeilen vergleichen
		if (rows == null) {
			if (other.rows != null) {
				return false;
			}
		} else if (rows.size() != other.rows.size()) {
			return false;
		} else {
			for (int i = 0; i < rows.size(); i++) {
				if (!rows.get(i).equals(other.rows.get(i), false)) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Gibt eine Kopie der Tabelle zurück. Änderungen an Zeilen dieser Tabelle haben keine Auswirkung auf die ursprüngliche Tabelle.
	 * 
	 * @return
	 */
	public Table copy() {
		Table table = new Table();
		table.setName(name);

		for (Column c : getColumns()) {
			table.addColumn(c);
		}

		for (Row r : getRows()) {
			Row newRow = new Row();
			for (Value v : r.getValues()) {
				if (v == null) {
					newRow.addValue(null);
				} else {
					newRow.addValue(new Value(v.getValue(), v.getType()));
				}
			}
			table.addRow(newRow);
		}

		return table;
	}

	@Override
	public String toString() {
		StringBuilder columnsString = new StringBuilder("\n");
		for (Column c : columns) {
			columnsString.append(c + " ");
		}
		StringBuilder rowString = new StringBuilder("\n");
		for (Row r : rows) {
			rowString.append(r + "\n");
		}
		return "Table " + name + columnsString.toString() + rowString.toString();
	}

	public void setValue(String columnName, int rowIndex, Value newValue) {
		setValue(getColumnIndex(columnName), rowIndex, newValue);
	}

	public void setValue(String columnName, Row r, Value newValue) {
		r.setValue(newValue, getColumnIndex(columnName));
	}

	public void setValue(int columnIndex, int rowIndex, Value newValue) {
		rows.get(rowIndex).setValue(newValue, columnIndex);
	}

	public Value getValue(String columnName, Row r) {
		return r.getValue(getColumnIndex(columnName));
	}

	public Value getValue(String columnName, int rowIndex) {
		return getValue(getColumnIndex(columnName), rowIndex);
	}

	public Value getValue(int col, int row) {
		return rows.get(row).getValue(col);
	}

	public void addColumns(List<Column> columns) {
		for (Column c : columns) {
			addColumn(c);
		}
	}

	public void addRows(List<Row> rows) {
		for (Row r : rows) {
			addRow(r);
		}
	}

	/**
	 * Versucht die Zeilen der übergebenen Tabelle hinzuzufügen. Dabei werden sie Spaltennamen verglichen. Spalten in der übergebenen Tabelle die in der
	 * aktuellen nicht existieren werden ignoriert. Fehlen Spalten der aktuellen Tabelle in der Übergebenen wird null eingetragen.
	 * 
	 * @param rowsToAdd
	 */
	public void addRowsFromTable(Table rowsToAdd) {
		for (Row rowInNewTable : rowsToAdd.getRows()) {
			Row rowInOriginal = addRow();

			// Passende Werte in der übergebenen Tabelle finden (über Column Namen)
			for (Column originalColumn : getColumns()) {

				for (Column newColumn : rowsToAdd.getColumns()) {
					if (originalColumn.getName().equalsIgnoreCase(newColumn.getName())) {
						Value v = rowInNewTable.getValue(rowsToAdd.getColumns().indexOf(newColumn));
						int index = getColumns().indexOf(originalColumn);
						rowInOriginal.setValue(v, index);
					}
				}
			}
		}
	}

	/**
	 * Liefert true, wenn die Tabelle eine Zeile mit gleichen Keys enthält. Es wird davon ausgegangen, dass die Zeilen die Werte in gleicher Reihenfolge
	 * enthalten.
	 * 
	 * @param r
	 * @return
	 */
	public boolean containsRowByKeys(Row r) {
		for (Row tableRow : rows) {
			boolean keysMatch = true;
			for (int i = 0; i < r.size(); i++) {
				if (columns.get(i).key && !Objects.equals(tableRow.getValue(i), r.getValue(i))) {
					keysMatch = false;
					break;
				}
			}
			if (keysMatch) {
				return true;
			}
		}
		return false;
	}

	public void fillMetaData(Integer limit, Integer totalResults, Integer page) {
		TableMetaData newMetaData = getMetaData();
		if (newMetaData == null) {
			newMetaData = new TableMetaData();
		}
		if (limit <= 0) {
			limit = totalResults;
		}
		if (totalResults != null && limit != null && totalResults > 0 && limit > 0) {
			int totalPages = (int) Math.ceil(totalResults / (double) limit);
			newMetaData.setResultsLeft(Math.max(totalResults - (page * limit), 0));
			newMetaData.setTotalPages(totalPages);
		}
		newMetaData.setLimited(limit);
		newMetaData.setPage(page);
		newMetaData.setTotalResults(totalResults);

		this.setMetaData(newMetaData);
	}

	public TableMetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(TableMetaData metaData) {
		this.metaData = metaData;
	}
}
