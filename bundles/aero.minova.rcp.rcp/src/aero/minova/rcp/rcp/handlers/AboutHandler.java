package aero.minova.rcp.rcp.handlers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;

public class AboutHandler {
	@Execute
	public void execute(Shell shell) {
		Version version = FrameworkUtil.getBundle(getClass()).getVersion();
		long lastModified = FrameworkUtil.getBundle(getClass()).getLastModified();

		String runtimeVersion = System.getProperty("java.runtime.version");
		
		MessageDialog.openInformation(shell, "About", "Version: " + version.toString() + ", BuildDate: "
				+ LocalDateTime.ofEpochSecond(lastModified / 1000, 0, ZoneOffset.UTC) + " "+ "Java Runntime: "
				+ runtimeVersion );
	}
}
