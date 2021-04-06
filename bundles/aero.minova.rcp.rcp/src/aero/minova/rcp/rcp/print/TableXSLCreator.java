package aero.minova.rcp.rcp.print;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.e4.core.services.translation.TranslationService;

import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.rcp.print.ReportCreationException.Cause;
import aero.minova.rcp.util.IOUtil;
import ca.odell.glazedlists.SortedList;

public class TableXSLCreator extends CommonPrint {

	private TranslationService translationService;

	// Templates
	private static HashMap<String, String> templates = new HashMap<>();

	public TableXSLCreator(TranslationService translationService2) {
		this.translationService = translationService2;
	}

	/**
	 * Erzeugt ein XSL-Template mittels einer Such- und Datentabelle
	 * 
	 * @throws ReportCreationException
	 */
	public String createXSL(String xmlRootTag, String reportName, SortedList<Row> dataList, List<ColumnInfo> colConfig, ReportConfiguration reportConf,
			Path path_reports) throws ReportCreationException {

		if (reportConf == null) {
			reportConf = ReportConfiguration.DEFAULT;
		}

		if (colConfig == null || dataList == null) {
			throw new IllegalArgumentException("Need dataList and colConfig");
		}

		// Wir bereinigen die unsichtbaren Spalten
		// als unsichtbar zählen auch die Spalten, die nicht mehr auf die Seite passen
		// hier entscheidet sich auch ob Hoch- oder Querformat
		// final List<ColumnInfo> cols = filterInvisibleColumns(colConfig, conf);

		final Orientation ori = reportConf.calculatePageOrientation(getPrintedRowWidth(colConfig, reportConf));
		final String xslData = getTemplate(ori);
		return interpolateXslData(xmlRootTag, reportName, dataList, colConfig, reportConf, path_reports, xslData);
	}

	/**
	 * Liefert eine Liste mit nur sichtbaren Spalten, d.h.<br>
	 * - die 'visible' sind<br>
	 * - die eine Minimalbreite aufweisen<br>
	 * - die den Druckbereich nicht übersteigen<br>
	 * Außerdem wird der restliche verfügbare Platz mit einer leeren Spalte aufgefüllt, sodass das Design passt.
	 */
	protected static List<ColumnInfo> filterInvisibleColumns(List<ColumnInfo> cols, ReportConfiguration conf) {
		final List<ColumnInfo> toRet = new ArrayList<>();
		if (cols == null || cols.size() == 0) {
			return toRet;
		}
		// wir gehen erst mal davon aus, dass wir Platz für Querformat haben
		// sollte Hochformat von der Breite her reichen, wird später noch umgestellt
		int mmLeft = conf.getAvailLandscapeWidth();
		int mmUsed = 0;
		for (final ColumnInfo ci : cols) {
			if (ci.width > MIN_COL_SIZE_PX && ci.visible) {
				final int mmWidth = getPrintedRowWidth(ci, conf);
				if (mmLeft < mmWidth) {
					break;
				} else {
					mmLeft -= mmWidth;
					mmUsed += mmWidth;
					toRet.add(ci);
				}
			}
		}

		if (toRet.size() > 0 && mmLeft > conf.CELL_PADDING_MM) {
			if (mmUsed <= conf.getAvailPortraitWidth()) {
				// Hochformat reicht!
				mmLeft = conf.getAvailPortraitWidth() - mmUsed;
			}
			if (mmLeft > conf.CELL_PADDING_MM) {
				final int pxEmpty = (int) ((mmLeft - conf.CELL_PADDING_MM) / conf.PIXEL_SIZE_MM);
				toRet.add(new ColumnInfo(null, pxEmpty));
			}
		}

		return toRet;
	}

	/**
	 * Liefert die XSL-Zellenbeschreibung in Form eines Strings
	 *
	 * @throws ReportCreationException
	 */
	private String getCellDefinition(final List<ColumnInfo> cols, final boolean asSum) throws ReportCreationException {
		final StringBuffer sb = new StringBuffer();
		String text;
		for (int i = 0; i < cols.size(); i++) {
			final ColumnInfo ci = cols.get(i);
			final String name = ci.column == null ? "" : translationService.translate(ci.column.getLabel(), null).replaceAll("[^a-zA-Z0-9]", "");

			if (ci.column == null) {
				text = getTemplate("CellEmptyDefinition");
			} else if (asSum) {
				// Die erste Spalte gilt als "Überschrift"
				text = (i == 0 ? "" : getTemplate("SumCellDefinition").replace("%%ColumnName%%", name));
			} else if (ci.column.getType().equals(DataType.BOOLEAN)) {
				text = getTemplate("CellBoolDefinition").replace("%%ColumnName%%", name);
			} else {
				text = getTemplate("CellDefinition").replace("%%ColumnName%%", name);
				// #37748: bei mehrzeiligen Feldern Zeilenumbruch zulassen
				if (ci.column.getType().equals(DataType.STRING)) {
					text = text.replace("wrap-option=\"no-wrap\"", "");
				}
			}

			boolean isNumeric = false;
			if (ci.column.getType() == DataType.INTEGER || ci.column.getType() == DataType.DOUBLE) {
				isNumeric = true;
			}

			text = text.replace("%%TextAlign%%", isNumeric ? "right" : "left");
			sb.append(text).append("\r\n");
		}
		return sb.toString();
	}

