package aero.minova.rcp.rcp.handlers;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.parts.WFCIndexPart;
import aero.minova.rcp.rcp.parts.WFCSearchPart;
import aero.minova.rcp.rcp.util.DeleteSearchCriteriaDialog;
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

	@Inject
	TranslationService translationService;

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@Inject
	private EPartService partService;

	ILog logger = Platform.getLog(this.getClass());

	Preferences loadedTablePrefs = InstanceScope.INSTANCE.getNode(Constants.LAST_LOADED_SEARCHCRITERIA);

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

	@Execute
	public void execute(@Named(COMMAND_ACTION) final String action, @Optional @Named(COMMAND_NAME) String name, final MPart part,
			@Named(IServiceConstants.ACTIVE_SHELL) final Shell shell, final MPerspective perspective) {

		WFCSearchPart wfcSearchPart = (WFCSearchPart) part.getObject();
		WFCIndexPart wfcIndexPart = (WFCIndexPart) partService.findPart(Constants.INDEX_PART).getObject();

		if (wfcSearchPart != null) {
			final CriteriaAction ia = CriteriaAction.valueOf(action.toUpperCase(Locale.ENGLISH));
			IEclipseContext context = perspective.getContext();
			Table data = wfcSearchPart.getData();
			String tableName = data.getName();

			try {
				switch (ia) {
				case LOAD_DEFAULT:
					name = Constants.SEARCHCRITERIA_DEFAULT;
				case LOAD:
					context.set("ConfigName", name);// setzen der Konfiguration, verfügbar auch später.
					ContextInjectionFactory.invoke(wfcSearchPart, LoadTableSelection.class, context);
					ContextInjectionFactory.invoke(wfcIndexPart, LoadTableSelection.class, context);
					loadedTablePrefs.put(Constants.LAST_SEARCHCRITERIA, name);
					break;
				case SAVE_DEFAULT:
					MessageDialog md = new MessageDialog(shell, translationService.translate("@Command.Warning", null), null,
							translationService.translate("@SelectionCriteria.WillBeOverwritten", null), MessageDialog.QUESTION, 0,
							translationService.translate("@Yes", null), translationService.translate("@No", null));
					int openQuestion = md.open();
					if (openQuestion != 0) {
						break;
					}
					name = Constants.SEARCHCRITERIA_DEFAULT;// setzen der Konfiguration, verfügbar auch später.
				case SAVE_NAME:

					invoke(true, name, perspective, context, wfcSearchPart, wfcIndexPart);

					break;
				case SAVE:
					final SaveSearchCriteriaDialog sscd = new SaveSearchCriteriaDialog(shell, translationService, prefs, tableName);
					sscd.open();
					String criteriaName = sscd.getCriteriaName();
					if (criteriaName == null) {
						break;
					}
					boolean saveColumnWidth = sscd.getSaveWidths();

					invoke(saveColumnWidth, criteriaName, perspective, context, wfcSearchPart, wfcIndexPart);

					loadedTablePrefs.put(Constants.LAST_SEARCHCRITERIA, criteriaName);
					break;
				case DELETE:
					final DeleteSearchCriteriaDialog dscd = new DeleteSearchCriteriaDialog(shell, translationService, prefs, tableName);
					dscd.open();
					break;
				}
			} catch (final Exception ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}

	private void invoke(boolean saveColumnWidth, String criteriaName, MPerspective perspective, IEclipseContext context, WFCSearchPart wfcSearchPart,
			WFCIndexPart wfcIndexPart) {
		context.set("SaveRowConfig", saveColumnWidth);// setzen der Konfiguration, verfügbar auch später.
		context.set("ConfigName", criteriaName);// setzen der Konfiguration, verfügbar auch später.
		ContextInjectionFactory.invoke(wfcSearchPart, PersistTableSelection.class, context);
		ContextInjectionFactory.invoke(wfcIndexPart, PersistTableSelection.class, context);
	}

}