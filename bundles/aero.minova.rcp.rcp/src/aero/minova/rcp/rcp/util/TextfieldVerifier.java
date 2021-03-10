package aero.minova.rcp.rcp.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.constant.Constants;
import aero.minova.rcp.form.model.xsd.Field;

public class TextfieldVerifier implements FocusListener {

	public static boolean verifyDouble(String newString) {
		boolean correctDouble = true;
		if (!newString.equals("")) {
			try {
				Float.parseFloat(newString);
			} catch (NumberFormatException ex) {
				correctDouble = false;
			}
		}
		return correctDouble;
	}

	public static boolean verifyText(String newString, int limit) {
		return newString.length() <= limit;
	}

	public static String verifyDate(String newString, String timezone) {
		String date = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.of(timezone));
		Instant instant = DateUtil.getDate(newString);
		if (instant != null) {
			date = df.format(instant);
		}
		return date;
	}

	public static String verifyTime(String newString, String timezone) {
		String time = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of(timezone));
		Instant instant = TimeUtil.getTime(newString);
		if (instant != null) {
			time = df.format(instant);
		}
		return time;
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusLost(FocusEvent e) {
		final String newString = ((Text) e.getSource()).getText();
		if (newString != "") {
			Text t = (Text) e.getSource();
			Field f = (Field) t.getData(Constants.CONTROL_FIELD);
			if (f.getNumber() != null) {
				if (t.getText() != null && !t.getText().equals("")) {
					int decimals = f.getNumber().getDecimals();
					String formatedString = t.getText();
					String format = "%1." + decimals + "f";
					formatedString = String.format(format, Double.valueOf(formatedString));
					formatedString = formatedString.replace(',', '.');
					t.setText(formatedString);
				}
			} else if (t.getData(Constants.FOCUSED_ORIGIN) instanceof WFCDetailUtil) {
				WFCDetailUtil xml = (WFCDetailUtil) t.getData(Constants.FOCUSED_ORIGIN);
				Field field = (Field) t.getData("field");
				if (field.getShortDate() != null || field.getLongDate() != null) {
					t.setText(verifyDate(newString, xml.getTimeZone()));
				} else {
					t.setText(verifyTime(newString, xml.getTimeZone()));
					xml.updateQuantitys();
				}
			}
		}

	}
}
