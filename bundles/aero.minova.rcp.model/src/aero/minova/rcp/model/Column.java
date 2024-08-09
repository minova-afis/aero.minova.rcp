package aero.minova.rcp.model;

public class Column {

	public Column(String name, DataType type, OutputType outputType) {
		this(name, type);
		this.setOutputType(outputType);
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
	boolean visible;
	KeyType keyType;
	boolean key;

	@Override
	public String toString() {
		return "Column [name=" + getName() + ", type=" + getType() + ", outputType=" + getOutputType() + "]";
	}

	public DataType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public OutputType getOutputType() {
		return outputType;
	}

	public void setOutputType(OutputType outputType) {
		this.outputType = outputType;
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

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public KeyType getKeyType() {
		return keyType;
	}

	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
		if (KeyType.PRIMARY.equals(keyType)) {
			key = true;
		}
	}

	public boolean isKey() {
		return key;
	}

	public void setKey(boolean key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dateTimeType == null) ? 0 : dateTimeType.hashCode());
		result = prime * result + ((decimals == null) ? 0 : decimals.hashCode());
		result = prime * result + (isLookup ? 1231 : 1237);
		result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((getOutputType() == null) ? 0 : getOutputType().hashCode());
		result = prime * result + (readOnly ? 1231 : 1237);
		result = prime * result + (required ? 1231 : 1237);
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Column other = (Column) obj;

		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equalsIgnoreCase(other.name))
			return false;

		// TODO: Typ überprüfen?
//		if (type != other.type)
//			return false;
		return true;
	}

}
