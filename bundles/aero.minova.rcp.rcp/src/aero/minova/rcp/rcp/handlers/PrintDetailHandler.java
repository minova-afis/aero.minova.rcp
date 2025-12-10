package aero.minova.rcp.rcp.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Evaluate;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Map.Entry;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.PrintUtil;

public class PrintDetailHandler {

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
	private EModelService eModelService;

	@Inject
	private MPerspective mPerspective;

	public static final String FORMS = "Forms";
	public static final String DEFAULT = "DEFAULT";
	public static final String PROCEDURENAME = "procedurename";
	public static final String REPORTNAME = "reportname";
	public static final String ROOTELEMENT = "rootelement";

	List<String> checkedMasks = new ArrayList<>();
	Map<String, String> procedureNames = new HashMap<>();
	Map<String, String> reportNames = new HashMap<>();
	Map<String, String> rootElements = new HashMap<>();

	boolean reportsFolderExists = false;

	/**
	 * Button nur anzeigen, wenn in xbs definiert ist, dass gedruckt werden kann
	 *
	 * @param part
	 * @return
	 */
	@Evaluate
	public boolean visible(MPerspective mPerspective) {
		String maskName = mPerspective.getPersistedState().get(Constants.FORM_NAME);
		Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);
		Node maskNode = XBSUtil.getNodeWithName(preferences, maskName);
		return maskNode != null;
	}

	@PostConstruct
	public void downloadReportsZip() {
		try {
			dataService.getHashedZip("reports.zip");
		} catch (Exception e) {}
		File reportsFolder = dataService.getStoragePath().resolve("reports/").toFile();
		reportsFolderExists = reportsFolder.exists();

		// Beim Starten Vorschau schließen
		PrintUtil.hidePreview(mPerspective, eModelService);
	}

	@CanExecute
	public boolean canExecute(MPart mpart, MPerspective mPerspective) {

		// Überprüfen, ob reports Ordner geladen wurde
		if (!reportsFolderExists) {
			return false;
		}

		String maskName = mPerspective.getPersistedState().get(Constants.FORM_NAME);
		String procedureName = procedureNames.get(maskName);
		String reportName = reportNames.get(maskName);
		String rootElement = rootElements.get(maskName);

		// Wenn diese Maske noch nicht geöffnet wurde application.xbs überprüfen
		if (!checkedMasks.contains(maskName)) {
			checkedMasks.add(maskName);

			// Finden des report, rootelement und procedurenamens (Über application.xbs definiert)
			Preferences preferences = (Preferences) mApplication.getTransientData().get(Constants.XBS_FILE_NAME);
			Node maskNode = XBSUtil.getNodeWithName(preferences, maskName);
			if (maskNode == null) {
				return false;
			}
			Node formsNode = XBSUtil.getNodeWithName(maskNode, FORMS);
			if (formsNode == null) {
				return false;
			}
			Node defaultNode = XBSUtil.getNodeWithName(formsNode, DEFAULT);
			if (defaultNode == null) {
				return false;
			}
			Node printNode = defaultNode.getNode().get(0);

			for (Entry e : printNode.getMap().getEntry()) {
				if (e.getKey().equals(REPORTNAME)) {
					reportName = e.getValue();
					reportNames.put(maskName, reportName);
				}
				if (e.getKey().equals(PROCEDURENAME)) {
					procedureName = e.getValue();
					procedureNames.put(maskName, procedureName);
				}
				if (e.getKey().equals(ROOTELEMENT)) {
					rootElement = e.getValue();
					rootElements.put(maskName, rootElement);
				}
			}
		}

		// Ist Report in .xbs definiert?
		if (procedureName == null || reportName == null || rootElement == null) {
			return false;
		}

		// Wurde das xsl-File geladen?
		if (dataService.getCachedFileContent("reports/" + reportName) == null) {
			return false;
		}

		// Ist ein Datensatz gewählt?
		WFCDetailPart wfcDetail = (WFCDetailPart) mpart.getObject();
		MDetail detail = wfcDetail.getDetail();

		return detail.getPrimaryFields().get(0).getValue() != null;

	}

	@Execute
	public void execute(MPart mpart, MWindow window, EModelService modelService, EPartService partService, MPerspective mPerspective) {
		if (!(mpart.getObject() instanceof WFCDetailPart)) {
			return;
		}

		// Keylong-Wert finden
		String maskName = mPerspective.getPersistedState().get(Constants.FORM_NAME);
		WFCDetailPart wfcDetail = (WFCDetailPart) mpart.getObject();
		MField field = wfcDetail.getDetail().getPrimaryFields().get(0);
		int integerValue = field.getValue().getIntegerValue();

		// Tabelle ans CAS aufbauen
		Table table = TableBuilder.newTable(procedureNames.get(maskName)).withColumn(field.getName(), DataType.STRING).create();
		Row row = RowBuilder.newRow().withValue("" + integerValue).create();
		table.addRow(row);

		PrintUtil.getXMLAndShowPDF(dataService, modelService, partService, mApplication, translationService, sync, table, rootElements.get(maskName),
				"reports/" + reportNames.get(maskName), "outputReports/" + maskName.replace(".xml", "") + "_" + integerValue + "_Detail.pdf", mPerspective,
				disablePreview);
	}
}
