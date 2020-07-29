package aero.minova.rcp.rcp.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;

public class DetailUtil {
	public static void createField(Field field, Composite composite) {
		if (field.isVisible()) {
			// GridData gridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 3);
//			Composite formComposite = new Composite(composite, SWT.FILL  | SWT.RIGHT);
			Label label = new Label(composite, SWT.RIGHT);
			label.setText(field.getTextAttribute());
//			label.setLayoutData(gridData);
			
			if (field.getNumber() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			} else if (field.getBignumber() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getLookup() != null) {
				CCombo combo = new CCombo(composite, SWT.BORDER);
				combo.setText("");
			}
			else if (field.getPercentage() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getText() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getShortDate() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getLongDate() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getShortTime() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getLongTime() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getDateTime() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getWeekDay() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getEditor() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getMoney() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getParamString() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getVoid() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			else if (field.getBoolean() != null) {
				Button button = new Button(composite, SWT.CHECK);
				button.setText("");
			}
			else if (field.getColor() != null) {
				Text text = new Text(composite, SWT.BORDER);
				text.setText("");
			}
			
			Label labelUnit = new Label(composite, SWT.None);
			if (field.getUnitText() != null) {
				labelUnit.setText(field.getUnitText());
			}
//				labelUnit.setLayoutData(gridData);
		}
	}

	public static Composite createSection(FormToolkit formToolkit, Composite parent, Head head) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX);

//		FormData formData = new FormData();
//		formData.top = new FormAttachment(0);
//		formData.bottom = new FormAttachment(100, -450);
//		formData.right = new FormAttachment(0, 450);
//		formData.left = new FormAttachment(0);
//		section.setLayoutData(formData);
		formToolkit.paintBordersFor(section);
		section.setText("Kopfdaten");
		section.setExpanded(true);
		Composite composite = formToolkit.createComposite(section, SWT.RIGHT);
		formToolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(6, true));

		return composite;
	}

	public static Composite createSection(FormToolkit formToolkit, Composite parent, Page page) {
		Section section = formToolkit.createSection(parent, Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX | Section.TWISTIE);

//		FormData formData = new FormData();
//		formData.top = new FormAttachment(450);
//		formData.bottom = new FormAttachment(550, -900);
//		formData.right = new FormAttachment(0, 450);
//		formData.left = new FormAttachment(0);
//		section.setLayoutData(formData);
		formToolkit.paintBordersFor(section);
		section.setText(page.getText());
		section.setExpanded(true);
		Composite composite = formToolkit.createComposite(section, SWT.RIGHT);
		formToolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(6, true));

		return composite;
	}
}
