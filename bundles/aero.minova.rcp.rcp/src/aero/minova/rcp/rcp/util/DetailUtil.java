package aero.minova.rcp.rcp.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;

public class DetailUtil {
	public static void createField(Field field) {
		
	}

	public static void createSection(Composite parent, Head head) {
		Section section = new Section(parent, SWT.NONE);
		section.setText("Kopfdaten");
	}

	public static void createSection(Composite parent, Page page) {
		Section section = new Section(parent, SWT.NONE);
		section.setText(page.getText());
	}
}
