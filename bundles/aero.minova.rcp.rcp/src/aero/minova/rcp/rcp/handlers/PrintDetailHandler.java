package aero.minova.rcp.rcp.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.xml.transform.TransformerException;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.xml.sax.SAXException;

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
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.rcp.util.PrintUtil;

public class PrintDetailHandler {

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

		String maskName = mPerspective.getPersistedState().get(Constants.FORM_NAME);
		String procedureName = procedureNames.get(maskName);
		String reportName = reportNames.get(maskName);
		String rootElement = rootElements.get(maskName);

		// Überprüfen, ob reports Ordner geladen wurde
		if (!reportsFolderExists) {
			return false;
		}

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
		MField field = detail.getField("KeyLong");

		return field.getValue() != null;

	}

	@Execute
	public void execute(MPart mpart, MWindow window, EModelService modelService, EPartService partService, MPerspective mPerspective) {
		try {

			if (!(mpart.getObject() instanceof WFCDetailPart)) {
				return;
			}

			// Keylong-Wert finden
			String maskName = mPerspective.getPersistedState().get(Constants.FORM_NAME);
			WFCDetailPart wfcDetail = (WFCDetailPart) mpart.getObject();
			MField field = wfcDetail.getDetail().getField("KeyLong");
			int integerValue = field.getValue().getIntegerValue();

			// Tabelle ans CAS aufbauen
			Table table = TableBuilder.newTable(procedureNames.get(maskName)).withColumn("KeyLong", DataType.STRING).create();
			Row row = RowBuilder.newRow().withValue("" + integerValue).create();
			table.addRow(row);

			// XML-Dateil vom CAS laden
			CompletableFuture<Path> tableFuture = dataService.getXMLAsync(table, rootElements.get(maskName));
			tableFuture.thenAccept(xmlPath -> sync.asyncExec(() -> {
				try {
					// Aus xml und xsl Datei PDF erstellen
					Path pdfPath = dataService.getStoragePath().resolve("reports/" + maskName.replace(".xml", "") + "_Detail.pdf");
					URL pdfFile = pdfPath.toFile().toURI().toURL();
					String xmlString = Files.readString(xmlPath);

					// Wenn ein file schon geladen wurde muss dieses erst freigegeben werden (unter Windows)
					PrintUtil.checkPreview(window, modelService, partService);

					PrintUtil.generatePDF(pdfFile, xmlString, dataService.getStoragePath().resolve("reports/" + reportNames.get(maskName)).toFile());
					PrintUtil.showFile(pdfFile.toString(), PrintUtil.checkPreview(window, modelService, partService));
				} catch (IOException | SAXException | TransformerException e) {
					e.printStackTrace();
					broker.post(Constants.BROKER_SHOWERRORMESSAGE, translationService.translate("@msg.ErrorShowingFile", null));
				}
			}));
		} catch (Exception ex) {
			ex.printStackTrace();
			broker.post(Constants.BROKER_SHOWERRORMESSAGE, translationService.translate("@msg.ErrorShowingFile", null));
		}
	}

}
