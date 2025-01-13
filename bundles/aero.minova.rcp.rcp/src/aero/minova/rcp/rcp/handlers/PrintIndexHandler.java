package aero.minova.rcp.rcp.handlers;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByDataLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.GroupByObject;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.groupBy.summary.IGroupBySummaryProvider;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.resize.MaxCellBoundsHelper;
import org.eclipse.nebula.widgets.nattable.summaryrow.FixedSummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.util.GCFactory;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.xml.sax.SAXException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.internal.FileUtil;
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
import aero.minova.rcp.rcp.util.CustomerPrintData;
import aero.minova.rcp.rcp.util.PrintIndexDialog;
import aero.minova.rcp.rcp.util.PrintUtil;
import aero.minova.rcp.util.DateTimeUtil;
import aero.minova.rcp.util.IOUtil;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TreeList;

public class PrintIndexHandler {

	private static final String A_Z_A_Z0_9 = "[^a-zA-Z0-9]";

	@Inject
	private IDataService dataService;

	@Inject
	private TranslationService translationService;

	@Inject
	private EModelService eModelService;

	@Inject
	private EPartService ePartService;

	@Inject
	private MPerspective mPerspective;

	@Inject
	private MApplication mApplication;

	ILog logger = Platform.getLog(this.getClass());

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
	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	public String timezone;

	private boolean pdfFolderExists;

	@PostConstruct
	public void downloadPFDZip() {
		try {
			dataService.getHashedZip("pdf.zip");
		} catch (Exception e) {
			// Trotzdem versuchen, den Ordner zu finden, könnte ja schon geladen worden sein
		}
		File pdfFolder = dataService.getStoragePath().resolve("pdf/").toFile();
		pdfFolderExists = pdfFolder.exists();
	}

