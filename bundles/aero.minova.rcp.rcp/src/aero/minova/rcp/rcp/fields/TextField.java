package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TEXT_WIDTH;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.TextFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.TextValueAccessor;
import aero.minova.rcp.rcp.labels.FieldLabel;

public class TextField {

	private TextField() {
		throw new IllegalStateException("Utility class");
	}

	public static Control create(Composite composite, MField field, int row, int column, MPerspective perspective) {
		Label label = FieldLabel.create(composite, field);

		int style = SWT.BORDER;
		if (field.getNumberRowsSpanned() > 1) {
			// Maskenentwickler hat mehrzeilige Eingabe definiert
			style |= SWT.MULTI | SWT.WRAP;
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

		text.addTraverseListener(new TraverseListener() {

			@Override
			public void keyTraversed(TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_TAB_NEXT && e.stateMask == 0) {
					e.doit = true;
				} else if (e.detail == SWT.TRAVERSE_TAB_NEXT && e.stateMask == 262144) {
					e.doit = false;
					text.setText(text.getText() + "\t");
					text.setSelection(text.getText().length());
				} else if (e.detail == SWT.TRAVERSE_TAB_NEXT && e.stateMask == 65536) {
					e.doit = true;
				}
			}

		});
		text.setData(Constants.CONTROL_FIELD, field);
		CssData cssData = new CssData(CssType.TEXT_FIELD, column + 1, row, field.getNumberColumnsSpanned(), field.getNumberRowsSpanned(), field.isFillToRight());
		text.setData(CssData.CSSDATA_KEY, cssData);

		// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
		IEclipseContext context = perspective.getContext();
		TextValueAccessor valueAccessor = new TextValueAccessor(field, text);
		ContextInjectionFactory.inject(valueAccessor, context);
		field.setValueAccessor(valueAccessor);

		FieldLabel.layout(label, text, row, column, field.getNumberRowsSpanned());

		FormData fd = new FormData();
		fd.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		fd.left = new FormAttachment((column == 0) ? 25 : 75);
		if (field.getNumberColumnsSpanned() != null && field.getNumberColumnsSpanned().intValue() > 2 && field.isFillToRight()) {
			fd.right = new FormAttachment(100, MARGIN_BORDER);
		} else {
			fd.width = TEXT_WIDTH;
		}
		if (field.getNumberRowsSpanned() > 1) {
			fd.height = COLUMN_HEIGHT * field.getNumberRowsSpanned() - MARGIN_TOP;
		}
		text.setLayoutData(fd);

		return text;
	}
}
