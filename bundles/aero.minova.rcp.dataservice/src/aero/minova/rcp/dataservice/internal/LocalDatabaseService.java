package aero.minova.rcp.dataservice.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import aero.minova.rcp.dataservice.ILocalDatabaseService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.rcp.util.Constants;

public class LocalDatabaseService implements ILocalDatabaseService {

	private Connection conn;

	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private String protocol = "jdbc:derby:";

	private String database = "lookupDB";

	private PreparedStatement deleteAllEntriesOfLookup;

	private PreparedStatement insertEntryOfLookup;

	private PreparedStatement createTableIfNotExisting;

	private Properties properties = new Properties();

	private Statement s;

	private ResultSet rs = null;

	@Override
	public Table getResultsForLookupField(String name) {
		Table t = new Table();
		Column collumn = new Column(Constants.TABLE_KEYLONG, DataType.INTEGER);
		t.addColumn(collumn);
		collumn = new Column(Constants.TABLE_KEYTEXT, DataType.STRING);
		t.addColumn(collumn);
		collumn = new Column(Constants.TABLE_DESCRIPTION, DataType.STRING);
		t.addColumn(collumn);
		connect();
		try {
			rs = s.executeQuery("SELECT * FROM lookupvalues WHERE Lookup =" + name);
			if (!rs.next()) {
				return null;
			} else {
				while (rs.next()) {
					Row r = new Row();
					r.addValue(new Value(rs.getInt(3), DataType.INTEGER));
					r.addValue(new Value(rs.getInt(4), DataType.STRING));
					r.addValue(new Value(rs.getInt(5), DataType.STRING));
					t.addRow(r);
				}
			}
		} catch (SQLException e) {
			System.out.println("Auslesen der Datenbank fehlgeschlagen.");
			e.printStackTrace();
			return null;
		}
		disconnect();
		return t;
	}

	@Override
	public void replaceResultsForLookupField(String name, Table table) {
		connect();

		try {
			insertEntryOfLookup = conn.prepareStatement(
					"INSERT INTO lookupvalues(Lookup varchar(40), KeyLong int, KeyText varchar(40), Description varchar(40))");
			deleteAllEntriesOfLookup = conn.prepareStatement("DELETE FROM lookupvalues WHERE Lookup=" + name);
			int i = 0;

			while (i < table.getRows().size()) {
				Row r = table.getRows().get(i);
				insertEntryOfLookup.setString(2, name);
				insertEntryOfLookup.setInt(3,
						r.getValue(table.getColumnIndex(Constants.TABLE_KEYLONG)).getIntegerValue());
				insertEntryOfLookup.setString(4,
						r.getValue(table.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue());
				insertEntryOfLookup.setString(5,
						r.getValue(table.getColumnIndex(Constants.TABLE_DESCRIPTION)).getStringValue());

				i++;
			}
			deleteAllEntriesOfLookup.execute();
			insertEntryOfLookup.execute();
		} catch (SQLException e) {
			System.out.println("Austausch der Werte der Datenbank fehlgeschlagen.");
			e.printStackTrace();
		}
		disconnect();

	}

	private void connect() {
		try {
			properties.put("user", "user1");
			properties.put("password", "password");
			conn = DriverManager.getConnection(protocol + database + ";create=true", properties);
			conn.setAutoCommit(false);
			createTableIfNotExisting = conn.prepareStatement("CREATE TABLE IF NOT EXISTS lookupvalues (" + //
					"	Lookupvalue_id int NOT NULL AUTO_INCREMENT," + //
					"	Lookup varchar(40)," + //
					"	KeyLong int," + //
					"	KeyText varchar(40)" + //
					"	Description varchar(40)," + //
					"	PRIMARY KEY (Lookupvalues_id))");
		} catch (SQLException e) {
			System.out.println("Verbindung zur Datenbank fehlgeschlagen.");
			e.printStackTrace();
		}
	}

	private void disconnect() {
		try {
			DriverManager.getConnection(protocol + database + ";shutdown=true");
		} catch (SQLException e) {
			System.out.println("Schließen der Datenbankverbindung schlug fehl.");
			e.printStackTrace();
		}
	}

}