	@CanExecute
	public boolean canExecute(MPart mpart) {
		Object o = mpart.getObject();
		boolean hasRows = false;
		if (o instanceof WFCIndexPart indexPart) {
			List<Row> dataList = indexPart.getBodyLayerStack().getSortedList();
			hasRows = !dataList.isEmpty();
		}
		return pdfFolderExists && hasRows;
	}

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SELECTION) List<Row> rows, @Named(IServiceConstants.ACTIVE_SHELL) final Shell shell, MPart mpart,
			MWindow window) {

		String xmlRootTag = null;
		String title = null;
		Object o = mpart.getObject();

		title = translationService.translate(mPerspective.getLabel(), null);

		Path pathReports = dataService.getStoragePath().resolve("pdf/");
		String xslString = null;
		if (o instanceof WFCIndexPart indexPart) {

			// Nach zusätzlichem Titel fragen
			IEclipseContext context = mPerspective.getContext();
			String searchConfigName = (String) context.get("ConfigName");
			final PrintIndexDialog pid = new PrintIndexDialog(shell, translationService, searchConfigName);
			if (pid.open() == Window.CANCEL) {
				return;
			}
			String customTitle = pid.getTitle();

			Table data = indexPart.getData();
			xmlRootTag = data.getName();
			SortedList<Row> sortedDataList = indexPart.getSortedList();
			ColumnReorderLayer columnReorderLayer = indexPart.getBodyLayerStack().getColumnReorderLayer();
			ColumnHideShowLayer columnHideShowLayer = indexPart.getBodyLayerStack().getColumnHideShowLayer();

			// Gruppierung
			@SuppressWarnings("unchecked")
			TreeList<Object> treeList = ((GroupByDataLayer<Object>) indexPart.getBodyLayerStack().getBodyDataLayer()).getTreeList();
			List<Integer> groupByIndices = indexPart.getGroupByHeaderLayer().getGroupByModel().getGroupByColumnIndexes();
			List<Integer> groupByIndicesReordered = new ArrayList<>();

			// Optimalen Spaltenbreiten ermitteln
			int[] widths = getWidths(indexPart, columnReorderLayer);

			List<ColumnInfo> colConfig = getColumnInfos(data, sortedDataList, columnReorderLayer, groupByIndices, groupByIndicesReordered, widths);

			ReportConfiguration rConfig = new ReportConfiguration();
			if (indexFont != null && !indexFont.isEmpty()) {
				FontData fontData = new FontData(indexFont);
				int fontsize = 8;
				try {
					fontsize = fontData.getHeight();
				} catch (Exception e) {
					// Dann default 8 nutzen
				}
				rConfig.setProp("FontSizeCriteria", fontsize + "");
				rConfig.setProp("FontSizeCell", fontsize + "");
				rConfig.setProp("FontFamily", fontData.getName());
				rConfig.guiFont = new Font(fontData.getName(), Font.PLAIN, fontsize);
			}

			try {
				TableXSLCreator tableCreator = new TableXSLCreator(this, ePartService);
				ContextInjectionFactory.inject(tableCreator, mPerspective.getContext());

				String titleInReport = title;
				if (customTitle != null && !customTitle.isBlank()) {
					titleInReport += "</fo:block>" + System.lineSeparator() + "<fo:block text-align=\"center\">" + customTitle;
				}
				xslString = tableCreator.createXSL(xmlRootTag, titleInReport, colConfig, rConfig, pathReports, groupByIndicesReordered);
			} catch (ReportCreationException e) {
				logger.error(e.getMessage(), e);
			}

			try {
				String xmlString = createXML(indexPart, treeList, groupByIndices, colConfig, columnReorderLayer.getColumnIndexOrder(),
						columnHideShowLayer.getHiddenColumnIndexes(), xmlRootTag, title);

				saveAndShowPDF(xmlRootTag, title, xslString, xmlString);
			} catch (Exception e) {
				ShowErrorDialogHandler.execute(Display.getCurrent().getActiveShell(), translationService.translate("@Error", null),
						translationService.translate("@msg.ErrorCreatingXML", null), e);
			}

		}
	}

	private void saveAndShowPDF(String xmlRootTag, String title, String xslString, String xmlString) {
		try {
			Path pathPDF = dataService.getStoragePath().resolve("outputReports/" + title.replace(" ", "_") + "_Index.pdf");
			Files.createDirectories(pathPDF.getParent());

			pathPDF = Path.of(FileUtil.createFile(pathPDF.toString()));
			URL urlPDF = pathPDF.toFile().toURI().toURL();

			Path pathXML = dataService.getStoragePath().resolve("pdf/" + xmlRootTag + "_Index.xml");
			Path pathXSL = dataService.getStoragePath().resolve("pdf/" + xmlRootTag + "_Index.xsl");
			pathXML = Path.of(FileUtil.createFile(pathXML.toString()));
			pathXSL = Path.of(FileUtil.createFile(pathXSL.toString()));
			IOUtil.saveLoud(xmlString, pathXML.toString(), "UTF-8");
			IOUtil.saveLoud(xslString, pathXSL.toString(), "UTF-8");

			// Wenn ein file schon geladen wurde muss dieses erst freigegeben werden (unter Windows)
			if (!disablePreview) {
				PrintUtil.checkPreview(mPerspective, eModelService, ePartService);
			}

			urlPDF = PrintUtil.generatePDF(urlPDF, xmlString, pathXSL.toFile());

			if (!createXmlXsl) {
				Files.delete(pathXSL);
				Files.delete(pathXML);
			}

			if (disablePreview) {
				PrintUtil.showFile(urlPDF.toString(), null);
			} else {
				PrintUtil.showFile(urlPDF.toString(), PrintUtil.checkPreview(mPerspective, eModelService, ePartService));
			}
		} catch (IOException | SAXException | TransformerException e) {
			ShowErrorDialogHandler.execute(Display.getCurrent().getActiveShell(), translationService.translate("@Error", null),
					translationService.translate("@msg.ErrorShowingFile", null), e);
		}
	}

	private List<ColumnInfo> getColumnInfos(Table data, SortedList<Row> sortedDataList, ColumnReorderLayer columnReorderLayer, List<Integer> groupByIndices,
			List<Integer> groupByIndicesReordered, int[] widths) {
		// ColumnInfo erstellen
		List<ColumnInfo> colConfig = new ArrayList<>();
		int i = 0;
		for (Integer i1 : columnReorderLayer.getColumnIndexOrder()) {
			int width = columnReorderLayer.getColumnWidthByPosition(i);
			if (optimizeWidths) {
				width = widths[i1];
			}
			boolean vis = (!hideEmptyCols || !isColumnEmpty(i1, sortedDataList)) && data.getColumns().get(i1).isVisible();

			Column c = data.getColumns().get(i1);
			ColumnInfo columnInfo = new ColumnInfo(c, width, vis, i);
			if (c.getDecimals() != null) {
				NumberFormat numberFormat = NumberFormat.getInstance(CustomLocale.getLocale());
				numberFormat.setMinimumFractionDigits(c.getDecimals());
				numberFormat.setMaximumFractionDigits(c.getDecimals());
				columnInfo.numberFormat = numberFormat;
			}
			colConfig.add(columnInfo);

			if (groupByIndices.contains(i1)) {
				groupByIndicesReordered.add(i);
			}
			i++;
		}
		return colConfig;
	}

	private int[] getWidths(WFCIndexPart indexPart, ColumnReorderLayer columnReorderLayer) {
		int[] widths = new int[columnReorderLayer.getColumnCount()];
		if (optimizeWidths) {
			for (int i = 0; i < widths.length; i++) {
				widths[i] = i;
			}
			// Hier wird die optimale Breite für Spalten aufgrund des Tabellen-Körpers ermittelt. Aufgrund der extremen Länge einzelner Spalten die durch
			// Gruppierung zustande kommt wird hier der GlazedListsEventLayer verwendet, der die Gruppierungs-Zeilen nicht enthält
			ILayer layer = indexPart.getBodyLayerStack().getGlazedListsEventLayer();
			int[] widthsBody = MaxCellBoundsHelper.getPreferredColumnWidths(indexPart.getNattable().getConfigRegistry(), new GCFactory(indexPart.getNattable()),
					layer, widths);
			// Hier wird die optimale Breite für Spalten aufgrund des Headers ermittelt
			layer = indexPart.getColumnHeaderDataLayer();
			int[] widthsHeader = MaxCellBoundsHelper.getPreferredColumnWidths(indexPart.getNattable().getConfigRegistry(),
					new GCFactory(indexPart.getNattable()), layer, widths);

			// Die Breite einer Spalte ist das Maximum der Header und Body Breite
			for (int i = 0; i < widths.length; i++) {
				widths[i] = Math.max(widthsBody[i], widthsHeader[i]);
			}
		}
		return widths;
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
	private String createXML(WFCIndexPart indexPart, TreeList<Object> treeList, List<Integer> groupByIndices, List<ColumnInfo> colConfig,
			List<Integer> columnReorderList, Collection<Integer> hiddenColumns, String fileName, String title) {

		StringBuilder xml = new StringBuilder();
		// Viewport layer umgehen, damit in addSumRow() auf alle Zeilen zugegriffen werden kann
		ILayer layer = indexPart.getBodyLayerStack().getUnderlyingLayerByPosition(2, 4).getUnderlyingLayerByPosition(2, 4);

		addHeader(xml, fileName);
		xml.append("<Title>" + title + "</Title>\n");
		xml.append("<IndexView>\n");
		xml.append("<Group>\n" + "<Text><![CDATA[" + translationService.translate("@Total", null) + "]]></Text>\n");

		if (groupByIndices.isEmpty()) { // Keine Gruppierung
			addRows(xml, treeList, colConfig, columnReorderList, hiddenColumns);
		} else {
			addGroupByRows(indexPart, treeList, groupByIndices, colConfig, columnReorderList, hiddenColumns, xml, layer);
		}
		addFinalSummary(xml, indexPart, colConfig);
		xml.append("</Group>\n");

		xml.append("</IndexView>\n");
		xml.append("</" + fileName + ">");

		return xml.toString();
	}

	@SuppressWarnings("unchecked")
	private void addGroupByRows(WFCIndexPart indexPart, TreeList<Object> treeList, List<Integer> groupByIndices, List<ColumnInfo> colConfig,
			List<Integer> columnReorderList, Collection<Integer> hiddenColumns, StringBuilder xml, ILayer layer) {
		int level = 0; // "Level" der Gruppierung (1: erste Gruppierung, 2: zweite Gruppierung, ...)
		int newLevel = 0;
		int rowIndex = 0;
		String[] sumRows = new String[groupByIndices.size() + 1];

		// Liste der Zeilen (inklusive Gruppierungs-Zeilen) der Reihe nach abarbeiten
		for (Object o : treeList) {
			if (o instanceof GroupByObject gbo) {
				// Zusammenfassung am Ende der Gruppierung einfügen
				newLevel = gbo.getDescriptor().size();
				for (int i = level; i >= newLevel; i--) {
					xml.append(sumRows[i]);
				}
				level = newLevel;

				addGroupByValues(groupByIndices, colConfig, columnReorderList, xml, gbo);

				// Tabelle wird nur für "tiefste" Gruppe gedruckt
				if (gbo.getDescriptor().containsKey(groupByIndices.get(groupByIndices.size() - 1))) {
					addRows(xml, ((GroupByDataLayer<Object>) indexPart.getBodyLayerStack().getBodyDataLayer()).getItemsInGroup(gbo), colConfig,
							columnReorderList, hiddenColumns);
				}

				// Zusammenfassung als String erstellen und für später speichern
				String sumRow = addSumRow(indexPart, colConfig, columnReorderList, rowIndex, gbo, layer);
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

	private void addGroupByValues(List<Integer> groupByIndices, List<ColumnInfo> colConfig, List<Integer> columnReorderList, StringBuilder xml,
			GroupByObject gbo) {
		// Überschrift für Gruppierung
		StringBuilder tableTitle = new StringBuilder();
		String colName = "";
		String colValString = "";
		for (int i : groupByIndices) {
			if (gbo.getDescriptor().containsKey(i)) {
				colName = translationService.translate(colConfig.get(columnReorderList.indexOf(i)).column.getLabel(), null);
				Object colVal = gbo.getDescriptor().get(i);
				colValString = colVal.toString();
				if (colVal instanceof Instant instant) {
					colValString = DateTimeUtil.getDateTimeString(instant, CustomLocale.getLocale(), dateUtilPref, timeUtilPref, timezone);
				}
				tableTitle.append(colName + ": " + colValString + ", ");
			}
		}

		// Gruppierung in xml
		xml.append("<Group>\n" + "<Text><![CDATA[" + colName + "]]></Text>\n");
		xml.append("<Field>" + colName + "</Field>\n");
		xml.append("<Value><![CDATA[" + colValString + "]]></Value>\n");
		xml.append("<GroupText><![CDATA[" + tableTitle.substring(0, tableTitle.length() - 2) + "]]></GroupText>\n");
	}

	private void addRows(StringBuilder xml, List<Object> list, List<ColumnInfo> colConfig, List<Integer> columnReorderList, Collection<Integer> hiddenColumns) {
		xml.append("<Rows>\n");
		for (Object o : list) {
			if (o instanceof Row r) {
				xml.append("<Row>\n");
				addRowString(xml, colConfig, columnReorderList, hiddenColumns, r);
				xml.append("</Row>\n");
			}
		}
		xml.append("</Rows>\n");

	}

	private void addRowString(StringBuilder xml, List<ColumnInfo> colConfig, List<Integer> columnReorderList, Collection<Integer> hiddenColumns, Row r) {
		int colIndex = 0;
		for (final Integer d : columnReorderList) {
			if (hiddenColumns.contains(d)) {
				colIndex++;
				continue;
			}

			Column c = colConfig.get(colIndex).column;
			xml.append("<" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll(A_Z_A_Z0_9, "") + ">");
			if (r.getValue(d) != null) {
				if (r.getValue(d).getType() == DataType.DOUBLE) {
					xml.append(colConfig.get(colIndex).numberFormat.format(r.getValue(d).getDoubleValue()));
				} else if (r.getValue(d).getType() == DataType.INTEGER) {
					xml.append(r.getValue(d).getValueString(CustomLocale.getLocale(), dateUtilPref, timeUtilPref, timezone));
				} else if (r.getValue(d).getType() == DataType.BOOLEAN && Boolean.TRUE.equals(r.getValue(d).getBooleanValue())) {
					xml.append(1);
				} else {
					// Information über Instant Formatierung wird übergeben
					xml.append("<![CDATA[");
					xml.append(r.getValue(d).getValueString(CustomLocale.getLocale(), c.getDateTimeType(), dateUtilPref, timeUtilPref, timezone));
					xml.append("]]>");
				}
			}
			xml.append("</" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll(A_Z_A_Z0_9, "") + ">\n");
			colIndex++;
		}
	}

	private String addSumRow(WFCIndexPart indexPart, List<ColumnInfo> colConfig, List<Integer> columnReorderList, int rowIndex, GroupByObject gbo,
			ILayer layer) {
		StringBuilder sumRow = new StringBuilder("<SumRow>\n");

		int indexInSummary = 0;
		for (int i = 0; i < colConfig.size(); i++) {

			if (!colConfig.get(i).column.isVisible()) {
				continue;
			}

			LabelStack labelStack = layer.getConfigLabelsByPosition(indexInSummary, rowIndex);
			@SuppressWarnings("unchecked")
			IGroupBySummaryProvider<Row> summaryProvider = ((GroupByDataLayer<Row>) indexPart.getBodyLayerStack().getBodyDataLayer())
					.getGroupBySummaryProvider(labelStack);

			if (summaryProvider != null) {
				int columnIndex = columnReorderList.get(i);
				@SuppressWarnings("unchecked")
				List<Row> children = ((GroupByDataLayer<Row>) indexPart.getBodyLayerStack().getBodyDataLayer()).getItemsInGroup(gbo);
				Object summary = summaryProvider.summarize(columnIndex, children);
				if (summary instanceof Double) {
					summary = colConfig.get(i).numberFormat.format(summary);
				}

				Column c = colConfig.get(i).column;
				sumRow.append("<" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll(A_Z_A_Z0_9, "") + ">");
				sumRow.append(summary);
				sumRow.append("</" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll(A_Z_A_Z0_9, "") + ">\n");
			}

			indexInSummary++;
		}

		sumRow.append("</SumRow>\n");
		return sumRow.toString();
	}

	private void addFinalSummary(StringBuilder xml, WFCIndexPart indexPart, List<ColumnInfo> colConfig) {
		xml.append("<SumRow>\n");

		FixedSummaryRowLayer summaryLayer = indexPart.getSummaryRowLayer();

		int indexInSummary = 0;
		for (int i = 0; i < colConfig.size(); i++) {

			if (!colConfig.get(i).column.isVisible()) {
				continue;
			}

			Object summary = summaryLayer.getDataValueByPosition(indexInSummary, 0);
			if (summary instanceof Double) {
				summary = colConfig.get(i).numberFormat.format(summary);
			}
			if (summary != null) {
				Column c = colConfig.get(i).column;
				xml.append("<" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll(A_Z_A_Z0_9, "") + ">");
				xml.append(summary);
				xml.append("</" + translationService.translate(PrintUtil.prepareTranslation(c), null).replaceAll(A_Z_A_Z0_9, "") + ">\n");
			}

			indexInSummary++;
		}

		xml.append("</SumRow>\n");
	}

	/**
	 * Packt die Header Daten in den StringBuffer inklusive Name der Datei
	 *
	 * @param xml
	 * @param filename
	 */
	private void addHeader(StringBuilder xml, String filename) {
		CustomerPrintData printData = (CustomerPrintData) mApplication.getTransientData().get(Constants.CUSTOMER_PRINT_DATA);

		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
		xml.append("<" + filename + ">\n");
		xml.append(printData.getXMLString());
		xml.append("<PrintDate><![CDATA["
				+ DateTimeUtil.getDateTimeString(DateTimeUtil.getDateTime("0 0", timezone), CustomLocale.getLocale(), dateUtilPref, timeUtilPref, timezone)
				+ "]]></PrintDate>\n"); // TODO: Darauf achten, dass das passt
	}

}
