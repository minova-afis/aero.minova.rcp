package aero.minova.rcp.rcp.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;

public class DetailUtil {
	public static void createField(Field field, Composite composite) {
		Composite fieldComposite = new Composite(composite, SWT.None);
		Label label = new Label(fieldComposite, SWT.RIGHT);
		if (field.getTextAttribute() != null) {
			label.setText(field.getTextAttribute());
		}
		Text text = new Text(fieldComposite, SWT.BORDER);
		text.setText("Hallo");
		if (field.getUnitText() != null) {
			Label labelUnit = new Label(fieldComposite, SWT.None);
			labelUnit.setText(field.getUnitText());
		}
	}

	public static Composite createSection(Composite parent, Head head) {
		GridLayout grid =  new GridLayout();
		Section section = new Section(parent, Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX);
		section.setText("Kopfdaten");
		section.setExpanded(true);
		section.setLayout(grid);
		Composite composite = new Composite(section, SWT.None);
		composite.setLayout(new GridLayout());
		
		return composite;
	}

	public static Composite createSection(Composite parent, Page page) {
		GridLayout grid =  new GridLayout();
		Section section = new Section(parent, Section.TWISTIE | Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX);
		section.setText(page.getText());
		section.setExpanded(true);
		section.setLayout(grid);
		Composite composite = new Composite(section, SWT.None);
		composite.setLayout(new GridLayout());
		return composite;
	}
}
