package aero.minova.rcp.model;

/**
 * Gültige Werte
 * <ul>
 * <li>">-Wert"</li>
 * <li>"<-Wert"</li>
 * <li>"=-Wert"</li>
 * <li>"<>-Wert"</li>
 * <li>"~-Wert"</li>
 * <li>"!~-Wert"</li>
 * <li>"0-"</li>
 * <li>"!0-"</li>
 * </ul>
 * <br/>
 * Heute ist der 22.02.2021 <br/>
 * Heute Eingabe im Feld "0" -> "f-=-i-2021-02-22 00:00.00" <br/>
 * Gestern Eingabe im Feld "-" -> "f-=-i-2021-02-21 00:00.00"<br/>
 * Gestern Eingabe im Feld "210221" -> "f-=-i-2021-02-21 00:00.00"
 * 
 * @author saak
 */
public class FilterValue extends Value {
	public final Value filterValue;

	private static final long serialVersionUID = 202102221518L;

	public FilterValue(String value) {
		super(value.substring(0, value.indexOf("-")));
		filterValue = ValueDeserializer.deserialize(value.substring(value.indexOf("-") + 1));
	}

	public static FilterValue valueOf(String value) {
		return null;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
}
