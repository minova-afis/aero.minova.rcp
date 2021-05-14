package aero.minova.rcp.rcp.processor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import aero.minova.rcp.constants.Constants;
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

	private String usingLocalMenu = "There was no response from the server. Application data may be out of date. Consider checking your connection and restarting the application.";
	private String couldntLoadMenu = "There was no response from the server. The application isn't loaded properly. Please check your connection and restart the application.";

	@Inject
	public MenuProcessor(@Named("org.eclipse.ui.main.menu") MMenu menu, EModelService modelService, IDataService dataService, MApplication mApplication) {

		this.menu = menu;
		this.modelService = modelService;
		this.mApplication = mApplication;

		try {
			dataService.getHashedFile(MDI_FILE_NAME).thenAccept(this::processXML);
		} catch (Exception e) {
			handleNoMDI(dataService);
		}

		File application = new File(dataService.getStoragePath() + "/" + MDI_FILE_NAME);
		if (!application.exists()) {
			handleNoMDI(dataService);
		}
	}

	private void processXML(String fileContent) {
		Main mainMDI = null;
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
						createMenu((MenuType) object, menu, actionsMDI, modelService, mApplication);
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
	private void createMenu(MenuType menu_MDI, MMenu menu, HashMap<String, Action> actions_MDI, EModelService modelService, MApplication mApplication) {
		MMenu menuGen = modelService.createModelElement(MMenu.class);
		menuGen.getPersistedState().put("persistState", String.valueOf(false));

		menuGen.setLabel(menu_MDI.getText());

		// TODO Sortierung des MENUS aus der MDI beachten!!!
		if (!menu_MDI.getEntryOrMenu().isEmpty() && menu_MDI.getEntryOrMenu() != null) {
			for (Object object : menu_MDI.getEntryOrMenu()) {
				if (object instanceof Entry) {
					Entry entryMDI = (Entry) object;
					String id2 = ((Action) entryMDI.getId()).getId();
					MHandledMenuItem handledMenuItem = createMenuEntry(entryMDI, actions_MDI.get(id2), modelService, mApplication);
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

		handledMenuItem.setElementId("menuItemFor." + actionMDI.getId());

		handledMenuItem.setLabel(actionMDI.getText());

		handledMenuItem.setIconURI(actionMDI.getIcon());

		return handledMenuItem;
	}

	private void handleNoMDI(IDataService dataService) {
		// Datei/Hash für Datei konnte nicht vom Server geladen werden, Versuchen lokale Datei zu nutzen
		try {
			processXML(dataService.getCachedFileContent(MDI_FILE_NAME).get());
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
