package aero.minova.rcp.rcp.accessor;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.model.PeriodValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.widgets.PeriodComposite;

public class PeriodValueAccessor extends AbstractValueAccessor {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.DATE_UTIL)
	String dateUtil;

	public PeriodValueAccessor(MField field, PeriodComposite control) {
		super(field, control);

		addFocusListener(field, control.getBaseDate());
		addFocusListener(field, control.getDueDate());
	}

	private void addFocusListener(MField field, Control control) {
		control.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				setFocussed(false);
				// Überprüfung ob der eingetragenen Wert in der Liste ist und ebenfalls gültig!
				field.setValue(field.getValue(), false);
				((DetailAccessor) field.getDetail().getDetailAccessor()).setSelectedControl(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setFocussed(true);
				((DetailAccessor) field.getDetail().getDetailAccessor()).setSelectedControl(control);
			}
		});
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		PeriodComposite pc = (PeriodComposite) control;
		PeriodValue pv = ((PeriodValue) value);
		Locale locale = (Locale) control.getData(TRANSLATE_LOCALE);

		if (pv == null) {
			setText(pc.getBaseDate(), "");
			setText(pc.getDueDate(), "");

			pc.setUserInput(null);
			pc.setBaseDate(null);
			pc.setDueDate(null);
		} else {
			pc.setUserInput(pv.getUserInput());

			if (pv.getBaseValue() != null) {
				setDateText(pc.getBaseDate(), locale, value.getBaseValue());
				pc.setBaseDate(value.getBaseValue());
			} else {
				setText(pc.getBaseDate(), "");
				pc.setBaseDate(null);
			}

			if (pv.getDueDate() != null && pv.getDueDate().getInstantValue() != null) {
				setDateText(pc.getDueDate(), locale, pv.getDueDate().getInstantValue());
				pc.setDueDate(pv.getDueDate().getInstantValue());
			} else {
				setText(pc.getDueDate(), "");
				pc.setDueDate(null);
			}
		}

	}

	private void setDateText(Control control, Locale locale, Instant date) {
		LocalDate localDate = LocalDate.ofInstant(date, ZoneId.of("UTC"));
		if (dateUtil == null || dateUtil.equals("")) {
			setText(control, localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)));
		} else {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern(dateUtil, locale);
			setText(control, localDate.format(dtf));
		}
	}
}
