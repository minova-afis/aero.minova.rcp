package aero.minova.rcp.rcp.processor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuContribution;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.ImageUtil;
import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.form.menu.mdi.Main;
import aero.minova.rcp.form.menu.mdi.Main.Action;
import aero.minova.rcp.form.menu.mdi.Main.Entry;
import aero.minova.rcp.form.menu.mdi.MenuType;
import aero.minova.rcp.form.setup.util.XBSUtil;
import aero.minova.rcp.form.setup.xbs.Map;
import aero.minova.rcp.form.setup.xbs.Node;
import aero.minova.rcp.form.setup.xbs.Preferences;

public class MenuProcessor {

	private EModelService modelService;
	private MApplication mApplication;

	private String usingLocalMenu = "There was no response from the server. Application data may be out of date. Consider checking your connection and restarting the application.";
	private String couldntLoadMenu = "There was no response from the server. The application isn't loaded properly. Please check your connection and restart the application.";
	private int menuId = 0;

	@Inject
	public MenuProcessor(EModelService modelService, IDataService dataService, MApplication mApplication, IEclipseContext context) {

		this.modelService = modelService;
		this.mApplication = mApplication;

		try {
			CompletableFuture<String> exceptionally = dataService.getHashedFile(Constants.MDI_FILE_NAME);
			String void1 = exceptionally.get();
			processXML(void1);
		} catch (Exception e) {
			e.printStackTrace();
			handleNoMDI(dataService);
		}

		File application = new File(dataService.getStoragePath() + "/" + Constants.MDI_FILE_NAME);
		if (!application.exists()) {
			handleNoMDI(dataService);
		}

		try {
			CompletableFuture<String> xbsFuture = dataService.getHashedFile(Constants.XBS_FILE_NAME);
			String xbsContent = xbsFuture.get();
			Preferences preferences = XmlProcessor.get(xbsContent, Preferences.class);
			mApplication.getTransientData().put(Constants.XBS_FILE_NAME, preferences);
			Node settingsNode = XBSUtil.getNodeWithName(preferences, "settings");
			if (settingsNode != null && settingsNode.getMap() != null && settingsNode.getMap().getEntry() != null) {
				Map map = settingsNode.getMap();
				for (aero.minova.rcp.form.setup.xbs.Map.Entry e : map.getEntry()) {
					if (e.getKey().equalsIgnoreCase("CustomerID")) {
						context.set("aero.minova.rcp.customerid", e.getValue());
					} else if (e.getKey().equalsIgnoreCase("ApplicationID")) {
						context.set("aero.minova.rcp.applicationid", e.getValue());
					}
				}
			}

		} catch (InterruptedException | ExecutionException | JAXBException e) {
			e.printStackTrace();
		}
	}

	private void processXML(String fileContent) {
		Main mainMDI = null;

		MMenuContribution menuContribution = modelService.createModelElement(MMenuContribution.class);
		menuContribution.setParentId("org.eclipse.ui.main.menu");
		menuContribution.setPositionInParent("after=additions");
		menuContribution.setElementId("generated" + menuId++);
		menuContribution.getPersistedState().put("persistState", "false");
		mApplication.getMenuContributions().add(menuContribution);
		try {
			mainMDI = XmlProcessor.get(fileContent, Main.class);

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
						MMenu createMenu = createMenu((MenuType) object, actionsMDI, modelService, mApplication);
						menuContribution.getChildren().add(createMenu);
					}
				}
			}

		} catch (JAXBException e) {
			e.printStackTrace();
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
	private MMenu createMenu(MenuType menu_MDI, HashMap<String, Action> actions_MDI, EModelService modelService, MApplication mApplication) {

		MMenu menuGen = modelService.createModelElement(MMenu.class);
		menuGen.getPersistedState().put("persistState", String.valueOf(false));
		menuGen.setElementId("generated" + menuId++);
		menuGen.setLabel(menu_MDI.getText());

		// TODO Sortierung des MENUS aus der MDI beachten!!!
		if (!menu_MDI.getEntryOrMenu().isEmpty() && menu_MDI.getEntryOrMenu() != null) {
			for (Object object : menu_MDI.getEntryOrMenu()) {
				if (object instanceof Entry) {
					Entry entryMDI = (Entry) object;
					if (!entryMDI.getType().equals("separator")) {
						String id2 = ((Action) entryMDI.getId()).getId();
						MHandledMenuItem handledMenuItem = createMenuEntry(entryMDI, actions_MDI.get(id2), modelService, mApplication);
						menuGen.getChildren().add(handledMenuItem);
					}

				}
			}
		}
		return menuGen;

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
	private MHandledMenuItem createMenuEntry(Entry entryMDI, Action actionMDI, EModelService modelService, MApplication mApplication) {

		MHandledMenuItem handledMenuItem = modelService.createModelElement(MHandledMenuItem.class);
		handledMenuItem.getPersistedState().put("persistState", String.valueOf(false));

		MCommand command = mApplication.getCommand("aero.minova.rcp.rcp.command.openform");
		handledMenuItem.setCommand(command);

		// set the form name as parameter
		MParameter mParameterForm = modelService.createModelElement(MParameter.class);
		mParameterForm.setElementId("parameter.formName." + entryMDI.getId()); // not used
		mParameterForm.setName(Constants.FORM_NAME);
		mParameterForm.setValue(actionMDI.getAction());
		handledMenuItem.getParameters().add(mParameterForm);

		// set the perspective id as parameter
		MParameter mParameterId = modelService.createModelElement(MParameter.class);
		mParameterId.setElementId("parameter.formId." + entryMDI.getId()); // not used
		mParameterId.setName(Constants.FORM_ID);
		mParameterId.setValue(actionMDI.getId());
		handledMenuItem.getParameters().add(mParameterId);

		// set the perspective name as parameter
		MParameter mParameterPerspectiveName = modelService.createModelElement(MParameter.class);
		mParameterPerspectiveName.setElementId("parameter.PerspectiveLabel." + entryMDI.getId()); // not used
		mParameterPerspectiveName.setName(Constants.FORM_LABEL);
		mParameterPerspectiveName.setValue(actionMDI.getText());
		handledMenuItem.getParameters().add(mParameterPerspectiveName);

		// set the perspective icon as parameter
		MParameter mParameterPerspectiveIcon = modelService.createModelElement(MParameter.class);
		mParameterPerspectiveIcon.setElementId("parameter.PerspectiveIcon." + entryMDI.getId()); // not used
		mParameterPerspectiveIcon.setName(Constants.FORM_ICON);
		mParameterPerspectiveIcon.setValue(actionMDI.getIcon());
		handledMenuItem.getParameters().add(mParameterPerspectiveIcon);

		handledMenuItem.setElementId("menuItemFor." + actionMDI.getId());

		handledMenuItem.setLabel(actionMDI.getText());

		String retrieveIcon = ImageUtil.retrieveIcon(actionMDI.getIcon(), false);
		handledMenuItem.setIconURI(retrieveIcon);

		return handledMenuItem;
	}

	private void handleNoMDI(IDataService dataService) {
		// Datei/Hash für Datei konnte nicht vom Server geladen werden, Versuchen lokale Datei zu nutzen
		try {
			processXML(dataService.getCachedFileContent(Constants.MDI_FILE_NAME).get());
			showConnectionErrorMessage(usingLocalMenu);
		} catch (InterruptedException | ExecutionException e1) {
			showConnectionErrorMessage(couldntLoadMenu);
		}
	}

	public void showConnectionErrorMessage(String message) {
		Shell shell = new Shell(Display.getCurrent());
		MessageDialog.openError(shell, "Error", message);
	}
}
