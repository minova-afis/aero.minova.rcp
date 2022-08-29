package aero.minova.rcp.exceptions;

public class ToolsException extends RuntimeException {

	private static final long serialVersionUID = 202208120927L;

	public ToolsException(String errorMessage) {
		super(errorMessage);
	}

	public ToolsException(String errorMessage, Throwable e) {
		super(errorMessage, e);
	}

	public ToolsException(Throwable e) {
		super(e);
	}
}
