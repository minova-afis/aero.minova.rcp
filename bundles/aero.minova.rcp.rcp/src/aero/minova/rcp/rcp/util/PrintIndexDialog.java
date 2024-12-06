package aero.minova.rcp.rcp.util;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.jface.widgets.TextFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dieser Dialog zeigt die bestehenden (Such-)Kriterien an und bietet die Möglichkeit, welche zu speichern
 */
public class PrintIndexDialog extends Dialog {

	private TranslationService translationService;

	// ungleich 0 oder 1 um zu verhindern, dass Dialog sich schliesst
	private static final int BUTTON_OK_ID = 4;
	private static final int BUTTON_CANCEL_ID = 5;
	private String title;

	private Text text;

	public PrintIndexDialog(final Shell shell, TranslationService translationService, String title) {
		super(shell);
		this.translationService = translationService;
		this.title = title;
	}

	@Override
	protected Control createDialogArea(Composite dialogParent) {
		Composite parent = (Composite) super.createDialogArea(dialogParent);

		LabelFactory.newLabel(SWT.BORDER).text(translationService.translate("@PrintIndex.Message", null))
				.layoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false)).create(parent);

		text = TextFactory.newText(SWT.BORDER).layoutData(new GridData(SWT.FILL, SWT.DEFAULT, true, false)).create(parent);

		text.setText(this.title);
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
				PrintIndexDialog.this.title = null;
				close();
			}
		});

		// Button zum Bestätigen
		Button ok = createButton(parent, BUTTON_OK_ID, translationService.translate("@OK", null), true);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				PrintIndexDialog.this.title = text.getText();
				close();
			}
		});
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(translationService.translate("@PrintIndex.Title", null));
	}

	public String getTitle() {
		return title;
	}
}