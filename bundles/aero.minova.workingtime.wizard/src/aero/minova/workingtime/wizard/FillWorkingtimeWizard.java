package aero.minova.workingtime.wizard;

import aero.minova.rcp.model.Table;

/**
 * Mit diesem Assistent (Wizard) kann man Arbeitszeiten über mehrere Tage auffüllen
 *
 * @author wild
 * @since 11.0.0
 */
public class FillWorkingtimeWizard extends MinovaWizard {

	public FillWorkingtimeWizard() {
		super("@Workingtime.FillWizard.Title");
		this.setFinishAction(new FinishAction());
	}

	@Override
	public void addPages() {
		// wird von WizardDialog automatisch aufgerufen
		super.setWindowTitle(translationService.translate("@Workingtime.FillWizard.Title", null));
		PeriodPage periodPage = new PeriodPage("Name");
		periodPage.setMDetail(mdetail);
		periodPage.setMPerspective(mPerspective);
		periodPage.setTranslationService(translationService);
		addPage(periodPage);

		// #23481
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
}