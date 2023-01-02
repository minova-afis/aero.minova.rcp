package aero.minova.rcp.rcp.addons;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.constants.Constants;

public class WindowCloseListenerAddon {

	// See https://learn.vogella.com/exercises/rich-client-platform/custom-close-handler-for-exit-hTbZa
	// @PostConstruct does not work as the workbench gets
	// instantiated after the processing of the add-ons
	// hence this approach uses method injection

	@SuppressWarnings("restriction")
	@Inject
	@Optional
	private void adjustWindowCloseHandler(@UIEventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) MApplication application, IWorkbench workbench,
			ECommandService commandService, EHandlerService handlerService, TranslationService translationService, @Preference IEclipsePreferences prefs) {
		MWindow mainWindow = application.getChildren().get(0);
		IEclipseContext appContext = application.getContext();

		// Handler, der Dialog anzeigt wenn versucht wird, die Anwendung mit ungespeicherten Änderungen zu schließen. Außerdem wird
		// "RESTORING_UI_MESSAGE_SHOWN_THIS_SESSION" wieder auf false gesetzt, damit die Nachricht beim nächsten Starten wieder angezeigt wird
		IWindowCloseHandler handler = mWindow -> {
			@SuppressWarnings("unchecked")
			List<MPerspective> pList = (List<MPerspective>) appContext.get(Constants.DIRTY_PERSPECTIVES);
			if (pList != null && !pList.isEmpty()) {
				StringBuilder listString = new StringBuilder();
				for (MPerspective mPerspective : pList) {
					listString.append(" - " + translationService.translate(mPerspective.getLabel(), null) + "\n");
				}
				MessageDialog dialog = new MessageDialog(Display.getDefault().getActiveShell(), translationService.translate("@msg.ChangesDialog", null), null,
						translationService.translate("@msg.Close.DirtyMessage", null) + listString, MessageDialog.CONFIRM,
						new String[] { translationService.translate("@Action.Discard", null), translationService.translate("@Abort", null) }, 0);

				return dialog.open() == 0;
			}

			return true;
		};
		mainWindow.getContext().set(IWindowCloseHandler.class, handler);
	}
}