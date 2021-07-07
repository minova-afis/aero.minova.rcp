package aero.minova.rcp.model;

public class Column {

	public Column(String name, DataType type, OutputType outputType) {
		this(name, type);
		this.outputType = outputType;
	}

	public Column(String name, DataType type) {
		this.name = name;
		this.type = type;
	}

	String name;
	DataType type;
	OutputType outputType;
	String label;
	Integer decimals;
	DateTimeType dateTimeType;
	boolean readOnly;
	boolean required;
	boolean isLookup;
	String lookupTable;

	@Override
	public String toString() {
		return "Column [name=" + getName() + ", type=" + getType() + ", outputType=" + outputType + "]";
	}

	public DataType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public Integer getDecimals() {
		return decimals;
	}

	public void setDecimals(Integer decimals) {
		this.decimals = decimals;
	}

	public DateTimeType getDateTimeType() {
		return dateTimeType;
	}

	public void setDateTimeType(DateTimeType dateTimeType) {
		this.dateTimeType = dateTimeType;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public boolean isLookup() {
		return isLookup;
	}

	public void setLookup(boolean isLookup) {
		this.isLookup = isLookup;
	}

	public String getLookupTable() {
		return lookupTable;
	}

	public void setLookupTable(String lookupTable) {
		this.lookupTable = lookupTable;
	}

}
