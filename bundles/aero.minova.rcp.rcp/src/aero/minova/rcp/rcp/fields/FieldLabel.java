package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.model.form.MField;

@SuppressWarnings("restriction")
public abstract class FieldLabel {

	public static Label create(Composite composite, MField field) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		Label label = LabelFactory.newLabel(SWT.RIGHT).text(labelText).create(composite);
		label.setData(TRANSLATE_PROPERTY, labelText);
		label.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "Description");
		return label;
	}

	public static void layout(Label label, Control field, int row, int column, int numberRowsSpanned) {
		FormData fd = new FormData();
		fd.right = new FormAttachment(field, MARGIN_LEFT * -1, SWT.LEFT);
		fd.left = new FormAttachment((column == 0) ? 0 : 50);
		fd.top = new FormAttachment(field, 0, (numberRowsSpanned > 1) ? SWT.TOP : SWT.CENTER);
		label.setLayoutData(fd);

		CssData cssData = new CssData(CssType.LABEL, column, row, 1, numberRowsSpanned, false);
		label.setData(CssData.CSSDATA_KEY, cssData);
	}
}
