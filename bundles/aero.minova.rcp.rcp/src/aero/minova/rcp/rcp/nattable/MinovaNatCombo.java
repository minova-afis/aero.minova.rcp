package aero.minova.rcp.rcp.nattable;

import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.widget.NatCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public class MinovaNatCombo extends NatCombo {

	public MinovaNatCombo(Composite parent, IStyle cellStyle, int style) {
		super(parent, cellStyle, style);
	}

	public MinovaNatCombo(Composite parent, IStyle cellStyle, int maxVisibleItems, int style, boolean showDropdownFilter) {
		super(parent, cellStyle, maxVisibleItems, style);
	}

	public MinovaNatCombo(Composite parent, IStyle cellStyle, int maxVisibleItems, int style, Image iconImage, boolean showDropdownFilter) {
		super(parent, cellStyle, maxVisibleItems, style, iconImage, showDropdownFilter);
	}

}
