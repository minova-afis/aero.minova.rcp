package aero.minova.rcp.rcp.handlers;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.parts.WFCSearchPart;
import aero.minova.rcp.rcp.util.LoadTableSelection;
import aero.minova.rcp.rcp.util.PersistTableSelection;
import aero.minova.rcp.rcp.util.SaveSearchCriteriaDialog;

public class SearchCriteriaHandler {
	public enum CriteriaAction {
		LOAD, SAVE, DELETE, LOAD_DEFAULT, SAVE_DEFAULT, SAVE_NAME
	}

	public static final String COMMAND = "aero.minova.rcp.rcp.command.searchCriteria";
	public static final String COMMAND_ACTION = "aero.minova.rcp.rcp.commandparameter.criteriaaction";
	public static final String COMMAND_NAME = "aero.minova.rcp.rcp.commandparameter.criterianame";

	/**
	 * @param action
	 *            (not used)
	 * @param part
	 * @return
	 */
	@CanExecute
	public boolean canExecute(@Named(COMMAND_ACTION) final String action, final MPart part) {
		return part.getObject() != null;
	}

	@Inject
	IEventBroker broker;

	@Inject
	TranslationService translationService;

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Execute
	public void execute(@Named(COMMAND_ACTION) final String action, @Optional @Named(COMMAND_NAME) String name, final MPart part,
			@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell, final MPerspective perspective) {
		Object wfcPart = part.getObject();
		if (wfcPart != null) {
			final CriteriaAction ia = CriteriaAction.valueOf(action.toUpperCase(Locale.ENGLISH));
			IEclipseContext context = part.getContext();
			String tableName = null;
			if (wfcPart instanceof WFCSearchPart) {
				Table data = ((WFCSearchPart) wfcPart).getData();
				tableName = data.getName();
			}

			try {
				switch (ia) {
				case LOAD_DEFAULT:
					name = Constants.SEARCHCRITERIA_DEFAULT;
				case LOAD:
					context.set("ConfigName", name);// setzen der Konfiguration, verfügbar auch später.
					ContextInjectionFactory.invoke(part.getObject(), LoadTableSelection.class, context);
					broker.send(Constants.BROKER_LOADSEARCHCRITERIA, name);
					break;
				case SAVE_DEFAULT:
					name = Constants.SEARCHCRITERIA_DEFAULT;// setzen der Konfiguration, verfügbar auch später.
				case SAVE_NAME:
					context.set("SaveRowConfig", true);// setzen der Konfiguration, verfügbar auch später.
					context.set("ConfigName", name);// setzen der Konfiguration, verfügbar auch später.
					ContextInjectionFactory.invoke(part.getObject(), PersistTableSelection.class, context);
					broker.send(Constants.BROKER_SAVESEARCHCRITERIA, Constants.SEARCHCRITERIA_DEFAULT);
					break;
				case SAVE:
					final SaveSearchCriteriaDialog sscd = new SaveSearchCriteriaDialog(shell, translationService, prefs, tableName);
					String criteriaName = sscd.open();
					boolean saveColumnWidth = sscd.getSaveWidths();
					context.set("SaveRowConfig", saveColumnWidth);// setzen der Konfiguration, verfügbar auch später.
					context.set("ConfigName", criteriaName);// setzen der Konfiguration, verfügbar auch später.
					ContextInjectionFactory.invoke(part.getObject(), PersistTableSelection.class, context);
					if (saveColumnWidth) {
						broker.send(Constants.BROKER_SAVESEARCHCRITERIA, criteriaName);
					}
					break;
				case DELETE:
					final DeleteSearchCriteriaDialog dscd = new DeleteSearchCriteriaDialog(shell, translationService, prefs, tableName);
					dscd.open();
					break;
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}