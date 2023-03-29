package aero.minova.rcp.rcp.addons;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.util.ErrorObject;
import aero.minova.rcp.rcp.handlers.ShowErrorDialogHandler;
import aero.minova.rcp.util.DateUtil;
import aero.minova.rcp.widgets.MinovaNotifier;

public class NotificationAndErrorAddon {

	@Inject
	TranslationService translationService;

	@Inject
	ECommandService commandService;

	@Inject
	EHandlerService handlerService;

	Locale locale;

	@Inject
	@Named(IServiceConstants.ACTIVE_SHELL)
	Shell shell;

	ILog logger = Platform.getLog(this.getClass());

	private static final String ERROR = "Error";
	private static final String DEFAULT = "DEFAULT";

	/**
	 * Liefert das übersetzte Objekt zurück
	 *
	 * @param translate
	 * @return
	 */
	private String getTranslation(String translate) {
		if (!translate.startsWith("@")) {
			translate = "@" + translate;
		}
		return translationService.translate(translate, null);
	}

	@Inject
	@Optional
	public void showErrorMessage(@UIEventTopic(Constants.BROKER_SHOWERRORMESSAGE) String message) {
		// Fokus auf den Search Part legen, damit Fehlermeldungen nicht mehrmals angezeigt werden
		selectSearchPart();

		MessageDialog.openError(shell, getTranslation(ERROR), getTranslation(message));
	}

	@Inject
	@Optional
	public void showErrorMessage(@UIEventTopic(Constants.BROKER_SHOWERROR) ErrorObject et) {
		String value = formatMessage(et);

		value += "\n\nUser: " + et.getUser();
		if (et.getProcedureOrView() != null) {
			value += "\nProcedure/View: " + et.getProcedureOrView();
		}

		// Fokus auf den Search Part legen, damit Fehlermeldungen von Lookups nicht mehrmals angezeigt werden
		selectSearchPart();

		if (et.getT() == null) {
			MessageDialog.openError(shell, getTranslation(ERROR), value);
		} else {
			ShowErrorDialogHandler.execute(shell, getTranslation(ERROR), value, et.getT());
		}
	}

	private void selectSearchPart() {
		try {
			String commandID = Constants.AERO_MINOVA_RCP_RCP_COMMAND_SELECTSEARCHPART;
			ParameterizedCommand cmd = commandService.createCommand(commandID, null);
			handlerService.executeHandler(cmd);
		} catch (Exception e) {
			// Teilweise wird das aufgerufen bevor der Handler aufgebaut ist, die Exception soll verhindert werden
		}
	}

	private String formatMessage(ErrorObject et) {
		Table errorTable = et.getErrorTable();

		if (errorTable == null || errorTable.getRows().get(0).getValue(0) == null) {
			return getTranslation(et.getMessage());
		}

		Value vMessageProperty = errorTable.getRows().get(0).getValue(0);
		String messageproperty = vMessageProperty.getStringValue().strip();
		String value = translationService.translate("@" + messageproperty, null);

		if (value.equals(messageproperty) && errorTable.getColumnIndex(DEFAULT) != -1) { // Keine Übersetzung gefunden -> Default nutzen
			value = errorTable.getRows().get(0).getValue(errorTable.getColumnIndex(DEFAULT)).getStringValue();
		} else if (errorTable.getColumnCount() > 1) {// Ticket number {0} is not numeric

			value = value.replaceAll("%(\\d*)", "{$1}"); // %n fürs Formattieren mit {n} ersetzen

			List<String> params = new ArrayList<>();
			for (int i = 1; i < errorTable.getColumnCount(); i++) {
				Value v = errorTable.getRows().get(0).getValue(i);
				String columnName = errorTable.getColumnName(i);
				switch (columnName) {
				case "p":
					params.add(translationService.translate("@" + v.getStringValue(), null));
					break;
				case "i":
					try {
						params.add("" + NumberFormat.getInstance(locale).parse(v.getStringValue()).intValue());
					} catch (ParseException e) {
						logger.error(e.getMessage(), e);
					}
					break;
				case "f.iso":
					params.add("" + Float.parseFloat(v.getStringValue()));
					break;
				case "f":
					try {
						params.add("" + NumberFormat.getInstance(locale).parse(v.getStringValue()).floatValue());
					} catch (ParseException e) {
						logger.error(e.getMessage(), e);
					}
					break;
				case "d.iso":
					try {
						Date parsedDate = new SimpleDateFormat("yyyyMMdd").parse(v.getStringValue());
						params.add(DateUtil.getDateString(parsedDate.toInstant(), locale, null));
					} catch (ParseException e) {
						logger.error(e.getMessage(), e);
					}
					break;
				case "d":
					try {
						Date parsedDate = new SimpleDateFormat("ddMMyyyy").parse(v.getStringValue());
						params.add(DateUtil.getDateString(parsedDate.toInstant(), locale, null));
					} catch (ParseException e) {
						logger.error(e.getMessage(), e);
					}
					break;
				case DEFAULT: // Spalte mit DEFAULT-String -> kein Param
					break;
				default:
					// "s" oder ungültiger Spaltenname -> String
					params.add(v.getStringValue());
					break;
				}
			}
			value = MessageFormat.format(value, params.toArray());
		}

		return value;
	}

	@Inject
	@Optional
	public void showNotification(@UIEventTopic(Constants.BROKER_SHOWNOTIFICATION) String message) {
		openNotificationPopup(message);
	}

	@Inject
	@Optional
	public void showNotification(@UIEventTopic(Constants.BROKER_SHOWNOTIFICATION) ErrorObject et) {
		openNotificationPopup(formatMessage(et));
	}

	/**
	 * Öffet ein Popup, welches dem Nutzer über den Erfolg oder das Scheitern seiner Anfrage informiert
	 *
	 * @param message
	 */
	public void openNotificationPopup(String message) {
		if (shell != null && !shell.getDisplay().isDisposed()) {
			MinovaNotifier.show(shell, getTranslation(message), getTranslation("Notification"));
		}
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
	}
}
