package aero.minova.rcp.model.util;

import aero.minova.rcp.model.Table;

public class ErrorObject {

	public ErrorObject(Table errorTable, String user, String serverRequest) {
		super();
		this.setErrorTable(errorTable);
		this.setUser(user);
		this.setServerRequest(serverRequest);
	}

	public ErrorObject(Table errorTable, String user) {
		super();
		this.setErrorTable(errorTable);
		this.setUser(user);
		this.setServerRequest(null);
	}

	private Table errorTable;
	private String user;
	private String serverRequest;

	public Table getErrorTable() {
		return errorTable;
	}

	public void setErrorTable(Table errorTable) {
		this.errorTable = errorTable;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getServerRequest() {
		return serverRequest;
	}

	public void setServerRequest(String serverRequest) {
		this.serverRequest = serverRequest;
	}


}
