package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.core.ui.PartsID;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.plugin1.model.Table;

public class LoadIndexHandler {

	@Inject
	IEventBroker broker;

	@Inject
	EModelService model;

	@Inject
	IDataService dataService;

	@Execute
	public void execute(MPart mpart, Shell shell, MPerspective mPerspective) {

		List<MPart> findElements = model.findElements(mPerspective, PartsID.SEARCH_PART, MPart.class);
		Table table = (Table) findElements.get(0).getContext().get("NatTableDataSearchArea");
		CompletableFuture<Table> tableFuture = dataService.getIndexDataAsync(table.getName(), table);
		tableFuture.thenAccept(t -> broker.post("PLAPLA", t));
	}

}
