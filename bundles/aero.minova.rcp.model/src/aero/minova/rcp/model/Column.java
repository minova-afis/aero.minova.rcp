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

}
