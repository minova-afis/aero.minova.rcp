package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Table;

public class LoadIndexHandler {

	@Inject
	private IEventBroker broker;

	@Inject
	private EModelService model;

	@Inject
	private IDataService dataService;

	@Inject
	private EPartService partService;

	@Execute
	public void execute(MPart mpart, Shell shell, MWindow window) {

		List<MPart> findElements = model.findElements(window, PartsID.SEARCH_PART, MPart.class);
		Table table = (Table) findElements.get(0).getContext().get("NatTableDataSearchArea");
		CompletableFuture<Table> tableFuture = dataService.getIndexDataAsync(table.getName(), table);
		tableFuture.join();
		tableFuture.thenAccept(t -> {
			broker.post("PLAPLA", t);
		});

		findElements = model.findElements(mPerspective, PartsID.INDEX_PART, MPart.class);
		partService.activate(findElements.get(0));
	}

}
