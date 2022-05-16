package aero.minova.rcp.rcp.widgets;

import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

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

	public MinovaWizardPage(String pageName, String pageTitle, String pageDescription) {
		super(pageName);
		setTitle(pageTitle);
		setDescription(pageDescription);
	}

	@Override
	/**
	 * Von Unterklassen überschrieben. Wichtig: setControl() und init() Aufruf am Ende!
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		init();
	}

	/**
	 * kann nachdem der Container (WizardDialog) gesetzt wurde noch etwas initialisieren (z.B. Listener). <br>
	 * Ohne wird die onSelect() Methode nicht aufgerufen!
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
	protected void onSelect() {
		// Von Unterklassen implementiert wenn bei Auswahl etwas geschehen soll
	}
}