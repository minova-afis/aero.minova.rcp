package aero.minova.rcp.rcp.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
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
import aero.minova.rcp.rcp.nattable.MinovaColumnConfiguration;
import aero.minova.rcp.rcp.nattable.MinovaStatisticConfiguration;

public class WFCStatisticIndexPart extends WFCIndexPart {

	private static final String STATISTIC = "Statistic";

	@Inject
	IEventBroker broker;

	@Inject
	MApplication mApplication;

	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout());
		createStatisticDataFromXBS();
		natTable = createNatTable(parent, null, data);
		loadPrefs(Constants.LAST_STATE);
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
		// TODO?
		// Wir brauchen die erste Spalte mit dem Namen der Statistik nicht für den Anwender sondern nur Intern!
		// bodyLayerStack.columnHideShowLayer.hideColumnPositions(0);

		return new MinovaStatisticConfiguration(table.getColumns());
	}

}
