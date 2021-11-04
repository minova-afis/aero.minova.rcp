package aero.minova.rcp.rcp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.concurrent.CompletableFuture;

import javax.xml.transform.TransformerException;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.xml.sax.SAXException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.parts.Preview;
import aero.minova.rcp.util.Tools;

public class PrintUtil {

	private PrintUtil() {}

	public static void getXMLAndShowPDF(IDataService dataService, EModelService modelService, EPartService partService, TranslationService translationService,
			MWindow window, IEventBroker broker, UISynchronize sync, Table table, String rootElement, String xslPath, String resultPath) {

		CompletableFuture<Path> tableFuture = dataService.getXMLAsync(table, rootElement);
		tableFuture.thenAccept(xmlPath -> sync.asyncExec(() -> {
			try {
				// Aus xml und xsl Datei PDF erstellen
				Path pdfPath = dataService.getStoragePath().resolve(resultPath);
				URL pdfFile = pdfPath.toFile().toURI().toURL();
				String xmlString = Files.readString(xmlPath);

				// Wenn ein file schon geladen wurde muss dieses erst freigegeben werden (unter Windows)
				PrintUtil.checkPreview(window, modelService, partService);

				PrintUtil.generatePDF(pdfFile, xmlString, dataService.getStoragePath().resolve(xslPath).toFile());
				PrintUtil.showFile(pdfFile.toString(), PrintUtil.checkPreview(window, modelService, partService));
			} catch (IOException | SAXException | TransformerException e) {
				e.printStackTrace();
				broker.post(Constants.BROKER_SHOWERRORMESSAGE, translationService.translate("@msg.ErrorShowingFile", null));
			}
		}));
	}

	/**
	 * Bereitet die Druckvorschau vor
	 *
	 * @param window
	 *            das aktuelle Fenster (benötigt um den Druckvorschau-Part zu finden)
	 * @param modelService
	 *            der ModelService (benötigt um den Druckvorschau-Part zu finden)
	 * @param partService
	 *            der PartService (benötigt um den Druckvorschau-Part anzuzeigen)
	 * @return der Druckvorschau-Part (falls schon vorhanden, wird dasselbe Objekt zurückgegeben)
	 * @author wild
	 * @since 11.0.0
	 */
	public static Preview checkPreview(MWindow window, EModelService modelService, EPartService partService) {

		// Wir suchen mal nach dem Druck-Part und aktivieren ihn
		MPart previewPart = (MPart) modelService.find(Preview.PART_ID, window);
		if (previewPart.getObject() == null) {
			partService.showPart(previewPart, PartState.CREATE);
		}
		previewPart.setVisible(true);
		previewPart.getParent().setSelectedElement(previewPart);
		return (Preview) previewPart.getObject();
	}

	/**
	 * Öffnet entwender das BrowserWiget um den Index-Druck anzuzeigen oder den Default PDF Reader!
	 *
	 * @param urlString
	 * @param preview
	 */
	public static void showFile(String urlString, Preview preview) {
		if (urlString != null) {
			System.out.println(MessageFormat.format("versuche {0} anzuzeigen", urlString));
			try {
				if (preview == null) {
					System.out.println(MessageFormat.format("öffne {0} auf dem Desktop", urlString));
					Tools.openURL(urlString);
				} else {
					System.out.println(MessageFormat.format("öffne {0} im Preview-Fenster", urlString));
					preview.openURL(urlString);
				}
			} catch (final Exception e) {
				e.printStackTrace();
				System.out.println("Error occured during the file open");
			}
		} else {
			System.out.println("kann Datei NULL nicht anzeigen");
		}
	}

	/**
	 * Generiert ein PDF Dokument mithilfe der gegebenen xml und xsl
	 *
	 * @param pdf
	 * @param xml
	 * @param xsl
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static void generatePDF(URL pdf, String xmlString, File stylesheet) throws IOException, SAXException, TransformerException {
		PDFGenerator.createPdfFile(xmlString, stylesheet, pdf);
	}

}
