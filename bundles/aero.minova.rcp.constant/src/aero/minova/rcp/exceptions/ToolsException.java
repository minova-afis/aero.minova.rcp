package aero.minova.rcp.exceptions;

public class ToolsException extends RuntimeException {
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
