package aero.minova.rcp.rcp.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import javax.xml.transform.TransformerException;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.xml.sax.SAXException;

import aero.minova.rcp.rcp.parts.Preview;
import aero.minova.rcp.util.Tools;

public class PrintUtil {

	private PrintUtil() {}

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
