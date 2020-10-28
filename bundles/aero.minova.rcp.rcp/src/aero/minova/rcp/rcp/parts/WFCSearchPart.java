package aero.minova.rcp.rcp.parts;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.rcp.util.PersistTableSelection;

public class WFCSearchPart {

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private ESelectionService selectionService;

	@Inject
	private IDataService dataService;

	@Inject
	private IEventBroker broker;

	@Inject
	private MPerspective perspective;

	@Inject
	@Named(E4WorkbenchParameterConstants.FORM_NAME)
	private String formName;

	private Form form;

	private FormToolkit formToolkit;

	private Composite composite;

	private Table data;

	@Inject
	MPart mPart;

	@PostConstruct
	public void createComposite(Composite parent) {

		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		form = perspective.getContext().get(Form.class);
		if (form == null) {
			dataService.getFileSynch(formName); // Datei ggf. vom Server holen
			form = dataFormService.getForm(formName);
		}
		if (form == null) {
			LabelFactory.newLabel(SWT.CENTER).align(SWT.CENTER).text(formName).create(parent);
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
