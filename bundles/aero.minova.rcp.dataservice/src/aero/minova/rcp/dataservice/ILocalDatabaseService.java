package aero.minova.rcp.dataservice;

import java.util.Map;

import aero.minova.rcp.model.LookupEntry;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public interface ILocalDatabaseService {

	Table getResultsForLookupField(String name);

	void replaceResultsForLookupField(String name, Table table);

	Map getResultsForKeyLong(String name, Integer keyLong);

	void addResultsForLookupField(String name, Table table);

	/**
	 * Aktualisiert die Werte für eine Lookup und einen KeyLong
	 * 
	 * @param lookupName
	 *            Name der Tabelle oder Name der Prozedur ohne 'List' / 'Resolve'). Dieser Wert darf nicht leer sein.
	 * @param keyLong
	 *            Wert des KeyLongs für die Lookup-Zeile. Dieser Wert darf nicht leer sein.
	 * @param keyText
	 *            Wert für den Benutzerschlüssel. Dieser Wert darf nicht leer sein. Dieser Wert darf sich normalerweise in der Datenbank nicht ändern.
	 * @param description
	 *            Optionale Beschreibung für den Schlüssel (keyText)
	 */
	public void updateResolveValue(String lookupName, Value keyLong, Value keyText, Value description);

	/**
	 * Diese Methode sucht in der lokalen Datenbank einen Wert für den KeyLong und den LookupName
	 * 
	 * @param lookupName
	 *            Name der Tabelle oder der gespeicherten Prozedur (ohne 'list' / 'Resolve'), über die die Werte ermittelt werden können.
	 * @param keyLong
	 *            KeyLong des Wertes. Dieser wird auch in der Datenbank als Referenz gespeichert.
	 * @return null, wenn der Wert nicht lokal gespeichert ist
	 */
	LookupEntry resolveValue(String lookupName, Value keyLong);
}
