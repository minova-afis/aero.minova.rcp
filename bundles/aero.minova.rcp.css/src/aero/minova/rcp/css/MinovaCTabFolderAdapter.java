package aero.minova.rcp.css;

import org.eclipse.e4.ui.css.core.engine.CSSEngine;
import org.eclipse.e4.ui.css.swt.dom.CTabFolderElement;
import org.eclipse.swt.custom.CTabFolder;

public class MinovaCTabFolderAdapter extends CTabFolderElement {
	public MinovaCTabFolderAdapter(Object element, CSSEngine engine) {
		super((CTabFolder) element, engine);
	}
}
