package aero.minova.rcp.rcp.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;

public class XMLSearchPart {

	@Inject
	@Preference
	IEclipsePreferences prefs;

	@Inject
	private IDataService dataService;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	ESelectionService selectionService;

	private Table data;

	@Inject MPart mPart;

	private NatTable natTable;

	@PostConstruct
	public void createComposite(Composite parent) {

		Form form = dataFormService.getForm();
		String tableName = form.getIndexView().getSource();
		String string = prefs.get(tableName, null);

		data = dataFormService.getTableFromFormIndex(form);
		if (string != null) {
			// Auslesen der zuletzt gespeicherten Daten
			data = mjs.json2Table(string);
		}
		data.addRow();

		parent.setLayout(new GridLayout());
		mPart.getContext().set("NatTableDataSearchArea", data);
		natTable = NatTableUtil.createNatTable(parent, form, data, false, null);
	}

	@PersistTableSelection
	public void savePrefs(@Named("SpaltenKonfiguration") Boolean name) {

		String tableName = data.getName();
		prefs.put(tableName, mjs.table2Json(data));
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	@PreDestroy
	public void test(Composite parent) {
		// Form form = dataFormService.getForm();
	}

}
