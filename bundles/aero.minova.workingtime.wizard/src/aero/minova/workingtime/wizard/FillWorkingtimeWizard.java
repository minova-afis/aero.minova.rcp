package aero.minova.workingtime.wizard;

import aero.minova.rcp.model.Table;

/**
 * Mit diesem Assistent (Wizard) kann man Arbeitszeiten über mehrere Tage auffüllen
 */
public class FillWorkingtimeWizard extends MinovaWizard {

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
		dataService.getDetailDataAsync(dataTable.getName(), dataTable);
	}
}