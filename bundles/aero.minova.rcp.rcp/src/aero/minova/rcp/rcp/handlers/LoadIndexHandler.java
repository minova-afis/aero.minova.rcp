package aero.minova.rcp.rcp.handlers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.rcp.parts.WFCSearchPart;

public class LoadIndexHandler {

	@Inject
	private IEventBroker broker;

	@Inject
	private EModelService model;

	@Inject
	private IDataService dataService;

	@Inject
	private EPartService partService;

	@Inject
	private TranslationService translationService;

	volatile boolean loading = false;

	@Execute
	public void execute(MPart mpart, Shell shell, @Optional MPerspective perspective, UISynchronize sync) throws URISyntaxException, IOException {

		if (perspective == null) {
			return;
		}

		// loading soll nur einmal zur Zeit laufen
		loading = true;
		broker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);

		List<MPart> findElements = model.findElements(perspective, PartsID.SEARCH_PART, MPart.class);
		((WFCSearchPart) findElements.get(0).getObject()).saveNattable();
		Table searchTable = (Table) findElements.get(0).getContext().get("NatTableDataSearchArea");
		CompletableFuture<Table> tableFuture = dataService.getIndexDataAsync(searchTable.getName(), searchTable);

		tableFuture.thenAccept(t -> {
			if (t.getName().equals("Error")) {
				ErrorObject e = new ErrorObject(t, "User", searchTable.getName());
				broker.post(Constants.BROKER_SHOWERROR, e);
			} else {
				broker.post(Constants.BROKER_LOADINDEXTABLE, Map.of(perspective, t));
			}
			sync.asyncExec(() -> {
				List<MPart> parts = model.findElements(perspective, PartsID.INDEX_PART, MPart.class);
				if (!parts.isEmpty()) {
					partService.activate(parts.get(0));
					loading = false;
					broker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
				} else {
					// TODO
					System.out.println("keine aktiven Parts");
				}

			});
		});

	}

	@CanExecute
	public boolean canExecute() {
		return !loading;
//		gson = new Gson();
//		gson = new GsonBuilder() //
//				.registerTypeAdapter(Value.class, new ValueSerializer()) //
//				.registerTypeAdapter(Value.class, new ValueDeserializer()) //
//				.setPrettyPrinting() //
//				.create();
//		Path path = Path.of(dataService.getStoragePath().toString(), "cache", "jsonTableSearch");
//
//		File jsonFile = new File(path.toString());
//		jsonFile.createNewFile();
//		Files.write(path, gson.toJson(table).getBytes(StandardCharsets.UTF_8));
	}

}
