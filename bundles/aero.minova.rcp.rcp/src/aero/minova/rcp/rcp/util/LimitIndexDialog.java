package aero.minova.rcp.rcp.util;

import java.text.MessageFormat;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.jface.widgets.WidgetFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

public class LimitIndexDialog extends IconAndMessageDialog {

	/**
	 * -1: alles Laden<br>
	 * -2: nichts Laden <br>
	 * >= 0: gebene Anzahl laden
	 */
	private int limit = -2;
	private int defaultLimit;

	private TranslationService translationService;

	Spinner limitEditor;

	public static final int BUTTON_SET_LIMIT_ID = 4;
	public static final int BUTTON_NO_LIMIT_ID = 5;
	public static final int BUTTON_CANCEL_ID = 6;

	public LimitIndexDialog(Shell parentShell, TranslationService translationService, int currentNumber, int defaultLimit) {
		super(parentShell);
		this.translationService = translationService;
		this.defaultLimit = defaultLimit;
		this.message = MessageFormat.format(translationService.translate("@limitIndex.message", null), currentNumber);
	}

	@Override
	protected Control createDialogArea(Composite dialogParent) {
		createMessageArea(dialogParent);

		Composite cmp = new Composite(dialogParent, SWT.NONE);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
		cmp.setLayoutData(data);
		cmp.setLayout(new FillLayout());
		Composite wrap = WidgetFactory.composite(SWT.NONE).layout(new GridLayout(2, false)).layoutData(new GridData(SWT.FILL, SWT.FILL, true, true))
				.create(cmp);

		LabelFactory.newLabel(SWT.BORDER).text(translationService.translate("@limitIndex.inputLabel", null)).create(wrap);

		limitEditor = new Spinner(wrap, SWT.BORDER);
		limitEditor.setValues(defaultLimit, 0, Integer.MAX_VALUE, 0, 1, 10);

		return dialogParent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Button zum Canceln
		Button cancel = createButton(parent, BUTTON_CANCEL_ID, translationService.translate("@Abort", null), false);
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				LimitIndexDialog.this.limit = -2;
				close();
			}
		});

		// Button für kein Limit setzten
		Button noLimit = createButton(parent, BUTTON_NO_LIMIT_ID, translationService.translate("@limitIndex.noLimit", null), false);
		noLimit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				LimitIndexDialog.this.limit = -1;
				close();
			}
		});

		// Button zum Limit setzten
		Button ok = createButton(parent, BUTTON_SET_LIMIT_ID, translationService.translate("@limitIndex.setLimit", null), true);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				LimitIndexDialog.this.limit = limitEditor.getSelection();
				close();
			}
		});
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(translationService.translate("@limitIndex.Title", null));
	}

	@Override
	protected Image getImage() {
		return getQuestionImage();
	}

	/**
	 * -1: alles Laden<br>
	 * -2: nichts Laden <br>
	 * >= 0: gebene Anzahl laden
	 * 
	 * @return
	 */
	public int getLimit() {
		return limit;
	}
}
