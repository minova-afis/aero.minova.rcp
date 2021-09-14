
package aero.minova.rcp.rcp.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.AboutToShow;
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

public class SearchCriteriaSaveHandler {

	@Inject
	@Preference
	private IEclipsePreferences prefs;

	@AboutToShow
	public void aboutToShow(EModelService service, List<MMenuElement> items, MPart mpart) {

		Table data = null;

		Object part = mpart.getObject();
		if (part instanceof WFCSearchPart) {
			data = ((WFCSearchPart) part).getData();
		}

		if (data != null) {
			try {
				String[] keys = prefs.keys();
				List<String> keyList = new ArrayList<>();
				for (String string : keys) {
					keyList.add(string);
				}
				Collections.sort(keyList);
				for (String s : keyList) {
					if (s.endsWith(".table") && s.startsWith(data.getName() + ".")) {
						MHandledMenuItem md = createMenuItem(service, s);
						if (!md.getLabel().equals(Constants.LAST_STATE)) { // Last_State ist nur zum wiederherstellen der UI
							items.add(md);
						}
					}
				}
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}

		}

	}

	private MHandledMenuItem createMenuItem(EModelService service, String criteriaTableName) {
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
		param.setValue("SAVE_NAME");
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