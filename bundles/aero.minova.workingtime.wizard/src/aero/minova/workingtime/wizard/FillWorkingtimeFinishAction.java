package aero.minova.workingtime.wizard;

import aero.minova.rcp.rcp.widgets.IMinovaWizardFinishAction;
import aero.minova.rcp.rcp.widgets.MinovaWizard;

/**
 * Arbeitszeit auffüllen mit den im Wizard angegebenen Daten
 */
public class FillWorkingtimeFinishAction implements IMinovaWizardFinishAction {
	private FillWorkingtimeWizard wizard;

	@Override
	public boolean execute() {
		if (wizard.pageIsReady()) {
			wizard.sendFillRequest();
		}
		return false;
	}

	@Override
	public void setWizard(MinovaWizard minovaWizard) {
		this.wizard = (FillWorkingtimeWizard) minovaWizard;
	}
}