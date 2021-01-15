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
		String language = preferences.get("LocalLanguage", Locale.getDefault().getDisplayLanguage(Locale.getDefault()));
		preferences.put("LocalLanguage", language);
		String country = preferences.get("country", Locale.getDefault().getDisplayCountry(Locale.getDefault()));
		preferences.put("country", country);
		String timezone = preferences.get("Timezone", ZoneId.systemDefault().getId());
		preferences.put("Timezone", timezone);
		String font = preferences.get("FontSize", "M");
		preferences.put("FontSize", font);
		String symbolMenu = preferences.get("IconSize", "24x24");
		preferences.put("IconSize", symbolMenu);
		String symbolToolbar = preferences.get("IconSizeBig", "32x32");
		preferences.put("IconSizeBig", symbolToolbar);
		boolean masks = preferences.getBoolean("AllowMultipleForms", false);
		preferences.putBoolean("AllowMultipleForms", masks);
		boolean dragdrop = preferences.getBoolean("DisallowDragAndDrop", false);
		preferences.putBoolean("DisallowDragAndDrop", dragdrop);
		boolean icons = preferences.getBoolean("ShowAllActioninToolbar", false);
		preferences.putBoolean("ShowAllActioninToolbar", icons);
		boolean indexautoload = preferences.getBoolean("AutoLoadIndex", false);
		preferences.putBoolean("AutoLoadIndex", indexautoload);
		boolean indexautoupdate = preferences.getBoolean("AutoReloadIndex", false);
		preferences.putBoolean("AutoReloadIndex", indexautoupdate);
		boolean reportwindow = preferences.getBoolean("SheetStylesMessageBoxes", true);
		preferences.putBoolean("SheetStylesMessageBoxes", reportwindow);
		boolean descriptionButton = preferences.getBoolean("ShowDetailButtonText", true);
		preferences.putBoolean("ShowDetailButtonText", descriptionButton);
		boolean maskbuffer = preferences.getBoolean("UseFormBuffer", true);
		preferences.putBoolean("UseFormBuffer", maskbuffer);
		int displaybuffer = preferences.getInt("DisplayBufferMs", 20);
		preferences.putInt("DisplayBufferMs", displaybuffer);
		int maxbuffer = preferences.getInt("MaxBufferMs", 90);
		preferences.putInt("MaxBufferMs", maxbuffer);
		int selectiondelay = preferences.getInt("TableSelectionBufferMs", 150);
		preferences.putInt("TableSelectionBufferMs", selectiondelay);
		boolean sizeautoadjust = preferences.getBoolean("AutoResize", false);
		preferences.putBoolean("AutoResize", sizeautoadjust);
		boolean fadeinbuttontext = preferences.getBoolean("ShowButtonText", false);
		preferences.putBoolean("ShowButtonText", fadeinbuttontext);
		boolean buttondetailarea = preferences.getBoolean("ShowButtonsInSection", true);
		preferences.putBoolean("ShowButtonsInSection", buttondetailarea);
		boolean showlookups = preferences.getBoolean("ShowLookups", true);
		preferences.putBoolean("ShowLookups", showlookups);
		boolean fadeingroups = preferences.getBoolean("ShowGroups", true);
		preferences.putBoolean("ShowGroups", fadeingroups);
		boolean showchangedrow = preferences.getBoolean("ShowChangedRows", true);
		preferences.putBoolean("ShowChangedRows", showchangedrow);
		boolean xmlxsdcreate = preferences.getBoolean("CreateXMLXS", false);
		preferences.putBoolean("CreateXMLXS", xmlxsdcreate);
		boolean optimizewidth = preferences.getBoolean("OptimizeWidths", true);
		preferences.putBoolean("OptimizeWidths", optimizewidth);
		boolean hideemptycolumn = preferences.getBoolean("HideEmptyCols", true);
		preferences.putBoolean("HideEmptyCols", hideemptycolumn);
		boolean hidegoupcolumns = preferences.getBoolean("HideGroupCols", true);
		preferences.putBoolean("HideGroupCols", hidegoupcolumns);
		boolean hidesearchdetails = preferences.getBoolean("HideSearchCriterias", true);
		preferences.putBoolean("HideSearchCriterias", hidesearchdetails);
		boolean deactivateinternpreview = preferences.getBoolean("DisablePreview", false);
		preferences.putBoolean("DisablePreview", deactivateinternpreview);
		int maxCharacter = preferences.getInt("MaxChars", 24000);
		preferences.putInt("MaxChars", maxCharacter);
		String user = preferences.get("UserPreselectDescription", System.getProperty("user.name"));
		preferences.put("UserPreselectDescription", user);
		String fd = preferences.get("IndexFont", null);
		if ("".equals(fd))
			fd = null;
		FontData fdC = (fd == null ? null : new FontData(fd));
		if (fd != null) {
			preferences.put("IndexFont", (fdC.toString()));
		} else {
			preferences.put("IndexFont", "");
		}
	}

}
