package aero.minova.rcp.rcp.widgets;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
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
	private Object opener;

	@Inject
	private IEclipseContext context;

	protected MDetail originalMDetail;

	public MinovaWizard(String wizardName) {
		this.setWindowTitle(wizardName);
		this.init();
	}

	@Override
	public void addPages() {
		// wird von WizardDialog automatisch aufgerufen
		// Beispiele:
		// addPage(new PrintPage1());
		// addPage(new PrintPage2());

		// #23481: Deshalb müssen die abgeleiteten Klassen super.addPages() aufrufen
		for (IWizardPage page : getPages()) {
			ContextInjectionFactory.inject(page, context);
		}
	}

	@Override
	public void dispose() {
		// pages werden von der Superklasse bereits bereinigt
		super.dispose();
	}

	/**
	 * @return the finishAction
	 */
	public IMinovaWizardFinishAction getFinishAction() {
		return finishAction;
	}

	/**
	 * liefert das Objekt, das den Wizard geöffnet hat<br>
	 * das kann das öffnende Fenster, oder auch ein Part oder DataSourceBundle sein
	 *
	 * @return
	 */
	public Object getOpener() {
		return this.opener;
	}

	/**
	 * kann verwendet werden, um Settings vorzubelegen und andere Werte zu initialisieren
	 */
	public void init() {
		IDialogSettings dialogSettings = new DialogSettings(this.getClass().getSimpleName());
		this.setDialogSettings(dialogSettings);
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

	/**
	 * setzt das Objekt, das den Wizard geöffnet hat<br>
	 * das kann das öffnende Fenster, oder auch ein Part oder DataSourceBundle sein
	 *
	 * @param opener
	 */
	public void setOpener(Object opener) {
		this.opener = opener;
	}

	@Override
	public void setOriginalMDetail(MDetail originalMDetail) {
		this.originalMDetail = originalMDetail;
	}
}