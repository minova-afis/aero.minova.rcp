package aero.minova.rcp.rcp.processor;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.inject.Inject;
import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
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
import aero.minova.rcp.form.setup.xbs.Preferences;
import aero.minova.rcp.form.setup.xbs.Preferences.Root;
import aero.minova.rcp.rcp.util.CustomerPrintData;

public class MenuProcessor {

	private static final String PERSIST_STATE = "persistState";
	private EModelService modelService;
	private MApplication mApplication;
	ILog logger = Platform.getLog(this.getClass());
	private String usingLocalMenu = "There was no response from the server. Application data may be out of date. Consider checking your connection and restarting the application.";
	private String couldntLoadMenu = "There was no response from the server. The application isn't loaded properly. Please check your connection and restart the application.";
	private int menuId = 0;

	@Inject
	public MenuProcessor(EModelService modelService, IDataService dataService, MApplication mApplication, IEclipseContext context) {

		this.modelService = modelService;
		this.mApplication = mApplication;

		// MDI (für Menü) herunterladen und parsen
		try {
			String mdiString = dataService.getHashedFile(Constants.MDI_FILE_NAME).get();
			processXML(mdiString);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			handleNoMDI(dataService);
		}

		File application = new File(dataService.getStoragePath().resolve(Constants.MDI_FILE_NAME).toString());
		if (!application.exists()) {
			handleNoMDI(dataService);
		}

		// XBS (für weitere Einstellungen, z.B. OPs, Drucks, Statistiken, ...) herunterladen und parsen
		try {
			CompletableFuture<String> xbsFuture = dataService.getHashedFile(Constants.XBS_FILE_NAME);
			String xbsContent = xbsFuture.get();
			Preferences preferences = XmlProcessor.get(xbsContent, Preferences.class);

			mApplication.getTransientData().put(Constants.XBS_FILE_NAME, preferences);

			java.util.Map<String, String> mapOfNode = XBSUtil.getMapOfNode(preferences, "settings");
			if (mapOfNode.containsKey("CustomerID")) {
				context.set("aero.minova.rcp.customerid", mapOfNode.get("CustomerID"));
			}
			if (mapOfNode.containsKey("ApplicationID")) {
				context.set("aero.minova.rcp.applicationid", mapOfNode.get("ApplicationID"));
			}

			// Daten für Detail- und Indexdruck
			mApplication.getTransientData().put(Constants.CUSTOMER_PRINT_DATA, new CustomerPrintData(//
					mapOfNode.get("siteaddress1"), //
					mapOfNode.get("siteaddress2"), //
					mapOfNode.get("siteaddress3"), //
					mapOfNode.get("sitephone"), //
					mapOfNode.get("sitefax"), //
					mapOfNode.get("siteemail")));

		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		} catch (ExecutionException | JAXBException e) {
			// Bei Fehler leere preference erstellen, siehe #1267
			Preferences preferences = new Preferences();
			preferences.setRoot(new Root());
			preferences.getRoot().setMap(new Map());

			mApplication.getTransientData().put(Constants.XBS_FILE_NAME, preferences);
		}
	}

	private void processXML(String fileContent) {
		Main mainMDI = null;

		MMenuContribution menuContribution = modelService.createModelElement(MMenuContribution.class);
		menuContribution.setParentId("org.eclipse.ui.main.menu");
		menuContribution.setPositionInParent("after=additions");
		menuContribution.setElementId("generated" + menuId++);
		menuContribution.getPersistedState().put(PERSIST_STATE, "false");
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
					if (object instanceof MenuType mt) {
						MMenu createMenu = createMenu(mt, actionsMDI, modelService, mApplication);
						menuContribution.getChildren().add(createMenu);
					}
				}
			}

		} catch (JAXBException e) {
			logger.error(e.getMessage(), e);
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
	private MMenu createMenu(MenuType menuMDI, HashMap<String, Action> actionsMDI, EModelService modelService, MApplication mApplication) {

		MMenu menuGen = modelService.createModelElement(MMenu.class);
		menuGen.getPersistedState().put(PERSIST_STATE, String.valueOf(false));
		menuGen.setElementId("generated" + menuId++);
		menuGen.setLabel(menuMDI.getText());

		if (!menuMDI.getEntryOrMenu().isEmpty() && menuMDI.getEntryOrMenu() != null) {
			for (Object object : menuMDI.getEntryOrMenu()) {
				if (object instanceof Entry entryMDI && !entryMDI.getType().equals("separator")) {
					String id2 = ((Action) entryMDI.getId()).getId();
					MHandledMenuItem handledMenuItem = createMenuEntry(entryMDI, actionsMDI.get(id2), modelService, mApplication);
					menuGen.getChildren().add(handledMenuItem);
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
		handledMenuItem.getPersistedState().put(PERSIST_STATE, String.valueOf(false));

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
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			showConnectionErrorMessage(couldntLoadMenu);
			Thread.currentThread().interrupt();
		} catch (ExecutionException e1) {
			showConnectionErrorMessage(couldntLoadMenu);
		}
	}

	public void showConnectionErrorMessage(String message) {
		Shell shell = new Shell(Display.getCurrent());
		MessageDialog.openError(shell, "Error", message);
	}
}
