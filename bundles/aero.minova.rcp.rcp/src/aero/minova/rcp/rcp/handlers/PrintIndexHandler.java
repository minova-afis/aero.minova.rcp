package aero.minova.rcp.rcp.handlers;

import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.transform.TransformerException;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary.IGroupBySummaryProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.resize.MaxCellBoundsHelper;
import org.eclipse.nebula.widgets.nattable.summaryrow.FixedSummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.util.GCFactory;
import org.eclipse.swt.graphics.FontData;
import org.xml.sax.SAXException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.rcp.parts.WFCIndexPart;
import aero.minova.rcp.rcp.print.ColumnInfo;
import aero.minova.rcp.rcp.print.ReportConfiguration;
import aero.minova.rcp.rcp.print.ReportCreationException;
import aero.minova.rcp.rcp.print.TableXSLCreator;
import aero.minova.rcp.rcp.util.PrintUtil;
import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.IOUtil;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TreeList;

public class PrintIndexHandler {

	@Inject
	private IDataService dataService;

	@Inject
	private TranslationService translationService;

	@Inject
	private IEventBroker broker;

	@Inject
	private EPartService ePartService;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.CREATE_XML_XS)
	public boolean createXmlXsl;
	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.INDEX_FONT)
	public String indexFont;
	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.OPTIMIZED_WIDTHS)
	public boolean optimizeWidths;
	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.HIDE_EMPTY_COLS)
	public boolean hideEmptyCols;
	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.HIDE_GROUP_COLS)
	public boolean hideGroupCols;
	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.HIDE_SEARCH_CRITERIAS)
	public boolean hideSearchCriterias;
	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.DISABLE_PREVIEW)
	public boolean disablePreview;
	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.DATE_UTIL)
	public String dateUtilPref;
	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIME_UTIL)
	public String timeUtilPref;

	private boolean pdfFolderExists;

	@PostConstruct
	public void downloadPFDZip() {
		try {
			dataService.getHashedZip("pdf.zip");
		} catch (Exception e) {}
		File pdfFolder = dataService.getStoragePath().resolve("pdf/").toFile();
		pdfFolderExists = pdfFolder.exists();
	}

	@CanExecute
	public boolean canExecute(MPart mpart) {
		Object o = mpart.getObject();
		boolean hasRows = false;
		if (o instanceof WFCIndexPart) {
			@SuppressWarnings("unchecked")
			List<Row> dataList = ((WFCIndexPart) o).getBodyLayerStack().getSortedList();
			hasRows = !dataList.isEmpty();
		}
		return pdfFolderExists && hasRows;
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) List<Row> rows, MPart mpart, MWindow window, EModelService modelService,
			EPartService partService) {

		String xmlRootTag = null;
		String title = null;
		Object o = mpart.getObject();
		StringBuffer xml = new StringBuffer();

		MPerspective activePerspective = modelService.getActivePerspective(window);
		title = translationService.translate(activePerspective.getLabel(), activePerspective.getLabel());

		Path path_reports = dataService.getStoragePath().resolve("pdf/");
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
			List<Integer> groupByIndicesReordered = new ArrayList<>();

			// Optimalen Spaltenbreiten ermitteln
			int[] widths = new int[columnReorderLayer.getColumnCount()];
			if (optimizeWidths) {
				for (int i = 0; i < widths.length; i++) {
					widths[i] = i;
				}
				// Hier wird die optimale Breite für Spalten aufgrund des Tabellen-Körpers ermittelt. Aufgrund der extremen Länge einzelner Spalten die durch
				// Gruppierung zustande kommt wird hier der GlazedListsEventLayer verwendet, der die Gruppierungs-Zeilen nicht enthält
				ILayer layer = indexPart.getBodyLayerStack().getGlazedListsEventLayer();
				int[] widthsBody = MaxCellBoundsHelper.getPreferredColumnWidths(indexPart.getNattable().getConfigRegistry(),
						new GCFactory(indexPart.getNattable()), layer, widths);
				// Hier wird die optimale Breite für Spalten aufgrund des Headers ermittelt
				layer = indexPart.getColumnHeaderDataLayer();
				int[] widthsHeader = MaxCellBoundsHelper.getPreferredColumnWidths(indexPart.getNattable().getConfigRegistry(),
						new GCFactory(indexPart.getNattable()), layer, widths);

				// Die Breite einer Spalte ist das Maximum der Header und Body Breite
				for (int i = 0; i < widths.length; i++) {
					widths[i] = Math.max(widthsBody[i], widthsHeader[i]);
				}
			}

			// ColumnInfo erstellen
			List<ColumnInfo> colConfig = new ArrayList<>();
			int i = 0;
			for (Integer i1 : columnReorderLayer.getColumnIndexOrder()) {
				int width = columnReorderLayer.getColumnWidthByPosition(i);
				if (optimizeWidths) {
					width = widths[i1];
				}
				boolean vis = !hideEmptyCols || !isColumnEmpty(i1, sortedDataList);
				colConfig.add(new ColumnInfo(data.getColumns().get(i1), width, vis, i));

				if (groupByIndices.contains(i1)) {
					groupByIndicesReordered.add(i);
				}
				i++;
			}

			ReportConfiguration rConfig = new ReportConfiguration();
			if (indexFont != null && !indexFont.isEmpty()) {
				FontData fontData = new FontData(indexFont);
				int fontsize = 8;
				try {
					fontsize = fontData.getHeight();
				} catch (Exception e) {}
				rConfig.setProp("FontSizeCriteria", fontsize + "");
				rConfig.setProp("FontSizeCell", fontsize + "");
				rConfig.setProp("FontFamily", fontData.getName());
				rConfig.guiFont = new Font(fontData.getName(), Font.PLAIN, fontsize);
			}

			try {
				TableXSLCreator tableCreator = new TableXSLCreator(translationService, this, ePartService);
				xslString = tableCreator.createXSL(xmlRootTag, title, colConfig, rConfig, path_reports, groupByIndicesReordered);
			} catch (ReportCreationException e) {
				e.printStackTrace();
			}

			createXML(indexPart, treeList, groupByIndices, colConfig, columnReorderLayer.getColumnIndexOrder(), xml, false, xmlRootTag, title);

			try {
				Path pathPDF = dataService.getStoragePath().resolve("pdf/" + xmlRootTag + "_Index.pdf");
				Files.createDirectories(pathPDF.getParent());
				createFile(pathPDF.toString());
				URL urlPDF = pathPDF.toFile().toURI().toURL();

				Path pathXML = dataService.getStoragePath().resolve("pdf/" + xmlRootTag + "_Index.xml");
				Path pathXSL = dataService.getStoragePath().resolve("pdf/" + xmlRootTag + "_Index.xsl");
				createFile(pathXML.toString());
				createFile(pathXSL.toString());
				IOUtil.saveLoud(xml.toString(), pathXML.toString(), "UTF-8");
				IOUtil.saveLoud(xslString, pathXSL.toString(), "UTF-8");

				// Wenn ein file schon geladen wurde muss dieses erst freigegeben werden (unter Windows)
				if (!disablePreview) {
					PrintUtil.checkPreview(activePerspective, modelService, partService);
				}

				PrintUtil.generatePDF(urlPDF, xml.toString(), pathXSL.toFile());

				if (!createXmlXsl) {
					Files.delete(pathXSL);
					Files.delete(pathXML);
				}

				if (disablePreview) {
					PrintUtil.showFile(urlPDF.toString(), null);
				} else {
					PrintUtil.showFile(urlPDF.toString(), PrintUtil.checkPreview(activePerspective, modelService, partService));
				}
			} catch (IOException | SAXException | TransformerException e) {
				e.printStackTrace();
				broker.post(Constants.BROKER_SHOWERRORMESSAGE, translationService.translate("@msg.ErrorShowingFile", null));
			}

		}
	}

	private boolean isColumnEmpty(Integer i, SortedList<Row> rows) {
		for (Row r : rows) {
			if (r.getValue(i) != null) {
				return false;
			}
		}
		return true;
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
	private void createXML(WFCIndexPart indexPart, TreeList<Row> treeList, List<Integer> groupByIndices, List<ColumnInfo> colConfig,
			List<Integer> columnReorderList, StringBuffer xml, boolean tabSeparated, String fileName, String title) {

		// Viewport layer umgehen, damit in addSumRow() auf alle Zeilen zugegriffen werden kann
		ILayer layer = indexPart.getBodyLayerStack().getUnderlyingLayerByPosition(2, 4).getUnderlyingLayerByPosition(2, 4);

		NumberFormat numberFormat = NumberFormat.getInstance(CustomLocale.getLocale());
		numberFormat.setMinimumFractionDigits(2);
		numberFormat.setMaximumFractionDigits(2);

		addHeader(xml, fileName);
		xml.append("<Title>" + title + "</Title>\n");
		xml.append("<IndexView>\n");
		xml.append("<Group>\n" + "<Text><![CDATA[" + translationService.translate("@Total", null) + "]]></Text>\n");

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
								colValString = DateTimeUtil.getDateTimeString((Instant) colVal, CustomLocale.getLocale(), dateUtilPref, timeUtilPref);
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
				xml.append("<" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll("[^a-zA-Z0-9]", "") + ">");
				if (r.getValue(d) != null) {
					if (r.getValue(d).getType() == DataType.DOUBLE) {
						// Definierte Nachkommastellen für diese Spalte werden gedruckt
						if (c.getDecimals() != null) {
							numberFormat.setMinimumIntegerDigits(c.getDecimals());
							numberFormat.setMaximumIntegerDigits(c.getDecimals());
						}
						xml.append(numberFormat.format(r.getValue(d).getDoubleValue()));
					} else if (r.getValue(d).getType() == DataType.INTEGER) {
						xml.append(r.getValue(d).getValueString(CustomLocale.getLocale()));
					} else if (r.getValue(d).getType() == DataType.BOOLEAN && r.getValue(d).getBooleanValue()) {
						xml.append(1);
					} else {
						// Information über Instant Formatierung wird übergeben
						xml.append("<![CDATA[");
						xml.append(r.getValue(d).getValueString(CustomLocale.getLocale(), c.getDateTimeType()));
						xml.append("]]>");
					}
				}
				xml.append("</" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll("[^a-zA-Z0-9]", "") + ">\n");
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
				sumRow += "<" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll("[^a-zA-Z0-9]", "") + ">";
				sumRow += summary;
				sumRow += "</" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll("[^a-zA-Z0-9]", "") + ">\n";
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
				xml.append("<" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll("[^a-zA-Z0-9]", "") + ">");
				xml.append(summary);
				xml.append("</" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll("[^a-zA-Z0-9]", "") + ">\n");
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
		xml.append("<PrintDate><![CDATA["
				+ DateTimeUtil.getDateTimeString(DateTimeUtil.getDateTime("0 0"), CustomLocale.getLocale(), dateUtilPref, timeUtilPref) + "]]></PrintDate>\n");
	}

}
