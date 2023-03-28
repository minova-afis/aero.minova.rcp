package aero.minova.rcp.css;

import org.eclipse.e4.ui.css.core.dom.IElementProvider;
import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;

/**
 * @author Wilfried Saak
 */
public class MinovaSectionElementProvider implements IElementProvider {

	@Override
	public Element getElement(Object element, CSSEngine engine) {
		return new MinovaSectionAdapter((Composite) element, engine);
	}
}
