package aero.minova.rcp.rcp.widgets;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.helper.IMinovaWizard;

/**
 * Basisklasse für alle Minova-Assistenten (Wizards)
 *
 * @author erlanger
 * @since 12.0.0
 */
public class MinovaWizard extends Wizard implements IMinovaWizard {
	private IMinovaWizardFinishAction finishAction;

	@Inject
	private IEclipseContext context;

	protected MDetail originalMDetail;

	public MinovaWizard(String wizardName) {
		this.setWindowTitle(wizardName);
	}

	@Override
	/**
	 * Am Ende von Implementierungen super.addPages() aufrufen, damit Pages im Kontext sind!
	 */
	public void addPages() {

		// Alle Pages in den Kontext injecten -> Am Ende von Implementierungen immer super.addPages() aufrufen
		for (IWizardPage page : getPages()) {
			ContextInjectionFactory.inject(page, context);
		}
	}

	/**
	 * @return the finishAction
	 */
	public IMinovaWizardFinishAction getFinishAction() {
		return finishAction;
	}

	@Override
	public boolean performCancel() {
		// wird aufgerufen, wenn man den "Abbrechen"-Button drückt
		return true;
	}

	@Override
	public boolean performFinish() {
		// wird aufgerufen, wenn man den "Finish"-Button drückt

		if (finishAction != null) {
			finishAction.setWizard(this);
			return finishAction.execute();
		} else {
			return true;
		}
	}

	/**
	 * @param finishAction
	 *            the finishAction to set
	 */
	public void setFinishAction(IMinovaWizardFinishAction finishAction) {
		this.finishAction = finishAction;
	}

	@Override
	public void setOriginalMDetail(MDetail originalMDetail) {
		this.originalMDetail = originalMDetail;
	}
}