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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.helper.IMinovaWizard;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.widgets.MinovaWizard;
import aero.minova.rcp.widgets.MinovaNotifier;

/**
 * Mit diesem Assistent (Wizard) kann man Arbeitszeiten über mehrere Tage auffüllen
 */
@Component
public class FillWorkingtimeWizard extends MinovaWizard implements IMinovaWizard {

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
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.ENTER_SELECTS_FIRST_REQUIRED)
	boolean selectFirstRequired;
	@Inject
	private ECommandService commandService;
	@Inject
	private EHandlerService handlerService;
	private PeriodPage periodPage;

	public FillWorkingtimeWizard() {
		super("@Workingtime.FillWizard.Name");
		this.setFinishAction(new FillWorkingtimeFinishAction());
	}

	@Override
	public void addPages() {

		// Die Pages sollen nur hinzugefügt werden, wenn noch keine Pages erstellt wurden
		if (this.getPages().length != 0) {
			return;
		}

		// wird von WizardDialog automatisch aufgerufen
		super.setWindowTitle(translationService.translate("@Workingtime.FillWizard.Name", null));
		periodPage = new PeriodPage("PeriodPage", translationService.translate("@Workingtime.FillWizard.Title", null),
				translationService.translate("@Workingtime.FillWizard.Description", null), selectFirstRequired);
		periodPage.setMPerspective(mPerspective);
		periodPage.setTranslationService(translationService);
		periodPage.setmPart(mPart);
		periodPage.setOriginalMDetail(originalMDetail);
		addPage(periodPage);

		// Verhindern, dass sich Fenster bei Escape schließt, wenn gerade ein Popup offen ist
		((WizardDialog) getContainer()).getShell().addListener(SWT.Traverse, e -> {
			if (e.detail == SWT.TRAVERSE_ESCAPE && periodPage.popupIsOpen()) {
				e.doit = false;
			}
		});

		super.addPages();
	}

	public Table getDataTable() {
		PeriodPage page = (PeriodPage) this.getPage("PeriodPage");
		return page != null ? page.getDataTable() : null;
	}

	public void sendFillRequest() {
		Table dataTable = getDataTable();
		CompletableFuture<SqlProcedureResult> tableFuture = dataService.getDetailDataAsync(dataTable.getName(), dataTable);

		// Wenn Anfrage erfolgreich war Dialogfenster schließen, Notification geben und Index gegebenenfalls neu laden
		tableFuture.thenAccept(ta -> {
			if (ta != null) {
				Display.getDefault().syncExec(() -> {
					((WizardDialog) getContainer()).close();

					MinovaNotifier.show(Display.getCurrent().getActiveShell(),
							translationService.translate("@msg.FillWorkingtimeSaved", null),
							translationService.translate("@Notification", null));

					if (autoLoadIndex) {
						ParameterizedCommand cmd = commandService.createCommand("aero.minova.rcp.rcp.command.loadindex", null);
						handlerService.executeHandler(cmd);
					}
				});
			}
		});
	}

	public boolean pageIsReady() {
		return periodPage.isPageComplete();
	}
}