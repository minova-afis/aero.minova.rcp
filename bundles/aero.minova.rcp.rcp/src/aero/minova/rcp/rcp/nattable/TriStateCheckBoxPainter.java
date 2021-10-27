package aero.minova.rcp.rcp.nattable;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.nebula.widgets.nattable.edit.CheckBoxStateEnum;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TreeCheckBoxPainter;
import org.eclipse.swt.graphics.Image;

import aero.minova.rcp.dataservice.ImageUtil;

/**
 * Verwendet den {@link TreeCheckBoxPainter} als "normalen"
 * {@link CheckBoxPainter},<br>
 * aber mit TriState-Funktion.
 *
 * @author wild
 * @since 11.0.0
 */
public class TriStateCheckBoxPainter extends TreeCheckBoxPainter {
	static ImageRegistry imageRegistry;

	static {
		imageRegistry = JFaceResources.getImageRegistry();
		imageRegistry.put("checkboxon.png", ImageUtil.getImageDefault("checkboxon.png").createImage());
		imageRegistry.put("checkboxnull.png", ImageUtil.getImageDefault("checkboxnull.png").createImage());
		imageRegistry.put("checkboxoff.png", ImageUtil.getImageDefault("checkboxoff.png").createImage());
	}

	public TriStateCheckBoxPainter() {
		super(imageRegistry.get("checkboxon.png"), //
				imageRegistry.get("checkboxnull.png"), //
				imageRegistry.get("checkboxoff.png"));

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