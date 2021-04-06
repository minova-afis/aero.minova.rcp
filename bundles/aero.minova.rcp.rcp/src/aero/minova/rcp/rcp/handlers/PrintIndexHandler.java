package aero.minova.rcp.rcp.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.transform.TransformerException;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.parts.Preview;
import aero.minova.rcp.rcp.parts.WFCIndexPart;
import aero.minova.rcp.rcp.print.ColumnInfo;
import aero.minova.rcp.rcp.print.ReportConfiguration;
import aero.minova.rcp.rcp.print.ReportCreationException;
import aero.minova.rcp.rcp.print.TableXSLCreator;
import aero.minova.rcp.rcp.util.PDFGenerator;
import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.IOUtil;
import aero.minova.rcp.util.Tools;
import ca.odell.glazedlists.SortedList;

public class PrintIndexHandler {

	@Inject
	private IDataService dataService;

	@Inject
	private TranslationService translationService;

	@Inject
	private IEventBroker broker;

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) List<Row> rows, MPart mpart, MWindow window, EModelService modelService,
			EPartService partService, @Optional Preview preview) {

		String xmlRootTag = null;
		String title = null;
		Object o = mpart.getObject();
		StringBuffer xml = new StringBuffer();

		MPerspective activePerspective = modelService.getActivePerspective(window);
		title = translationService.translate(activePerspective.getLabel(), activePerspective.getLabel());

		Path path_reports = dataService.getStoragePath().resolve("PDF/");
		String xslString = null;
		if (o instanceof WFCIndexPart) {

			Table data = ((WFCIndexPart) o).getData();
			xmlRootTag = data.getName();
			SortedList<Row> sortedDataList = ((WFCIndexPart) o).getSortedList();
			ColumnReorderLayer columnReorderLayer = ((WFCIndexPart) o).getBodyLayerStack().getColumnReorderLayer();
			columnReorderLayer.getColumnIndexOrder();

			List<ColumnInfo> colConfig = new ArrayList<>();
			int i = 0;
			for (Integer i1 : columnReorderLayer.getColumnIndexOrder()) {
				colConfig.add(new ColumnInfo(data.getColumns().get(i1), columnReorderLayer.getColumnWidthByPosition(i)));
				i++;
			}

			ReportConfiguration rConfig = new ReportConfiguration();

			try {
				TableXSLCreator tableCreator = new TableXSLCreator(translationService);
				xslString = tableCreator.createXSL(xmlRootTag, title, sortedDataList, colConfig, rConfig, path_reports);
			} catch (ReportCreationException e) {
				e.printStackTrace();
			}

			saveIntoXSL(xslString, xmlRootTag);
			saveIntoXML(sortedDataList, colConfig, columnReorderLayer.getColumnIndexOrder(), xml, false, xmlRootTag, title);
		}

		Path path_pdf = dataService.getStoragePath().resolve("PDF/" + xmlRootTag + "_Index.pdf");
		Path path_xml = dataService.getStoragePath().resolve("PDF/" + xmlRootTag + "_Index.xml");
		Path path_xsl = dataService.getStoragePath().resolve("PDF/" + xmlRootTag + "_Index.xsl");
		URL url_pdf = null;
		URL url_xml = null;
		URL url_xsl = null;
		try {
			Files.createDirectories(path_pdf.getParent());
			Files.createDirectories(path_xml.getParent());
			createFile(path_pdf.toString());
			createFile(path_xml.toString());
//			Files.createDirectories(path_xml.getParent());
//			Files.createFile(path_xml);

			url_pdf = path_pdf.toFile().toURI().toURL();
			url_xml = path_xml.toFile().toURI().toURL();
			url_xsl = path_xsl.toFile().toURI().toURL();
			// Schreibt den Inhalt in die XML Datei
			IOUtil.saveLoud(xml.toString(), path_xml.toString(), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}

		generatePDF(url_pdf, url_xml, url_xsl);
		showFile(url_pdf.toString(), checkPreview(window, modelService, partService, preview));
	}

	private void saveIntoXSL(String xslString, String fileName) {
		if (xslString != null) {
			Path path_xsl = dataService.getStoragePath().resolve("PDF/" + fileName + "_Index.xsl");
			try {
				Files.createDirectories(path_xsl.getParent());
				createFile(path_xsl.toString());
				// Schreibt den Inhalt in die XML Datei
				IOUtil.saveLoud(xslString, path_xsl.toString(), "UTF-8");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Erstellt eine Datei falls sie existiert, wird sie geleert.
	 *
	 * @param path
	 */
	public void createFile(String path) {
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				FileOutputStream writer = new FileOutputStream(path);
				writer.write(("").getBytes());
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Schreibt die XML Datei aus den Daten der NatTable
	 *
	 * @param rows
	 * @param colConfig
	 * @param columnReorderList
	 * @param xml
	 * @param tabSeparated
	 * @param fileName
	 * @param title
	 */
	private void saveIntoXML(SortedList<Row> rows, List<ColumnInfo> colConfig, List<Integer> columnReorderList, StringBuffer xml, boolean tabSeparated,
			String fileName, String title) {
		if (xml != null && rows != null && rows.iterator().hasNext()) {
			int colIndex = 0;
			addHeader(xml, fileName);
			xml.append("<Title>" + title + "</Title>\n");
			xml.append("<IndexView>\n" + "<Group>\n" + "<Text><![CDATA[Gesamt]]></Text>");
			xml.append("<Rows>\n");
			for (final Row r : rows) {
				colIndex = 0;
				xml.append("<Row>\n");
				for (final Integer d : columnReorderList) {
					Column c = colConfig.get(colIndex).column;
					xml.append("<" + translationService.translate(c.getLabel(), null).replaceAll("[^a-zA-Z0-9]", "") + ">");
					if (r.getValue(d) != null) {
						if (r.getValue(d).getType() == DataType.DOUBLE || r.getValue(d).getType() == DataType.INTEGER) {
							xml.append(r.getValue(d).getValueString(Locale.getDefault()));
						} else if (r.getValue(d).getType() == DataType.BOOLEAN && r.getValue(d).getBooleanValue()) {
							xml.append(1);
						} else {
							xml.append("<![CDATA[");
							xml.append(r.getValue(d).getValueString(Locale.getDefault()));
							xml.append("]]>");
						}
					}
					xml.append("</" + translationService.translate(c.getLabel(), null).replaceAll("[^a-zA-Z0-9]", "") + ">\n");
					colIndex++;
				}
				xml.append("</Row>\n");
			}
			xml.append("</Rows>\n");
			xml.append("</Group>\n" + "</IndexView>\n");
			xml.append("</" + fileName + ">");
		}
	}

	/**
	 * Packt die Header Daten in den StringBuffer inklusive Name der Datei
	 *
	 * @param xml
	 * @param filename
	 */
	private void addHeader(StringBuffer xml, String filename) {
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
		xml.append("<" + filename + ">\n");
		xml.append("<Site>\n" + "<Address1><![CDATA[MINOVA Information Services GmbH]]></Address1>\n" + "<Address2><![CDATA[Tröltschstraße 4]]></Address2>\n"
				+ "<Address3><![CDATA[97072 Würzburg]]></Address3>\n" + "<Phone><![CDATA[+49 (931) 322 35-0]]></Phone>\n"
				+ "<Fax><![CDATA[+49 (931) 322 35-55]]></Fax>\n" + "<Application>WFC</Application>\n" + "<Logo>logo.gif</Logo>\n" + "</Site>");
		xml.append("<PrintDate><![CDATA[" + DateTimeUtil.getDateTimeString(Instant.now(), Locale.getDefault()) + "]]></PrintDate>\n");
	}

	/**
	 * Generiert ein PDF Dokument und gibt es als FileOutputStream zurück!
	 *
	 * @param pdf
	 * @param xml
	 * @param xsl
	 * @return
	 */
	public void generatePDF(URL pdf, URL xml, URL xsl) {
		PDFGenerator pdfGenerator = new PDFGenerator(new HashMap<String, String>());
		try {
			FileOutputStream pdfOutput = new FileOutputStream(pdf.getFile());
			pdfGenerator.createPdfFile(xml.getFile(), xsl.getFile(), pdfOutput);
			return;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		broker.post(Constants.BROKER_SHOWERRORMESSAGE, "Drucken des Index schlug fehl!");
	}

	private void showFile(String urlString, Preview preview) {
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
	 * Bereitet die Druckvorschau vor
	 *
	 * @param window
	 *            das aktuelle Fenster (benötigt um den Druckvorschau-Part zu finden)
	 * @param modelService
	 *            der ModelService (benötigt um den Druckvorschau-Part zu finden)
	 * @param partService
	 *            der PartService (benötigt um den Druckvorschau-Part anzuzeigen)
	 * @param preview
	 *            der Druckvorschau-Part (falls nicht vorhanden, wird versucht, einen zu erzeugen)
	 * @return der Druckvorschau-Part (falls schon vorhanden, wird dasselbe Objekt zurückgegeben)
	 * @author wild
	 * @since 11.0.0
	 */
	protected static Preview checkPreview(MWindow window, EModelService modelService, EPartService partService, Preview preview) {
		if (preview == null) {
			// Wir suchen mal nach dem Druck-Part und aktivieren ihn
			MPart previewPart = (MPart) modelService.find(Preview.PART_ID, window);
			if (previewPart.getObject() == null) {
				partService.showPart(previewPart, PartState.CREATE);
			}
			previewPart.setVisible(true);
			previewPart.getParent().setSelectedElement(previewPart);
			preview = (Preview) previewPart.getObject();
		} else {
			preview.clear();
		}

		return preview;
	}

}
