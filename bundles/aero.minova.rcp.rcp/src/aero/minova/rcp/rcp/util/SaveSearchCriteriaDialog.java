package aero.minova.rcp.rcp.util;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.jface.widgets.TextFactory;
import org.eclipse.swt.SWT;
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
		Shell parentShell = getParent();
		// Wir erzeugen unsere Shell für den Dialog
		Shell shell = new Shell(parentShell, /* SWT.DIALOG_TRIM | SWT.RESIZE | */SWT.TITLE | SWT.APPLICATION_MODAL);
		// und setzen ein Layout
		shell.setLayout(new FillLayout());

		// Den Titel aus den Messages holen
		shell.setText(translationService.translate("@EnterName", null));

		// Das Composite, auf das die anderen Widgets gepackt werden
		Composite parent = new Composite(shell, SWT.BORDER | SWT.FILL);
		// Ein-spaltig
		parent.setLayout(new GridLayout(1, true));

		Label infoLabel = LabelFactory.newLabel(SWT.BORDER).text("").layoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false)).create(parent);

		Text text = TextFactory.newText(SWT.BORDER).layoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false)).create(parent);

		Button btnSaveWidths = ButtonFactory.newButton(SWT.CHECK).text(translationService.translate("@SelectionCriteria.SaveColumnWidths", null))
				.onSelect(e -> SaveSearchCriteriaDialog.this.saveWidths = ((Button) e.widget).getSelection()).create(parent);
		btnSaveWidths.setSelection(true);

		// Composite mit den Buttons
		Composite buttons = new Composite(parent, SWT.NONE | SWT.RIGHT | SWT.BOTTOM);
		buttons.setLayout(new GridLayout(2, true));

		// Button zum Canceln
		ButtonFactory.newButton(SWT.PUSH).text(translationService.translate("@Abort", null)).onSelect(e -> {
			SaveSearchCriteriaDialog.this.criteriaName = null;
			shell.close();
		}).create(buttons);

		// OK-Button
		Button ok = ButtonFactory.newButton(SWT.PUSH).text(translationService.translate("@OK", null)).onSelect(e -> {
			SaveSearchCriteriaDialog.this.criteriaName = text.getText();
			shell.close();
		}).create(buttons);

		// Auf Änderungen hören und den OK-Button entsprechend freigeben
		text.addModifyListener(e -> {
			final String c = prefs.get(tableName + "." + text.getText() + ".table", null);
			if (text.getText().isBlank()) {
				ok.setEnabled(false);
			} else if (c == null) {
				infoLabel.setForeground(shell.getDisplay().getSystemColor(SWT.DEFAULT));
				infoLabel.setText(translationService.translate("@SelectionCriteria.NameAvailable", null));
				ok.setEnabled(true);
			} else {
				infoLabel.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
				infoLabel.setText(translationService.translate("@SelectionCriteria.WillBeOverwritten", null));
				ok.setEnabled(true);
			}
		});

		shell.setDefaultButton(ok);

		// Die Shell packen und öffnen
		shell.pack();
		shell.setSize(300, shell.getSize().y);
		shell.layout();
		UiUtil.setLocation(shell, parentShell);
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