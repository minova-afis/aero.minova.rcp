package aero.minova.rcp.rcp.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Dieser Dialog zeigt die bestehenden (Such-)Kriterien an und bietet die Möglichkeit, welche zu löschen
 */
public class DeleteSearchCriteriaDialog extends Dialog {
	private String criteriaName;

	private TranslationService translationService;
	private IEclipsePreferences prefs;

	private String tableName;

	private List<String> criterias;

	public DeleteSearchCriteriaDialog(final Shell parent, TranslationService translationService, IEclipsePreferences prefs, String tableName) {
		super(parent);
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
		shell.setText(translationService.translate("@SelectionCriteria.DeleteTitle", null));

		// Das Composite, auf das die anderen Widgets gepackt werden
		final Composite parent = new Composite(shell, SWT.BORDER | SWT.FILL);
		// Ein-spaltig
		parent.setLayout(new GridLayout(1, true));

		// TableViewer erzeugen
		final TableViewer tv = new TableViewer(parent, SWT.BORDER | SWT.FILL);

		// LayoutData setzen
		final GridData tvGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		tv.getTable().setLayoutData(tvGd);

		// Label-und Content-Provider setzen
		tv.setLabelProvider(new LabelProvider());
		tv.setContentProvider(ArrayContentProvider.getInstance());

		criterias = getCriteriaNames();

		// Input setzen
		tv.setInput(criterias);

		// Composite mit den Buttons
		final Composite buttons = new Composite(parent, SWT.NONE | SWT.RIGHT | SWT.BOTTOM);
		buttons.setLayout(new GridLayout(2, true));

		// Button zum Löschen
		final Button delete = new Button(buttons, SWT.PUSH);
		delete.setText(translationService.translate("@Action.Delete", null));
		delete.setEnabled(false);
		// OK-Button
		final Button ok = new Button(buttons, SWT.PUSH);
		ok.setText(translationService.translate("@Action.Close", null));

		// Die entsprechenden Listener anhängen
		delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final IStructuredSelection ss = tv.getStructuredSelection();
				// Ist etwas ausgewählt?
				if (!ss.isEmpty()) {
					// Nach unserer Konvention löschen wir alle Prefs die dazu gehören
					String criteriaName = ss.getFirstElement().toString();
					criteriaName = tableName + "." + criteriaName;
					prefs.remove(criteriaName + ".table");
					prefs.remove(criteriaName + ".search.size");
					prefs.remove(criteriaName + ".index.size");
					prefs.remove(criteriaName + ".index.sortby");
					prefs.remove(criteriaName + ".index.groupby");
					try {
						prefs.flush();
					} catch (BackingStoreException e1) {
						System.out.println("probleme mit dem Löschen der Kriterien!");
						e1.printStackTrace();
					}
					criterias.remove(ss.getFirstElement());
					tv.refresh();
				}
			}
		});

		ok.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final StructuredSelection sel = (StructuredSelection) tv.getSelection();
				// Ist etwas ausgewählt?
				if (!sel.isEmpty()) {
					// Wenn ja, setzen wir den criteriaName
					DeleteSearchCriteriaDialog.this.criteriaName = sel.getFirstElement().toString();
				} else {
					// Wenn nein, setzen wir ihn auf null
					DeleteSearchCriteriaDialog.this.criteriaName = null;
				}
				// OK schließt immer die Shell
				shell.close();
			}
		});

		tv.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final StructuredSelection ss = (StructuredSelection) tv.getSelection();
				// Ist etwas ausgewählt?
				if (ss.isEmpty()) {
					delete.setEnabled(false);
				} else {
					String string = ss.getFirstElement().toString();
					delete.setEnabled(string != null);
				}
			}
		});

		shell.setDefaultButton(ok);

		// Die Shell packen und öffnen
		shell.pack();
		shell.open();

		// Darauf warten, bis die Shell geschlossen wird
		while (!shell.isDisposed()) {
			if (!parentShell.getDisplay().readAndDispatch()) {
				parentShell.getDisplay().sleep();
			}
		}

		return this.criteriaName;
	}

	private List<String> getCriteriaNames() {
		List<String> criterias = new ArrayList<>();
		try {
			String[] keys = prefs.keys();
			for (String s : keys) {
				if (s.endsWith(".table") && s.startsWith(tableName + ".")) {
					String displayName = s.replace(".table", "");
					displayName = displayName.substring(displayName.indexOf(".") + 1, displayName.length());
					criterias.add(displayName);
				}
			}
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		return criterias;
	}

}