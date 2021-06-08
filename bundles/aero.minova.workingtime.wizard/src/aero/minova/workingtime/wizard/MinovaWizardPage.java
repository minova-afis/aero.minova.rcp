package aero.minova.workingtime.wizard;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Basisklasse für alle Seiten / Masken (Pages), die in Assistenten (Wizards) eingehängt werden können.<br>
 * Es gibt spezialisierte Klassen:<br>
 * - {@link WizardConfirmPage}<br>
 * - {@link WizardFormPage}<br>
 * - {@link WizardIndexPage}
 *
 * @author wild
 * @since 10.5.0
 */
public class MinovaWizardPage extends WizardPage {
	private static class MinovaWizardPageChangedListener implements IPageChangedListener {
		@Override
		public void pageChanged(PageChangedEvent event) {
			Object selectedPage = event.getSelectedPage();
			if (selectedPage instanceof MinovaWizardPage) {
				((MinovaWizardPage) selectedPage).onSelect();
			}
		}
	}

	private static final MinovaWizardPageChangedListener mwpcl = new MinovaWizardPageChangedListener();

	@Inject
	protected IEclipseContext context;

	@Inject
	private TranslationService translationService;

	public MinovaWizardPage(String pageName, String pageTitle, String pageDescription) {
		super(pageName);
		setTitle(translationService.translate(pageTitle, null));
		setDescription(translationService.translate(pageDescription, null));
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		// fill the container

		setControl(container);

		// erst hier, weil sonst der Container (WizardDialog) noch nicht da ist
		init();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	/**
	 * kann nachdem der Container (WizardDialog) gesetzt wurde noch etwas initialisieren (z.B. Listener)
	 */
	protected void init() {
		IWizardContainer wizardContainer = this.getContainer();
		if (wizardContainer instanceof WizardDialog) {
			// und was anderes kommt eigentlich nicht vor...

			// füge den oben definierten Listener hinzu, der onSelect aufruft
			// intern wird bereits geprüft, ob der Listener doppelt ist
			((WizardDialog) wizardContainer).addPageChangedListener(mwpcl);

			// von abgeleiteten Klassen können auch mehr Listener hinzugefügt werden
		}
	}

	/**
	 * wird aufgerufen, wenn die Seite ausgewählt wird
	 */
	protected void onSelect() {}
}