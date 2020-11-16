package aero.minova.rcp.rcp.nattable;

import java.text.NumberFormat;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.DataType;

public class MinovaEditConfiguration extends AbstractRegistryConfiguration {

	private List<Column> columns;

	public MinovaEditConfiguration(List<Column> columns) {
		this.columns = columns;
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);
		registerEditors(configRegistry);
	}

	private void registerEditors(IConfigRegistry configRegistry) {
		int i = 0;
		for (Column column : columns) {
			
			if (column.getType().equals(DataType.BOOLEAN)) {
				registerBooleanEditor(configRegistry, i++);
			} else if (column.getType().equals(DataType.DOUBLE)) {
				// auch ein text editor aber mit double konvertierung
				registerDoubleEditor(configRegistry, i++);
			}
			else {
				registerTextEditor(configRegistry, i++);
			}
		}
	}

	private void registerTextEditor(IConfigRegistry configRegistry, int columnIndex) {
		// register a TextCellEditor for column two that commits on key up/down
		// moves the selection after commit by enter
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new TextCellEditor(true, true),
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

		// configure to open the adjacent editor after commit
		// default behavior - enter - down, tab - right, shift+tab - left, arrows -
		// arrow direction
		configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, Boolean.TRUE,
				DisplayMode.EDIT, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
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
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
				defaultDoubleDisplayConverter, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
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

		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new CheckBoxCellEditor(),
				DisplayMode.EDIT, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

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
