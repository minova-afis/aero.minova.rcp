package aero.minova.rcp.workspace.handler;

import java.net.URL;

import org.eclipse.e4.core.services.log.Logger;
import aero.minova.rcp.workspace.WorkspaceException;

@SuppressWarnings("restriction")
public class SpringBootWorkspace extends WorkspaceHandler {

	public SpringBootWorkspace(URL connection, Logger logger) {
		super(logger);
	}

	@Override
	public boolean checkConnection(String username, String password) throws WorkspaceException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRemoteUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getConnectionString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void open() throws WorkspaceException {
		// TODO Auto-generated method stub

	}

}
