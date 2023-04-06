package aero.minova.rcp.css;

import org.eclipse.e4.ui.css.core.dom.properties.ICSSPropertyHandler;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.properties.AbstractCSSPropertySWTHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;

public class MinovaCTabFolderPropertyHandler extends AbstractCSSPropertySWTHandler implements ICSSPropertyHandler {

	@Override
	protected void applyCSSProperty(Control control, String property, CSSValue value, String pseudo, CSSEngine engine) throws Exception {
		if (!(control instanceof CTabFolder)) {
			return;
		}

		if ((value.getCssValueType() == CSSValue.CSS_PRIMITIVE_VALUE) && (((CSSPrimitiveValue) value).getPrimitiveType() == CSSPrimitiveValue.CSS_PX)) {
			int height = (int) ((CSSPrimitiveValue) value).getFloatValue(CSSPrimitiveValue.CSS_PX);

			// Skalierung unter Windows beachten -> Höhe entsprechend vergrößern
			if ("win32".equals(SWT.getPlatform())) {
				height = scaleWindows(height);
			}

			((CTabFolder) control).setTabHeight(height);
		}
	}

	private int scaleWindows(int height) {
		int dpi = Display.getCurrent().getDPI().x;

		if (dpi == 144 || dpi == 168) {
			switch (height) {
			case 24:
				height = 32;
				break;
			case 32:
				height = 40;
				break;
			case 40:
				height = 54;
				break;
			case 54:
				height = 70;
				break;
			default: // Darf nicht vorkommen
				break;
			}
		} else if (dpi >= 192) {
			switch (height) {
			case 24:
				height = 40;
				break;
			case 32:
				height = 54;
				break;
			case 40, 54:
				height = 70;
				break;
			default: // Darf nicht vorkommen
				break;
			}
		}
		return height;
	}

	@Override
	protected String retrieveCSSProperty(Control control, String property, String pseudo, CSSEngine engine) throws Exception {
		if (control instanceof CTabFolder folder) {
			return Integer.toString(folder.getTabHeight());
		}
		return null;
	}
}
