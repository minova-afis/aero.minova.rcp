package aero.minova.rcp.rcp.fields;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_DECIMALS;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_MAX_VALUE;
import static aero.minova.rcp.rcp.fields.FieldUtil.FIELD_MIN_VALUE;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.NUMBER_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.nebula.widgets.opal.textassist.TextAssistContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.CssData;
import aero.minova.rcp.css.CssType;
import aero.minova.rcp.model.QuantityValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MQuantityField;
import aero.minova.rcp.rcp.accessor.QuantityValueAccessor;
import aero.minova.rcp.rcp.util.NumberFormatUtil;
import aero.minova.rcp.util.OSUtil;

public class QuantityField {
	
	public static Control create(Composite composite, MQuantityField field, int row, int column, Locale locale, MPerspective perspective,
			TranslationService translationService) {
		String unitText = field.getUnitText() == null ? "" : field.getUnitText();

		Label label = FieldLabel.create(composite, field);

		TextAssistContentProvider contentProvider = new TextAssistContentProvider() {

			@Override
			public List<String> getContent(String entry) {
				String number = null;
				String unit = null;
				
				ArrayList<String> result = new ArrayList<>();
				int decimals = field.getDecimals();

				NumberFormat numberFormat = NumberFormat.getNumberInstance(locale);
				numberFormat.setMaximumFractionDigits(decimals);
				numberFormat.setMinimumFractionDigits(decimals);
				numberFormat.setGroupingUsed(true);

				entry = NumberFormatUtil.clearNumberFromGroupingSymbols(entry, locale);
				
				if(Character.isDigit(entry.charAt(0))) {
					String[] numberAndUnit = NumberFormatUtil.splitNumberUnitEntry(entry);
					number = numberAndUnit[0];
					unit = numberAndUnit[1];
				}

				DecimalFormatSymbols dfs = new DecimalFormatSymbols(locale);
				try {
					Value value = NumberFormatUtil.newValue(number, field.getDataType(), dfs);
					if(!unit.isBlank()) {
						unit = field.getUnitFromEntry(unit);
						if (unit != null) {
							field.setUnitText(unit);
						} else {
							throw new Exception();
						}
					}
					field.setValue(new QuantityValue(number, unit, field.getDataType(), dfs), true);
					result.add(NumberFormatUtil.getValueString(numberFormat, field.getDataType(), value) + " " + unit);
				} catch (Exception e) {
					result.add(translationService.translate("@msg.ErrorConverting", null));
				}

				return result;
			}

		};
		Label unitLabel = LabelFactory.newLabel(SWT.LEFT).text(unitText).create(composite);
		FormData textFormData = new FormData();
		FormData unitFormData = new FormData();

		field.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChange(ValueChangeEvent evt) {
				// Einheit neu setzen, wenn sie sich geändert hat
				if (field.getUnitText() != null && !field.getUnitText().isBlank()) {
					unitLabel.setText(translationService.translate(field.getUnitText(), null));
				}
			}
		});

		Control text;
		if (OSUtil.isLinux()) {
			Text text2 = new Text(composite, SWT.BORDER);
			text = text2;
			ToolTip tooltip = new ToolTip(text2.getShell(), SWT.ICON_INFORMATION);
			text2.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					text2.selectAll();
					text2.setText(NumberFormatUtil.clearNumberFromGroupingSymbols(text2.getText(), locale));
					tooltip.setAutoHide(false);
				}

				@Override
				public void focusLost(FocusEvent e) {
					tooltip.setAutoHide(true);
					if (text2.getText().isBlank()) {
						field.setValue(null, true);
					} else {
						// Eventuelle neue Einheit setzen
						if (!field.getUnitText().isBlank() && !field.getUnitText().equals(unitText)) {
							unitLabel.setText(field.getUnitText());
						}
					}
				}
			});
			text2.addModifyListener(e -> {
				try {
					if (!tooltip.getAutoHide()) {
						List<String> values = contentProvider.getContent(((Text) e.widget).getText());
						if (!values.isEmpty()) {
							tooltip.setText(values.get(0));
							tooltip.setVisible(true);
						}
					} else {
						tooltip.setText("");
					}
				} catch (NullPointerException ex) {}
			});
		} else {
			TextAssist text2 = new TextAssist(composite, SWT.BORDER, contentProvider);
			text = text2;

			text2.setNumberOfLines(1);
			text2.setData(TRANSLATE_LOCALE, locale);
			text2.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					text2.selectAll();
					text2.setText(NumberFormatUtil.clearNumberFromGroupingSymbols(text2.getText(), locale));
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (text2.getText().isBlank()) {
						field.setValue(null, true);
					} else {
						// Eventuelle neue Einheit setzen
						if (!field.getUnitText().isBlank()) {
							unitLabel.setText(field.getUnitText());
						}
					}
				}
			});
		}
		QuantityValueAccessor quantityValueAccessor = new QuantityValueAccessor(field, text);

		// ValueAccessor in den Context injecten, damit IStylingEngine über @Inject verfügbar ist (in AbstractValueAccessor)
		IEclipseContext context = perspective.getContext();
		ContextInjectionFactory.inject(quantityValueAccessor, context);
		field.setValueAccessor(quantityValueAccessor);

		FieldLabel.layout(label, text, row, column, field.getNumberRowsSpanned());

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment((column == 0) ? 25 : 75);
		textFormData.width = NUMBER_WIDTH;

		unitFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		unitFormData.left = new FormAttachment(text, FieldUtil.UNIT_GAP, SWT.RIGHT); // etwas Abstand zw. NumberField und Unit
		unitFormData.right = new FormAttachment((column == 0) ? 50 : 100);

		Integer decimals = field.getDecimals();
		decimals = decimals == null ? 0 : decimals;
		Double maximum = field.getMaximumValue();
		maximum = maximum == null ? Double.MAX_VALUE : maximum;
		Double minimum = field.getMinimumValue();
		minimum = minimum == null ? Double.MIN_VALUE : minimum;
		text.setData(FIELD_DECIMALS, decimals);
		text.setData(FIELD_MAX_VALUE, maximum);
		text.setData(FIELD_MIN_VALUE, minimum);
		text.setData(Constants.CONTROL_FIELD, field);
		text.setLayoutData(textFormData);

		text.setData(CssData.CSSDATA_KEY, new CssData(CssType.NUMBER_FIELD, column + 1, row, field.getNumberColumnsSpanned(), field.getNumberRowsSpanned(),
				field.isFillToRight() || field.isFillHorizontal()));

		unitLabel.setData(TRANSLATE_PROPERTY, unitText);
		unitLabel.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "DescriptionLabel");
		unitLabel.setLayoutData(unitFormData);

		return text;
	}
	
}
