package aero.minova.rcp.rcp.parts;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import aero.minova.rcp.dataservice.IMinovaJsonService;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.ValueDeserializer;
import aero.minova.rcp.model.ValueSerializer;
import aero.minova.rcp.rcp.nattable.NatTableWrapper;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.PersistTableSelection;

public class WFCIndexPart extends WFCFormPart {

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	private IMinovaJsonService mjs;

	@Inject
	private ESelectionService selectionService;

	private Table data;

	private FormToolkit formToolkit;

	private Composite composite;

	private NatTableWrapper natTable;

	private Gson gson;


	@PostConstruct
	public void createComposite(Composite parent, MPart part, EModelService modelService) {

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
		MPerspective perspectiveFor = modelService.getPerspectiveFor(part);
		natTable = new NatTableWrapper().createNatTable(parent, form, data, true, selectionService,
				perspectiveFor.getContext());

		gson = new Gson();
		gson = new GsonBuilder() //
				.registerTypeAdapter(Value.class, new ValueSerializer()) //
				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
				.setPrettyPrinting() //
				.create();

		try {

			Path path = Paths.get(dataService.getStoragePath() + "/cache/jsonTableIndex");
			File jsonFile = new File(path.toString());
			jsonFile.createNewFile();

			String content = Files.readString(path, StandardCharsets.UTF_8);
			if (!content.equals("")) {
				Table indexTable = gson.fromJson(content, Table.class);
				if (indexTable.getRows() != null) {
					natTable.updateData(indexTable.getRows());
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public void load(@UIEventTopic(Constants.BROKER_LOADINDEXTABLE) Map<MPerspective, Table> map) {
		if (map.get(perspective) != null) {
			Table table = map.get(perspective);
			natTable.updateData(table.getRows());

			try {
				Files.write(dataService.getStoragePath(), gson.toJson(table).getBytes(StandardCharsets.UTF_8));
				System.out.println("Table saved");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
