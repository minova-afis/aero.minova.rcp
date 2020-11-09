package aero.minova.rcp.dataservice.internal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.ResourceBundle.Control;

import aero.minova.rcp.dataservice.ILocalDatabaseService;
import aero.minova.rcp.model.Table;

public class LocalDatabaseService implements ILocalDatabaseService {

	private Connection conn;

	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";

	private String protocol = "jdbc:derby:";

	private String database = "lookupDB";

	@Override
	public Table getResultsForLookupField(String name) {
		connect();

		disconnect();
		return null;
	}

	@Override
	public void replaceResultsForLookupField(String name, Map<String, Control> map) {
		connect();

		disconnect();

	}

	private void connect() {
		try {
			conn = DriverManager.getConnection(protocol + "derbyDB;create=true");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void disconnect() {
		try {
			DriverManager.getConnection(protocol + database + ";shutdown=true");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
