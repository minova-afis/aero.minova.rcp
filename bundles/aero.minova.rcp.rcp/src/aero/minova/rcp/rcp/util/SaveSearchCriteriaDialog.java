package aero.minova.rcp.rcp.util;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dieser Dialog zeigt die bestehenden (Such-)Kriterien an und bietet die Möglichkeit, welche zu speichern
 */
public class SaveSearchCriteriaDialog extends Dialog {


	private String criteriaName = "DEFAULT";

	private boolean saveWidths = true;

	private TranslationService translationService;
	private IEclipsePreferences prefs;

	private String tableName;

	public SaveSearchCriteriaDialog(final Shell shell, TranslationService translationService, IEclipsePreferences prefs, String tableName) {
		super(shell);
		this.translationService = translationService;
		this.prefs = prefs;
		this.tableName = tableName;
	}

	public String open() {
		// Die Eltern-Shell
		final Shell parentShell = getParent();
		// Wir erzeugen unsere Shell für den Dialog
		final Shell shell = new Shell(parentShell, /* SWT.DIALOG_TRIM | SWT.RESIZE | */SWT.TITLE | SWT.APPLICATION_MODAL);
		// und setzen ein Layout
		shell.setLayout(new FillLayout());

		// Den Titel aus den Messages holen
		shell.setText(translationService.translate("@EnterName", null));

		// Das Composite, auf das die anderen Widgets gepackt werden
		final Composite parent = new Composite(shell, SWT.BORDER | SWT.FILL);
		// Ein-spaltig
		parent.setLayout(new GridLayout(1, true));

		final Label infoLabel = new Label(parent, SWT.BORDER);
		infoLabel.setText("");

		final GridData infoGd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		infoLabel.setLayoutData(infoGd);

		final Text text = new Text(parent, SWT.BORDER);

		final Button btnSaveWidths = new Button(parent, SWT.CHECK);
		btnSaveWidths.setText(translationService.translate("@SelectionCriteria.SaveColumnWidths", null));
		btnSaveWidths.setSelection(true);

		final GridData textGd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
		text.setLayoutData(textGd);

		// Composite mit den Buttons
		final Composite buttons = new Composite(parent, SWT.NONE | SWT.RIGHT | SWT.BOTTOM);
		buttons.setLayout(new GridLayout(2, true));

		// Button zum Canceln
		final Button cancel = new Button(buttons, SWT.PUSH);
		cancel.setText(translationService.translate("@Abort", null)); // das ist eigentlich Cancel, aber die so bezeichnete messages-property bezieht sich auf
																		// Storno :-/
		// OK-Button
		final Button ok = new Button(buttons, SWT.PUSH);
		ok.setText(translationService.translate("@OK", null));

		// Die entsprechenden Listener anhängen
		cancel.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SaveSearchCriteriaDialog.this.criteriaName = null;
				shell.close();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {/* DO NOTHING */}
		});

		ok.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				// Ist etwas ausgewählt?
				SaveSearchCriteriaDialog.this.criteriaName = text.getText();
				// OK schließt immer die Shell
				shell.close();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {/* DO NOTHING */}
		});

		btnSaveWidths.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				SaveSearchCriteriaDialog.this.saveWidths = btnSaveWidths.getSelection();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {/* DO NOTHING */}
		});

		// Auf Änderungen hören und den OK-Button entsprechend freigeben
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				final String c = prefs.get(tableName + text.getText() + ".table", null);
				if (c == null) {
					infoLabel.setForeground(shell.getDisplay().getSystemColor(SWT.DEFAULT));
					infoLabel.setText(translationService.translate("@SelectionCriteria.NameAvailable", null));
					ok.setEnabled(true);
				} else {
					infoLabel.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
					infoLabel.setText(translationService.translate("@SelectionCriteria.WillBeOverwritten", null));
					ok.setEnabled(true);
				}
			}
		});

		shell.setDefaultButton(ok);

		// Die Shell packen und öffnen
		shell.pack();
		shell.setSize(300, shell.getSize().y);
		shell.layout();
		shell.open();

		text.setText(this.criteriaName);
		text.setSelection(0, text.getText().length());

		// Darauf warten, bis die Shell geschlossen wird
		while (!shell.isDisposed()) {
			if (!parentShell.getDisplay().readAndDispatch()) {
				parentShell.getDisplay().sleep();
			}
		}
		return this.criteriaName;
	}

	/**
	 * Liefert true, wenn der Benutzer sich das Abspeichern der Spaltenbreiten/Anordnung wünscht
	 */
	public boolean getSaveWidths() {
		return this.saveWidths;
	}
}