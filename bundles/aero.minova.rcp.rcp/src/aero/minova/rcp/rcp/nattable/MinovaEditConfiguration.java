package aero.minova.rcp.rcp.nattable;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultIntegerDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.EditorSelectionEnum;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;

import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;

public class MinovaEditConfiguration extends AbstractRegistryConfiguration {

	private List<Column> columns;
	private Locale locale;
	private Form form;
	private Map<String, aero.minova.rcp.form.model.xsd.Column> formColumns;

	public MinovaEditConfiguration(List<Column> columns, TranslationService translationService, Form form) {
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
		int i = 0;
		for (Column column : columns) {

			if (column.getType().equals(DataType.BOOLEAN)) {
				configureBooleanCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT)
					&& formColumns.get(column.getName()).getShortDate() != null) {
				configureShortDateCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT)
					&& formColumns.get(column.getName()).getShortTime() != null) {
				configureShortTimeCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT)
					&& formColumns.get(column.getName()).getDateTime() != null) {
				configureDateTimeCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.DOUBLE)) {
				configureDoubleCell(configRegistry, i++, formColumns.get(column.getName()).getNumber().getDecimals());
			} else if (column.getType().equals(DataType.INTEGER)) {
				configureIntegerCell(configRegistry, i++);
			} else {
				configureTextCell(configRegistry, i++);
			}
		}
	}

	private void configureIntegerCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		NumberFormat nf = NumberFormat.getInstance();
		DefaultIntegerDisplayConverter defaultIntegerDisplayConverter = new DefaultIntegerDisplayConverter(true);
		defaultIntegerDisplayConverter.setNumberFormat(nf);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultIntegerDisplayConverter,
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureDateTimeCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		ShortDateTimeDisplayConverter shortDateTimeDisplayConverter = new ShortDateTimeDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortDateTimeDisplayConverter,
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureShortTimeCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		ShortTimeDisplayConverter shortTimeDisplayConverter = new ShortTimeDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortTimeDisplayConverter,
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureShortDateCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
		if (locale == null) {
			locale = Locale.getDefault();
		}
		ShortDateDisplayConverter shortDateDisplayConverter = new ShortDateDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortDateDisplayConverter,
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

	private void configureBooleanCell(IConfigRegistry configRegistry, int columnIndex) {
		// visuelle anpassung [x] oder [_]
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new CheckBoxPainter(),
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		// using a CheckBoxCellEditor also needs a Boolean conversion to work
		// correctly
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
				new DefaultBooleanDisplayConverter(), DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new CheckBoxCellEditor(),
				DisplayMode.EDIT, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureDoubleCell(IConfigRegistry configRegistry, int columnIndex, int decimals) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		if (locale == null) {
			locale = Locale.getDefault();
		}
		NumberFormat numberFormat = NumberFormat.getInstance(locale);
		numberFormat.setMinimumFractionDigits(decimals);
		numberFormat.setMaximumFractionDigits(decimals);

		DefaultDoubleDisplayConverter defaultDoubleDisplayConverter = new DefaultDoubleDisplayConverter(true);
		defaultDoubleDisplayConverter.setNumberFormat(numberFormat);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultDoubleDisplayConverter,
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

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
	}
}
