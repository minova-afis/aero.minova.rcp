package aero.minova.rcp.rcp.parts;

import static org.eclipse.nebula.widgets.nattable.selection.SelectionUtils.getSelectedRowObjects;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.event.RowStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.nattable.MinovaColumnConfiguration;
import aero.minova.rcp.rcp.nattable.MinovaIndexConfiguration;
import aero.minova.rcp.rcp.nattable.SelectionThread;
import aero.minova.rcp.rcp.util.NatTableUtil;

public class WFCIndexPart extends WFCNattablePart {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.AUTO_LOAD_INDEX)
	boolean autoLoadIndex;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TABLE_SELECTION_BUFFER_MS)
	protected int tableSelectionBuffer;

	@Inject
	IEventBroker broker;

	protected SelectionThread selectionThread;

	@Override
	public void createComposite(Composite parent) {
		new FormToolkit(parent.getDisplay());
		getForm();
		if (form == null) {
			return;
		}
		data = dataFormService.getTableFromFormIndex(form);

		parent.setLayout(new GridLayout());

		natTable = createNatTable(parent, form, getData());

		restorePrefs(Constants.LAST_STATE);
		if (autoLoadIndex) {
			ParameterizedCommand cmd = commandService.createCommand("aero.minova.rcp.rcp.command.loadindex", null);
			handlerService.executeHandler(cmd);
		}
	}

	@Override
	public MinovaColumnConfiguration createColumnConfiguration(Table table) {
		return new MinovaIndexConfiguration(table.getColumns(), form);
	}

	@Override
	protected void addNattableConfiguration(NatTable natTable) {

		bodyLayerStack.getSelectionLayer().addConfiguration(new DefaultRowSelectionLayerConfiguration());

		bodyLayerStack.getSelectionLayer().addLayerListener(event -> {
			if (event instanceof RowSelectionEvent) {
				if (selectionThread != null) {
					selectionThread.interrupt();
				}
				selectionThread = new IndexSelection(tableSelectionBuffer);
				selectionThread.start();
			} else if (event instanceof RowStructuralRefreshEvent) {
				NatTableUtil.resizeRows(natTable);
			}
		});

		CopyDataCommandHandler copyHandler = new CopyDataCommandHandler(bodyLayerStack.getSelectionLayer(), getColumnHeaderDataLayer(), rowHeaderDataLayer);
		copyHandler.setCopyFormattedText(true);
		gridLayer.registerCommandHandler(copyHandler);

	}

	class IndexSelection extends SelectionThread {

		public IndexSelection(int sleepMillis) {
			super(sleepMillis);
		}

		@Override
		protected void doSelectionAction() {
			// Ausgewählten Zeilen müssen gefiltert werden, um Gruppen-Zeilen zu entfernen
			@SuppressWarnings("unchecked")
			List<? extends Object> c = getSelectedRowObjects(getSelectionLayer(), getBodyLayerStack().getBodyDataProvider(), false);
			List<Row> collection = c.stream().filter(Row.class::isInstance).map(Row.class::cast).toList();

			Table t = dataFormService.getTableFromFormIndex(form);
			for (Row r : collection) {
				t.addRow(r);
			}
			if (!collection.isEmpty()) {
				context.set(Constants.BROKER_ACTIVEROWS, t);
			}
		}
	}

	@Override
	protected boolean useGroupBy() {
		return true;
	}

	@Override
	protected boolean useSortingHeader() {
		return true;
	}

	@Override
	protected boolean useSummaryRow() {
		return true;
	}

	/**
	 * Diese Methode ließt die Index-Spalten aus und erstellet daraus eine Table, diese wird dann an den CAS als Anfrage übergeben.
	 */
	@SuppressWarnings("rawtypes")
	@Inject
	@Optional
	public void load(@UIEventTopic(Constants.BROKER_LOADINDEXTABLE) Table resultTable) {
		MPerspective activePerspective = modelService.getActivePerspective(context.get(MWindow.class));
		if (!activePerspective.equals(mPerspective)) {
			return;
		}

		// clear the group by summary cache so the new summary calculation gets triggered
		((GroupByDataLayer) bodyLayerStack.getBodyDataLayer()).clearCache();

		// Daten in Nattable schreiben. Wenn Page != 1 werden die Zeilen nur angehängt
		updateData(resultTable.getRows(), resultTable.getMetaData().getPage() != 1);

		if (resultTable.getRows().isEmpty()) {
			broker.post(Constants.BROKER_SHOWNOTIFICATION, "@msg.NoRecordsLoaded");
		}
	}

	/**
	 * Lädt die Liste von Zeilen in die Nattable.
	 *
	 * @param list
	 * @param add
	 *            true: Liste wird unten angehängt; false: Alte Einträge werden verworfen
	 */
	public void updateData(List<Row> list, boolean add) {
		if (!add) {
			bodyLayerStack.getSortedList().clear();
		}
		bodyLayerStack.getSortedList().addAll(list);
		natTable.refresh(false); // Damit Summary-Row richtig aktualisiert wird
	}
}
