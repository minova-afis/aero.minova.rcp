package aero.minova.rcp.model;

public class LookupValue extends Value {
	private static final long serialVersionUID = 202102061225L;
	public final Integer keyLong;
	public final String keyText;
	public final String description;

	public LookupValue(Integer keyLong, String keyText, String description) {
		super(keyLong);
		this.keyLong = keyLong;
		this.keyText = keyText == null ? "" : keyText;
		this.description = description == null ? "" : description;
	}

	public int compareTo(LookupValue lv2) {
		if (keyText.compareTo(lv2.keyText) == 0) return description.compareTo(lv2.description);
		return keyText.compareTo(lv2.keyText);
	}
}

