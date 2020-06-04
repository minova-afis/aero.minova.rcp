package aero.minova.rcp.workspace.dialogs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnChecker {

	private final static String url = "jdbc:postgresql://localhost/security-test";
	private final static String user = "bauer";
	private final static String password = "Svetlana21";

	private static final String QUERY = "select current_user username";
	private static final String SELECT_ALL_QUERY = "select * from input";

	
	public boolean checkConnection(String url, String username, String password) {
		if (url == null) url = this.url;
		
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		try (Connection connection = DriverManager.getConnection(url, username, password);
				// Step 2:Create a statement using connection object
				PreparedStatement preparedStatement = connection.prepareStatement(QUERY);) {
//			preparedStatement.setInt(1, 1);
			System.out.println(preparedStatement);
			// Step 3: Execute the query or update query
			ResultSet rs = preparedStatement.executeQuery();

			// Step 4: Process the ResultSet object.
			while (rs.next()) {
//				int id = rs.getInt("id");
				String name = rs.getString("username");
//				String email = rs.getString("email");
//				String country = rs.getString("country");
//				String password = rs.getString("password");
				System.out.println(name);
			}
			rs.close();
			connection.close();
			return true;
		} catch (SQLException e) {
			printSQLException(e);
		}
		return false;
	}
	
	public void getUserById() {
		// using try-with-resources to avoid closing resources (boiler plate
		// code)

		// Step 1: Establishing a Connection
		try (Connection connection = DriverManager.getConnection(url, user, password);
				// Step 2:Create a statement using connection object
				PreparedStatement preparedStatement = connection.prepareStatement(QUERY);) {
//			preparedStatement.setInt(1, 1);
			System.out.println(preparedStatement);
			// Step 3: Execute the query or update query
			ResultSet rs = preparedStatement.executeQuery();

			// Step 4: Process the ResultSet object.
			while (rs.next()) {
//				int id = rs.getInt("id");
				String name = rs.getString("username");
//				String email = rs.getString("email");
//				String country = rs.getString("country");
//				String password = rs.getString("password");
				System.out.println(name);
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
	}

	public void getAllUsers() {
		// using try-with-resources to avoid closing resources (boiler plate
		// code)

		// Step 1: Establishing a Connection
		try (Connection connection = DriverManager.getConnection(url, user, password);
				// Step 2:Create a statement using connection object
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);) {
			System.out.println(preparedStatement);
			// Step 3: Execute the query or update query
			ResultSet rs = preparedStatement.executeQuery();

			// Step 4: Process the ResultSet object.
			while (rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("description");
				String country = rs.getString("notice");
				System.out.println(id + "," + name + "," + email + "," + country);
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
	}

	public static void main(String[] args) {
		DatabaseConnChecker example = new DatabaseConnChecker();
		example.getUserById();
		example.getAllUsers();
	}

	public static void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}

}
