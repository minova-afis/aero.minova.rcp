package aero.minova.workingtime.wizard;


/**
 * Interface für Klassen, die die Aktion definieren, die beim Drücken des "Finish"-Buttons ausgeführt wird.
 *
 * @author erlanger
 * @since 10.6.0
 */
public interface IWizardFinishAction {
	/**
	 * führt die Aktion beim Beenden des Assistenten aus<br>
	 * (Wenn auf "Finish" gedrückt wird)
	 *
	 * @return
	 */
	boolean execute();

	/**
	 * setzt den Wizard, damit dessen Werte in {@link #execute()} ausgelesen und verarbeitet werden können
	 *
	 * @param minovaWizard
	 */
	void setWizard(MinovaWizard minovaWizard);
}