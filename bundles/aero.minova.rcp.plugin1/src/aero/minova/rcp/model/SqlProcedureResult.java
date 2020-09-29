package aero.minova.rcp.model;

public class SqlProcedureResult {

	private Table resultSet;
	private Table outputParameters;
	private Integer returnCode;

	public Table getResultSet() {
		return resultSet;
	}
	public void setResultSet(Table resultSet) {
		this.resultSet = resultSet;
	}
	public Table getOutputParameters() {
		return outputParameters;
	}
	public void setOutputParameters(Table outputParameters) {
		this.outputParameters = outputParameters;
	}
	public Integer getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(Integer returnCode) {
		this.returnCode = returnCode;
	}

}
