package aero.minova.rcp.rcp.widgets;

import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;

import aero.minova.rcp.dataservice.ImageUtil;

public class BooleanCheckBoxPainter extends CheckBoxPainter {

	public BooleanCheckBoxPainter() {
		super(ImageUtil.getImageDefault("checkboxon.png").createImage(), //$NON-NLS-1$
				ImageUtil.getImageDefault("checkboxoff.png").createImage() //$NON-NLS-1$
		);
	}

}
