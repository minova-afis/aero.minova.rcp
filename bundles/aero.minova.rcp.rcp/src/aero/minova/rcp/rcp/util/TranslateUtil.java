package aero.minova.rcp.rcp.util;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.Locale;

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
				String property = (String) control.getData(TRANSLATE_PROPERTY);
				String value = translationService.translate(property, null);
				if (control instanceof ExpandableComposite) {
					ExpandableComposite expandableComposite = (ExpandableComposite) control;
					expandableComposite.setText(value);
					translate((Composite) expandableComposite.getClient(), translationService, locale);

					translateToolbar(expandableComposite, translationService);
				} else if (control instanceof Label) {
					Label l = ((Label) control);
					Object data = l.getData(LookupField.AERO_MINOVA_RCP_LOOKUP);
					if (data != null) {
						// TODO aus den Preferences Laden
						value = value + " ▼";
					}
					((Label) control).setText(value);
				} else if (control instanceof Button) {
					((Button) control).setText(value);
				}
				if (control instanceof Composite) {
					translate((Composite) control, translationService, locale);
				}
			} else {
				for (Control child : composite.getChildren()) {
					if (child instanceof Composite) {
						translate((Composite) child, translationService, locale);
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

	private static void translateToolbar(ExpandableComposite expandableComposite, TranslationService translationService) {
		if (expandableComposite.getTextClient() instanceof ToolBar) {
			ToolBar bar = (ToolBar) expandableComposite.getTextClient();

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
}
