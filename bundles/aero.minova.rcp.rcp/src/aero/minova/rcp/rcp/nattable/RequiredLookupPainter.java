package aero.minova.rcp.rcp.nattable;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.PaddingDecorator;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import aero.minova.rcp.constants.Constants;

public class RequiredLookupPainter extends ComboBoxPainter {

	public RequiredLookupPainter() {
		// Der Paintern (aus ComboBoxPainter) wird in einen PaddingDecorator gewrapped, damit links padding hinzugefügt werden kann
		setWrappedPainter(new PaddingDecorator(new CellPainterDecorator(new TextPainter(), CellEdgeEnum.RIGHT, new ImagePainter(GUIHelper.getImage("down_2"))),
				0, 0, 0, 2));
	}

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
