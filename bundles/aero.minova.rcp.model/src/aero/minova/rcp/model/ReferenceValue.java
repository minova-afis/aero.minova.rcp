package aero.minova.rcp.model;

public class ReferenceValue extends Value {

	private static final long serialVersionUID = 202111220929L;

	private int rowNumber;
	private String columnName;

	public ReferenceValue(String referenceID, int rowNumber, String columnName) {
		super(referenceID, DataType.REFERENCE);
		this.setRowNumber(rowNumber);
		this.setColumnName(columnName);
	}

	public ReferenceValue(String referenceID, String columnName) {
		this(referenceID, 0, columnName);
	}

	@Override
	public String toString() {
		return ValueSerializer.serialize(this).toString();
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
}
