package aero.minova.rcp.rcp.nattable;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import aero.minova.rcp.constants.Constants;

public class RequiredTriStateCheckBoxPainter extends TriStateCheckBoxPainter {

	@Override
	public void paintCell(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
		Object dataValue = cell.getDataValue();
		if (dataValue == null || dataValue.equals("")) {
			// #FCD267 Pflichtfeld, RGB = 252,210,103
			cell.getConfigLabels().addLabelOnTop(Constants.REQUIRED_CELL_LABEL);
		}
		super.paintCell(cell, gc, bounds, configRegistry);
	}

}
