package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_BORDER;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.Locale;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.BooleanValueAccessor;

@SuppressWarnings("restriction")
public class BooleanField {

	private BooleanField() {}

	public static Control create(Composite composite, MField field, int row, int column, Locale locale, MPerspective perspective) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();

		Button button = ButtonFactory.newButton(SWT.CHECK).text(field.getLabel()).create(composite);

		// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
		IEclipseContext context = perspective.getContext();
		BooleanValueAccessor valueAccessor = new BooleanValueAccessor(field, button);
		ContextInjectionFactory.inject(valueAccessor, context);
		field.setValueAccessor(valueAccessor);

		FormData fd = new FormData();
		fd.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		fd.left = new FormAttachment((column == 0) ? 25 : 75);
		if (field.getNumberColumnsSpanned() > 2 || field.isFillHorizontal() || column >= 2) {
			fd.right = new FormAttachment(100, -MARGIN_BORDER);
		} else {
			fd.right = new FormAttachment(50, -ICssStyler.CSS_SECTION_SPACING);
		}
		button.setLayoutData(fd);

		button.setData(CssData.CSSDATA_KEY, new CssData(CssType.TEXT_FIELD, column + 1, row, 1, 1, false));
		button.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "Description");

		button.setData(TRANSLATE_PROPERTY, labelText);
		button.setData(TRANSLATE_LOCALE, locale);
		button.setData(Constants.CONTROL_FIELD, field);
		button.setData(Constants.CONTROL_DATATYPE, DataType.BOOLEAN);

		button.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				field.setValue(new Value(button.getSelection()), true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				field.setValue(new Value(button.getSelection()), true);
			}
		});

		return button;
	}

}
