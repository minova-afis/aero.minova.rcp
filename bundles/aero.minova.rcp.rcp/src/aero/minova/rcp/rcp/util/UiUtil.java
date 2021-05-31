package aero.minova.rcp.rcp.util;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;

public class UiUtil {
	
	public static void setLocation(Shell shell, Shell parentShell) {
		Rectangle parentRect = parentShell.getBounds();
		Rectangle shellRect = shell.getBounds();
		int x = parentRect.x + (parentRect.width - shellRect.width) / 2;
		int y = parentRect.y + (parentRect.height - shellRect.height) / 2;
		shell.setLocation(x, y);
	}

}
