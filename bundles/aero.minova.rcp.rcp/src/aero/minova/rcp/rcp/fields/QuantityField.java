package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_DECIMALS;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_MAX_VALUE;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_MIN_VALUE;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.NUMBER_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.Locale;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
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
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.model.form.MQuantityField;
import aero.minova.rcp.rcp.accessor.QuantityValueAccessor;

public class QuantityField {
	
	public static Control create(Composite composite, MQuantityField field, int row, int column, Locale locale, MPerspective perspective) {
		String unitText = field.getUnitText() == null ? "" : field.getUnitText();

		Label label = FieldLabel.create(composite, field);
		Text text = TextFactory.newText(SWT.BORDER | SWT.RIGHT).text("").create(composite);
		QuantityValueAccessor quantityValueAccessor = new QuantityValueAccessor(field, text);

		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				text.selectAll();
			}
		});
		text.addVerifyListener(quantityValueAccessor);

		// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
		IEclipseContext context = perspective.getContext();
		ContextInjectionFactory.inject(quantityValueAccessor, context);
		field.setValueAccessor(quantityValueAccessor);

		Label unit = LabelFactory.newLabel(SWT.LEFT).text(unitText).create(composite);
		FormData textFormData = new FormData();
		FormData unitFormData = new FormData();

		FieldLabel.layout(label, text, row, column, field.getNumberRowsSpanned());

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment((column == 0) ? 25 : 75);
		textFormData.width = NUMBER_WIDTH;

		unitFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		unitFormData.left = new FormAttachment(text, FieldUtil.UNIT_GAP, SWT.RIGHT);  // etwas Abstand zw. NumberField und Unit
		unitFormData.right = new FormAttachment((column == 0) ? 50 : 100);

		Integer decimals = field.getDecimals();
		decimals = decimals == null ? 0 : decimals;
		Double maximum = field.getMaximumValue();
		maximum = maximum == null ? Double.MAX_VALUE : maximum;
		Double minimum = field.getMinimumValue();
		minimum = minimum == null ? Double.MIN_VALUE : minimum;
		text.setData(TRANSLATE_LOCALE, locale);
		text.setData(FIELD_DECIMALS, decimals);
		text.setData(FIELD_MAX_VALUE, maximum);
		text.setData(FIELD_MIN_VALUE, minimum);
		text.setData(Constants.CONTROL_FIELD, field);
		text.setLayoutData(textFormData);
		NumberFieldUtil.setMessage(text);

		text.setData(CssData.CSSDATA_KEY, new CssData(CssType.NUMBER_FIELD, column + 1, row, field.getNumberColumnsSpanned(), field.getNumberRowsSpanned(),
				field.isFillToRight() || field.isFillHorizontal()));

		unit.setData(TRANSLATE_PROPERTY, unitText);
		unit.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "DescriptionLabel");
		unit.setLayoutData(unitFormData);

		return text;
	}

}
