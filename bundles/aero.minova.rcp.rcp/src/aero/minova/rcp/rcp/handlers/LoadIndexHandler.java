package aero.minova.rcp.rcp.handlers;

import java.util.ArrayList;
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
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.rcp.parts.WFCIndexPart;
import aero.minova.rcp.rcp.parts.WFCSearchPart;

public class LoadIndexHandler {

	@Inject
	private IEventBroker broker;

	@Inject
	private EModelService model;

	@Inject
	private IDataService dataService;

	volatile boolean loading = false;

	@CanExecute
	public boolean canExecute() {
		return !loading;
	}

	@Execute
	public void execute(MPart mpart, Shell shell, @Optional MPerspective perspective, UISynchronize sync, MPerspective mPerspective) {

		if (perspective == null) {
			return;
		}

		// loading soll nur einmal zur Zeit laufen
		loading = true;
		broker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);

		MPart searchMPart = model.findElements(perspective, Constants.SEARCH_PART, MPart.class).get(0);
		WFCSearchPart searchPart = (WFCSearchPart) searchMPart.getObject();
		searchPart.saveNattable();
		searchPart.updateUserInput();

		Table searchTable = (Table) mPerspective.getContext().get(Constants.SEARCH_TABLE);
		Table requestTable = filterInvisibleColumns(searchTable);
		CompletableFuture<Table> tableFuture = dataService.getTableAsync(requestTable);

		tableFuture.thenAccept(t -> {
			loading = false;
			broker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
			if (t.getName().equals("Error")) {
				ErrorObject e = new ErrorObject(t, dataService.getUserName());
				broker.post(Constants.BROKER_SHOWERROR, e);
			} else {
				MPart indexMPart = model.findElements(perspective, Constants.INDEX_PART, MPart.class).get(0);
				WFCIndexPart indexPart = (WFCIndexPart) indexMPart.getObject();
				Table resultTable = addColumns(indexPart.getData(), t);
				broker.post(Constants.BROKER_LOADINDEXTABLE, Map.of(perspective, resultTable));
			}
		});

		tableFuture.exceptionally(ex -> {
			loading = false;
			broker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
			return null;
		});
	}

	/**
	 * Entfernt unsichtbare Spalten aus der Anfrage (außer Keys)
	 * 
	 * @param searchTable
	 * @return
	 */
	private Table filterInvisibleColumns(Table searchTable) {
		Table requestTable = TableBuilder.newTable(searchTable.getName()).create();

		List<Column> columns = new ArrayList<>();
		List<Row> rows = new ArrayList<>();

		for (int i = 0; i < searchTable.getRows().size(); i++) {
			rows.add(new Row());
		}

		for (Column c : searchTable.getColumns()) {
			if (c.isKey() || c.isVisible()) {
				columns.add(c);

				for (int i = 0; i < searchTable.getRows().size(); i++) {
					Row searchRow = searchTable.getRows().get(i);
					Row requestRow = rows.get(i);
					requestRow.addValue(searchRow.getValue(searchTable.getColumnIndex(c.getName())));
				}
			}
		}

		requestTable.addColumns(columns);
		requestTable.addRows(rows);

		return requestTable;
	}

	/**
	 * Fügt alle Spalten wieder hinzu, damit die Tabelle korrekt angezeigt werden kann
	 * 
	 * @param indexTable
	 * @param res
	 * @return
	 */
	private Table addColumns(Table indexTable, Table res) {
		Table result = TableBuilder.newTable(res.getName()).create();

		List<Column> columns = new ArrayList<>();
		List<Row> rows = new ArrayList<>();

		for (int i = 0; i < res.getRows().size(); i++) {
			rows.add(new Row());
		}

		for (Column c : indexTable.getColumns()) {
			columns.add(c);

			for (int i = 0; i < res.getRows().size(); i++) {
				Value v = null;
				int index = res.getColumnIndex(c.getName());
				if (index != -1) {
					v = res.getRows().get(i).getValue(index);
				}
				Row resRow = rows.get(i);
				resRow.addValue(v);
			}
		}

		result.addColumns(columns);
		result.addRows(rows);

		return result;
	}
}