	/**
	 * Liefert die XSL-Spaltenbeschreibung in Form eines Strings Die Breiten werden dabei prozentuell zur IndexView-Breite berechnet
	 *
	 * @throws ReportCreationException
	 */
	private static String getColumnDefinition(final List<ColumnInfo> cols, final ReportConfiguration conf) throws ReportCreationException {
		final StringBuffer sb = new StringBuffer();
		for (final ColumnInfo ci : cols) {
			final int absSize = getPrintedRowWidth(ci, conf);
			final String colDef = getTemplate("ColumnDefinition").replace("%%Size%%", "" + absSize);
			// String colDef = TEMPLATE_COLUMN_DEFINITION.replace("%%Size%%", "" + absSize);
			sb.append(colDef).append("\r\n");
		}
		return sb.toString();
	}

	/**
	 * Liefert die Breite einer Spalte beim Drucken in mm
	 */
	private static int getPrintedRowWidth(final ColumnInfo col, final ReportConfiguration conf) {
		if (col == null) {
			return 0;
		}

		boolean isOptimized = false;
		double dColSize;
		if (col.optimizedPrintWidth > 0) {
			dColSize = col.optimizedPrintWidth;
			isOptimized = true;
		} else {
			dColSize = col.width;
		}

		dColSize *= conf.PIXEL_SIZE_MM;
		dColSize += conf.CELL_PADDING_MM;

		if (col.column == null || isOptimized) {
			// WIS: empty field oder optimizeFieldWidth schon geschehen
			// wir haben die Breite schon so berechnet, dass es stimmt
			// darf also nicht noch mal umgerechnet werden!
			return (int) (dColSize + 0.5);
		}

		// WIS: Umrechnung DPI
		final double dpiFactor = (double) conf.DPI / 96;

		// Umrechnung Schriftgröße
		double fontFactor = 1;
		if (!conf.standardFont.equals(conf.guiFont)) {
			fontFactor = conf.standardFont.getSize2D() / conf.guiFont.getSize2D();
		}

		final int iColSize = (int) (dColSize * dpiFactor * fontFactor);

		return iColSize;
	}

	/**
	 * Liefert die gesamte Breite der Spalten beim Drucken in mm
	 */
	private static int getPrintedRowWidth(final List<ColumnInfo> cols, final ReportConfiguration conf) {
		if (cols == null || cols.size() == 0) {
			return 0;
		}

		int sumSize = 0;
		for (final ColumnInfo ci : cols) {
			sumSize += getPrintedRowWidth(ci, conf);
		}
		return sumSize;
	}

	/**
	 * Suchkriterien für das Feld übernehmen
	 *
	 * @param searchTable
	 * @param ci
	 * @return
	 * @throws ReportCreationException
	 */
	private static String getSearchCriteria(final SortedList<Row> dataList, final ColumnInfo ci) throws ReportCreationException {
		String searchCriteria, searchCriterias = "", searchCriteriaText;
//		if (dataList != null && ci.column != null) {
//			for (int iRow = 0; iRow < dataList.size(); iRow++) {
//				final Row row = searchTable.getRow(iRow);
//				// AND / ODER ermitteln
//				searchCriteria = getTemplate("SearchCriteria");
//				final Object and = row.getValueContent(ValueDefinition.AND_FIELD_NAME);
//				final Value v = row.getValue(ci.column.getName());
//				final String value = ValueFormatter.toString(v);
//				if (value == null || value.length() == 0) {
//					continue;
//				}
//				searchCriteriaText = (iRow == 0 ? "" : (and == Boolean.TRUE ? "& " : "| "));
//				searchCriteriaText += value;
//				searchCriteria = searchCriteria.replace("%%CriteriaText%%", StringTools.replaceSpecialChars(searchCriteriaText));
//				searchCriterias += searchCriteria + "\r\n";
//			}
//		}
//
		return searchCriterias;
	}

