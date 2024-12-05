package aero.minova.rcp.rcp.handlers;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.export.command.ExportCommand;
import org.eclipse.nebula.widgets.nattable.extension.poi.PoiExcelExporter;
import org.eclipse.nebula.widgets.nattable.extension.poi.XSSFExcelExporter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.rcp.parts.WFCIndexPart;
import aero.minova.rcp.util.IOUtil;
import aero.minova.rcp.util.Tools;
import ca.odell.glazedlists.SortedList;

public class ExportIndexHandler {

	@Inject
	private IEventBroker broker;

	@Inject
	private TranslationService translationService;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.DATE_UTIL)
	String datePattern;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIME_UTIL)
	String timePattern;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	String timezone;

	public enum ExportTo {
		CLIPBOARD, FILE, EXCEL
	}

	public static final String COMMAND_ACTION = "aero.minova.rcp.rcp.commandparameter.exportto";

	private final StringBuffer setNatTableDatatToStringBuffer(Object wfcPart) {
		final StringBuffer csv = new StringBuffer();
		WFCIndexPart indexPart = (WFCIndexPart) wfcPart;
		SortedList<Row> sortedList = indexPart.getSortedList();
		ColumnReorderLayer columnReorderLayer = indexPart.getBodyLayerStack().getColumnReorderLayer();
		columnReorderLayer.getColumnIndexOrder();
		// Spaltennamen raussuchen
		List<String> columnHeaderList = new ArrayList<>();

		for (int i = 0; i < columnReorderLayer.getColumnCount(); i++) {
			columnHeaderList.add((String) indexPart.getColumnHeaderLayer().getDataValueByPosition(i, 0));
		}

		saveIntoCSV(sortedList, columnHeaderList, columnReorderLayer.getColumnIndexOrder(), csv, false);
		return csv;
	}

	@Execute
	public void execute(Shell shell, MPart mpart, @Named(COMMAND_ACTION) final String action) {
		final ExportTo target = ExportTo.valueOf(action);
		Object wfcPart = mpart.getObject();
		if (wfcPart instanceof WFCIndexPart indexPart) {
			NatTable natTable = indexPart.getNattable();
			StringBuffer csv;

			switch (target) {
			case CLIPBOARD:
				csv = setNatTableDatatToStringBuffer(wfcPart);
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				clip.setContents(new StringSelection(csv.toString()), null);
				broker.post(Constants.BROKER_SHOWNOTIFICATION, "msg.ExportIntoClipboardSuccess");
				break;
			case FILE:
				// Dateinamen fragen...
				String fileName = null;
				fileName = askFileName(shell, fileName);
				if (fileName == null) {
					broker.post(Constants.BROKER_SHOWNOTIFICATION, "msg.ActionAborted");
				} else {
					if (!fileName.endsWith(".csv")) {
						fileName = fileName + ".csv";
					}
					csv = setNatTableDatatToStringBuffer(wfcPart);
					// Datei speichern mit dem gegebenen Inhalt
					try {
						IOUtil.saveLoud(csv.toString(), fileName, "UTF-8");
						broker.post(Constants.BROKER_SHOWNOTIFICATION, "msg.ExportSuccess");
						// Datei direkt öffnen
						Tools.openURL(fileName);
					} catch (final Exception e) {
						broker.post(Constants.BROKER_SHOWNOTIFICATION, "msg.ExportError");
					}
				}
				break;
			case EXCEL:
				PoiExcelExporter exporter = new XSSFExcelExporter();
				exporter.setApplyVerticalTextConfiguration(true);
				exporter.setApplyBackgroundColor(false);
				natTable.doCommand(new ExportCommand(natTable.getConfigRegistry(), natTable.getShell(), false, false, exporter));
				break;
			}

		}

	}

	/**
	 * Speichert den Zeilen-Inhalt als CSV (analog der Methode in {@link Table})
	 */
	protected void saveIntoCSV(SortedList<Row> rows, List<String> cHaederList, List<Integer> columnReorderList, StringBuffer csv, boolean tabSeparated) {
		if (csv != null && rows != null && rows.iterator().hasNext()) {
			// In der Zwischenablage werden die Werte nicht mit Gänsefüßchen umschlossen
			final String brackets = tabSeparated ? "" : "\"";
			// In der Zwischenablage werden die Werte mit Tabulator getrennt
			final String separator = tabSeparated ? "\t" : ",";
			int colIndex = 0;
			for (final String d : cHaederList) {
				if (colIndex++ > 0) {
					csv.append(separator);
				}
				csv.append(brackets);

				csv.append(d);

				csv.append(brackets);
			}

			// Werte einfügen
			csv.append("\r\n");
			for (final Row r : rows) {
				colIndex = 0;
				for (final Integer d : columnReorderList) {
					if (colIndex++ > 0) {
						csv.append(separator);
					}
					csv.append(brackets);
					if (r.getValue(d) != null) {
						csv.append(r.getValue(d).getValueString(CustomLocale.getLocale(), datePattern, timePattern, timezone));
					}

					csv.append(brackets);
				}
				csv.append("\r\n");
			}
		}
	}

	/**
	 * Fragt nach einem CSV Dateinamen
	 *
	 * @param shell
	 * @param fileName
	 *            Vorbelegung mit dem Dateiname
	 * @return Dateiname oder null, wenn abgebrochen
	 */
	private String askFileName(final Shell shell, String fileName) {
		// User has selected to save a file
		final FileDialog dlg = new FileDialog(shell, SWT.SAVE);
		dlg.setText(translationService.translate("@msg.EnterFileName", null));
		dlg.setFilterExtensions(new String[] { "csv" });
		dlg.setFileName(fileName);
		return dlg.open();
	}
}
