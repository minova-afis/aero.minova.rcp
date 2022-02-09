package aero.minova.rcp.rcp.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.model.Column;

public class ManageColumnsDialog extends TitleAreaDialog {

	private TranslationService translationService;

	// ungleich 0 oder 1 um zu verhindern, dass Dialog sich schliesst
	private static final int BUTTON_OK_ID = 4;
	private static final int BUTTON_CANCEL_ID = 5;

	private List<Column> columns;
	private Map<Column, Button> checkboxes;
	private Map<Column, Boolean> originalState;

	public ManageColumnsDialog(Shell parentShell, TranslationService translationService, List<Column> columns) {
		super(parentShell);
		this.translationService = translationService;
		this.columns = columns;
		checkboxes = new HashMap<>();
		originalState = new HashMap<>();
		for (Column c : columns) {
			originalState.put(c, c.isVisible());
		}
	}

	@Override
	protected Control createDialogArea(Composite dialogParent) {
		setTitle(translationService.translate("@ManageColumns.Title", null));
		setMessage(translationService.translate("@ManageColumns.Message", null));

		Composite parent = (Composite) super.createDialogArea(dialogParent);

		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		sc.setLayout(new GridLayout());
		sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sc.setExpandVertical(true);
		sc.setExpandHorizontal(true);
		sc.setAlwaysShowScrollBars(true);

		Composite wrap = new Composite(sc, SWT.NONE);
		wrap.setLayout(new GridLayout());
		wrap.setSize(200, 100);
		sc.setContent(wrap);

		Button selectAll = ButtonFactory.newButton(SWT.PUSH).text(translationService.translate("@SelectAll", null)).create(wrap);
		selectAll.addListener(SWT.Selection, event -> selectAll(true));
		Button deselectAll = ButtonFactory.newButton(SWT.PUSH).text(translationService.translate("@DeselectAll", null)).create(wrap);
		deselectAll.addListener(SWT.Selection, event -> selectAll(false));
		Button revert = ButtonFactory.newButton(SWT.PUSH).text(translationService.translate("@Revert", null)).create(wrap);
		revert.addListener(SWT.Selection, event -> revertVisible());

		for (Column c : columns) {
			if (c.getLabel() != null) {
				Button b = ButtonFactory.newButton(SWT.CHECK).text(translationService.translate(c.getLabel(), null)).create(wrap);
				b.setSelection(c.isVisible());
				b.addListener(SWT.Selection, event -> c.setVisible(b.getSelection()));
				checkboxes.put(c, b);
			}
		}

		// System.out.println(wrap.getSize());

//		sc.setExpandHorizontal(true);
//		sc.setExpandVertical(true);
//		sc.setSize(200, 100);
//		sc.addListener(SWT.Resize, event -> {
//			wrap.setSize(200, 200);
//
//			int width = 200;
//			sc.setMinSize(wrap.computeSize(width, SWT.DEFAULT));
//			System.out.println(wrap.getSize() + " " + width + " " + sc.getMinHeight());
//		});

		return parent;
	}

	private void revertVisible() {
		for (Entry<Column, Button> e : checkboxes.entrySet()) {
			boolean select = originalState.get(e.getKey());
			e.getValue().setSelection(select);
			e.getKey().setVisible(select);
		}
	}

	private void selectAll(boolean select) {
		for (Entry<Column, Button> e : checkboxes.entrySet()) {
			e.getValue().setSelection(select);
			e.getKey().setVisible(select);
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// Button zum Canceln
		Button cancel = createButton(parent, BUTTON_CANCEL_ID, translationService.translate("@Abort", null), false);
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				revertVisible();
				close();
			}
		});

		// Button zum Bestätigen
		Button ok = createButton(parent, BUTTON_OK_ID, translationService.translate("@Apply", null), true);
		ok.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				// TODO
				close();
			}
		});
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(translationService.translate("@ManageColumns.Title", null));
	}
}
