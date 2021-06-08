package aero.minova.workingtime.wizard;

/**
 * Arbeitszeit auffüllen mit den im Wizard angegebenen Daten
 */
public class FinishAction implements IWizardFinishAction {
	private MinovaWizard wizard;

	@Override
	public boolean execute() {
		boolean success = false;
		if (wizard instanceof FillWorkingtimeWizard) {
			((FillWorkingtimeWizard) wizard).sendFillRequest();
		}
		return success;
	}

	@Override
	public void setWizard(MinovaWizard minovaWizard) {
		this.wizard = minovaWizard;
	}
}