package aero.minova.rcp.rcp.nattable;

import java.text.MessageFormat;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;

public class RequiredValuePainter extends TextPainter {

	@Override
	protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
		try {
			Object dataValue = cell.getDataValue();
			if (dataValue == null || dataValue.equals("")) {
				// #FCD267 Pflichtfeld, RGB = 252,210,103
				return GUIHelper.getColor(252, 210, 103);
			}
		} catch (Exception e) {
			System.out.println(MessageFormat.format("getBackgroundColour meldet: {0}", e.getMessage()));
		}
		return super.getBackgroundColour(cell, configRegistry);
	}
}
