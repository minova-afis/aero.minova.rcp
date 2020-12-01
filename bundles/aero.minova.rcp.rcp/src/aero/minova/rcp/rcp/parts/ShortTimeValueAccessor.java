package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;

public class ShortTimeValueAccessor extends AbstractValueAccessor {

	public ShortTimeValueAccessor(MField field, TextAssist textAssist) {
		super(field, textAssist);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		if (value == null) {
			((TextAssist) control).setText("");
		} else {
			Instant time = value.getInstantValue();
			LocalTime localTime = LocalTime.ofInstant(time, ZoneId.of("UTC"));
			Locale locale = (Locale) control.getData(TRANSLATE_LOCALE);
			((TextAssist) control).setText(localTime.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale)));
		}
	}
}
