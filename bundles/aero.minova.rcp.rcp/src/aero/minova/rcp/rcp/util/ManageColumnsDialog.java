package aero.minova.rcp.rcp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.model.Column;

public class ManageColumnsDialog extends TitleAreaDialog {

	// ungleich 0 oder 1 um zu verhindern, dass Dialog sich schliesst
	private static final int BUTTON_OK_ID = 4;

	private TranslationService translationService;
	private List<Column> columns;
	private Map<Column, Button> checkboxes;

	public ManageColumnsDialog(Shell parentShell, TranslationService translationService, List<Column> columns) {
		super(parentShell);
		this.translationService = translationService;
		this.columns = columns;
		checkboxes = new HashMap<>();
		setTitleAreaColor(new RGB(236, 236, 236));
	}

	@Override
	protected Control createDialogArea(Composite dialogParent) {
		setTitle(translationService.translate("@ManageColumns.Message", null));

		Composite parent = new Composite(dialogParent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		parent.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = computePreferredHeight(parent, 15);
		parent.setLayoutData(gd);
		parent.setFont(dialogParent.getFont());
		// Build the separator line
		Label titleBarSeparator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
		titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		parent.setSize(200, 200);

		// Buttons für alles de-/aktivieren
		Composite buttonComp = new Composite(parent, SWT.NONE);
		buttonComp.setLayout(new GridLayout(2, false));
		Button selectAll = ButtonFactory.newButton(SWT.PUSH).text(translationService.translate("@SelectAll", null)).create(buttonComp);
		selectAll.addListener(SWT.Selection, event -> selectAll(true));
		Button deselectAll = ButtonFactory.newButton(SWT.PUSH).text(translationService.translate("@DeselectAll", null)).create(buttonComp);
		deselectAll.addListener(SWT.Selection, event -> selectAll(false));

		ScrolledComposite sc = new ScrolledComposite(parent, SWT.V_SCROLL);
		sc.setLayout(new GridLayout());

		Composite wrap = new Composite(sc, SWT.NONE);
		wrap.setLayout(new GridLayout());
		sc.setContent(wrap);

		// Checkbox für jede Spalte
		for (Column c : columns) {
			if (c.getLabel() != null) {
				Button b = ButtonFactory.newButton(SWT.CHECK).text(translationService.translate(c.getLabel(), null)).create(wrap);
				b.setSelection(c.isVisible());
				checkboxes.put(c, b);
			}
		}

		// Scrolled Composite konfigurieren
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.heightHint = computePreferredHeight(sc, checkboxes.entrySet().size());
		sc.setLayoutData(gridData);
		sc.setContent(wrap);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.addListener(SWT.Resize, event -> {
			int width = sc.getClientArea().width;
			sc.setMinSize(parent.computeSize(width, SWT.DEFAULT));
		});

		return parent;
	}

	private void updateVisible() {
		for (Entry<Column, Button> e : checkboxes.entrySet()) {
			e.getKey().setVisible(e.getValue().getSelection());
		}
	}

	private void selectAll(boolean select) {
		for (Entry<Column, Button> e : checkboxes.entrySet()) {
			e.getValue().setSelection(select);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Button zum Canceln
		createButton(parent, IDialogConstants.CANCEL_ID, translationService.translate("@Abort", null), false);

		// Button zum Bestätigen
		Button ok = createButton(parent, BUTTON_OK_ID, translationService.translate("@Apply", null), true);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				updateVisible();
				close();
			}
		});
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(translationService.translate("@ManageColumns.Title", null));
	}

	private int computePreferredHeight(Composite c, int numberOfLines) {
		int defaultHorizontalSpacing = 6;
		Button b = ButtonFactory.newButton(SWT.CHECK).text("TMP").create(c);
		Point preferredSize = b.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		b.dispose();

		return numberOfLines * (preferredSize.y + defaultHorizontalSpacing);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

}
