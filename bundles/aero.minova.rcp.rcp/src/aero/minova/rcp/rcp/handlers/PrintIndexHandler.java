package aero.minova.rcp.rcp.handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary.IGroupBySummaryProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.summaryrow.FixedSummaryRowLayer;

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
import ca.odell.glazedlists.TreeList;

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

			WFCIndexPart indexPart = (WFCIndexPart) o;
			Table data = indexPart.getData();
			xmlRootTag = data.getName();
			SortedList<Row> sortedDataList = indexPart.getSortedList();
			ColumnReorderLayer columnReorderLayer = indexPart.getBodyLayerStack().getColumnReorderLayer();
			columnReorderLayer.getColumnIndexOrder();

			// Gruppierung
			TreeList<Row> treeList = indexPart.getBodyLayerStack().getBodyDataLayer().getTreeList();
			List<Integer> groupByIndices = indexPart.getGroupByHeaderLayer().getGroupByModel().getGroupByColumnIndexes();

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
			saveIntoXML(indexPart, treeList, groupByIndices, colConfig, columnReorderLayer.getColumnIndexOrder(), xml, false, xmlRootTag, title);
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
		showFile(url_pdf.toString(), null);
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
	 * @param indexPart
	 * @param groupByIndices
	 * @param treeList
	 * @param rows
	 * @param colConfig
	 * @param columnReorderList
	 * @param xml
	 * @param tabSeparated
	 * @param fileName
	 * @param title
	 */
	private void saveIntoXML(WFCIndexPart indexPart, TreeList<Row> treeList, List<Integer> groupByIndices, List<ColumnInfo> colConfig,
			List<Integer> columnReorderList, StringBuffer xml, boolean tabSeparated, String fileName, String title) {

		// Viewport layer umgehen, damit in addSumRow() auf alle Zeilen zugegriffen werden kann
		ILayer layer = indexPart.getBodyLayerStack().getUnderlyingLayerByPosition(2, 4).getUnderlyingLayerByPosition(2, 4);

		NumberFormat numberFormat = NumberFormat.getInstance(Locale.getDefault());
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2); // TODO anpassen?

		addHeader(xml, fileName);
		xml.append("<Title>" + title + "</Title>\n");
		xml.append("<IndexView>\n");
		xml.append("<Group>\n" + "<Text><![CDATA[Gesamt]]></Text>\n"); // TODO: Übersetzen!

		if (groupByIndices.isEmpty()) { // Keine Gruppierung
			addRows(xml, treeList, colConfig, columnReorderList, numberFormat);
		} else {
			int level = 0; // "Level" der Gruppierung (1: erste Gruppierung, 2: zweite Gruppierung, ...)
			int newLevel = 0;
			int rowIndex = 0;
			String[] sumRows = new String[groupByIndices.size() + 1];

			// Liste der Zeilen (inklusive Gruppierungs-Zeilen) der Reihe nach abarbeiten
			for (Object o : treeList) {
				if (o instanceof GroupByObject) {
					GroupByObject gbo = (GroupByObject) o;

					// Zusammenfassung am Ende der Gruppierung einfügen
					newLevel = gbo.getDescriptor().size();
					for (int i = level; i >= newLevel; i--) {
						xml.append(sumRows[i]);
					}
					level = newLevel;

					// Überschrift für Gruppierung
					String tableTitle = "";
					String colName = "";
					String colValString = "";
					for (int i : groupByIndices) {
						if (gbo.getDescriptor().containsKey(i)) {
							colName = translationService.translate(colConfig.get(columnReorderList.indexOf(i)).column.getLabel(), null);
							Object colVal = gbo.getDescriptor().get(i);
							colValString = colVal.toString();
							if (colVal instanceof Instant) {
								colValString = DateTimeUtil.getDateTimeString((Instant) colVal, Locale.getDefault());
							}
							tableTitle += colName + ": " + colValString + ", ";
						}
					}
					tableTitle = tableTitle.substring(0, tableTitle.length() - 2);

					// Gruppierung in xml
					xml.append("<Group>\n" + "<Text><![CDATA[" + colName + "]]></Text>\n");
					xml.append("<Field>" + colName + "</Field>\n");
					xml.append("<Value><![CDATA[" + colValString + "]]></Value>\n");
					xml.append("<GroupText><![CDATA[" + tableTitle + "]]></GroupText>\n");

					// Tabelle wird nur für "tiefste" Gruppe gedruckt
					if (gbo.getDescriptor().containsKey(groupByIndices.get(groupByIndices.size() - 1))) {
						addRows(xml, indexPart.getBodyLayerStack().getBodyDataLayer().getItemsInGroup(gbo), colConfig, columnReorderList, numberFormat);
					}

					// Zusammenfassung als String erstellen und für später speichern
					String sumRow = addSumRow(indexPart, colConfig, columnReorderList, rowIndex, gbo, layer, numberFormat);
					sumRow += "</Group>\n";
					sumRows[level] = sumRow;
				}
				rowIndex++;
			}

			// Letzten Zusammenfassungen einfügen
			for (int i = level; i > 0; i--) {
				xml.append(sumRows[i]);
			}
		}
		addFinalSummary(xml, indexPart, colConfig, columnReorderList, numberFormat);
		xml.append("</Group>\n");

		xml.append("</IndexView>\n");
		xml.append("</" + fileName + ">");

	}

	private void addRows(StringBuffer xml, List<Row> rows, List<ColumnInfo> colConfig, List<Integer> columnReorderList, NumberFormat numberFormat) {
		xml.append("<Rows>\n");
		for (final Row r : rows) {
			int colIndex = 0;
			xml.append("<Row>\n");
			for (final Integer d : columnReorderList) {
				Column c = colConfig.get(colIndex).column;
				xml.append("<" + translationService.translate(c.getLabel(), null).replaceAll("[^a-zA-Z0-9]", "") + ">");
				if (r.getValue(d) != null) {
					if (r.getValue(d).getType() == DataType.DOUBLE) {
						xml.append(numberFormat.format(r.getValue(d).getDoubleValue()));
					} else if (r.getValue(d).getType() == DataType.INTEGER) {
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

	}

	private String addSumRow(WFCIndexPart indexPart, List<ColumnInfo> colConfig, List<Integer> columnReorderList, int rowIndex, GroupByObject gbo, ILayer layer,
			NumberFormat numberFormat) {
		String sumRow = "<SumRow>\n";

		for (int i = 0; i < colConfig.size(); i++) {
			LabelStack labelStack = layer.getConfigLabelsByPosition(i, rowIndex);
			IGroupBySummaryProvider<Row> summaryProvider = indexPart.getBodyLayerStack().getBodyDataLayer().getGroupBySummaryProvider(labelStack);

			if (summaryProvider != null) {
				int columnIndex = columnReorderList.get(i);
				List<Row> children = indexPart.getBodyLayerStack().getBodyDataLayer().getItemsInGroup(gbo);
				Object summary = summaryProvider.summarize(columnIndex, children);
				if (summary instanceof Double) {
					summary = numberFormat.format(summary);
				}

				Column c = colConfig.get(i).column;
				sumRow += "<" + translationService.translate(c.getLabel(), null).replaceAll("[^a-zA-Z0-9]", "") + ">";
				sumRow += summary;
				sumRow += "</" + translationService.translate(c.getLabel(), null).replaceAll("[^a-zA-Z0-9]", "") + ">\n";
			}
		}

		sumRow += "</SumRow>\n";
		return sumRow;
	}

	private void addFinalSummary(StringBuffer xml, WFCIndexPart indexPart, List<ColumnInfo> colConfig, List<Integer> columnReorderList,
			NumberFormat numberFormat) {
		xml.append("<SumRow>\n");

		FixedSummaryRowLayer summaryLayer = indexPart.getSummaryRowLayer();
		for (int i = 0; i < colConfig.size(); i++) {
			Object summary = summaryLayer.getDataValueByPosition(i, 0);
			if (summary instanceof Double) {
				summary = numberFormat.format(summary);
			}
			if (summary != null) {
				Column c = colConfig.get(i).column;
				xml.append("<" + translationService.translate(c.getLabel(), null).replaceAll("[^a-zA-Z0-9]", "") + ">");
				xml.append(summary);
				xml.append("</" + translationService.translate(c.getLabel(), null).replaceAll("[^a-zA-Z0-9]", "") + ">\n");
			}
		}

		xml.append("</SumRow>\n");
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

		// TODO: Entfernen
		Instant now = Instant.now().plus(2, ChronoUnit.HOURS);
		xml.append("<PrintDate><![CDATA[" + DateTimeUtil.getDateTimeString(now, Locale.getDefault()) + "]]></PrintDate>\n");
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

	/**
	 * Öffnet entwender das BrowserWiget um den Index-Druck anzuzeigen oder den Default PDF Reader!
	 *
	 * @param urlString
	 * @param preview
	 */
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
		// Hier erstaml nur Ohne Preview öffnen!
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
		preview = null;
		return preview;
	}

}
