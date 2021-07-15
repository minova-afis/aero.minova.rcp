package aero.minova.rcp.rcp.util;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.jface.widgets.TextFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;

/**
 * Dieser Dialog zeigt die bestehenden (Such-)Kriterien an und bietet die Möglichkeit, welche zu speichern
 */
public class SaveSearchCriteriaDialog extends Dialog {

	Preferences lastSearchCriteriaPrefs = InstanceScope.INSTANCE.getNode(Constants.LAST_LOADED_SEARCHCRITERIA);
	private String criteriaName = lastSearchCriteriaPrefs.get(Constants.LAST_SEARCHCRITERIA, "DEFAULT");

	private boolean saveWidths = true;

	private TranslationService translationService;
	private IEclipsePreferences prefs;

	// ungleich 0 oder 1 um zu verhindern, dass Dialog sich schliesst
	private static final int BUTTON_OK_ID = 4;
	private static final int BUTTON_CANCEL_ID = 5;
	private String tableName;

	private Button ok;
	private Text text;

	public SaveSearchCriteriaDialog(final Shell shell, TranslationService translationService, IEclipsePreferences prefs, String tableName) {
		super(shell);
		this.translationService = translationService;
		this.prefs = prefs;
		this.tableName = tableName;
	}

	@Override
	protected Control createDialogArea(Composite dialogParent) {
		Composite parent = (Composite) super.createDialogArea(dialogParent);

		Label infoLabel = LabelFactory.newLabel(SWT.BORDER).text("").layoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false)).create(parent);

		text = TextFactory.newText(SWT.BORDER).layoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false)).create(parent);

		Button btnSaveWidths = ButtonFactory.newButton(SWT.CHECK).text(translationService.translate("@SelectionCriteria.SaveColumnWidths", null))
				.onSelect(e -> SaveSearchCriteriaDialog.this.saveWidths = ((Button) e.widget).getSelection()).create(parent);
		btnSaveWidths.setSelection(true);

		// Auf Änderungen hören und den OK-Button entsprechend freigeben
		text.addModifyListener(e -> {
			final String c = prefs.get(tableName + "." + text.getText() + ".table", null);
			boolean enabled = false;
			if (text.getText().isBlank()) {
				enabled = false;
			} else if (c == null) {
				infoLabel.setForeground(Display.getCurrent().getSystemColor(SWT.DEFAULT));
				infoLabel.setText(translationService.translate("@SelectionCriteria.NameAvailable", null));
				enabled = true;
			} else {
				infoLabel.setText(translationService.translate("@SelectionCriteria.WillBeOverwritten", null));
				infoLabel.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				enabled = true;
			}
			if (ok != null) {
				ok.setEnabled(enabled);
			}
		});
		text.setText(this.criteriaName);
		text.selectAll();

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Button zum Canceln
		Button cancel = createButton(parent, BUTTON_CANCEL_ID, translationService.translate("@Abort", null), false);
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SaveSearchCriteriaDialog.this.criteriaName = null;
				close();
			}
		});

		// Button zum Bestätigen
		ok = createButton(parent, BUTTON_OK_ID, translationService.translate("@OK", null), true);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SaveSearchCriteriaDialog.this.criteriaName = text.getText();
				close();
			}
		});
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(translationService.translate("@SelectionCriteria.EnterName", null));
	}

	/**
	 * Liefert true, wenn der Benutzer sich das Abspeichern der Spaltenbreiten/Anordnung wünscht
	 */
	public boolean getSaveWidths() {
		return this.saveWidths;
	}

	public String getCriteriaName() {
		return criteriaName;
	}
}