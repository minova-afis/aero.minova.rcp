package aero.minova.rcp.rcp.nattable;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultIntegerDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.EditorSelectionEnum;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.AbstractCellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.PaddingDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.summaryrow.SummaryRowLayer;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.preferencewindow.control.CustomLocale;

public class MinovaGridConfiguration extends AbstractRegistryConfiguration {

	private List<Column> columns;
	private List<MField> fields;
	private Map<String, MField> gridFields;
	private Locale locale = CustomLocale.getLocale();
	private IDataService dataService;
	private List<String> readOnlyColumns;
	private IConfigRegistry configRegistry;
	private List<GridLookupContentProvider> contentProviderList;
	private TranslationService translationService;
	private boolean gridIsReadOnly;

	public MinovaGridConfiguration(List<Column> columns, List<MField> fields, IDataService dataService, TranslationService translationService,
			boolean gridIsReadOnly) {
		this.columns = columns;
		this.fields = fields;
		this.dataService = dataService;
		this.gridIsReadOnly = gridIsReadOnly;
		this.readOnlyColumns = new ArrayList<>();
		this.contentProviderList = new ArrayList<>();
		this.translationService = translationService;
		initGridFields();
	}

	public void initGridFields() {
		gridFields = new HashMap<>();
		for (MField field : fields) {
			gridFields.put(field.getName(), field);
		}
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		this.configRegistry = configRegistry;

		// EDITABLE_RULE anpassen, damit Read-Only Spalten nicht bearbeitet werden können
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, new IEditableRule() {

			@Override
			public boolean isEditable(ILayerCell cell, IConfigRegistry configRegistry) {
				for (String s : readOnlyColumns) {
					if (cell.getConfigLabels().hasLabel(s)) {
						return false;
					}
				}

				return !cell.getConfigLabels().hasLabel("SUMMARY");
			}

			@Override
			public boolean isEditable(int columnIndex, int rowIndex) {
				return false;
			}
		});

