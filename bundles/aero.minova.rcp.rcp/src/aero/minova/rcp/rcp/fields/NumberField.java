package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_DECIMALS;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_MAX_VALUE;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_MIN_VALUE;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.NUMBER_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.Locale;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.LabelFactory;
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
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.rcp.accessor.NumberValueAccessor;

public class NumberField {

	public static Control create(Composite composite, MNumberField field, int row, int column, Locale locale,
			MPerspective perspective) {
		String labelText = field.getLabel() == null ? "" : field.getLabel();
		String unitText = field.getUnitText() == null ? "" : field.getUnitText();

		Label label = LabelFactory.newLabel(SWT.RIGHT).text(labelText).create(composite);
		Text text = TextFactory.newText(SWT.BORDER | SWT.RIGHT).text("").create(composite);
		NumberValueAccessor numberValueAccessor = new NumberValueAccessor(field, text);

		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});
		text.addVerifyListener(numberValueAccessor);

		// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
		IEclipseContext context = perspective.getContext();
		ContextInjectionFactory.inject(numberValueAccessor, context);
		field.setValueAccessor(numberValueAccessor);

		Label unit = LabelFactory.newLabel(SWT.LEFT).text(unitText).create(composite);
		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();
		FormData unitFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		textFormData.width = NUMBER_WIDTH;

		unitFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		unitFormData.left = new FormAttachment(text, 0, SWT.RIGHT);
		unitFormData.width = COLUMN_WIDTH - NUMBER_WIDTH;

		label.setData(TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		Integer decimals = field.getDecimals();
		decimals = decimals == null ? 0 : decimals;
		Double maximum = field.getMaximumValue();
		maximum = maximum == null ? Double.MAX_VALUE : maximum;
		Double minimum = field.getMinimumValue();
		minimum = minimum == null ? Double.MIN_VALUE : maximum;
		text.setData(TRANSLATE_LOCALE, locale);
		text.setData(FIELD_DECIMALS, decimals);
		text.setData(FIELD_MAX_VALUE, maximum);
		text.setData(FIELD_MIN_VALUE, minimum);
		text.setData(Constants.CONTROL_FIELD, field);
		text.setLayoutData(textFormData);
		NumberFieldUtil.setMessage(text);

		// TODO SAW_ERC korrigieren NumberFieldVerifier
		// text.addVerifyListener(new NumberFieldVerifier(text));

		unit.setData(TRANSLATE_PROPERTY, unitText);
		unit.setLayoutData(unitFormData);

		return text;
	}

}
