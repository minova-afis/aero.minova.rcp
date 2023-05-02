package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.TextFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.TextValueAccessor;

public class TextField {

	private TextField() {
		throw new IllegalStateException("Utility class");
	}

	public static Control create(Composite composite, MField field, int row, int column, MPerspective perspective) {
		Label label = FieldLabel.create(composite, field);

		int style = SWT.BORDER;
		if (field.getNumberRowsSpanned() > 1) {
			// Maskenentwickler hat mehrzeilige Eingabe definiert
			style |= SWT.WRAP;
		}

		Text text = TextFactory.newText(style).text("").create(composite);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});
		// Wenn der Anwender den Wert ändert, muss es weitergegeben werden
		text.addModifyListener(e -> {
			if (text.isFocusControl()) {
				String newValue = text.getText();
				if (newValue.length() < 1) {
					field.setValue(null, true);
				} else {
					field.setValue(new Value(newValue), true);
				}
			}
		});

		addTraverseListener(text);

		text.setData(Constants.CONTROL_FIELD, field);
		CssData cssData = new CssData(CssType.TEXT_FIELD, column + 1, row, field.getNumberColumnsSpanned(), field.getNumberRowsSpanned(),
				field.isFillToRight() || field.isFillHorizontal());
		text.setData(CssData.CSSDATA_KEY, cssData);

		// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
		IEclipseContext context = perspective.getContext();
		TextValueAccessor valueAccessor = new TextValueAccessor(field, text);
		ContextInjectionFactory.inject(valueAccessor, context);
		field.setValueAccessor(valueAccessor);

		FieldLabel.layout(label, text, row, column, field.getNumberRowsSpanned());

		addFormData(composite, field, row, column, text);

		return text;
	}

	private static void addTraverseListener(Text text) {
		text.addTraverseListener(e -> {
			if (e.detail == SWT.TRAVERSE_TAB_NEXT && e.stateMask == 0) {
				e.doit = true;
			} else if (e.detail == SWT.TRAVERSE_TAB_NEXT && e.stateMask == 262144) {
				e.doit = false;
				text.setText(text.getText() + "\t");
				text.setSelection(text.getText().length());
			} else if (e.detail == SWT.TRAVERSE_TAB_NEXT && e.stateMask == 65536) {
				e.doit = true;
			}
		});
	}

	private static void addFormData(Composite composite, MField field, int row, int column, Text text) {
		FormData fd = new FormData();
		fd.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);

		if (field.isFillHorizontal() && field.getLabel() == null) {
			fd.left = new FormAttachment((column == 0) ? 0 : 50);
		} else {
			fd.left = new FormAttachment((column == 0) ? 25 : 75);
		}

		if ((field.getNumberColumnsSpanned() > 2 && field.isFillToRight()) || field.isFillHorizontal() || column >= 2) {
			fd.right = new FormAttachment(100, -MARGIN_BORDER);
		} else {
			fd.right = new FormAttachment(50, -ICssStyler.CSS_SECTION_SPACING);
		}

		text.setLayoutData(fd);
	}
}
