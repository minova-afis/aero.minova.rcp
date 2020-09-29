package aero.minova.rcp.plugin1.model;

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
}
