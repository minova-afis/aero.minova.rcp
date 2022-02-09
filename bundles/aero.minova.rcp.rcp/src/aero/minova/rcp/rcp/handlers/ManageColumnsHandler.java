package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.parts.WFCIndexPart;
import aero.minova.rcp.rcp.util.ManageColumnsDialog;

public class ManageColumnsHandler {

	@Inject
	TranslationService translationsService;

	@Inject
	IEventBroker broker;

	@Execute
	public void execute(Shell shell, MPart mpart, MPerspective mPerspective) {

		WFCIndexPart indexPart = (WFCIndexPart) mpart.getObject();

		ManageColumnsDialog dialog = new ManageColumnsDialog(shell, translationsService, indexPart.getData().getColumns());
		dialog.open();

		// Auch Such-Spalten updaten
		Table searchTable = (Table) mPerspective.getContext().get(Constants.SEARCH_TABLE);
		for (Column c : searchTable.getColumns()) {
			Column indexColumn = indexPart.getData().getColumn(c.getName());
			if (indexColumn != null) {
				c.setVisible(indexColumn.isVisible());
			}
		}

		// UI updaten (Suche und Index)
		broker.post(Constants.BROKER_UPDATECOLUMNS, "");

		// Index neu laden
		broker.post(Constants.BROKER_RELOADINDEX, "");
	}

}
