package aero.minova.rcp.rcp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.Display;
import org.xml.sax.SAXException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.rcp.handlers.ShowErrorDialogHandler;
import aero.minova.rcp.rcp.parts.Preview;
import aero.minova.rcp.util.Tools;

public class PrintUtil {

	private PrintUtil() {}

	static ILog logger = Platform.getLog(PrintUtil.class);

	public static void getXMLAndShowPDF(IDataService dataService, EModelService modelService, EPartService partService, MApplication mApplication,
			TranslationService translationService, UISynchronize sync, Table table, String rootElement, String xslPath, String resultPath,
			MPerspective mPerspective, boolean disablePreview) {

		CompletableFuture<Path> tableFuture = dataService.getXMLAsync(table, rootElement);
		tableFuture.thenAccept(xmlPath -> sync.asyncExec(() -> {
			try {
				if (xmlPath == null) {
					return;// Fehler im DataService, wird dort angezeigt
				}

				// Aus xml und xsl Datei PDF erstellen
				Path pdfPath = dataService.getStoragePath().resolve(resultPath);
				URL pdfFile = pdfPath.toFile().toURI().toURL();
				String xmlString = Files.readString(xmlPath);

				CustomerPrintData printData = (CustomerPrintData) mApplication.getTransientData().get(Constants.CUSTOMER_PRINT_DATA);
				if (printData != null) {
					xmlString = xmlString.replace("</" + rootElement + ">", printData.getXMLString() + "</" + rootElement + ">");
				}

				// Wenn ein file schon geladen wurde muss dieses erst freigegeben werden (unter Windows)
				if (!disablePreview) {
					PrintUtil.checkPreview(mPerspective, modelService, partService);
				}

				String xslPathNew = getXSLPathWithLocale(dataService, xslPath);

				pdfFile = PrintUtil.generatePDF(pdfFile, xmlString, dataService.getStoragePath().resolve(xslPathNew).toFile());

				if (disablePreview) {
					PrintUtil.showFile(pdfFile.toString(), null);
				} else {
					PrintUtil.showFile(pdfFile.toString(), PrintUtil.checkPreview(mPerspective, modelService, partService));
				}
			} catch (IOException | SAXException | TransformerException e) {
				logger.error(e.getMessage(), e);
				ShowErrorDialogHandler.execute(Display.getCurrent().getActiveShell(), translationService.translate("@Error", null),
						translationService.translate("@msg.ErrorShowingFile", null), e);
			}
		}));
	}

	/**
	 * Diese Methode liefert den Pfad zur XSL Datei mit dem korrekten kürzel (locale) Sollte die Sprache für die Kombination aus Sprache und Land nicht
	 * existieren, reduzieren wir es auf die Sprache. Ansonsten wird nur der Name + ".xsl" zurückgegeben.
	 *
	 * @param dataService
	 * @param xslPath
	 * @return
	 */
	public static String getXSLPathWithLocale(IDataService dataService, String xslPath) {
		String xslPathNew = xslPath;

		try {
			Locale locale = CustomLocale.getLocale();
			if (locale != null) {
				String xslPathDummy = xslPath.replace(".xsl", "_" + locale + ".xsl");
				File file = dataService.getStoragePath().resolve(xslPathDummy).toFile();
				if (file.exists()) {
					xslPathNew = xslPathDummy;
				} else {
					xslPathDummy = xslPath.replace(".xsl", "_" + locale.getLanguage() + ".xsl");
					file = dataService.getStoragePath().resolve(xslPathDummy).toFile();
					if (file.exists()) {
						xslPathNew = xslPathDummy;
					}
				}
			}
		} catch (Exception e) {
			// XSL nicht gefunden
		}

		return xslPathNew;
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
	public static Preview checkPreview(MPerspective mPerspective, EModelService modelService, EPartService partService) {

		// Wir suchen mal nach dem Druck-Part und aktivieren ihn
		MPart previewPart = (MPart) modelService.find(Preview.PART_ID, mPerspective);
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
		if (preview == null) {
			Tools.openURL(urlString);
		} else {
			preview.openURL(urlString);
		}
	}

	/**
	 * Generiert ein PDF Dokument mithilfe der gegebenen xml und xsl
	 *
	 * @param pdf
	 * @param xml
	 * @param xsl
	 * @return
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static URL generatePDF(URL pdf, String xmlString, File stylesheet) throws IOException, SAXException, TransformerException {
		return PDFGenerator.createPdfFile(xmlString, stylesheet, pdf);
	}

	/**
	 * Sucht den zu übersetzenden Text raus. Label, @+Name oder ""
	 *
	 * @param c
	 *            Column
	 * @return String
	 */
	public static String prepareTranslation(Column c) {
		if (c != null && c.getLabel() != null) {
			return c.getLabel();
		} else if (c != null && c.getName() != null) {
			return "@" + c.getName();
		}
		return "";
	}

	/**
	 * Sucht den zu übersetzenden Text raus. Label, @+Name oder ""
	 *
	 * @param c
	 *            aero.minova.rcp.form.model.xsd.Column
	 * @return String
	 */
	public static String prepareTranslation(aero.minova.rcp.form.model.xsd.Column c) {
		if (c != null && c.getLabel() != null) {
			return c.getLabel();
		} else if (c != null && c.getName() != null) {
			return "@" + c.getName();
		}
		return "";
	}

}
