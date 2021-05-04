package aero.minova.rcp.rcp.util;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.VisualRefreshCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.InitializeAutoResizeColumnsCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.InitializeAutoResizeRowsCommand;
import org.eclipse.nebula.widgets.nattable.util.GCFactory;

public class NatTableUtil {

	public static void resize(NatTable natTable) {
		for (int i = 0; i < natTable.getColumnCount(); i++) {
			InitializeAutoResizeColumnsCommand columnCommand = new InitializeAutoResizeColumnsCommand(natTable, i, natTable.getConfigRegistry(),
					new GCFactory(natTable));
			natTable.doCommand(columnCommand);
		}

		for (int i = 0; i < natTable.getRowCount(); i++) {
			InitializeAutoResizeRowsCommand rowCommand = new InitializeAutoResizeRowsCommand(natTable, i, natTable.getConfigRegistry(),
					new GCFactory(natTable));
			natTable.doCommand(rowCommand);
		}
	}

	public static void refresh(NatTable natTable) {
		natTable.doCommand(new VisualRefreshCommand());
	}

}
