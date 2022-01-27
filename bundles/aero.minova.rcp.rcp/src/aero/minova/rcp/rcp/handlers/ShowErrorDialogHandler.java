package aero.minova.rcp.rcp.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;

public class ShowErrorDialogHandler {
	@Execute
	public static void execute(final Shell shell, String title, String message, Throwable t) {
		MultiStatus status;
		status = createMultiStatus(t);
		// show error dialog
		ErrorDialog.openError(shell, title, message, status);
	}

	private static MultiStatus createMultiStatus(Throwable t) {
		MultiStatus ms;
		List<Status> childStatuses = new ArrayList<>();
		for (StackTraceElement stackTrace : t.getStackTrace()) {
			Status status = new Status(IStatus.ERROR, "aero.minova.rcp.rcp", stackTrace.toString());
			childStatuses.add(status);
		}
		ms = new MultiStatus("aero.minova.rcp.rcp", IStatus.ERROR, childStatuses.toArray(new Status[] {}), t.toString(), t);
		return ms;
	}
}
