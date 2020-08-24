package aero.minova.rcp.core.ui;

import org.eclipse.swt.widgets.Widget;

public class Util {
	private Util() {
		// only static methods
	}

	public static boolean isAvailable(Widget widget) {
		if (widget != null && !widget.isDisposed()) {
			return true;
		}
		return false;
	}
}
