package aero.minova.rcp.rcp.nattable;

import static org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes.CELL_PAINTER;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultIntegerDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.EditorSelectionEnum;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.PaddingDecorator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Grid;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.rcp.widgets.TriStateCheckBoxCellEditor;
import aero.minova.rcp.rcp.widgets.TriStateCheckBoxPainter;

public class MinovaGridConfiguration extends AbstractRegistryConfiguration {

	private List<Column> columns;
	private Locale locale;
	private Grid grid;
	private Map<String, aero.minova.rcp.form.model.xsd.Field> gridFields;
	private IDataService dataService;

	public MinovaGridConfiguration(List<Column> columns, Grid grid, IDataService dataService) {
		this.columns = columns;
		this.grid = grid;
		this.dataService = dataService;
		initGridFields();
	}

	public void initGridFields() {
		gridFields = new HashMap<>();
		for (aero.minova.rcp.form.model.xsd.Field field : grid.getField()) {
			gridFields.put(field.getName(), field);
		}
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);

		// RequiredValue Style
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(252, 210, 103));
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, Constants.REQUIRED_CELL_LABEL);

		configureCells(configRegistry);
	}

	public List<Integer> getHiddenColumns() {
		List<Integer> hiddenCols = new ArrayList<>();
		for (int i = 0; i < grid.getField().size(); i++) {
			Field f = grid.getField().get(i);
			if (!f.isVisible()) {
				hiddenCols.add(i);
			}
		}
		return hiddenCols;
	}

	private void configureCells(IConfigRegistry configRegistry) {
		int i = 0;
		for (Column column : columns) {

			if (column.isLookup()) {
				configureLookupCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, column.isReadOnly(), column.isRequired(),
						column.getLookupTable());
			} else if (column.getType().equals(DataType.BOOLEAN)) {
				configureBooleanCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, column.isReadOnly(), column.isRequired());
			} else if (column.getType().equals(DataType.INSTANT) && gridFields.get(column.getName()).getShortDate() != null) {
				configureShortDateCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, column.isReadOnly(), column.isRequired());
			} else if (column.getType().equals(DataType.INSTANT) && gridFields.get(column.getName()).getShortTime() != null) {
				configureShortTimeCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, column.isReadOnly(), column.isRequired());
			} else if (column.getType().equals(DataType.INSTANT) && gridFields.get(column.getName()).getDateTime() != null) {
				configureDateTimeCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, column.isReadOnly(), column.isRequired());
			} else if (column.getType().equals(DataType.DOUBLE) || column.getType().equals(DataType.BIGDECIMAL)) {
				configureDoubleCell(configRegistry, i++, gridFields.get(column.getName()), ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, column.isReadOnly(),
						column.isRequired());
			} else if (column.getType().equals(DataType.INTEGER)) {
				configureIntegerCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, column.isReadOnly(), column.isRequired());
			} else {
				configureTextCell(configRegistry, i++, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX, column.isReadOnly(), column.isRequired());
			}
		}
	}

	private void configureLookupCell(IConfigRegistry configRegistry, int columnIndex, String configLabel, boolean isReadOnly, boolean isRequired,
			String lookupTable) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

		GridLookupContentProvider contentProvider = new GridLookupContentProvider(dataService, lookupTable);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new LookupDisplayConverter(contentProvider), DisplayMode.NORMAL,
				configLabel + columnIndex);

		if (!isReadOnly) {
			ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(contentProvider.getValues());
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.NORMAL, configLabel + columnIndex);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT, configLabel + columnIndex);
		}

		if (isRequired) {
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
			MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isRequired) {
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
			MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isRequired) {
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
			MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isRequired) {
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
			MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isRequired) {
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

		if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}
	}

	private void configureDoubleCell(IConfigRegistry configRegistry, int columnIndex, aero.minova.rcp.form.model.xsd.Field field, String configLabel,
			boolean isReadOnly, boolean isRequired) {

		int decimals = 0;
		if (field.getNumber() != null) {
			decimals = field.getNumber().getDecimals();
		}

		if (field.getPercentage() != null) {
			decimals = field.getPercentage().getDecimals();

		}
		if (field.getMoney() != null) {
			decimals = field.getMoney().getDecimals();
		}

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

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

		if (!isReadOnly) {
			MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}
	}

	private void configureTextCell(IConfigRegistry configRegistry, int columnIndex, String configLabel, boolean isReadOnly, boolean isRequired) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);

		if (!isReadOnly) {
			MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
			attributeValue.setSelectionMode(EditorSelectionEnum.START);
			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}

		if (isRequired) {
			configRegistry.registerConfigAttribute(CELL_PAINTER, new RequiredValuePainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}
	}
}