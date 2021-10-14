package aero.minova.rcp.rcp.handlers;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
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

	volatile boolean loading = false;

	@Execute
	public void execute(MPart mpart, Shell shell, @Optional MPerspective perspective, UISynchronize sync) {

		if (perspective == null) {
			return;
		}

		// loading soll nur einmal zur Zeit laufen
		loading = true;
		broker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);

		List<MPart> findElements = model.findElements(perspective, Constants.SEARCH_PART, MPart.class);
		((WFCSearchPart) findElements.get(0).getObject()).saveNattable();
		((WFCSearchPart) findElements.get(0).getObject()).updateUserInput();
		Table searchTable = (Table) findElements.get(0).getContext().get("NatTableDataSearchArea");
		CompletableFuture<Table> tableFuture = dataService.getTableAsync(searchTable);

		tableFuture.thenAccept(t -> {
			loading = false;
			broker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
			if (t.getName().equals("Error")) {
				ErrorObject e = new ErrorObject(t, dataService.getUserName());
				broker.post(Constants.BROKER_SHOWERROR, e);
			} else {
				broker.post(Constants.BROKER_LOADINDEXTABLE, Map.of(perspective, t));
			}
		});

		tableFuture.exceptionally(ex -> {
			loading = false;
			broker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
			return null;
		});
	}

	@CanExecute
	public boolean canExecute() {
		return !loading;
	}
}