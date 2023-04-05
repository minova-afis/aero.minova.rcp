package aero.minova.rcp.css;

import org.eclipse.e4.ui.css.core.dom.IElementProvider;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.swt.custom.CTabFolder;
import org.w3c.dom.Element;

/**
 * @author Wilfried Saak
 */
public class MinovaCTabFolderElementProvider implements IElementProvider {

	@Override
	public Element getElement(Object element, CSSEngine engine) {
		if (element instanceof CTabFolder) {
			return new MinovaCTabFolderAdapter(element, engine);
		}
		return null;
	}
}
