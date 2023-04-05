package aero.minova.rcp.rcp.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MCommandsFactory;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.osgi.service.prefs.BackingStoreException;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.parts.WFCSearchPart;

public class SeachCriteriaDynamicHandler {

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	ILog logger = Platform.getLog(this.getClass());

	public void aboutToShow(EModelService service, List<MMenuElement> items, MPart mpart, String action) {
		// Hier müssen wir wissen welche Form geladen ist, damit wir die Korrekten Kriterien laden.
		Table data = null;

		if (mpart.getObject() instanceof WFCSearchPart searchpart) {
			data = searchpart.getData();
		}

		if (data != null) {
			try {
				String[] keys = prefs.keys();
				List<String> keyList = new ArrayList<>();
				Collections.addAll(keyList, keys);
				Collections.sort(keyList);
				for (String s : keyList) {
					if (s.endsWith(".table") && s.startsWith(data.getName() + ".")) {
						MHandledMenuItem item = createMenuItem(service, s, action);
						if (!item.getLabel().equals(Constants.LAST_STATE)) { // Last_State ist nur zum wiederherstellen der UI
							items.add(item);
						}
					}
				}
			} catch (BackingStoreException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private MHandledMenuItem createMenuItem(EModelService service, String criteriaTableName, String action) {
		MHandledMenuItem mi = service.createModelElement(MHandledMenuItem.class);
		// Name aus dem Eintrag suchen!
		// vWorkingTime.erlanger Heute.table
		// vWorkingTime.erlanger Heute
		// erlanger Heute
		String displayName = criteriaTableName.replace(".table", "");
		displayName = displayName.substring(displayName.lastIndexOf(".") + 1, displayName.length());
		mi.setLabel(displayName);

		final MCommand cmd = MCommandsFactory.INSTANCE.createCommand();
		cmd.setElementId("aero.minova.rcp.rcp.command.searchCriteria");
		mi.setCommand(cmd);

		// action
		MParameter param = MCommandsFactory.INSTANCE.createParameter();
		param.setName("aero.minova.rcp.rcp.commandparameter.criteriaaction");
		param.setValue(action);
		mi.getParameters().add(param);
		// Name
		param = MCommandsFactory.INSTANCE.createParameter();
		param.setName("aero.minova.rcp.rcp.commandparameter.criterianame");
		param.setValue(displayName);
		mi.getParameters().add(param);

		// Handler der aufgerufen werden soll, wenn wir auf den Button drücken
		mi.getPersistedState().put("persistState", "false");
		return mi;
	}
}
