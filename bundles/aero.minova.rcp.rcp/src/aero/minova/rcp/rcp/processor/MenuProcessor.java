package aero.minova.rcp.rcp.processor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

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

public class MenuProcessor {

	@Inject
	public MenuProcessor(@Named("org.eclipse.ui.main.menu") MMenu menu, EModelService modelService,
			IDataService dataService, MApplication mApplication) {
		System.out.println("Starting to process the model " + menu);
		// TODO Download and parse file
		// dataService.getFile(null);
		// dataService.getFile("mdi.xml");

		String basePath = null;
		try {
			basePath = Platform.getInstanceLocation().getURL().toURI().toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		basePath = basePath + "mdi.xml";

		URI uri = null;
		Main mdi_menu = null;
		try {
			uri = new URI(basePath);
			XmlProcessor xmlProcessor = new XmlProcessor(Main.class);
			mdi_menu = (Main) xmlProcessor.load(Path.of(uri).toFile());
		} catch (JAXBException | SAXException | IOException | URISyntaxException e) {
			e.printStackTrace();
		}

		if (mdi_menu != null) {
			mdi_menu.getMenu().getMenuOrEntry();
		}

		// TODO
		// Parsen der MDI Datei um die Menus zu generieren

		MMenu menuGen = modelService.createModelElement(MMenu.class);
		menuGen.setLabel("Generate1");
		MHandledMenuItem handledMenuItem = modelService.createModelElement(MHandledMenuItem.class);
		MCommand command = mApplication.getCommand("aero.minova.rcp.rcp.command.openform");
		handledMenuItem.setCommand(command);

		MParameter mParameter = modelService.createModelElement(MParameter.class);
		mParameter.setElementId("org.eclipse.e4.ui.perspectives.parameters.perspectiveId_");
		mParameter.setName("org.eclipse.e4.ui.perspectives.parameters.perspectiveId");
		mParameter.setValue("WorkingTime.xml");

		handledMenuItem.getParameters().add(mParameter);

		handledMenuItem.setElementId("Test");

		handledMenuItem.setLabel("Direct Gen1");

		handledMenuItem.getPersistedState().put("persistState", String.valueOf(false));
		menuGen.getChildren().add(handledMenuItem);
		menu.getChildren().add(menuGen);
	}

}
