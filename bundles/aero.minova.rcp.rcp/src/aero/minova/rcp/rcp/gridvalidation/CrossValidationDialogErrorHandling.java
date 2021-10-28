package aero.minova.rcp.rcp.gridvalidation;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.nebula.widgets.nattable.edit.config.DialogErrorHandling;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;

import aero.minova.rcp.constants.Constants;

public class CrossValidationDialogErrorHandling extends DialogErrorHandling {

	IEventBroker broker;

	public CrossValidationDialogErrorHandling(boolean allowCommit, IEventBroker broker) {
		super(allowCommit);
		this.broker = broker;
	}

	@Override
	public void showWarningDialog(String dialogMessage, String dialogTitle) {
		if (!isWarningDialogActive() && dialogMessage != null) {
			// Übersetzung wird im WFCDetailPart erstellt
			broker.send(Constants.BROKER_SHOWNOTIFICATION, dialogMessage);
			this.editor.commit(MoveDirectionEnum.NONE, true, true);
		}
	}
}