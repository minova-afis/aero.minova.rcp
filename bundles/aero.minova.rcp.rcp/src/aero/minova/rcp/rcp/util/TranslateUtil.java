package aero.minova.rcp.rcp.util;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.rcp.fields.LookupField;

public class TranslateUtil {

	private TranslateUtil() {}

	public static void translate(Composite composite, TranslationService translationService, Locale locale) {
		if (composite == null) {
			return;
		}
		for (Control control : composite.getChildren()) {
			if (control.getData(TRANSLATE_PROPERTY) != null) {
				translateControl(translationService, locale, control);
			} else {
				for (Control child : composite.getChildren()) {
					if (child instanceof Composite c) {
						translate(c, translationService, locale);
					}
				}
			}
		}
		for (Control control : composite.getChildren()) {
			if (control.getData(TRANSLATE_LOCALE) != null) {
				control.setData(TRANSLATE_LOCALE, locale);
			}
		}
	}

	private static void translateControl(TranslationService translationService, Locale locale, Control control) {
		String property = (String) control.getData(TRANSLATE_PROPERTY);
		String value = translationService.translate(property, null);
		if (control instanceof ExpandableComposite expandableComposite) {
			expandableComposite.setText(value);
			translate((Composite) expandableComposite.getClient(), translationService, locale);

			translateToolbar(expandableComposite, translationService);
		} else if (control instanceof Label l) {
			Object data = l.getData(LookupField.AERO_MINOVA_RCP_LOOKUP);
			if (data != null) {
				value = value + " ▼";
			}
			l.setText(value);
		} else if (control instanceof Button b) {
			b.setText(value);
		}
		if (control instanceof Composite c) {
			translate(c, translationService, locale);
		}
	}

	private static void translateToolbar(ExpandableComposite expandableComposite, TranslationService translationService) {
		if (expandableComposite.getTextClient() instanceof ToolBar bar) {
			for (ToolItem i : bar.getItems()) {
				String property = (String) i.getData(TRANSLATE_PROPERTY);
				String value = translationService.translate(property, null);
				i.setToolTipText(value);

				if (i.getData(Constants.GROUP_MENU) != null) {
					Menu m = (Menu) i.getData(Constants.GROUP_MENU);
					for (MenuItem mi : m.getItems()) {
						property = (String) mi.getData(TRANSLATE_PROPERTY);
						value = translationService.translate(property, null);
						mi.setText(value);
						mi.setToolTipText(value);
					}
				}
			}
		}
	}

	/**
	 * Übersetzt einen String der Form "@key %parameter1 %parameter2", wobei die Parameter in den übersetzten String eingesetzt werden. Es ist auch möglich,
	 * keine Parameter einzugeben.<br>
	 * <br>
	 * Beispiel <br>
	 * Eingabge String: "@msg.TextTooLong %110>100" <br>
	 * Übersetzung in messagesProperties: "Maximale L\u00E4nge \u00FCberschritten ({0})" <br>
	 * Rückgabewert dieser Methode: "Maximale Lange überschritten (110>100)"
	 * 
	 * @param errorMessage
	 * @param translationService
	 * @return
	 */
	public static String translateWithParameters(String errorMessage, TranslationService translationService) {

		List<String> errorMessageParts = Stream.of(errorMessage.split("%")).map(String::trim).toList();

		String translatedBase = translationService.translate(errorMessageParts.get(0), null);

		return MessageFormat.format(translatedBase, errorMessageParts.subList(1, errorMessageParts.size()).toArray(new Object[0]));
	}
}
