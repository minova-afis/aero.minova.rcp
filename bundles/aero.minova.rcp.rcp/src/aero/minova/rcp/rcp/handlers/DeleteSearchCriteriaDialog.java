package aero.minova.rcp.rcp.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Dieser Dialog zeigt die bestehenden (Such-)Kriterien an und bietet die
 * Möglichkeit, welche zu löschen
 */
public class DeleteSearchCriteriaDialog extends Dialog {
	private TranslationService translationService;
	private IEclipsePreferences prefs;

	private String tableName;

	private List<String> criterias;
	private TableViewer viewer;
	private Button delete;

	public DeleteSearchCriteriaDialog(Shell parent, TranslationService translationService, IEclipsePreferences prefs,
			String tableName) {
		super(parent);
		this.translationService = translationService;
		this.prefs = prefs;
		this.tableName = tableName;
	}

	@Override
	protected Control createDialogArea(Composite dialogParent) {
		Composite parent = (Composite) super.createDialogArea(dialogParent);

		viewer = new TableViewer(parent, SWT.BORDER | SWT.FILL);

		// LayoutData setzen
		GridData tvGd = new GridData(SWT.FILL, SWT.TOP, true, false);
		viewer.getTable().setLayoutData(tvGd);

		// Label-und Content-Provider setzen
		viewer.setLabelProvider(new LabelProvider());
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				StructuredSelection ss = (StructuredSelection) viewer.getSelection();
				// Ist etwas ausgewählt?
				if (ss.isEmpty()) {
					delete.setEnabled(false);
				} else {
					String string = ss.getFirstElement().toString();
					delete.setEnabled(string != null);
				}
			}
		});

		criterias = getCriteriaNames();

		// Input setzen
		viewer.setInput(criterias);

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		delete = createButton(parent, 1, translationService.translate("@Action.Delete", null), false);
		// Button zum Löschen
		delete.setEnabled(false);

		// Die entsprechenden Listener anhängen
		delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				IStructuredSelection ss = viewer.getStructuredSelection();
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
					viewer.refresh();
				}
			}
		});

		createButton(parent, IDialogConstants.OK_ID, translationService.translate("@Action.Close", null), true);

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