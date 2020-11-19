package aero.minova.rcp.translate.lifecycle;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.swt.graphics.FontData;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.translate.service.WFCTranslationService;

public class Manager {

	public static final String PREFERENCES_NODE = "aero.minova.rcp.preferencewindow";
	Preferences preferences = InstanceScope.INSTANCE.getNode(PREFERENCES_NODE);

	@PostContextCreate
	public void postContextCreate(IEclipseContext context) {
		setLocale(context);
		checkTranslationService(context);
		initPrefs();
	}

	private void checkTranslationService(IEclipseContext context) {
		Object o = context.get(TranslationService.class);
		if (o.getClass().getName().equals("org.eclipse.e4.core.internal.services.BundleTranslationProvider")) {
			WFCTranslationService translationService = ContextInjectionFactory.make(WFCTranslationService.class,
					context);
			translationService.setTranslationService((TranslationService) o);
			context.set(TranslationService.class, translationService);
			context.set("aero.minova.rcp.applicationid", "WFC");
			context.set("aero.minova.rcp.customerid", "MIN");
		}
	}

	private void setLocale(IEclipseContext context) {
		context.set(TranslationService.LOCALE, CustomLocale.getLocale());

	}

	private void initPrefs() {
		String language = preferences.get("language", Locale.getDefault().getDisplayLanguage(Locale.getDefault()));
		preferences.put("language", language);
		String country = preferences.get("country", Locale.getDefault().getDisplayCountry(Locale.getDefault()));
		preferences.put("country", country);
		String timezone = preferences.get("timezone", ZoneId.systemDefault().getId());
		preferences.put("timezone", timezone);
		String font = preferences.get("font", "M");
		preferences.put("font", font);
		String symbolMenu = preferences.get("symbolMenu", "24x24");
		preferences.put("symbolMenu", symbolMenu);
		String symbolToolbar = preferences.get("symbolToolbar", "32x32");
		preferences.put("symbolToolbar", symbolToolbar);
		boolean masks = preferences.getBoolean("masks", false);
		preferences.putBoolean("masks", masks);
		boolean dragdrop = preferences.getBoolean("dragdrop", false);
		preferences.putBoolean("dragdrop", dragdrop);
		boolean icons = preferences.getBoolean("icons", false);
		preferences.putBoolean("icons", icons);
		boolean indexautoload = preferences.getBoolean("indexautoload", false);
		preferences.putBoolean("indexautoload", indexautoload);
		boolean indexautoupdate = preferences.getBoolean("indexautoupdate", false);
		preferences.putBoolean("indexautoupdate", indexautoupdate);
		boolean reportwindow = preferences.getBoolean("reportwindow", true);
		preferences.putBoolean("reportwindow", reportwindow);
		boolean descriptionButton = preferences.getBoolean("descriptionButton", true);
		preferences.putBoolean("descriptionButton", descriptionButton);
		boolean maskbuffer = preferences.getBoolean("maskbuffer", true);
		preferences.putBoolean("maskbuffer", maskbuffer);
		int displaybuffer = preferences.getInt("displaybuffer", 20);
		preferences.putInt("displaybuffer", displaybuffer);
		int maxbuffer = preferences.getInt("maxbuffer", 90);
		preferences.putInt("maxbuffer", maxbuffer);
		int selectiondelay = preferences.getInt("selectiondelay", 150);
		preferences.putInt("selectiondelay", selectiondelay);
		boolean sizeautoadjust = preferences.getBoolean("sizeautoadjust", false);
		preferences.putBoolean("sizeautoadjust", sizeautoadjust);
		boolean fadeinbuttontext = preferences.getBoolean("fadeinbuttontext", false);
		preferences.putBoolean("fadeinbuttontext", fadeinbuttontext);
		boolean buttondetailarea = preferences.getBoolean("buttondetailarea", true);
		preferences.putBoolean("buttondetailarea", buttondetailarea);
		boolean showlookups = preferences.getBoolean("showlookups", true);
		preferences.putBoolean("showlookups", showlookups);
		boolean fadeingroups = preferences.getBoolean("fadeingroups", true);
		preferences.putBoolean("fadeingroups", fadeingroups);
		boolean showchangedrow = preferences.getBoolean("showchangedrow", true);
		preferences.putBoolean("showchangedrow", showchangedrow);
		boolean xmlxsdcreate = preferences.getBoolean("xmlxsdcreate", false);
		preferences.putBoolean("xmlxsdcreate", xmlxsdcreate);
		boolean optimizewidth = preferences.getBoolean("optimizewidth", true);
		preferences.putBoolean("optimizewidth", optimizewidth);
		boolean hideemptycolumn = preferences.getBoolean("hideemptycolumn", true);
		preferences.putBoolean("hideemptycolumn", hideemptycolumn);
		boolean hidegoupcolumns = preferences.getBoolean("hidegoupcolumns", true);
		preferences.putBoolean("hidegoupcolumns", hidegoupcolumns);
		boolean hidesearchdetails = preferences.getBoolean("hidesearchdetails", true);
		preferences.putBoolean("hidesearchdetails", hidesearchdetails);
		boolean deactivateinternpreview = preferences.getBoolean("deactivateinternpreview", false);
		preferences.putBoolean("deactivateinternpreview", deactivateinternpreview);
		int maxCharacter = preferences.getInt("maxCharacter", 24000);
		preferences.putInt("maxCharacter", maxCharacter);
		String user = preferences.get("user", System.getProperty("user.name"));
		preferences.put("user", user);
		String fd = preferences.get("fontChooser", null);
		if ("".equals(fd))
			fd = null;
		FontData fdC = (fd == null ? null : new FontData(fd));
		if (fd != null) {
			preferences.put("fontChooser", (fdC.toString()));
		} else {
			preferences.put("fontChooser", "");
		}
	}

}
