package aero.minova.rcp.rcp.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;

/**
 * Dieser Dialog zeigt die bestehenden (Such-)Kriterien an und bietet die Möglichkeit, welche zu löschen
 */
public class DeleteSearchCriteriaDialog extends Dialog {
	private TranslationService translationService;
	private IEclipsePreferences prefs;

	private String tableName;

	// ungleich 0 oder 1 um zu verhindern, dass Dialog sich schliesst
	private static final int BUTTON_DELETE_ID = 4;
	private List<String> criterias;
	private TableViewer viewer;
	private Button delete;

	private String criteriaName;

	Preferences loadedTablePrefs = InstanceScope.INSTANCE.getNode(Constants.LAST_LOADED_SEARCHCRITERIA);

	public DeleteSearchCriteriaDialog(Shell parent, TranslationService translationService, IEclipsePreferences prefs, String tableName) {
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
		viewer.addSelectionChangedListener(event -> {
			StructuredSelection ss = (StructuredSelection) viewer.getSelection();
			// Ist etwas ausgewählt?
			if (ss.isEmpty()) {
				delete.setEnabled(false);
			} else {
				criteriaName = ss.getFirstElement().toString();
				delete.setEnabled(criteriaName != null);
			}
		});

		criterias = getCriteriaNames();
		Collections.sort(criterias);

		// Input setzen
		viewer.setInput(criterias);

		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		delete = createButton(parent, BUTTON_DELETE_ID, translationService.translate("@Action.Delete", null), false);
		// Button zum Löschen
		delete.setEnabled(false);

		// Die entsprechenden Listener anhängen
		delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				// Ist etwas ausgewählt?
				if (criteriaName != null && !criteriaName.isEmpty()) {
					// Nach unserer Konvention löschen wir alle Prefs die dazu gehören
					String prefCriteriaName = tableName + "." + criteriaName;
					prefs.remove(prefCriteriaName + ".table");
					prefs.remove(prefCriteriaName + ".search.size");
					prefs.remove(prefCriteriaName + ".index.size");
					prefs.remove(prefCriteriaName + ".index.sortby");
					prefs.remove(prefCriteriaName + ".index.groupby");
					try {
						prefs.flush();
					} catch (BackingStoreException e1) {
						// Fehler beim Persistieren
					}
					String prefValue = loadedTablePrefs.get(Constants.LAST_SEARCHCRITERIA, prefCriteriaName);
					if (prefCriteriaName.equals(prefValue)) {
						loadedTablePrefs.remove(Constants.LAST_SEARCHCRITERIA);
					}
					criterias.remove(criteriaName);
					viewer.refresh();
				}
			}
		});

		createButton(parent, IDialogConstants.CANCEL_ID, translationService.translate("@Action.Close", null), true);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(translationService.translate("@SelectionCriteria.DeleteTitle", null));
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
			// Fehler beim Auslesen
		}

		return criterias;
	}

}