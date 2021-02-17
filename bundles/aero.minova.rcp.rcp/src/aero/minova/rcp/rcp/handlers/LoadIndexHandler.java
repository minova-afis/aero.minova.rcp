package aero.minova.rcp.rcp.handlers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import com.google.gson.Gson;

import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.util.Constants;

public class LoadIndexHandler {

	@Inject
	private IEventBroker broker;

	@Inject
	private EModelService model;

	@Inject
	private IDataService dataService;

	@Inject
	private EPartService partService;

	private Gson gson;


	@Execute
	public void execute(MPart mpart, Shell shell, @Optional MPerspective perspective)
			throws URISyntaxException, IOException {
		if (perspective == null)
			return;

		List<MPart> findElements = model.findElements(perspective, PartsID.SEARCH_PART, MPart.class);
		Table table = (Table) findElements.get(0).getContext().get("NatTableDataSearchArea");
		CompletableFuture<Table> tableFuture = dataService.getIndexDataAsync(table.getName(), table);

		tableFuture.join();
		tableFuture.thenAccept(t -> {
			Map<MPerspective, Table> brokerObject = new HashMap<>();
			brokerObject.put(perspective, t);

			broker.post(Constants.BROKER_LOADINDEXTABLE, brokerObject);
		});

		findElements = model.findElements(perspective, PartsID.INDEX_PART, MPart.class);
		partService.activate(findElements.get(0));

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
