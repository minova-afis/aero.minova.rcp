package aero.minova.rcp.rcp.widgets;

import org.eclipse.nebula.widgets.nattable.edit.CheckBoxStateEnum;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TreeCheckBoxPainter;
import org.eclipse.swt.graphics.Image;

import aero.minova.rcp.rcp.util.ImageUtil;

/**
 * Verwendet den {@link TreeCheckBoxPainter} als "normalen" {@link CheckBoxPainter},<br>
 * aber mit TriState-Funktion.
 *
 * @author wild
 * @since 11.0.0
 */
public class TriStateCheckBoxPainter extends TreeCheckBoxPainter {
	public TriStateCheckBoxPainter() {
		super(ImageUtil.getImageDefault("checkboxon.png"), ImageUtil.getImageDefault("checkboxnull.png"), ImageUtil.getImageDefault("checkboxoff.png"));
	}

	public TriStateCheckBoxPainter(Image checkedImg, Image semicheckedImage, Image uncheckedImg) {
		super(checkedImg, semicheckedImage, uncheckedImg);
	}

	@Override
	protected CheckBoxStateEnum getCheckBoxState(ILayerCell cell) {
		Object dataValue = cell.getDataValue();
		if (dataValue == null || "".equals(dataValue)) {
			return CheckBoxStateEnum.SEMICHECKED;
		} else {
			synchronized (dataValue) {
				if (dataValue == null || !(dataValue instanceof Boolean)) {
					if (dataValue != null) {
						System.out.println("nicht-Boolean-Wert: " + dataValue.toString());
					}
					return CheckBoxStateEnum.SEMICHECKED;
				}

				return ((Boolean) dataValue) ? CheckBoxStateEnum.CHECKED : CheckBoxStateEnum.UNCHECKED;
			}
		}
	}
}