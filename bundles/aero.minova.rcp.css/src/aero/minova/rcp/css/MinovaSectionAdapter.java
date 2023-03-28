package aero.minova.rcp.css;

import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.dom.CompositeElement;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Wilfried Saak
 */
public class MinovaSectionAdapter extends CompositeElement {
	public MinovaSectionAdapter(Composite composite, CSSEngine engine) {
		super(composite, engine);
	}
}
