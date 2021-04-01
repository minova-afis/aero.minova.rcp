package aero.minova.rcp.rcp.handlers;

import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;

import aero.minova.rcp.rcp.parts.WFCIndexPart;

public class ExportIndexHandler {

	public enum ExportTo {
		CLIPBOARD, FILE, EXCEL
	}

	public static final String COMMAND_ACTION = "aero.minova.rcp.rcp.commandparameter.exportto";

	@Execute
	public void execute(MPart mpart, @Named(COMMAND_ACTION) final String action) {
		System.out.println("Export the Index " + action);
		final ExportTo target = ExportTo.valueOf(action);

		Object wfcPart = mpart.getObject();
		if (wfcPart instanceof WFCIndexPart) {

			NatTable natTable = ((WFCIndexPart) wfcPart).getNattable();
			natTable.doCommand(new CopyDataToClipboardCommand("\t", //$NON-NLS-1$
					System.getProperty("line.separator"), //$NON-NLS-1$
					natTable.getConfigRegistry()));
		}

	}
}
