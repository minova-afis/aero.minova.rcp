package aero.minova.rcp.model;

import java.text.MessageFormat;

public class LookupValue extends Value {
	private static final long serialVersionUID = 202102061225L;
	public final Integer keyLong;
	public String keyText;
	public String description;

	public LookupValue(Integer keyLong, String keyText, String description) {
		super(keyLong);
		this.keyLong = keyLong;
		this.keyText = keyText == null ? "" : keyText;
		this.description = description == null ? "" : description;
	}

	public int compareTo(LookupValue lv2) {
		if (keyText.compareTo(lv2.keyText) == 0) {
			return description.compareTo(lv2.description);
		}
		return keyText.compareTo(lv2.keyText);
	}

	public Integer getKeyLong() {
		return keyLong;
	}

	public String getKeyText() {
		return keyText;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return MessageFormat.format("LookupValue [type=INTEGER, value={0},{1},{2}]", keyLong, keyText, description);
	}
}