		// Invalid Style
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_RED);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, Constants.INVALID_CELL_LABEL);

		// RequiredValue Style
		cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(252, 210, 103));
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, Constants.REQUIRED_CELL_LABEL);

		// ReadOnlyValue Style
		cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(235, 235, 235));
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, Constants.READ_ONLY_CELL_LABEL);

		configureCells(configRegistry);
	}

	public List<Integer> getHiddenColumns() {
		List<Integer> hiddenCols = new ArrayList<>();
		for (int i = 0; i < fields.size(); i++) {
			MField f = fields.get(i);
			if (!f.isVisible()) {
				hiddenCols.add(i);
			}
		}
		return hiddenCols;
	}

	private void configureCells(IConfigRegistry configRegistry) {
		int i = 0;
		for (Column column : columns) {

			configureSummary(configRegistry, i);

			boolean isReadOnly = column.isReadOnly() || gridIsReadOnly;

			if (column.isLookup()) {
				configureLookupCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, isReadOnly, column.isRequired(),
						gridFields.get(column.getName()));

			} else if (column.getType().equals(DataType.BOOLEAN)) {
				configureBooleanCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, isReadOnly, column.isRequired());

			} else if (column.getType().equals(DataType.INSTANT) && gridFields.get(column.getName()).getDateTimeType().equals(DateTimeType.DATE)) {
				configureShortDateCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, isReadOnly, column.isRequired());

			} else if (column.getType().equals(DataType.INSTANT) && gridFields.get(column.getName()).getDateTimeType().equals(DateTimeType.TIME)) {
				configureShortTimeCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, isReadOnly, column.isRequired());

			} else if (column.getType().equals(DataType.INSTANT) && gridFields.get(column.getName()).getDateTimeType().equals(DateTimeType.DATETIME)) {
				configureDateTimeCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, isReadOnly, column.isRequired());

			} else if (column.getType().equals(DataType.DOUBLE) || column.getType().equals(DataType.BIGDECIMAL)) {
				configureDoubleCell(configRegistry, i++, gridFields.get(column.getName()), ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, isReadOnly,
						column.isRequired());

			} else if (column.getType().equals(DataType.INTEGER)) {
				configureIntegerCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, isReadOnly, column.isRequired());

			} else {
				configureTextCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, isReadOnly, column.isRequired());
			}
		}
	}

	/**
	 * Default style für Summary ist "Integer Look". In configureDoubleCell() werden die Attribute überschrieben
	 * 
	 * @param configRegistry
	 * @param columnIndex
	 */
	private void configureSummary(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + columnIndex);

		NumberFormat nf = NumberFormat.getInstance();
		DefaultIntegerDisplayConverter defaultIntegerDisplayConverter = new DefaultIntegerDisplayConverter(true);
		defaultIntegerDisplayConverter.setNumberFormat(nf);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultIntegerDisplayConverter, DisplayMode.NORMAL,
				SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + columnIndex);
	}

	public void updateContentProvider() {
		contentProviderList.stream().forEach(GridLookupContentProvider::update);
	}

	public void translateLookups() {
		contentProviderList.stream().forEach(GridLookupContentProvider::translateAllLookups);
	}

	private void configureLookupCell(IConfigRegistry configRegistry, int columnIndex, String configLabel, boolean isReadOnly, boolean isRequired,
			MField mField) {

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

		GridLookupContentProvider contentProvider = new GridLookupContentProvider(dataService, (MLookupField) mField, translationService);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new LookupDisplayConverter(contentProvider), DisplayMode.NORMAL,
				configLabel + columnIndex);

		contentProviderList.add(contentProvider);

		if (!isReadOnly) {
			MinovaComboBoxCellEditor comboBoxCellEditor = new MinovaComboBoxCellEditor(contentProvider);
			comboBoxCellEditor.setFreeEdit(true);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.NORMAL, configLabel + columnIndex);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT, configLabel + columnIndex);
		}

		if (isReadOnly) {
			// Bei Read-Only Zellen soll der Pfeil des ComboBoxPainters nicht gezeigt werden, deswegen einfacher ReadOnlyValuePainter
			configRegistry.registerConfigAttribute(CELL_PAINTER, new ReadOnlyValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
			readOnlyColumns.add(configLabel + columnIndex);
		} else if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredLookupPainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		} else {
			// Der ComboBoxPainter wird in einen PaddingDecorator gewrapped, damit links padding hinzugefügt werden kann
			PaddingDecorator wrappedComboBoxCellPainter = new PaddingDecorator(new ComboBoxPainter(), 0, 0, 0, 2);
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, wrappedComboBoxCellPainter, DisplayMode.NORMAL,
					configLabel + columnIndex);
		}
	}

	private void configureIntegerCell(IConfigRegistry configRegistry, int columnIndex, String configLabel, boolean isReadOnly, boolean isRequired) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

		NumberFormat nf = NumberFormat.getInstance();
		DefaultIntegerDisplayConverter defaultIntegerDisplayConverter = new DefaultIntegerDisplayConverter(true);
		defaultIntegerDisplayConverter.setNumberFormat(nf);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultIntegerDisplayConverter, DisplayMode.NORMAL,
				configLabel + columnIndex);

		if (!isReadOnly) {
			MinovaGridTextCellEditor attributeValue = new MinovaGridTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isReadOnly) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new ReadOnlyValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
			readOnlyColumns.add(configLabel + columnIndex);
		} else if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}
	}

	private void configureDateTimeCell(IConfigRegistry configRegistry, int columnIndex, String configLabel, boolean isReadOnly, boolean isRequired) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

		if (locale == null) {
			locale = Locale.getDefault();
		}
		DateTimeDisplayConverter dateTimeDisplayConverter = new DateTimeDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, dateTimeDisplayConverter, DisplayMode.NORMAL, configLabel + columnIndex);

		if (!isReadOnly) {
			MinovaGridTextCellEditor attributeValue = new MinovaGridTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isReadOnly) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new ReadOnlyValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
			readOnlyColumns.add(configLabel + columnIndex);
		} else if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

	}

	private void configureShortTimeCell(IConfigRegistry configRegistry, int columnIndex, String configLabel, boolean isReadOnly, boolean isRequired) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

		if (locale == null) {
			locale = Locale.getDefault();
		}
		ShortTimeDisplayConverter shortTimeDisplayConverter = new ShortTimeDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortTimeDisplayConverter, DisplayMode.NORMAL,
				configLabel + columnIndex);

		if (!isReadOnly) {
			MinovaGridTextCellEditor attributeValue = new MinovaGridTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isReadOnly) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new ReadOnlyValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
			readOnlyColumns.add(configLabel + columnIndex);
		} else if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}
	}

	private void configureShortDateCell(IConfigRegistry configRegistry, int columnIndex, String configLabel, boolean isReadOnly, boolean isRequired) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

		if (locale == null) {
			locale = Locale.getDefault();
		}
		ShortDateDisplayConverter shortDateDisplayConverter = new ShortDateDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortDateDisplayConverter, DisplayMode.NORMAL,
				configLabel + columnIndex);

		if (!isReadOnly) {
			MinovaGridTextCellEditor attributeValue = new MinovaGridTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isReadOnly) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new ReadOnlyValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
			readOnlyColumns.add(configLabel + columnIndex);
		} else if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}
	}

	private void configureBooleanCell(IConfigRegistry configRegistry, int columnIndex, String configLabel, boolean isReadOnly, boolean isRequired) {
		// visuelle Anpassung [x] oder [_] oder [-]
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new TriStateCheckBoxPainter(), DisplayMode.NORMAL, configLabel + columnIndex);

		// using a CheckBoxCellEditor also needs a Boolean conversion to work correctly
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new BooleanDisplayConverter(), DisplayMode.NORMAL,
				configLabel + columnIndex);

		if (!isReadOnly) {
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new TriStateCheckBoxCellEditor(), DisplayMode.EDIT,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isReadOnly) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new ReadOnlyTriStateCheckBoxPainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
			readOnlyColumns.add(configLabel + columnIndex);
		} else if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredTriStateCheckBoxPainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}
	}

	private void configureDoubleCell(IConfigRegistry configRegistry, int columnIndex, MField mField, String configLabel, boolean isReadOnly,
			boolean isRequired) {

		int decimals = 0;
		if (mField.getDecimals() != null) {
			decimals = mField.getDecimals();
		}

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + columnIndex);

		if (locale == null) {
			locale = Locale.getDefault();
		}
		NumberFormat numberFormat = NumberFormat.getInstance(locale);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setMaximumFractionDigits(decimals);

		DefaultDoubleDisplayConverter defaultDoubleDisplayConverter = new DefaultDoubleDisplayConverter(true);
		defaultDoubleDisplayConverter.setNumberFormat(numberFormat);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultDoubleDisplayConverter, DisplayMode.NORMAL,
				configLabel + columnIndex);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultDoubleDisplayConverter, DisplayMode.NORMAL,
				SummaryRowLayer.DEFAULT_SUMMARY_COLUMN_CONFIG_LABEL_PREFIX + columnIndex);

		if (!isReadOnly) {
			MinovaGridTextCellEditor attributeValue = new MinovaGridTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isReadOnly) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new ReadOnlyValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
			readOnlyColumns.add(configLabel + columnIndex);
		} else if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}
	}

	private void configureTextCell(IConfigRegistry configRegistry, int columnIndex, String configLabel, boolean isReadOnly, boolean isRequired) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

		if (!isReadOnly) {
			MinovaGridTextCellEditor attributeValue = new MinovaGridTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isReadOnly) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new ReadOnlyValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
			readOnlyColumns.add(configLabel + columnIndex);
		} else if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}
	}

	/**
	 * Updatet die CellPainter, damit required-Felder entsprechend dargestellt werden
	 *
	 * @param columnIndex
	 * @param required
	 */
	public void setColumnRequired(int columnIndex, boolean required) {

		// Read-Only Darstellung hat Vorrang
		if (readOnlyColumns.contains(ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex)) {
			return;
		}

		// Cellpainter ermitteln
		AbstractCellPainter newPainter = null;
		Column c = columns.get(columnIndex);
		if (required) {
			if (c.getType().equals(DataType.BOOLEAN)) {
				newPainter = new RequiredTriStateCheckBoxPainter();
			} else if (c.isLookup()) {
				newPainter = new RequiredLookupPainter();
			} else {
				newPainter = new RequiredValuePainter();
			}

		} else {
			if (c.getType().equals(DataType.BOOLEAN)) {
				newPainter = new TriStateCheckBoxPainter();
			} else if (c.isLookup()) {
				newPainter = new PaddingDecorator(new ComboBoxPainter(), 0, 0, 0, 2);
			} else {
				newPainter = new PaddingDecorator(new TextPainter(), 0, 2, 0, 2);
			}

		}

		// Cellpainter registrieren
		configRegistry.registerConfigAttribute(CELL_PAINTER, newPainter, DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

	/**
	 * Updatet die CellPainter, damit readOnly-Felder entsprechend dargestellt werden <br>
	 * Außerdem wird die Spalte zu den readOnlyColumns hinzugefügt, damit die Felder nicht bearbeitet werden können
	 *
	 * @param columnIndex
	 * @param required
	 */
	public void setColumnReadOnly(int columnIndex, boolean readOnly) {

		// Zu readOnlyColumns hinzufügen oder entfernen
		String configLabel = ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex;
		if (readOnly && !readOnlyColumns.contains(configLabel)) {
			readOnlyColumns.add(configLabel);
		} else if (!readOnly) {
			readOnlyColumns.remove(configLabel);
		}

		// Cellpainter ermitteln
		AbstractCellPainter newPainter = null;
		Column c = columns.get(columnIndex);
		if (readOnly) {
			if (c.getType().equals(DataType.BOOLEAN)) {
				newPainter = new ReadOnlyTriStateCheckBoxPainter();
			} else {
				newPainter = new ReadOnlyValuePainter();
			}
		} else {
			if (c.getType().equals(DataType.BOOLEAN)) {
				newPainter = new TriStateCheckBoxPainter();
			} else if (c.isLookup()) {
				newPainter = new PaddingDecorator(new ComboBoxPainter(), 0, 0, 0, 2);
			} else {
				newPainter = new PaddingDecorator(new TextPainter(), 0, 2, 0, 2);
			}
		}

		// Cellpainter registrieren
		configRegistry.registerConfigAttribute(CELL_PAINTER, newPainter, DisplayMode.NORMAL, configLabel);

	}
}