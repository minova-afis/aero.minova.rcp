package aero.minova.rcp.model;

public class TransactionResultEntry {

	private String id;
	private SqlProcedureResult resultSet;

	public TransactionResultEntry(String id, SqlProcedureResult result) {
		this.setId(id);
		this.setResult(result);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public SqlProcedureResult getSQLProcedureResult() {
		return resultSet;
	}

	public void setResult(SqlProcedureResult result) {
		this.resultSet = result;
	}
}
