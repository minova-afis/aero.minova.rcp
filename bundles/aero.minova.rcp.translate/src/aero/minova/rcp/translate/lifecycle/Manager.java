package aero.minova.rcp.translate.lifecycle;

import java.time.ZoneId;
import java.util.Locale;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.internal.services.BundleTranslationProvider;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.workbench.lifecycle.PostContextCreate;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.translate.service.WFCTranslationService;

@SuppressWarnings("restriction")
public class Manager {

	Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);

	@PostContextCreate
	public void postContextCreate(IEclipseContext context) {
		context.set(TranslationService.LOCALE, CustomLocale.getLocale());
		configureTranslationService(context);
		if (!context.containsKey("aero.minova.rcp.applicationid")) {
			context.set("aero.minova.rcp.applicationid", "SIS");
		}
		if (!context.containsKey("aero.minova.rcp.customerid")) {
			context.set("aero.minova.rcp.customerid", "MIN");
		}
		initPrefs();
	}

	private void configureTranslationService(IEclipseContext context) {
		// der Eclipse translation service wird weiterhin für bestimmte Strings (aktuell
		// welche die mit % anfangen unter der Haube von dem Minova Übersetztungsservice
		// verwendet)
		Object currentTranslationService = context.get(TranslationService.class);

		if (currentTranslationService instanceof BundleTranslationProvider) {
			WFCTranslationService translationService = ContextInjectionFactory.make(WFCTranslationService.class, context);
			translationService.setTranslationService((TranslationService) currentTranslationService);
			context.set(TranslationService.class, translationService);
			openResetWorkspaceUIDialog(translationService, context);
		}
	}

	/**
	 * Öffnet den "Structural changes"-Dialog wenn sich die Model-Version geändert hat
	 *
	 * @param translationService
	 * @param context
	 */
	private void openResetWorkspaceUIDialog(WFCTranslationService translationService, IEclipseContext context) {
		boolean showMessage = context.get(Constants.SHOW_WORKSPACE_RESET_MESSAGE) != null && (boolean) context.get(Constants.SHOW_WORKSPACE_RESET_MESSAGE);
		if (showMessage) {
			MessageDialog.openWarning(Display.getCurrent().getActiveShell(), translationService.translate("@msg.WorkspaceResetTitle", null),
					translationService.translate("@msg.WorkspaceResetMessage", null));
			context.set(Constants.SHOW_WORKSPACE_RESET_MESSAGE, false);
		}
	}

	private void initPrefs() {
		String language = preferences.get(ApplicationPreferences.LOCALE_LANGUAGE, Locale.getDefault().getDisplayLanguage(Locale.getDefault()));
		preferences.put(ApplicationPreferences.LOCALE_LANGUAGE, language);
		String country = preferences.get(ApplicationPreferences.COUNTRY, Locale.getDefault().getDisplayCountry(Locale.getDefault()));
		preferences.put(ApplicationPreferences.COUNTRY, country);
		String timezone = preferences.get(ApplicationPreferences.TIMEZONE, ZoneId.systemDefault().getId());
		preferences.put(ApplicationPreferences.TIMEZONE, timezone);
		String font = preferences.get(ApplicationPreferences.FONT_ICON_SIZE, "M");
		preferences.put(ApplicationPreferences.FONT_ICON_SIZE, font);
		boolean indexautoload = preferences.getBoolean(ApplicationPreferences.AUTO_LOAD_INDEX, false);
		preferences.putBoolean(ApplicationPreferences.AUTO_LOAD_INDEX, indexautoload);
		boolean indexautoupdate = preferences.getBoolean(ApplicationPreferences.AUTO_RELOAD_INDEX, false);
		preferences.putBoolean(ApplicationPreferences.AUTO_RELOAD_INDEX, indexautoupdate);
		boolean reportwindow = preferences.getBoolean(ApplicationPreferences.SHEET_STYLES_MESSAGE_BOXES, true);
		preferences.putBoolean(ApplicationPreferences.SHEET_STYLES_MESSAGE_BOXES, reportwindow);
		boolean descriptionButton = preferences.getBoolean(ApplicationPreferences.SHOW_DETAIL_BUTTON_TEXT, true);
		preferences.putBoolean(ApplicationPreferences.SHOW_DETAIL_BUTTON_TEXT, descriptionButton);
		boolean maskbuffer = preferences.getBoolean(ApplicationPreferences.USE_FORM_BUFFER, true);
		preferences.putBoolean(ApplicationPreferences.USE_FORM_BUFFER, maskbuffer);
		int displaybuffer = preferences.getInt(ApplicationPreferences.DISPLAY_BUFFER_MS, 20);
		preferences.putInt(ApplicationPreferences.DISPLAY_BUFFER_MS, displaybuffer);
		int maxbuffer = preferences.getInt(ApplicationPreferences.MAX_BUFFER_MS, 90);
		preferences.putInt(ApplicationPreferences.MAX_BUFFER_MS, maxbuffer);
		int selectiondelay = preferences.getInt(ApplicationPreferences.TABLE_SELECTION_BUFFER_MS, 150);
		preferences.putInt(ApplicationPreferences.TABLE_SELECTION_BUFFER_MS, selectiondelay);
		boolean fadeinbuttontext = preferences.getBoolean(ApplicationPreferences.SHOW_BUTTON_TEXT, false);
		preferences.putBoolean(ApplicationPreferences.SHOW_BUTTON_TEXT, fadeinbuttontext);
		boolean buttondetailarea = preferences.getBoolean(ApplicationPreferences.SHOW_BUTTON_IN_SECTION, true);
		preferences.putBoolean(ApplicationPreferences.SHOW_BUTTON_IN_SECTION, buttondetailarea);
		boolean fadeingroups = preferences.getBoolean(ApplicationPreferences.SHOW_GROUPS, true);
		preferences.putBoolean(ApplicationPreferences.SHOW_GROUPS, fadeingroups);
		boolean showchangedrow = preferences.getBoolean(ApplicationPreferences.SHOW_CHANGED_ROWS, true);
		preferences.putBoolean(ApplicationPreferences.SHOW_CHANGED_ROWS, showchangedrow);
		boolean xmlxsdcreate = preferences.getBoolean(ApplicationPreferences.CREATE_XML_XS, false);
		preferences.putBoolean(ApplicationPreferences.CREATE_XML_XS, xmlxsdcreate);
		boolean optimizewidth = preferences.getBoolean(ApplicationPreferences.OPTIMIZED_WIDTHS, true);
		preferences.putBoolean(ApplicationPreferences.OPTIMIZED_WIDTHS, optimizewidth);
		boolean hideemptycolumn = preferences.getBoolean(ApplicationPreferences.HIDE_EMPTY_COLS, true);
		preferences.putBoolean(ApplicationPreferences.HIDE_EMPTY_COLS, hideemptycolumn);
		boolean hidegoupcolumns = preferences.getBoolean(ApplicationPreferences.HIDE_GROUP_COLS, true);
		preferences.putBoolean(ApplicationPreferences.HIDE_GROUP_COLS, hidegoupcolumns);
		boolean hidesearchdetails = preferences.getBoolean(ApplicationPreferences.HIDE_SEARCH_CRITERIAS, true);
		preferences.putBoolean(ApplicationPreferences.HIDE_SEARCH_CRITERIAS, hidesearchdetails);
		boolean deactivateinternpreview = preferences.getBoolean(ApplicationPreferences.DISABLE_PREVIEW, false);
		preferences.putBoolean(ApplicationPreferences.DISABLE_PREVIEW, deactivateinternpreview);
		int maxCharacter = preferences.getInt(ApplicationPreferences.MAX_CHARS, 24000);
		preferences.putInt(ApplicationPreferences.MAX_CHARS, maxCharacter);
		boolean gridTabNavigation = preferences.getBoolean(ApplicationPreferences.GRID_TAB_NAVIGATION, true);
		preferences.putBoolean(ApplicationPreferences.GRID_TAB_NAVIGATION, gridTabNavigation);
		boolean disableFileCache = preferences.getBoolean(ApplicationPreferences.DISABLE_FILE_CACHE, false);
		preferences.putBoolean(ApplicationPreferences.DISABLE_FILE_CACHE, disableFileCache);
		String fd = preferences.get(ApplicationPreferences.INDEX_FONT, null);
		if ("".equals(fd)) {
			fd = null;
		}
		FontData fdC = (fd == null ? null : new FontData(fd));
		if (fd != null) {
			preferences.put(ApplicationPreferences.INDEX_FONT, (fdC.toString()));
		} else {
			preferences.put(ApplicationPreferences.INDEX_FONT, "");
		}
		int indexLimit = preferences.getInt(ApplicationPreferences.INDEX_LIMIT, 1000);
		preferences.putInt(ApplicationPreferences.INDEX_LIMIT, indexLimit);

	}

}
