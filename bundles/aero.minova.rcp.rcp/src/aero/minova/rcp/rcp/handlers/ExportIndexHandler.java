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
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Row;
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

	@Execute
	public void execute(Shell shell, MPart mpart, @Named(COMMAND_ACTION) final String action) {
		final ExportTo target = ExportTo.valueOf(action);
		Object wfcPart = mpart.getObject();
		if (wfcPart instanceof WFCIndexPart indexPart) {
			NatTable natTable = indexPart.getNattable();

			switch (target) {
			case CLIPBOARD:
				String csv = getNattableAsCSVString(indexPart);
				Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
				clip.setContents(new StringSelection(csv), null);
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
					csv = getNattableAsCSVString(indexPart);
					// Datei speichern mit dem gegebenen Inhalt
					try {
						IOUtil.saveLoud(csv, fileName, "UTF-8");
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

	private final String getNattableAsCSVString(WFCIndexPart indexPart) {

		// Spaltennamen raussuchen
		ColumnReorderLayer columnReorderLayer = indexPart.getBodyLayerStack().getColumnReorderLayer();
		List<String> columnHeaderList = new ArrayList<>();
		for (int i = 0; i < columnReorderLayer.getColumnCount(); i++) {
			columnHeaderList.add(((DefaultColumnHeaderDataProvider) indexPart.getColumnHeaderDataLayer().getDataProvider()).getColumnHeaderLabel(i));
		}

		return createCSVString(indexPart.getSortedList(), columnHeaderList, columnReorderLayer.getColumnIndexOrder());
	}

	/**
	 * Speichert den Zeilen-Inhalt als CSV
	 */
	protected String createCSVString(SortedList<Row> rows, List<String> cHaederList, List<Integer> columnReorderList) {
		StringBuilder csv = new StringBuilder();

		String brackets = "\"";
		String separator = ",";

		int colIndex = 0;

		// Header einfügen
		for (Integer d : columnReorderList) {
			if (colIndex++ > 0) {
				csv.append(separator);
			}

			csv.append(brackets);
			csv.append(cHaederList.get(d));
			csv.append(brackets);
		}
		csv.append("\r\n");

		// Werte einfügen
		for (Row r : rows) {
			colIndex = 0;

			for (Integer d : columnReorderList) {
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

		return csv.toString();
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
