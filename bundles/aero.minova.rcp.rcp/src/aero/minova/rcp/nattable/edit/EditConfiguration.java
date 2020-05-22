package aero.minova.rcp.nattable.edit;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultBooleanDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDateDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.CheckBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.DateCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

import aero.minova.rcp.model.person.Person.Gender;

public class EditConfiguration extends AbstractRegistryConfiguration {
	
    @Override
    public void configureRegistry(IConfigRegistry configRegistry) {
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);

        registerEditors(configRegistry);
    }

    private void registerEditors(IConfigRegistry configRegistry) {
        registerLastNameEditor(configRegistry, 1);
        registerGenderEditor(configRegistry, 2);
        registerMarriedEditor(configRegistry, 3);
        registerBirthdayEditor(configRegistry, 4);
    }

    private void registerLastNameEditor(IConfigRegistry configRegistry, int columnIndex) {
        // register a TextCellEditor for column two that commits on key up/down
        // moves the selection after commit by enter
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new TextCellEditor(true, true),
                DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

        // configure to open the adjacent editor after commit
        configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, Boolean.TRUE,
                DisplayMode.EDIT, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
    }

    private void registerGenderEditor(IConfigRegistry configRegistry, int columnIndex) {
        ComboBoxCellEditor comboBoxCellEditor = new ComboBoxCellEditor(Arrays.asList(Gender.FEMALE, Gender.MALE));
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, comboBoxCellEditor, DisplayMode.EDIT,
                ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new ComboBoxPainter(),
                DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
    }

    private void registerMarriedEditor(IConfigRegistry configRegistry, int columnIndex) {
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new CheckBoxCellEditor(),
                DisplayMode.EDIT, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

        // The CheckBoxCellEditor can also be visualized like a check button
        configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new CheckBoxPainter(),
                DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

        // using a CheckBoxCellEditor also needs a Boolean conversion to work
        // correctly
        configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
                new DefaultBooleanDisplayConverter(), DisplayMode.NORMAL,
                ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
    }

    private void registerBirthdayEditor(IConfigRegistry configRegistry, int columnIndex) {
        DateCellEditor dateCellEditor = new DateCellEditor();
        configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, dateCellEditor, DisplayMode.EDIT,
                ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);

        DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        String pattern = ((SimpleDateFormat) formatter).toPattern();

        // using a DateCellEditor also needs a Date conversion to work correctly
        configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER,
                new DefaultDateDisplayConverter(pattern), DisplayMode.NORMAL,
                ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + columnIndex);
    }



}
