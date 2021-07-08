package aero.minova.rcp.rcp.nattable;

import java.text.MessageFormat;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.ComboBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class RequiredLookupPainter extends ComboBoxPainter {

	public RequiredLookupPainter() {
		// Die einzelnen Painter werden mit paintBg = false erstellt, damit wir die Farbe hier bestimmen können
		setWrappedPainter(new CellPainterDecorator(new TextPainter(false, false), CellEdgeEnum.RIGHT, 2, new ImagePainter(GUIHelper.getImage("down_2"), false),
				true, false));
	}

	@Override
	// Die Methode ist aus BackgroundPainter übernommen
	public void paintCell(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
		Color backgroundColor = getBackgroundColour(cell, configRegistry);
		if (backgroundColor != null) {
			Color originalBackground = gc.getBackground();

			gc.setBackground(backgroundColor);
			gc.fillRectangle(bounds);

			gc.setBackground(originalBackground);
		}

		super.paintCell(cell, gc, bounds, configRegistry);
	}

	// Wie in RequiredValuePainter
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
		return null;
	}
}
