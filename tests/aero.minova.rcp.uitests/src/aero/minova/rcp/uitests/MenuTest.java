package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRootMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.osgi.framework.FrameworkUtil;

import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.form.menu.mdi.Main;
import aero.minova.rcp.form.menu.mdi.Main.Action;
import aero.minova.rcp.form.menu.mdi.Main.Entry;
import aero.minova.rcp.form.menu.mdi.MenuType;

@RunWith(SWTBotJunit4ClassRunner.class)
public class MenuTest {

	private static SWTWorkbenchBot bot;
	TranslationService translationService;

	@Before
	public void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot(getEclipseContext());
		SWTBotPreferences.TIMEOUT = 30000;
		this.translationService = getEclipseContext().get(TranslationService.class);
	}

	@Test
	public void openPreferencesAndTestMenu() {

		// Einstellungen über Command öffnen
		Display.getDefault().asyncExec(() -> {
			ECommandService commandService = getEclipseContext().get(ECommandService.class);
			EHandlerService handlerService = getEclipseContext().get(EHandlerService.class);
			ParameterizedCommand cmd = commandService.createCommand("org.eclipse.ui.window.preferences", null);
			handlerService.executeHandler(cmd);
		});

		// Workspace-Ordner auslesen
		SWTBotShell shell = bot.shell("Preferences");
		assertNotNull(shell);
		SWTBot childBot = new SWTBot(shell.widget);
		SWTBotText currentWorkspaceText = childBot.text(2);
		assertNotNull(currentWorkspaceText);
		assertNotNull(currentWorkspaceText.getText());
		String pathWorkspace = currentWorkspaceText.getText();
		shell.close();

		// application.mdi einlesen und Menü überprüfen
		try {
			Path path = Path.of(pathWorkspace, "application.mdi");
			String applicationString = Files.readString(path);
			assertNotNull(applicationString);
			Main mainMDI = XmlProcessor.get(applicationString, Main.class);
			assertNotNull(mainMDI);

			// Liste mit Menueinträgen (depth-first)
			List<String> menuEntries = new ArrayList<>();
			SWTBotRootMenu menu = bot.menu();
			for (String menuEntry : menu.menuItems()) {
				SWTBotMenu menu2 = bot.menu(menuEntry);
				// File Menü überspringen (nicht von uns)
				if (menu2.getText().equals("File")) {
					continue;
				}
				getMenuEntries(menuEntries, menu2);
			}

			// TODO Reihenfolge in der mdi beachten (siehe MenuProcessor)
			int counter = 0;
			for (Object menuOrEntry : mainMDI.getMenu().getMenuOrEntry()) {
				counter = checkEntries(menuEntries, counter, menuOrEntry);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	private void getMenuEntries(List<String> entries, SWTBotMenu menu) {
		entries.add(menu.getText());
		for (String menuEntry : menu.menuItems()) {
			getMenuEntries(entries, menu.menu(menuEntry));
		}
	}

	private int checkEntries(List<String> entries, int counter, Object menuOrEntry) {
		if (menuOrEntry instanceof MenuType) {
			String translated = translationService.translate(((MenuType) menuOrEntry).getText(), null);
			assertEquals(translated, entries.get(counter));
			counter++;

			for (Object o : ((MenuType) menuOrEntry).getEntryOrMenu()) {
				counter = checkEntries(entries, counter, o);
			}
		} else {
			Entry entry = (Entry) menuOrEntry;
			if (entry.getId() instanceof Action) {
				String name = ((Action) entry.getId()).getText();
				String translated = translationService.translate(name, null);
				assertEquals(translated, entries.get(counter));
				counter++;
			}
		}
		return counter;
	}

	protected static IEclipseContext getEclipseContext() {
		final IEclipseContext serviceContext = EclipseContextFactory
				.getServiceContext(FrameworkUtil.getBundle(OpenStundenerfassungsTest.class).getBundleContext());
		return serviceContext.get(IWorkbench.class).getApplication().getContext();
	}

	@AfterEach
	public void sleep() {
		bot.sleep(10000);
	}

}
