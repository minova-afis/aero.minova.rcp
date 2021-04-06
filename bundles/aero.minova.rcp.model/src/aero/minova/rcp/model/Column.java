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

}
