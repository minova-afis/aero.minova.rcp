package aero.minova.rcp.workspace;

public class WorkspaceException extends Exception {

	private static final long serialVersionUID = 202006040756L;

	public WorkspaceException(String message) {
		super(message);
	}

	public WorkspaceException(String message, Exception e) {
		super(message, e);
	}
}
