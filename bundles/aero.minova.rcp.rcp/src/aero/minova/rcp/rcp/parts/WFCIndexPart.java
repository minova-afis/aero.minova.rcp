package aero.minova.rcp.rcp.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;

public class WFCIndexPart extends WFCFormPart {

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	private IEventBroker broker;

	@Inject
	private ESelectionService selectionService;

	private Table data;

	private FormToolkit formToolkit;

	private Composite composite;

	private NatTable natTable;

	@PostConstruct
	public void createComposite(Composite parent) {

		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		if (getForm(parent) == null) {
			return;
		}

		perspective.getContext().set(Form.class, form); // Wir merken es uns im Context; so können andere es nutzen
		String tableName = form.getIndexView().getSource();

		String string = prefs.get(tableName, null);
		data = dataFormService.getTableFromFormIndex(form);
		data.addRow();
		if (string != null) {
			data = mjs.json2Table(string);
		}

		parent.setLayout(new GridLayout());
		natTable = NatTableUtil.createNatTable(parent, form, data, true, selectionService);
	}

	@PersistTableSelection
	public void savePrefs() {
		// TODO INDEX Part reihenfolge + Gruppierung speichern
	}

	/**
	 * Diese Methode ließt die Index-Apalten aus und erstellet daraus eine Tabel,
	 * diese wir dann an den CAS als Anfrage übergeben.
	 */
	@Inject
	@Optional
	public void load(@UIEventTopic("PLAPLA") Table table) {
		data.getRows().clear();
		for (Row r : table.getRows()) {
			data.addRow(r);
		}
		natTable.refresh(false);
		natTable.requestLayout();
	}

	public NatTable getNatTable() {
		return natTable;
	}

}
