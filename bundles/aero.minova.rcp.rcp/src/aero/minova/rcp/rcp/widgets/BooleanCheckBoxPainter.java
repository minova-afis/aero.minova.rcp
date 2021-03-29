package aero.minova.rcp.rcp.widgets;

import org.eclipse.nebula.widgets.nattable.painter.cell.CheckBoxPainter;

import aero.minova.rcp.rcp.util.ImageUtil;

public class BooleanCheckBoxPainter extends CheckBoxPainter {

	public BooleanCheckBoxPainter() {
		super(ImageUtil.getImageDefault("checkboxon.png"), //$NON-NLS-1$
				ImageUtil.getImageDefault("checkboxoff.png") //$NON-NLS-1$
		);
	}

}
