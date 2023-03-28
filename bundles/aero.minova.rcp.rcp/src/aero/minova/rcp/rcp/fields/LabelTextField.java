package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.model.form.MField;

@SuppressWarnings("restriction")
public class LabelTextField {

	private LabelTextField() {
		throw new IllegalStateException("Utility class");
	}

	public static Control createBold(Composite composite, MField field, int row, int column, MPerspective perspective) {

		return createLabel(composite, field, row, column, true);
	}

	public static Control create(Composite composite, MField field, int row, int column, MPerspective perspective) {

		return createLabel(composite, field, row, column, true);
	}

	public static Label createLabel(Composite composite, MField field, int row, int column, boolean bold) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();

		int style = SWT.BORDER;
		if (field.getNumberRowsSpanned() > 1) {
			// Maskenentwickler hat mehrzeilige Eingabe definiert
			style |= SWT.WRAP;
		}

		style |= SWT.LEFT;
		Label label = LabelFactory.newLabel(style).text(labelText).create(composite);
		CssData cssData;
		if (bold) {
			cssData = new CssData(CssType.LABEL_TEXT_BOLD_FIELD, column, row, field.getNumberColumnsSpanned(), field.getNumberRowsSpanned(),
					field.isFillToRight() || field.isFillHorizontal());
		} else {
			cssData = new CssData(CssType.LABEL_TEXT_FIELD, column, row, field.getNumberColumnsSpanned(), field.getNumberRowsSpanned(),
					field.isFillToRight() || field.isFillHorizontal());
		}
		label.setData(CssData.CSSDATA_KEY, cssData);

		label.setData(TRANSLATE_PROPERTY, labelText);
		label.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "Description");

		FormData fd = new FormData();
		fd.top = new FormAttachment(composite, FieldUtil.MARGIN_TOP + row * FieldUtil.COLUMN_HEIGHT);
		fd.left = new FormAttachment((column == 0) ? 0 : 50);

		if (field.getNumberColumnsSpanned() > 2 || field.isFillHorizontal() || column >= 2) {
			fd.right = new FormAttachment(100, -MARGIN_BORDER);
		} else {
			fd.right = new FormAttachment(50, -ICssStyler.CSS_SECTION_SPACING);
		}

		label.setLayoutData(fd);
		return label;
	}
}
