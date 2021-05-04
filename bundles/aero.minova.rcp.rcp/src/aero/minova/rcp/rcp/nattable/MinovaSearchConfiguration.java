package aero.minova.rcp.rcp.nattable;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.EditorSelectionEnum;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;

import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;
import aero.minova.rcp.rcp.widgets.BooleanCheckBoxPainter;
import aero.minova.rcp.rcp.widgets.TriStateCheckBoxCellEditor;
import aero.minova.rcp.rcp.widgets.TriStateCheckBoxPainter;

public class MinovaSearchConfiguration extends AbstractRegistryConfiguration {

	private List<Column> columns;
	private Locale locale;
	private Form form;
	private Map<String, aero.minova.rcp.form.model.xsd.Column> formColumns;

	public MinovaSearchConfiguration(List<Column> columns, TranslationService translationService, Form form) {
		this.columns = columns;
		this.form = form;
		initFormFields();
	}

	public void initFormFields() {
		formColumns = new HashMap<>();
		List<aero.minova.rcp.form.model.xsd.Column> column = form.getIndexView().getColumn();
		for (aero.minova.rcp.form.model.xsd.Column column2 : column) {
			formColumns.put(column2.getName(), column2);
		}
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);
		configureCells(configRegistry);
	}

	private void configureCells(IConfigRegistry configRegistry) {

		// Verundungsspalte konfigurieren
		configureBooleanCell(configRegistry, 0, false);

		int i = 1;
		for (Column column : columns.subList(1, columns.size())) {
			if (column.getType().equals(DataType.BOOLEAN)) {
				configureBooleanCell(configRegistry, i++, true);
			} else if (column.getType().equals(DataType.INSTANT) && formColumns.get(column.getName()).getShortDate() != null) {
				configureShortDateCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT) && formColumns.get(column.getName()).getShortTime() != null) {
				configureShortTimeCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT) && formColumns.get(column.getName()).getDateTime() != null) {
				configureDateTimeCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.DOUBLE)) {
				configureDoubleCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INTEGER)) {
				configureIntegerCell(configRegistry, i++);
			} else {
				configureTextCell(configRegistry, i++);
			}
		}
	}

	private void configureIntegerCell(IConfigRegistry configRegistry, int columnIndex) {
		MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
		attributeValue.setSelectionMode(EditorSelectionEnum.START);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		FilterDisplayConverter fdc = new FilterDisplayConverter(DataType.INTEGER);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, fdc, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureDateTimeCell(IConfigRegistry configRegistry, int columnIndex) {
		MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
		attributeValue.setSelectionMode(EditorSelectionEnum.START);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		IEclipsePreferences node = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");
		String string = node.get("timezone", "UTC");
		ZoneId z = ZoneId.of(string);
		FilterDisplayConverter fdc = new FilterDisplayConverter(DataType.INSTANT, locale, DateTimeType.DATETIME, z);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, fdc, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureShortTimeCell(IConfigRegistry configRegistry, int columnIndex) {
		MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
		attributeValue.setSelectionMode(EditorSelectionEnum.START);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		FilterDisplayConverter fdc = new FilterDisplayConverter(DataType.INSTANT, locale, DateTimeType.TIME);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, fdc, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureShortDateCell(IConfigRegistry configRegistry, int columnIndex) {
		MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
		attributeValue.setSelectionMode(EditorSelectionEnum.START);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		FilterDisplayConverter fdc = new FilterDisplayConverter(DataType.INSTANT, locale, DateTimeType.DATE);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, fdc, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

	private void configureBooleanCell(IConfigRegistry configRegistry, int columnIndex, Boolean tristate) {

		if (tristate == null || tristate) {
			// visuelle anpassung [x] oder [_] oder [-]
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new TriStateCheckBoxPainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

			// using a CheckBoxCellEditor also needs a Boolean conversion to work correctly
			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new BooleanDisplayConverter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new TriStateCheckBoxCellEditor(), DisplayMode.EDIT,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		} else {
			// visuelle anpassung [x] oder [_]
			configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new BooleanCheckBoxPainter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

			configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new DefaultBooleanDisplayConverter(), DisplayMode.NORMAL,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

			configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new CheckBoxCellEditor(), DisplayMode.EDIT,
					ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		}
	}

	private void configureDoubleCell(IConfigRegistry configRegistry, int columnIndex) {
		MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
		attributeValue.setSelectionMode(EditorSelectionEnum.START);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		if (locale == null) {
			locale = Locale.getDefault();
		}
		FilterDisplayConverter fdc = new FilterDisplayConverter(DataType.DOUBLE, locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, fdc, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureTextCell(IConfigRegistry configRegistry, int columnIndex) {
		MinovaTextCellEditor attributeValue = new MinovaTextCellEditor(true, true);
		attributeValue.setSelectionMode(EditorSelectionEnum.START);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, attributeValue, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		FilterDisplayConverter fdc = new FilterDisplayConverter(DataType.STRING);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, fdc, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}
}