	/**
	 * Suchkriterien des Feldes als Text zurückliefern
	 *
	 * @param searchTable
	 * @param ci
	 * @return einen String mit den Suchkriterien
	 */
	private static String getSearchCriteriaValue(final SortedList<Row> sortedDataList, final ColumnInfo ci) {
		String searchCriteria, searchCriterias = "", searchCriteriaText;
//		if (searchTable != null && ci.column != null) {
//			for (int iRow = 0; iRow < searchTable.getRowCount(); iRow++) {
//				final Row row = searchTable.getRow(iRow);
//				// AND / ODER ermitteln
//				final Object and = row.getValueContent(ValueDefinition.AND_FIELD_NAME);
//				final Value v = row.getValue(ci.column.getName());
//				final String value = ValueFormatter.toString(v);
//				if (value == null || value.length() == 0) {
//					continue;
//				}
//				searchCriteriaText = (iRow == 0 ? "" : (and == Boolean.TRUE ? "& " : "| "));
//				searchCriteriaText += value;
//				searchCriteria = StringTools.replaceSpecialChars(searchCriteriaText);
//				searchCriterias += searchCriteria;
//			}
//		}

		return searchCriterias;
	}

	/**
	 * erzeugt eine textuelle Auflistung aller Suchkriterien im Format Spalte: Kriterien, ...
	 *
	 * @param searchTable
	 * @param cols
	 * @return
	 */
	private String getSearchCriteriaValues(final SortedList<Row> sortedDataList, final List<ColumnInfo> cols) {
		String toRet = "";

		for (final ColumnInfo ci : cols) {
			if (ci != null && ci.column != null) {
				final String colName = translationService.translate(ci.column.getLabel(), null).replaceAll("[^a-zA-Z0-9]", "");
				final String scValues = getSearchCriteriaValue(sortedDataList, ci);
				if (scValues != null && scValues != "") {
					if (toRet != "") {
						toRet += ",\n";
					}
					toRet += colName + ": " + scValues;
				}
			}
		}

		return toRet;
	}

	/**
	 * Liefert die XSL-Tabellenüberschrift in Form eines Strings
	 *
	 * @param searchTable
	 * @param cols
	 * @param conf
	 * @return
	 * @throws ReportCreationException
	 */
	private String getTableTitle(final SortedList<Row> sortedDataList, final List<ColumnInfo> cols, final ReportConfiguration conf)
			throws ReportCreationException {
		String toRet = "";
		String text;
		boolean isFirstRow = true;
		for (int iCol = 0; iCol < cols.size(); iCol++) {
			final ColumnInfo ci = cols.get(iCol);
			final boolean lastColumn = (iCol == cols.size() - 1);
			text = ci.column == null ? "" : translationService.translate(ci.column.getLabel(), null);
			boolean isNumber = false;
			if (ci.column.getType() == DataType.DOUBLE || ci.column.getType() == DataType.INTEGER) {
				isNumber = true;
			}
			text = getTemplate("TitleColumn").replace("%%ColumnText%%", text);
			text = text.replace("%%TextAlign%%", isNumber ? "right" : "left");
			text = text.replace("%%BorderLeftStyle%%", isFirstRow ? "solid" : "none");
			text = text.replace("%%BorderRightStyle%%", lastColumn ? "solid" : "none");
			text = text.replace("%%SearchCriterias%%", conf.hideSearchCriterias ? "" : getSearchCriteria(sortedDataList, ci));
			toRet += text + "\r\n";
			isFirstRow = false;
		}

		return toRet;
	}

	/**
	 * Holt das XSL-Template aus der Variable oder lädt es falls erforderlich
	 *
	 * @param templateName
	 * @return den Template-Text
	 * @throws ReportCreationException
	 */
	private static String getTemplate(final Orientation ori) throws ReportCreationException {
		final String templateName = "PDFReportIndex_" + ((ori == Orientation.LANDSCAPE) ? "Wide" : "Portrait");
		final String template = getTemplate(templateName);
		// hier könnte man auch die Lokalisierung u.a. einbauen, das nützt uns aber nichts bei XSL-Templates, die individuell aus einer Datei geladen werden
		return template;
	}

	/**
	 * Holt das Template aus der Variable oder lädt es falls erforderlich
	 *
	 * @param templateName
	 * @return den Template-Text
	 * @throws ReportCreationException
	 */
	private static String getTemplate(final String templateName) throws ReportCreationException {
		if (templates.containsKey(templateName)) {
			return templates.get(templateName);
		} else {
			// wenn es das Template noch nicht gibt, lade es
			try {
				final String template = loadTemplate(null, templateName);
				templates.put(templateName, template);
				return template;
			} catch (final Exception ex) {
				throw new ReportCreationException(Cause.XSL_TEMPLATES_ERROR, "XSL Templates could not be loaded", ex);
			}
		}
	}

