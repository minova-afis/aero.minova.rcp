package aero.minova.rcp.dataservice.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.LookupEntry;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

public class LocalDatabaseService {

	private String protocol = "jdbc:h2:file:";

	private PreparedStatement deleteAllEntriesOfLookup;

	private PreparedStatement insertEntryOfLookup;

	private PreparedStatement createTableIfNotExisting;

	private PreparedStatement insertLookupResolveValue;
	private PreparedStatement selectLookupResolveValue;
	private PreparedStatement updateLookupResolveValue;

	private Statement s;

	private ResultSet rs = null;

	private Connection conn;

	/**
	 * Öffnen der Datenbankverbindung beim Öffnen der Klasse und erstellt, wenn nicht vorhanden, zwei Tabellen Lookupvalues: enthält sämtliche Optionen, welche
	 * derzeit von der Anwendung verwendet werden. Der Inhalt wird ständig ausgetauscht AllLookupvalues: enthält sämtliche Optionen. Diese Tabelle wird nur
	 * erweitert, um ein schnelles Laden zu ermöglichen Lookup: enthält die procedureprefix, um einzräge eindeutig zuordnen zu können KeyLong, KeyText,
	 * Description: die Informationen aus dem CAS
	 */
	@Activate
	protected void activateComponent() {
		if (conn != null) {
			return;
		}
		try {
			Class.forName("org.h2.Driver");
			String dbUrl = protocol + Platform.getInstanceLocation().getURL().getPath().toString() + "/cache/lookup";
			conn = DriverManager.getConnection(dbUrl);

			conn.setAutoCommit(false);
			createTableIfNotExisting = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS Lookupvalues ( Lookupvalue_id IDENTITY NOT NULL PRIMARY KEY, Lookup VARCHAR NOT NULL, KeyLong int NOT NULL, KeyText VARCHAR NOT NULL, Description VARCHAR)");
			createTableIfNotExisting.execute();
			createTableIfNotExisting = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS AllLookupvalues ( Lookupvalue_id IDENTITY NOT NULL PRIMARY KEY, Lookup VARCHAR NOT NULL, KeyLong int NOT NULL, KeyText VARCHAR NOT NULL, Description VARCHAR)");
			createTableIfNotExisting.execute();
			createTableIfNotExisting = conn.prepareStatement(
					"CREATE TABLE IF NOT EXISTS LookupResolveValues ( Lookup VARCHAR NOT NULL, KeyLong int NOT NULL, KeyText VARCHAR NOT NULL, Description VARCHAR, PRIMARY KEY (Lookup, KeyLong))");
			createTableIfNotExisting.execute();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Schließen der Datenbankverbindung beim Schließen der Klasse
	 */
	@Deactivate
	protected void deactivateComponent() {
		disconnect();
	}

	/**
	 * @param name
	 *            Name des Lookupfields, für welches wir die die Optionen Anfragen
	 * @return ein TableObject, welches alle wichtigen informationen enthält, um es zu verwenden wie eine Anfrage an den CAS
	 */
	public Table getResultsForLookupField(String name) {
		if (conn != null) {
			if (name != null) {
				Table t = new Table();
				Column collumn = new Column(Constants.TABLE_KEYLONG, DataType.INTEGER);
				t.addColumn(collumn);
				collumn = new Column(Constants.TABLE_KEYTEXT, DataType.STRING);
				t.addColumn(collumn);
				collumn = new Column(Constants.TABLE_DESCRIPTION, DataType.STRING);
				t.addColumn(collumn);
				try {
					s = conn.createStatement();
					rs = s.executeQuery("SELECT * FROM Lookupvalues WHERE Lookup ='" + name + "'");
					while (rs.next()) {
						Row r = new Row();
						r.addValue(new Value(rs.getInt(3), DataType.INTEGER));
						r.addValue(new Value(rs.getString(4), DataType.STRING));
						if (rs.getString(5).equals("")) {
							r.addValue(null);
						} else {
							r.addValue(new Value(rs.getString(5), DataType.STRING));
						}
						t.addRow(r);

					}
				} catch (SQLException e) {
					System.out.println("Auslesen der Datenbank fehlgeschlagen.");
					e.printStackTrace();
					return null;
				}
				if (t.getRows() != null) {
					return t;
				} else {
					return null;
				}

			} else {
				System.out.println("Fehlende LookupField-Bezeichnung");
				return null;
			}
		} else {
			System.out.println("Fehlende Verbindung zur Datenbank");
			return null;
		}
	}

	public Map<String, Object> getResultsForKeyLong(String name, Integer keyLong) {
		Map<String, Object> map = new HashMap<>();
		if (conn != null) {
			if (name != null && keyLong != null) {
				try {
					s = conn.createStatement();
					rs = s.executeQuery("SELECT * FROM AllLookupvalues WHERE Lookup ='" + name + "'AND KeyLong ='" + keyLong + "'");
					while (rs.next()) {
						map.put(Constants.TABLE_KEYLONG, rs.getInt(3));
						map.put(Constants.TABLE_KEYTEXT, rs.getString(4));
						if (rs.getString(5).equals("")) {
							map.put(Constants.TABLE_DESCRIPTION, null);
						} else {
							map.put(Constants.TABLE_DESCRIPTION, rs.getString(5));
						}

					}
				} catch (SQLException e) {
					System.out.println("Auslesen der Datenbank fehlgeschlagen.");
					e.printStackTrace();
					return null;
				}
				if (map.get(Constants.TABLE_KEYLONG) != null) {
					return map;
				} else {
					return null;
				}

			} else {
				System.out.println("Fehlende LookupField-Bezeichnung");
				return null;
			}
		} else {
			System.out.println("Fehlende Verbindung zur Datenbank");
			return null;
		}
	}

	/**
	 * Wir löschen sämtlichen Optionen, welche wir für ein bestimmtes Lookupfield gespeichert haben und hinterlegen die neuen Ergebnisse des CAS
	 * 
	 * @param name
	 *            Name des Lookupfields, für welches wir die die Optionen Anfragen
	 * @param Table
	 *            Das Ergebniss der CAS-Anfrage, welches wir in die Datenbank schreiben möchten
	 */
	public void replaceResultsForLookupField(String name, Table table) {
		if (conn != null) {
			if (table != null || name != null) {
				try {
					insertEntryOfLookup = conn.prepareStatement("INSERT INTO Lookupvalues(Lookup, KeyLong, KeyText, Description) VALUES (?, ?, ?, ?)");
					deleteAllEntriesOfLookup = conn.prepareStatement("DELETE FROM Lookupvalues WHERE Lookup='" + name + "'");
					deleteAllEntriesOfLookup.execute();

					int i = 0;
					while (i < table.getRows().size()) {
						Row r = table.getRows().get(i);
						insertEntryOfLookup.setString(1, name);
						insertEntryOfLookup.setInt(2, r.getValue(table.getColumnIndex(Constants.TABLE_KEYLONG)).getIntegerValue());
						insertEntryOfLookup.setString(3, r.getValue(table.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue());
						if (r.getValue(table.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
							insertEntryOfLookup.setString(4, r.getValue(table.getColumnIndex(Constants.TABLE_DESCRIPTION)).getStringValue());
						} else {
							insertEntryOfLookup.setString(4, "");
						}
						insertEntryOfLookup.execute();

						i++;
					}
					conn.commit();
				} catch (SQLException e) {
					System.out.println("Austausch der Werte der Datenbank fehlgeschlagen.");
					e.printStackTrace();
				}
			} else {
				System.out.println("Kein Gültiges TableObject oder fehlende LookupField-Bezeichnung");
			}
		}
	}

	/**
	 * Nur für den Fall verwenden, das sämtliche Optionen gespeichert werden, um die Latenz zwischen Index und Detail zu vermindern
	 * 
	 * @param name
	 *            Name des Lookupfields, für welches wir die die Optionen Anfragen
	 * @param Table
	 *            Der Eintrag, welchen wir in die Datenbank schreiben möchten
	 * @return void
	 */
	public void addResultsForLookupField(String name, Table table) {
		if (conn != null) {
			if (table != null || name != null) {
				try {
					Row row = table.getRows().get(0);
					int keyLong = row.getValue(table.getColumnIndex(Constants.TABLE_KEYLONG)).getIntegerValue();
					Map<String, Object> found = getResultsForKeyLong(name, keyLong);
					if (found == null) {
						insertEntryOfLookup = conn.prepareStatement("INSERT INTO AllLookupvalues(Lookup, KeyLong, KeyText, Description) VALUES (?, ?, ?, ?)");
						int i = 0;
						while (i < table.getRows().size()) {
							Row r = table.getRows().get(i);
							insertEntryOfLookup.setString(1, name);
							insertEntryOfLookup.setInt(2, r.getValue(table.getColumnIndex(Constants.TABLE_KEYLONG)).getIntegerValue());
							insertEntryOfLookup.setString(3, r.getValue(table.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue());
							if (r.getValue(table.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
								insertEntryOfLookup.setString(4, r.getValue(table.getColumnIndex(Constants.TABLE_DESCRIPTION)).getStringValue());
							} else {
								insertEntryOfLookup.setString(4, "");
							}
							insertEntryOfLookup.execute();

							i++;
						}
						conn.commit();
					}
				} catch (SQLException e) {
					System.out.println("Eintragen der Werte der Datenbank fehlgeschlagen.");
					e.printStackTrace();
				}
			} else {
				System.out.println("Kein Gültiges TableObject oder fehlende LookupField-Bezeichnung");
			}
		}
	}

	private void disconnect() {
		try {
//			DriverManager.getConnection(protocol + database + ";shutdown=true");
			conn.close();
		} catch (SQLException e) {
			System.out.println("Schließen der Datenbankverbindung schlug fehl.");
			e.printStackTrace();
		}
	}

	// "CREATE TABLE IF NOT EXISTS LookupResolveValues ( Lookup VARCHAR NOT NULL, KeyLong int NOT NULL, KeyText VARCHAR NOT NULL, Description VARCHAR, PRIMARY
	// KEY (Lookup, KeyLong)");
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
	public void updateResolveValue(String lookupName, Value keyLong, Value keyText, Value description) {
		int rowCount = 0;

		if (conn == null)
			return; // da können wir nichts tun

		try {
			if (updateLookupResolveValue == null) {
				updateLookupResolveValue = conn
						.prepareStatement("UPDATE LookupResolveValues SET KeyText = ?, Description = ? WHERE Lookup = ? AND KeyLong = ?");
			}
			if (insertLookupResolveValue == null) {
				insertLookupResolveValue = conn.prepareStatement("INSERT INTO LookupResolveValues (Lookup, KeyLong, KeyText, Description) VALUES (?, ?, ?, ?)");
			}

			updateLookupResolveValue.setString(1, keyText.getStringValue());
			if (description == null)
				updateLookupResolveValue.setNull(2, Types.VARCHAR);
			else
				updateLookupResolveValue.setString(2, description.getStringValue());
			updateLookupResolveValue.setString(3, lookupName);
			updateLookupResolveValue.setInt(4, keyLong.getIntegerValue());
			rowCount = updateLookupResolveValue.executeUpdate();
			if (rowCount > 0)
				return;

			insertLookupResolveValue.setString(1, lookupName);
			insertLookupResolveValue.setInt(2, keyLong.getIntegerValue());
			insertLookupResolveValue.setString(3, keyText.getStringValue());
			if (description == null)
				insertLookupResolveValue.setNull(4, Types.VARCHAR);
			else
				insertLookupResolveValue.setString(4, description.getStringValue());
			insertLookupResolveValue.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Diese Methode sucht in der lokalen Datenbank einen Wert für den KeyLong und den LookupName
	 * 
	 * @param lookupName
	 *            Name der Tabelle oder der gespeicherten Prozedur (ohne 'list' / 'Resolve'), über die die Werte ermittelt werden können.
	 * @param keyLong
	 *            KeyLong des Wertes. Dieser wird auch in der Datenbank als Referenz gespeichert.
	 * @return null, wenn der Wert nicht lokal gespeichert ist
	 */
	public LookupEntry resolveValue(String lookupName, Value keyLong) {
		if (conn == null)
			return null;
		if (keyLong == null)
			return new LookupEntry(0, "", null);

		try {
			if (selectLookupResolveValue == null)
				selectLookupResolveValue = conn.prepareStatement("SELECT KeyText, Description FROM LookupResolveValues WHERE Lookup = ? AND KeyLong = ?");

			selectLookupResolveValue.setString(1, lookupName);
			selectLookupResolveValue.setInt(2, keyLong.getIntegerValue());

			ResultSet resultSet = selectLookupResolveValue.executeQuery();
			if (resultSet.first()) {
				return new LookupEntry(keyLong.getIntegerValue(), resultSet.getString(1), (String) resultSet.getObject(2));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

}
