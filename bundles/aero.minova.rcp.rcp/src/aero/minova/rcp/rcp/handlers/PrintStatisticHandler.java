package aero.minova.rcp.rcp.handlers;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Map.Entry;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.parts.WFCStatisticDetailPart;
import aero.minova.rcp.rcp.util.PrintUtil;

public class PrintStatisticHandler implements ValueChangeListener {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.DISABLE_PREVIEW)
	public boolean disablePreview;

	@Inject
	protected IDataService dataService;

	@Inject
	protected IEventBroker broker;

	@Inject
	protected UISynchronize sync;

	@Inject
	TranslationService translationService;

	@Inject
	MApplication mApplication;

	@Inject
	Logger logger;

	public static final String PROCEDURENAME = "procedurename";
	public static final String REPORTNAME = "reportname";
	public static final String ROOT = "root";

	boolean reportsFolderExists = false;

	@PostConstruct
	public void downloadReportsZip() {
		try {
			dataService.getHashedZip("reports.zip");
		} catch (Exception e) {}
		File reportsFolder = dataService.getStoragePath().resolve("reports/").toFile();
		reportsFolderExists = reportsFolder.exists();
	}

	@CanExecute
	public boolean canExecute(MPart mpart, MPerspective mPerspective) {

		MDetail detail = ((WFCStatisticDetailPart) mpart.getObject()).getMDetail();

		// Wurde reports Ordner geladen?
		if (!reportsFolderExists) {
			return false;
		}

		// Handler als Listener hinzufügen, damit auf Änderungen reagiert werden kann
		for (MField f : detail.getFields()) {
			f.removeValueChangeListener(this);
			f.addValueChangeListener(this);
		}

		// Sind alle Pflichtfelder gefüllt?
		if (!detail.allFieldsAndGridsValid()) {
			return false;
		}

		// Ist ein Datensatz gewählt?
		WFCStatisticDetailPart statisticPart = (WFCStatisticDetailPart) mpart.getObject();
		return statisticPart.getCurrentRow() != null;
	}

	@Execute
	public void execute(MPart mpart, MWindow window, EModelService modelService, EPartService partService, MPerspective mPerspective) {
		try {

			if (!(mpart.getObject() instanceof WFCStatisticDetailPart)) {
				return;
			}

			WFCStatisticDetailPart statisticPart = (WFCStatisticDetailPart) mpart.getObject();

			// Werte aus .xbs auslesen
			Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);
			Node statisticNode = XBSUtil.getNodeWithName(preferences, statisticPart.getCurrentRow().getValue(0).getStringValue());
			String procedureName = "";
			String reportName = null;
			String rootElement = null;
			for (Entry e : statisticNode.getMap().getEntry()) {
				switch (e.getKey().toLowerCase()) {
				case PROCEDURENAME:
					procedureName = e.getValue();
					break;
				case REPORTNAME:
					reportName = e.getValue();
					break;
				case ROOT:
					rootElement = e.getValue();
					break;
				default:
					break;
				}
			}

			// Tabelle ans CAS aufbauen
			Table table = TableBuilder.newTable(procedureName).create();
			Row row = RowBuilder.newRow().create();
			table.addColumn(new Column("StatisticName", DataType.STRING));
			row.addValue(new Value(rootElement));
			for (MField f : statisticPart.getMDetail().getFields()) {
				table.addColumn(new Column(f.getName(), f.getDataType()));
				row.addValue(f.getValue());
			}
			table.addRow(row);

			PrintUtil.getXMLAndShowPDF(dataService, modelService, partService, translationService, sync, table, rootElement, "reports/" + reportName,
					"outputReports/" + statisticPart.getCurrentRow().getValue(0).getStringValue() + ".pdf", mPerspective, disablePreview);

		} catch (Exception ex) {
			logger.error(ex);
			ShowErrorDialogHandler.execute(Display.getCurrent().getActiveShell(), translationService.translate("@Error", null),
					translationService.translate("@msg.ErrorShowingFile", null), ex);
		}
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		// canExecute() Methode wird aufgerufen
		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, Constants.SAVE_DETAIL_BUTTON);
	}
}