	/**
	 * interpoliert bestimmte Daten in das XML-Template, indem es die Platzhalter ersetzt
	 *
	 * @param xslData
	 * @param searchTable
	 * @param a
	 * @param cols
	 * @param conf
	 * @return
	 * @throws ReportCreationException
	 */
	public String interpolateXslData(String xmlRootTag, String reportName, SortedList<Row> sortedDataList, List<ColumnInfo> cols, ReportConfiguration conf,
			Path path, String xslData) throws ReportCreationException {
		final String columnDefinition = getColumnDefinition(cols, conf);
		final String tableTitle = getTableTitle(sortedDataList, cols, conf);
		final String cellDefinition = getCellDefinition(cols, false);
		final String sumCellDefinition = getCellDefinition(cols, true);
		final String encoding = "UTF-8";
		final String reportPath = path.toFile().getAbsolutePath();

		xslData = xslData.replace("%%ReportPath%%", reportPath);
		xslData = xslData.replace("%%Encoding%%", "" + encoding);
		xslData = xslData.replace("%%PageHeight%%", "" + conf.getPageWidth());
		xslData = xslData.replace("%%PageWidth%%", "" + conf.getPageWidth());
		xslData = xslData.replace("%%ColumnDefinition%%", columnDefinition);
		xslData = xslData.replace("%%CellDefinition%%", cellDefinition);
		xslData = xslData.replace("%%SumCellDefinition%%", sumCellDefinition);
		xslData = xslData.replace("%%TableTitle%%", tableTitle);

		// #22149: i.a. wollen wir die Suchkriterien auch nicht mit drucken!
		if (!conf.hideSearchCriterias) {
			for (final ColumnInfo ci : cols) {
				if (ci != null && ci.column != null) {
					final String colName = translationService.translate(ci.column.getLabel(), null);
					xslData = xslData.replace("%%SearchCriteria#" + colName + "%%", getSearchCriteria(sortedDataList, ci));
					xslData = xslData.replace("%%SearchCriteriaValue#" + colName + "%%", getSearchCriteriaValue(sortedDataList, ci));
				}
			}
			xslData = xslData.replace("%%SearchCriteriaValues%%", getSearchCriteriaValues(sortedDataList, cols));
		}

		xslData = xslData.replace("%%XMLMainTag%%", xmlRootTag);
		xslData = xslData.replace("%%FormTitle%%", reportName);
		xslData = xslData.replace("%%ColumnCount%%", "" + cols.size());
		xslData = xslData.replace("%%SumVisibility%%", "false");
		// #22149: ohne die vorige Ersetzung sollte das auch nicht auftreten
		if (!conf.hideSearchCriterias) {
			xslData = xslData.replace("%%FontSizeCriteria%%", conf.getProp("FontSizeCriteria", "8"));
		}
		xslData = xslData.replace("%%FontSizeCell%%", conf.getProp("FontSizeCell", "8"));
		xslData = xslData.replace("%%FontFamily%%", conf.getProp("FontFamily", "Humanist521 BT, Arial, ArialUni, Helvetica, Tahoma"));

		xslData = localize(xslData);

		return xslData;
	}

	/**
	 * Lädt eine Template Datei - zuerst aus dem /XSL-Templates Unterordner der Applikation - wenn nicht gefunden dann wird eine interne Kopie geladen
	 */
	protected static String loadTemplate(Path a, String name) throws IOException {
		String toRet = null;
		if (a != null) {
			toRet = IOUtil.open("/XSL-Templates/" + name + ".xsl");
		}
		if (toRet == null) {
			toRet = IOUtil.open(TableXSLCreator.class.getResourceAsStream(name + ".xsl"));
		}
		return toRet;
	}

	/**
	 * Lokalisiert statische Werte im Report
	 */
	public String localize(String text) {
		text = text.replace("%%tAddress.Phone%%", translationService.translate("@Phone", "Tel."));
		text = text.replace("%%tAddress.Fax%%", translationService.translate("@tAddress.Fax", "Fax"));
		text = text.replace("%%tAddress.Page%%", translationService.translate("@tAddress.Page", "Seite"));
		text = text.replace("%%tAddress.Index%%", translationService.translate("@tAddress.Index", "Index"));
		text = text.replace("%%tDate%%", translationService.translate("@tDate", "Datum"));
		return text;
	}
}