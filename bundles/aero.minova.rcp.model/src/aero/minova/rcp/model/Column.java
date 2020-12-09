package aero.minova.rcp.model;

public class Column {
	public Column(String name, DataType type, OutputType outputType) {
		this.name = name;
		this.type = type;
		this.outputType = outputType;
	}

	public Column(String name, DataType type) {
		this.name = name;
		this.type = type;
	}
	String name;
	DataType type;
	OutputType outputType;


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

}
