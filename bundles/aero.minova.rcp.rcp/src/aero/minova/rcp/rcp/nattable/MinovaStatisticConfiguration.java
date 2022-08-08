package aero.minova.rcp.rcp.nattable;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;

import aero.minova.rcp.model.Column;

public class MinovaStatisticConfiguration extends MinovaColumnConfiguration {

	public MinovaStatisticConfiguration(List<Column> columns) {
		super(columns, null);
	}

	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE);
		configureCells(configRegistry);
	}

	private void configureCells(IConfigRegistry configRegistry) {
		for (int i = 0; i < columns.size(); i++) {
			configureTextCell(configRegistry, i, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX);
		}
	}

	private void configureTextCell(IConfigRegistry configRegistry, int columnIndex, String configLabel) {
		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL, configLabel + columnIndex);
	}
}
