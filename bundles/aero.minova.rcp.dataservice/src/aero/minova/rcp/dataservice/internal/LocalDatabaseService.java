package aero.minova.rcp.dataservice.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import aero.minova.rcp.dataservice.ILocalDatabaseService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;

@Component
public class LocalDatabaseService implements ILocalDatabaseService {

	private String protocol = "jdbc:h2:file:";

	private String database = "derbyDB";

	private PreparedStatement deleteAllEntriesOfLookup;

	private PreparedStatement insertEntryOfLookup;

	private PreparedStatement createTableIfNotExisting;

	private Properties properties = new Properties();

	private Statement s;

	private ResultSet rs = null;

	private Connection conn;

	// Öffnen der Datenbankverbindung beim Öffnen der Klasse
//	@Activate
//	@SuppressWarnings("unchecked")
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
					"CREATE TABLE IF NOT EXISTS Lookupvalues ( Lookupvalue_id IDENTITY NOT NULL PRIMARY KEY, Lookup VARCHAR NOT NULL, KeyLong int NOT NULL, KeyText VARCHAR NOT NULL, DescriptionText VARCHAR)");
			createTableIfNotExisting.execute();
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;

	}

	@Deactivate
	protected void deactivateComponent() {
		disconnect();
	}

	@Override
	public Table getResultsForLookupField(String name) {

		activateComponent();
		if (conn != null) {
			Table t = new Table();
			Column collumn = new Column("KeyLong", DataType.INTEGER);
			t.addColumn(collumn);
			collumn = new Column("KeyText", DataType.STRING);
			t.addColumn(collumn);
			collumn = new Column("Description", DataType.STRING);
			t.addColumn(collumn);
			try {
				s = conn.createStatement();
				rs = s.executeQuery("SELECT * FROM Lookupvalues WHERE Lookup ='" + name + "'");
				while (rs.next()) {
					Row r = new Row();
					r.addValue(new Value(rs.getInt(3), DataType.INTEGER));
					r.addValue(new Value(rs.getString(4), DataType.STRING));
					r.addValue(new Value(rs.getString(5), DataType.STRING));
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
			return null;
		}
	}

	@Override
	public void replaceResultsForLookupField(String name, Table table) {
		activateComponent();
		// Connection conn = connect();
		if (conn != null) {
			try {
				insertEntryOfLookup = conn.prepareStatement(
						"INSERT INTO Lookupvalues(Lookup, KeyLong, KeyText, DescriptionText) VALUES (?, ?, ?, ?)");
				deleteAllEntriesOfLookup = conn
						.prepareStatement("DELETE FROM Lookupvalues WHERE Lookup='" + name + "'");
				deleteAllEntriesOfLookup.execute();

				int i = 0;
				while (i < table.getRows().size()) {
					Row r = table.getRows().get(i);
					insertEntryOfLookup.setString(1, name);
					insertEntryOfLookup.setInt(2, r.getValue(table.getColumnIndex("KeyLong")).getIntegerValue());
					insertEntryOfLookup.setString(3, r.getValue(table.getColumnIndex("KeyText")).getStringValue());
					if (r.getValue(table.getColumnIndex("Description")) != null) {
						insertEntryOfLookup.setString(4,
								r.getValue(table.getColumnIndex("Description")).getStringValue());
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

}
