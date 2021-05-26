package aero.minova.workingtime.wizard;

/**
 * Arbeitszeit auffüllen mit den im Wizard angegebenen Daten
 *
 * @author wild
 * @since 11.0.0
 */
public class FinishAction implements IWizardFinishAction {
	private MinovaWizard wizard;

	@Override
	public boolean execute() {
		boolean success = false;
		if (wizard != null && wizard instanceof FillWorkingtimeWizard) {
			System.out.println("spWorkingTimeFill aufrufen!, CAS");
		}
		return success;
	}

	@Override
	public void setWizard(MinovaWizard minovaWizard) {
		this.wizard = minovaWizard;
	}
}