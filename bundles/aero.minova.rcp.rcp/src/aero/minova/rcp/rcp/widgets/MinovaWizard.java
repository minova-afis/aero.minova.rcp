package aero.minova.rcp.rcp.widgets;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.helper.IMinovaWizard;
import aero.minova.rcp.preferences.ApplicationPreferences;

/**
 * Basisklasse für alle Minova-Assistenten (Wizards)
 *
 * @author erlanger
 * @since 12.0.0
 */
public class MinovaWizard extends Wizard implements IMinovaWizard {

	@Inject
	private IEclipseContext context;
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
	protected boolean autoReloadIndex;
	@Inject
	protected IEventBroker broker;

	protected MDetail originalMDetail;

	private IMinovaWizardFinishAction finishAction;

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

		// Verhindern, dass sich Fenster bei Escape schließt, wenn gerade ein Popup offen ist
		((WizardDialog) getContainer()).getShell().addListener(SWT.Traverse, e -> {

			boolean popupIsOpen = false;
			for (IWizardPage page : getPages()) {
				if (((MinovaWizardPage) page).popupIsOpen()) {
					popupIsOpen = true;
				}
			}

			if (e.detail == SWT.TRAVERSE_ESCAPE && popupIsOpen) {
				e.doit = false;
			}
		});
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

	@Override
	public MDetail getOriginalMDetail() {
		return originalMDetail;
	}
}