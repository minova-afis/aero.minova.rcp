package aero.minova.rcp.rcp.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.event.RowStructuralRefreshEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionUtils;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.nebula.widgets.nattable.sort.action.SortColumnAction;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.sort.event.ColumnHeaderClickEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Map.Entry;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.nattable.MinovaColumnConfiguration;
import aero.minova.rcp.rcp.nattable.MinovaStatisticConfiguration;
import aero.minova.rcp.rcp.util.NatTableUtil;
import aero.minova.rcp.util.OSUtil;

public class WFCStatisticIndexPart extends WFCNattablePart {

	private static final String STATISTIC = "Statistic";

	@Inject
	IEventBroker broker;

	@Inject
	MApplication mApplication;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TABLE_SELECTION_BUFFER_MS)
	protected int tableSelectionBuffer;

	protected SelectionThread selectionThread;

	@Override
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout());
		createStatisticDataFromXBS();
		natTable = createNatTable(parent, null, data);
		restorePrefs(Constants.LAST_STATE);
	}

	/**
	 * Diese Methode erstellt aus der ausgelesenen XBS die Statistsic Einträge und speichert sie in das Table Objekt.
	 *
	 * @param mApplication
	 */
	private void createStatisticDataFromXBS() {
		String name = translationService.translate("@Name", null);
		String type = translationService.translate("@Type", null);
		String description = translationService.translate("@Description", null);

		data = TableBuilder.newTable("statistic").withColumn("MatchCode", DataType.STRING).withColumn("name", DataType.STRING)
				.withColumn("type", DataType.STRING).withColumn("description", DataType.STRING).create();
		data.getColumns().get(1).setLabel(name);
		data.getColumns().get(2).setLabel(type);
		data.getColumns().get(3).setLabel(description);
		data.getColumns().get(1).setVisible(true);
		data.getColumns().get(2).setVisible(true);
		data.getColumns().get(3).setVisible(true);

		Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);
		Node statisticNode = XBSUtil.getNodeWithName(preferences, STATISTIC);
		for (Node n : statisticNode.getNode()) {
			Row row = RowBuilder.newRow().withValue("").withValue("").withValue("").withValue("").create();
			row.setValue(new Value(n.getName()), 0);
			for (Entry e : n.getMap().getEntry()) {
				switch (e.getKey().toLowerCase()) {
				case "":
					row.setValue(new Value(translationService.translate(e.getValue(), null)), 1);
					break;
				case "group":
					row.setValue(new Value(translationService.translate(e.getValue(), null)), 2);
					break;
				case "description":
					row.setValue(new Value(translationService.translate(e.getValue(), null)), 3);
					break;
				default:
					break;
				}
			}
			data.addRow(row);
		}
	}

	@Override
	public MinovaColumnConfiguration createColumnConfiguration(Table table) {
		return new MinovaStatisticConfiguration(table.getColumns());
	}

	class SelectionThread extends Thread {
		private int sleepMillis;

		/*
		 * Thread, der für sleepMillis Millisekunden schläft und danach die Daten ins Detail lädt, wenn er nicht unterbrochen wurde
		 */
		public SelectionThread(int sleepMillis) {
			this.sleepMillis = sleepMillis;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(sleepMillis);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}

			List c = SelectionUtils.getSelectedRowObjects(getSelectionLayer(), getBodyLayerStack().getBodyDataProvider(), false);
			if (c.get(0) instanceof Row) {
				broker.post(Constants.BROKER_SELECTSTATISTIC, c.get(0));
			}
		}
	}

	@Override
	protected SingleClickSortConfiguration getSingleClickSortConfiguration() {
		return new SingleClickSortConfiguration() {
			@Override
			public void configureUiBindings(final UiBindingRegistry uiBindingRegistry) {
				// normal
				uiBindingRegistry.registerFirstSingleClickBinding(new ColumnHeaderClickEventMatcher(SWT.NONE, 1), new SortColumnAction(false));

				// multi
				int keyMask = SWT.MOD3;
				// für Linux andere Tastenkombi definieren
				if (OSUtil.isLinux()) {
					keyMask |= SWT.MOD2;
				}
				uiBindingRegistry.registerSingleClickBinding(MouseEventMatcher.columnHeaderLeftClick(keyMask), new SortColumnAction(true));
			}
		};
	}

	@Override
	protected void addNattableConfiguration(NatTable natTable) {
		bodyLayerStack.getSelectionLayer().addConfiguration(new DefaultRowSelectionLayerConfiguration());

		bodyLayerStack.getSelectionLayer().addLayerListener(event -> {
			if (event instanceof RowSelectionEvent) {
				if (selectionThread != null) {
					selectionThread.interrupt();
				}
				selectionThread = new SelectionThread(tableSelectionBuffer);
				selectionThread.start();
			} else if (event instanceof RowStructuralRefreshEvent) {
				NatTableUtil.resizeRows(natTable);
			}
		});

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
		return false;
	}

	@Override
	protected void restoreHidden(String prefix) {
		// Im statistic Part können Spalten nicht ausgeblendet werden
	}
}
