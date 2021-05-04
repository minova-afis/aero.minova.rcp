package aero.minova.rcp.rcp.processor;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.form.menu.mdi.Main;
import aero.minova.rcp.form.menu.mdi.Main.Action;
import aero.minova.rcp.form.menu.mdi.Main.Entry;
import aero.minova.rcp.form.menu.mdi.MenuType;

public class MenuProcessor {

	public static final String MDI_FILE_NAME = "application.mdi";
	public static final String XBS_FILE_NAME = "application.xbs";
	private MMenu menu;
	private EModelService modelService;
	private MApplication mApplication;

	@Inject
	public MenuProcessor(@Named("org.eclipse.ui.main.menu") MMenu menu, EModelService modelService,
			IDataService dataService, MApplication mApplication) {

		this.menu = menu;
		this.modelService = modelService;
		this.mApplication = mApplication;
		dataService.getHashedFile(MDI_FILE_NAME).thenAccept(fileContent -> processXML(fileContent));
		
	}

	private void processXML(String fileContent) {
		Main mainMDI = null;
		try {
			mainMDI = XmlProcessor.get(fileContent, Main.class);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		if (mainMDI != null) {
			List<Object> menuOrEntry = mainMDI.getMenu().getMenuOrEntry();
			if (!menuOrEntry.isEmpty()) {

				HashMap<String, Action> actionsMDI = new HashMap<>();
				if (mainMDI.getAction() != null && !mainMDI.getAction().isEmpty()) {
					for (Action action : mainMDI.getAction()) {
						actionsMDI.put(action.getId(), action);
					}
				}

				for (Object object : menuOrEntry) {
					if (object instanceof MenuType) {
						createMenu((MenuType) object, menu, actionsMDI, modelService, mApplication);
					}
				}
			}
		}
	}

	/**
	 * Diese Methode erstellt aus dem übergebenen menu ein MMenu inklusiver der Einträge
	 *
	 * @param menu_MDI
	 * @param menu
	 * @param actions_MDI
	 * @param modelService
	 * @param mApplication
	 */
	private void createMenu(MenuType menu_MDI, MMenu menu, HashMap<String, Action> actions_MDI,
			EModelService modelService, MApplication mApplication) {
		MMenu menuGen = modelService.createModelElement(MMenu.class);
		menuGen.getPersistedState().put("persistState", String.valueOf(false));
		// TODO Übersetzung einbauen
		menuGen.setLabel(menu_MDI.getText());

		// TODO Sortierung des MENUS aus der MDI beachten!!!
		if (!menu_MDI.getEntryOrMenu().isEmpty() && menu_MDI.getEntryOrMenu() != null) {
			for (Object object : menu_MDI.getEntryOrMenu()) {
				if (object instanceof Entry) {
					Entry entryMDI = (Entry) object;
					String id2 = ((Action) entryMDI.getId()).getId();
					MHandledMenuItem handledMenuItem = createMenuEntry(entryMDI, actions_MDI.get(id2), modelService,
							mApplication);
					menuGen.getChildren().add(handledMenuItem);
					menu.getChildren().add(menuGen);
				}
			}
		}
	}

	/**
	 * Diese Methode erstellt einen Eintrag für ein MMenu aus dem übergebenen MDI-Eintrag
	 *
	 * @param entryMDI
	 * @param actionMDI
	 * @param modelService
	 * @param mApplication
	 * @return
	 */
	private MHandledMenuItem createMenuEntry(Entry entryMDI, Action actionMDI, EModelService modelService,
			MApplication mApplication) {

		MHandledMenuItem handledMenuItem = modelService.createModelElement(MHandledMenuItem.class);
		MCommand command = mApplication.getCommand("aero.minova.rcp.rcp.command.openform");
		MParameter mParameter = modelService.createModelElement(MParameter.class);
		handledMenuItem.setCommand(command);
		mParameter.setElementId("org.eclipse.e4.ui.perspectives.parameters.perspectiveId" + entryMDI.getId());
		mParameter.setName("aero.minova.rcp.perspectiveswitcher.parameters.formName");
		mParameter.setValue(actionMDI.getAction());

		handledMenuItem.getParameters().add(mParameter);
		handledMenuItem.setElementId(actionMDI.getId());

		handledMenuItem.setLabel(actionMDI.getText());
		// TODO Icon setzen
		handledMenuItem.setIconURI(actionMDI.getIcon());

		handledMenuItem.getPersistedState().put("persistState", String.valueOf(false));
		return handledMenuItem;

	}

}
