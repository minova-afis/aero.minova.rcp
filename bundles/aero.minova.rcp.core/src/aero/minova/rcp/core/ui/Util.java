package aero.minova.rcp.core.ui;

import org.eclipse.swt.widgets.Widget;

public class Util {
	private Util() {}

	public static boolean isAvailable(Widget widget) {
		return widget != null && !widget.isDisposed();
	}
}
