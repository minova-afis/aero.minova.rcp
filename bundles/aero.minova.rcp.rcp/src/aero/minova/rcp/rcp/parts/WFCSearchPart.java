package aero.minova.rcp.rcp.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;

public class WFCSearchPart extends WFCFormPart {

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	private MPerspective perspective;

	private Table data;

	@Inject
	MPart mPart;

	@PostConstruct
	public void createComposite(Composite parent) {

		new FormToolkit(parent.getDisplay());
		if (getForm(parent) == null) {
			return;
		}

		perspective.getContext().set(Form.class, form); // Wir merken es uns im Context; so können andere es nutzen
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
		NatTableUtil.createNatTable(parent, form, data, false, null);
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
