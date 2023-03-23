package aero.minova.rcp.rcp.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.model.application.ui.menu.impl.HandledToolItemImpl;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.DirtyFlagUtil;

public class DeleteDetailHandler {

	@Inject
	IEventBroker broker;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.SHOW_DELETE_WARNING)
	boolean showDiscardDialogIndex;

	@Inject
	TranslationService translationService;

	@Inject
	@Optional
	DirtyFlagUtil dirtyFlagUtil;

	boolean firstCall = true;
	MPart mPart;
	WFCDetailPart detail;

	@CanExecute
	public boolean canExecute(MPart mPart) {
		if (firstCall) {
			init(mPart);
		}

		return detail.getDetail().getPrimaryFields().get(0).getValue() != null;
	}

	private void init(MPart mPart) {
		this.mPart = mPart;
		this.detail = (WFCDetailPart) mPart.getObject();

		// Bei Booking muss das Icon und Label angepasst werden
		if (detail.getDetail().isBooking()) {
			for (MToolBarElement item : mPart.getToolbar().getChildren()) {
				if (Constants.DELETE_DETAIL_BUTTON.equals(item.getElementId())) {
					HandledToolItemImpl toolItem = (HandledToolItemImpl) item;
					toolItem.setIconURI(toolItem.getIconURI().replace("DeleteRecord", "CancelBooking"));
					toolItem.setLabel("@Action.CancelBooking");
					toolItem.setTooltip("@Action.CancelBooking");
					break;
				}
			}
		}

		firstCall = false;
	}

	@Execute
	public void execute(@Optional MPerspective perspective) {
		if (perspective == null) {
			return;
		}

		// Bei ungespeicherten Änderungen darf nicht gelöscht werden
		if (mPart.getObject() instanceof WFCDetailPart && dirtyFlagUtil.isDirty()) {
			MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(),
					translationService.translate("@msg.DeleteUnsavedChangesTitle", null), null,
					translationService.translate("@msg.DeleteUnsavedChangesMessage", null), MessageDialog.CONFIRM, new String[] { "OK" }, 0);
			dialog.open();
			return;
		}

		MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), translationService.translate("@msg.DeleteWarningTitle", null), null,
				translationService.translate("@msg.DeleteWarningMessage", null), MessageDialog.WARNING,
				new String[] { translationService.translate("@Action.Delete", null), translationService.translate("@Abort", null) }, 0);

		if (!showDiscardDialogIndex || dialog.open() == 0) {
			broker.post(Constants.BROKER_DELETEENTRY, perspective);
		}
	}
}
