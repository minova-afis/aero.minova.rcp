package aero.minova.rcp.rcp.nattable;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import aero.minova.rcp.constants.Constants;

public class ReadOnlyTriStateCheckBoxPainter extends TriStateCheckBoxPainter {

	@Override
	public void paintCell(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
		cell.getConfigLabels().addLabelOnTop(Constants.READ_ONLY_CELL_LABEL);
		super.paintCell(cell, gc, bounds, configRegistry);
	}

}
