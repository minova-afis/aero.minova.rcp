package aero.minova.rcp.rcp.accessor;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;

public class ShortDateValueAccessor extends AbstractValueAccessor {

	public ShortDateValueAccessor(MField field, TextAssist textAssist) {
		super(field, textAssist);
	}

	@Override
	protected void updateControlFromValue(Control control, Value value) {
		if (value == null) {
			((TextAssist) control).setText("");
		} else {
			Instant date = value.getInstantValue();
			LocalDate localDate = LocalDate.ofInstant(date, ZoneId.of("UTC"));
			Locale locale = (Locale) control.getData(TRANSLATE_LOCALE);
			// Bei der Formatierung geschehen fehler, wir erhalten das Milienium zurück
			((TextAssist) control).setText(localDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)));
		}
	}

}
