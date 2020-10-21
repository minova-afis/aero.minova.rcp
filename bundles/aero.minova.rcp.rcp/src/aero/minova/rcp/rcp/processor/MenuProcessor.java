package aero.minova.rcp.rcp.processor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import aero.minova.rcp.dataservice.IDataService;

public class MenuProcessor {

	@Inject
	public MenuProcessor(@Named("org.eclipse.ui.main.menu") MMenu menu, EModelService modelService,
			IDataService dataService) {
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
		try {
			uri = new URI(basePath);
		} catch (URISyntaxException e1) {
		}

		try {
			String content = new String(Files.readAllBytes(Path.of(uri)));
			System.out.println(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO
		// Parsen der MDI Datei um die Menus zu generieren

		MMenu menuGen = modelService.createModelElement(MMenu.class);
		menuGen.setLabel("Generate1");
		MDirectMenuItem directModelItem = modelService.createModelElement(MDirectMenuItem.class);
		directModelItem.setLabel("Direct Gen1");
		directModelItem.setContributionURI(
				"bundleclass://aero.minova.rcp.rcp/aero.minova.rcp.rcp.handlers.AboutHandler");
		directModelItem.getPersistedState().put("persistState", String.valueOf(false));
		menuGen.getChildren().add(directModelItem);
		menu.getChildren().add(menuGen);
	}

}
