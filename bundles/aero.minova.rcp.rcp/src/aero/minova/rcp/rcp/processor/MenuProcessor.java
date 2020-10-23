package aero.minova.rcp.rcp.processor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.commands.MParameter;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.xml.sax.SAXException;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.dataservice.internal.XmlProcessor;
import aero.minova.rcp.form.menu.mdi.Main;
import aero.minova.rcp.form.menu.mdi.Main.Action;
import aero.minova.rcp.form.menu.mdi.Main.Entry;
import aero.minova.rcp.form.menu.mdi.MenuType;

public class MenuProcessor {

	static public String mdiFileName = "application.mdi";
	static public String mdiFileName_old = "ServicesInvoicingSystem_MDI.xml";
	static public String xbsFileName = "application.xbs";

	@Inject
	public MenuProcessor(@Named("org.eclipse.ui.main.menu") MMenu menu, EModelService modelService,
			IDataService dataService, MApplication mApplication) {

		// TODO Liste mit Nachzuladenen Masken/Ops erstellen und asynchroin nachladen.

		String basePath = null;
		File mdiFile = null;
		try {
			basePath = Platform.getInstanceLocation().getURL().toURI().toString();
			mdiFile = new File(new URI(basePath + mdiFileName));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		if (mdiFile == null || !mdiFile.exists())
			mdiFile = dataService.getFileSynch(basePath, mdiFileName);

		// hier ist der FallBack auf eine bereits bestehendes MDI File aus dem Verzeichnis.
		basePath = basePath + mdiFileName_old;

		Main mainMDI = readOutMDI(basePath, mdiFile);

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
	 * Diese Methode erstellt aus den übergebenen Pfad und der Datei ein Main Element aus einer MDI. Wenn der File nicht
	 * eingelesen werden kann, gibt es noch einen FallBack auf die URI, beim Aufruf könnten so unterschiedliche
	 * Dateiorte übergeben werden.
	 *
	 * Zum Beispiel: basePath ist der default mit dem Standard, mdiFile könnte eine neue Version der Datei sein, die
	 * erstmal in einem anderen Ordner liegt.
	 *
	 *
	 * @param basePath
	 * @param mdiFile
	 * @return Main oder null wenn es nicht ausgelesen werden kann
	 */
	public Main readOutMDI(String basePath, File mdiFile) {
		URI uri = null;
		Main mainMDI = null;
		XmlProcessor xmlProcessor = new XmlProcessor(Main.class);

		if (mdiFile != null) {
			try {
				mainMDI = (Main) xmlProcessor.load(mdiFile);
				return mainMDI;
			} catch (JAXBException | SAXException | IOException e) {
				e.printStackTrace();
			}
		}
		try {
			uri = new URI(basePath);
			mainMDI = (Main) xmlProcessor.load(Path.of(uri).toFile());
		} catch (JAXBException | SAXException | IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		return mainMDI;
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
	 * @param entry_MDI
	 * @param action_MDI
	 * @param modelService
	 * @param mApplication
	 * @return
	 */
	private MHandledMenuItem createMenuEntry(Entry entry_MDI, Action action_MDI, EModelService modelService,
			MApplication mApplication) {

		MHandledMenuItem handledMenuItem = modelService.createModelElement(MHandledMenuItem.class);
		MCommand command = mApplication.getCommand("aero.minova.rcp.rcp.command.openform");
		MParameter mParameter = modelService.createModelElement(MParameter.class);
		handledMenuItem.setCommand(command);
		mParameter.setElementId("org.eclipse.e4.ui.perspectives.parameters.perspectiveId" + entry_MDI.getId());
		mParameter.setName("org.eclipse.e4.ui.perspectives.parameters.perspectiveId");
		mParameter.setValue(action_MDI.getAction());

		handledMenuItem.getParameters().add(mParameter);
		handledMenuItem.setElementId(action_MDI.getId());

		// TODO Übersetzung
		handledMenuItem.setLabel(action_MDI.getText());
		// TODO Icon setzen
		handledMenuItem.setIconURI(action_MDI.getIcon());

		handledMenuItem.getPersistedState().put("persistState", String.valueOf(false));
		return handledMenuItem;

	}

}
