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
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;

import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;

public class MinovaDisplayConfiguration extends AbstractRegistryConfiguration {

	private List<Column> columns;
	private Locale locale;
	private TranslationService translationService;
	private Form form;
	private Map<String, aero.minova.rcp.form.model.xsd.Column> formColumns;

	public MinovaDisplayConfiguration(List<Column> columns, TranslationService translationService, Form form) {
		this.columns = columns;
		this.translationService = translationService;
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
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE);
		configureCells(configRegistry);
	}

	private void configureCells(IConfigRegistry configRegistry) {
		int i = 0;
		for (Column column : columns) {

			if (column.getType().equals(DataType.BOOLEAN)) {
				// configureBooleanCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT)
					&& formColumns.get(column.getName()).getShortDate() != null) {
				configureShortDateCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT)
					&& formColumns.get(column.getName()).getShortTime() != null) {
				configureShortTimeCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.INSTANT)
					&& formColumns.get(column.getName()).getDateTime() != null) {
				configureDateTimeCell(configRegistry, i++);
			} else if (column.getType().equals(DataType.DOUBLE) || column.getType().equals(DataType.INTEGER)) {
				configureNumberCell(configRegistry, i++);
			} else {
				configureTextCell(configRegistry, i++);
			}
		}
	}

	private void configureDateTimeCell(IConfigRegistry configRegistry, int i) {
		// TODO Auto-generated method stub

	}

	private void configureShortTimeCell(IConfigRegistry configRegistry, int i) {
		// TODO Auto-generated method stub

	}

	private void configureShortDateCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		ShortDateDisplayConverter shortDateDisplayConverter = new ShortDateDisplayConverter(locale);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, shortDateDisplayConverter,
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

	private void configureBooleanCell(IConfigRegistry configRegistry, int columnIndex) {
		// _
		// visuelle anpassung [x] oder [_]
//		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new CheckBoxPainter(),
//				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

	private void configureNumberCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.RIGHT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		NumberFormat nf = NumberFormat.getInstance();
		DefaultDoubleDisplayConverter defaultDoubleDisplayConverter = new DefaultDoubleDisplayConverter(true);
		defaultDoubleDisplayConverter.setNumberFormat(nf);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultDoubleDisplayConverter,
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureInstantCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

	}

	private void configureTextCell(IConfigRegistry configRegistry, int columnIndex) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

	private void registerDoubleEditor(IConfigRegistry configRegistry, int columnIndex) {
		// register a TextCellEditor for column two that commits on key up/down
		// moves the selection after commit by enter
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new TextCellEditor(true, true),
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		// configure to open the adjacent editor after commit
		// default behavior - enter - down, tab - right, shift+tab - left, arrows -
		// arrow direction
		configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, Boolean.TRUE,
				DisplayMode.EDIT, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		// TODO get the number format of the user
		NumberFormat nf = NumberFormat.getInstance();
//		NumberFormat nf = NumberFormat.getInstance(new Locale("en", "EN"));
		DefaultDoubleDisplayConverter defaultDoubleDisplayConverter = new DefaultDoubleDisplayConverter(true);
		defaultDoubleDisplayConverter.setNumberFormat(nf);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, defaultDoubleDisplayConverter,
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

//	private void registerGenderEditor(IConfigRegistry configRegistry, int columnIndex) {
//		ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(Arrays.asList("Done", "Not Done"));
//		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
//				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
//
//		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(),
//				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
//	}

	private void registerBooleanEditor(IConfigRegistry configRegistry, int columnIndex) {

		// Das hier würde einen kundenspezifischen Checkbox editor nutzen
		// configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new
		// ExampleCheckBoxPainter(),
		// DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX +
		// columnIndex);

		// The CheckBoxCellEditor can also be visualized like a check button

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new CheckBoxPainter(),
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		// using a CheckBoxCellEditor also needs a Boolean conversion to work
		// correctly
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
				new DefaultBooleanDisplayConverter(), DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
	}

//	private void registerDateEditor(IConfigRegistry configRegistry, int columnIndex) {
//		DateCellEditor dateCellEditor = new DateCellEditor();
//		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, dateCellEditor, DisplayMode.EDIT,
//				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
//
//		// using a DateCellEditor also needs a Date conversion to work correctly
//		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
//				new DefaultLocalDateDisplayConverter(), DisplayMode.NORMAL,
//				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
//	}

}
