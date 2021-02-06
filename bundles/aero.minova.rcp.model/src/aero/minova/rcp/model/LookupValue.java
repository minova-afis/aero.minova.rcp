package aero.minova.rcp.model;

public class LookupValue {
	public final int keyLong;
	public final String keyText;
	public final String description;

	public LookupValue(int keyLong, String keyText, String description) {
		this.keyLong = keyLong;
		this.keyText = keyText == null ? "" : keyText;
		this.description = description == null ? "" : description;
	}

	public int compareTo(LookupValue lv2) {
		if (keyText.compareTo(lv2.keyText) == 0) return description.compareTo(lv2.description);
		return keyText.compareTo(lv2.keyText);
	}
}

