package aero.minova.rcp.rcp.parts;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.e4.ui.workbench.modeling.ISelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.nattable.NatTableWrapper;
import aero.minova.rcp.rcp.util.PersistTableSelection;

public class XMLIndexPart {

	@Inject
	@Preference
	IEclipsePreferences prefs;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	private IDataFormService dataFormService;

	private Table data;

	@Inject
	IEventBroker broker;

	@Inject
	ESelectionService selectionService;

	private NatTableWrapper natTable;

	private MPerspective perspective = null;

	@PostConstruct
	public void createComposite(Composite parent, IEclipseContext context, MPerspective perspective) {

		this.perspective = perspective;

		Form form = dataFormService.getForm();
		String tableName = form.getIndexView().getSource();

		String string = prefs.get(tableName, null);
		data = dataFormService.getTableFromFormIndex(form);
		data.addRow();
		if (string != null) {
			data = mjs.json2Table(string);
		}

		parent.setLayout(new GridLayout());

		ESelectionService x = new ESelectionService() {

			@Override
			public void setSelection(Object selection) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setPostSelection(Object selection) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeSelectionListener(String partId, ISelectionListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removeSelectionListener(ISelectionListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removePostSelectionListener(String partId, ISelectionListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void removePostSelectionListener(ISelectionListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public Object getSelection(String partId) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object getSelection() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void addSelectionListener(String partId, ISelectionListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addSelectionListener(ISelectionListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addPostSelectionListener(String partId, ISelectionListener listener) {
				// TODO Auto-generated method stub

			}

			@Override
			public void addPostSelectionListener(ISelectionListener listener) {
				// TODO Auto-generated method stub

			}
		};

		natTable = new NatTableWrapper().createNatTable(parent, form, data, true, selectionService, context);
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
	public void load(@UIEventTopic("PLAPLA") Map<MPerspective, Table> map) {
		if (map.get(perspective) != null) {
			Table table = map.get(perspective);
			natTable.updateData(table.getRows());
		}
	}
	public void load(@UIEventTopic("PLAPLA") Table table) {
		natTable.updateData(table.getRows());
	}

	// Aufruf von resize handler, ev. Umstellen auf Event auf welches die NatTable
	// reagiert
	public NatTableWrapper getNatTable() {
		return natTable;
	}

}
