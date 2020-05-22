package aero.minova.rcp.rcp.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
public class OpenHandler {

	@Execute
	public void execute(Shell shell, MWindow window){
		FileDialog dialog = new FileDialog(shell);
		String filename = dialog.open();
		if (filename != null && !filename.isEmpty()) {
			window.getContext().set("JSONFilename", filename);
		}
	}
}
