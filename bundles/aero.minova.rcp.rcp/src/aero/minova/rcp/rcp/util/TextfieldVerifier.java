package aero.minova.rcp.rcp.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.rcp.parts.XMLDetailPart;

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
		if (newString.length() > limit) {
			return false;
		} else {
			return true;
		}
	}

	public static String verifyDate(String newString, String timezone) {
		String date = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.of(timezone));
		Instant instant = DateTimeUtil.getDate(newString);
		if (instant != null) {
			date = df.format(instant);
		}
		return date;
	}

	public static String verifyTime(String newString, String timezone) {
		String time = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.of(timezone));
		Instant instant = TimeUtil.getTime(newString, timezone);
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
			if (t.getData(Constants.FOCUSED_ORIGIN) instanceof WFCDetailUtil) {
				WFCDetailUtil xml = (WFCDetailUtil) t.getData(Constants.FOCUSED_ORIGIN);
				Field field = (Field) t.getData("field");
				if (field.getShortDate() != null || field.getLongDate() != null) {
					t.setText(verifyDate(newString, xml.getTimeZone()));
				} else {
					t.setText(verifyTime(newString, xml.getTimeZone()));
					xml.updateQuantitys();
				}
			} else {
				XMLDetailPart xml = (XMLDetailPart) t.getData(Constants.FOCUSED_ORIGIN);
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
