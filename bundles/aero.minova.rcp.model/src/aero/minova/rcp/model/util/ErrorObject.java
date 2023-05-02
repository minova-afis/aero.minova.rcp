package aero.minova.rcp.model.util;

import aero.minova.rcp.model.Table;

public class ErrorObject {

	public ErrorObject(Table errorTable, String user, Throwable t) {
		super();
		this.setErrorTable(errorTable);
		this.setUser(user);
		this.setT(t);
	}

	public ErrorObject(String message, String user, Throwable t) {
		super();
		this.setMessage(message);
		this.setUser(user);
		this.setT(t);
	}

	public ErrorObject(String message, Throwable t) {
		super();
		this.setMessage(message);
		this.setT(t);
	}

	public ErrorObject(Table errorTable, String user) {
		super();
		this.setErrorTable(errorTable);
		this.setUser(user);
		this.t = null;
	}

	public ErrorObject(Table errorTable, String user, String procedureOrView) {
		super();
		this.setErrorTable(errorTable);
		this.setUser(user);
		this.t = null;
		this.procedureOrView = procedureOrView;
	}

	private Table errorTable;
	private String user;
	private String message;
	private Throwable t;
	private String procedureOrView;

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

	public Throwable getT() {
		return t;
	}

	public void setT(Throwable t) {
		this.t = t;
	}

	public String getProcedureOrView() {
		if (procedureOrView != null) {
			return procedureOrView;
		} else if (errorTable != null && errorTable.getColumnCount() > 1) {
			return errorTable.getRows().get(0).getValue(1).getStringValue();
		}
		return null;
	}

	public String getMessage() {
		if (message != null) {
			return message;
		} else if (errorTable != null) {
			return errorTable.getRows().get(0).getValue(0).getStringValue();
		}
		return null;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
