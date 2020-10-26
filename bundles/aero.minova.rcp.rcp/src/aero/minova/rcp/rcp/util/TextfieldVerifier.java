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
		try {
			Float.parseFloat(newString);
		} catch (NumberFormatException ex) {
			correctDouble = false;
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

	public static String verifyDate(String newString) {
		String date = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());
		Instant instant = DateTimeUtil.getDate(newString);
		if (instant != null) {
			date = df.format(instant);
		} else {
			date = "";
		}
		return date;
	}

	public static String verifyTime(String newString) {
		String time = "";
		DateTimeFormatter df = DateTimeFormatter.ofPattern("hh:mm").withZone(ZoneId.systemDefault());
		Instant instant = TimeUtil.getTime(newString);
		if (instant != null) {
			time = df.format(instant);
		} else {
			time = "";
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
			Field field = (Field) t.getData("field");
			if (field.getShortDate() != null || field.getLongDate() != null) {
				t.setText(verifyDate(newString));
			} else {
				t.setText(verifyTime(newString));
				XMLDetailPart xml = (XMLDetailPart) t.getData("XMLDetailPart");
				xml.updateQuantitys();
			}
		}

	}
}
