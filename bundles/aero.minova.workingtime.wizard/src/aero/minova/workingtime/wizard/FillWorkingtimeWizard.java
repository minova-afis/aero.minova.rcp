package aero.minova.workingtime.wizard;

import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dialogs.NotificationPopUp;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.preferences.ApplicationPreferences;

/**
 * Mit diesem Assistent (Wizard) kann man Arbeitszeiten über mehrere Tage auffüllen
 */
public class FillWorkingtimeWizard extends MinovaWizard {

	@Inject
	protected TranslationService translationService;
	@Inject
	protected MPerspective mPerspective;
	@Inject
	protected MPart mPart;
	@Inject
	protected IDataService dataService;
	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.AUTO_LOAD_INDEX)
	boolean autoLoadIndex;
	@Inject
	private ECommandService commandService;
	@Inject
	private EHandlerService handlerService;

	public FillWorkingtimeWizard() {
		super("@Workingtime.FillWizard.Name");
		this.setFinishAction(new FinishAction());
	}

	@Override
	public void addPages() {
		// wird von WizardDialog automatisch aufgerufen
		super.setWindowTitle(translationService.translate("@Workingtime.FillWizard.Name", null));
		PeriodPage periodPage = new PeriodPage("PeriodPage", translationService.translate("@Workingtime.FillWizard.Title", null),
				translationService.translate("@Workingtime.FillWizard.Description", null));
		periodPage.setMPerspective(mPerspective);
		periodPage.setTranslationService(translationService);
		periodPage.setmPart(mPart);
		periodPage.setOriginalMDetail(originalMDetail);
		addPage(periodPage);
		super.addPages();
	}

	public Table getDataTable() {
		PeriodPage page = (PeriodPage) this.getPage("PeriodPage");
		if (page != null) {
			return page.getDataTable();
		} else {
			return null;
		}
	}

	public void sendFillRequest() {
		Table dataTable = getDataTable();
		CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(dataTable.getName(), dataTable);

		// Wenn Anfrage erfolgreich war Dialogfenster schließen und Notification geben
		tableFuture.thenAccept(ta -> {
			if (ta != null) {
				Display.getDefault().syncExec(() -> {
					((WizardDialog) getContainer()).close();

					NotificationPopUp notificationPopUp = new NotificationPopUp(Display.getCurrent(), translationService.translate("@msg.DataUpdated", null),
							Display.getCurrent().getActiveShell());
					notificationPopUp.open();

					if (autoLoadIndex) {
						ParameterizedCommand cmd = commandService.createCommand("aero.minova.rcp.rcp.command.loadindex", null);
						handlerService.executeHandler(cmd);
					}
				});
			}
		});
	}
}