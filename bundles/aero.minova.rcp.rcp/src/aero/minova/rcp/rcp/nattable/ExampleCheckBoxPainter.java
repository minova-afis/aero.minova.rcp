package aero.minova.rcp.rcp.nattable;

import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

/**
 * Beispiel für einen eigenen Checkbox painter der Unicode malt. Könnte man
 * alternativ zum Standard CheckBoxPainter verwenden, wenn man hard-gemalten
 * Checkboxen ersetzen will.
 * 
 * @author Lars
 *
 */
public class ExampleCheckBoxPainter extends CheckBoxPainter {


	@Override
	public void paintCell(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
		int x = bounds.x + (bounds.width / 2) - (16 / 2);
		if (isChecked(cell, configRegistry)) {
			gc.drawString("\u00d7", x, bounds.y, true);
		} else {
			gc.drawString("▼", x, bounds.y, true);
		}
	}

    @Override
	public void paintIconImage(GC gc, Rectangle rectangle, int yOffset, boolean checked) {
        // Center image

    }

	@Override
	public int getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return 24;
	}

	@Override
	public int getPreferredHeight(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return 24;
	}



    @Override
	protected boolean isChecked(ILayerCell cell, IConfigRegistry configRegistry) {
        return convertDataType(cell, configRegistry).booleanValue();
    }

    @Override
	protected Boolean convertDataType(ILayerCell cell, IConfigRegistry configRegistry) {
        if (cell.getDataValue() instanceof Boolean) {
            return (Boolean) cell.getDataValue();
        }
        IDisplayConverter displayConverter = configRegistry.getConfigAttribute(
                CellConfigAttributes.DISPLAY_CONVERTER,
                cell.getDisplayMode(),
                cell.getConfigLabels().getLabels());
        Boolean convertedValue = null;
        if (displayConverter != null) {
            convertedValue =
                    (Boolean) displayConverter.canonicalToDisplayValue(
                            cell, configRegistry, cell.getDataValue());
        }
        if (convertedValue == null) {
            convertedValue = Boolean.FALSE;
        }
        return convertedValue;
    }
}
