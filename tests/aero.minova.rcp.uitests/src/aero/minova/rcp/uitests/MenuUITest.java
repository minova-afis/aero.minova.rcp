package aero.minova.rcp.uitests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRootMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import aero.minova.rcp.dataservice.XmlProcessor;
import aero.minova.rcp.form.menu.mdi.Main;
import aero.minova.rcp.form.menu.mdi.Main.Action;
import aero.minova.rcp.form.menu.mdi.Main.Entry;
import aero.minova.rcp.form.menu.mdi.MenuType;
import aero.minova.rcp.uitests.util.UITestUtil;
import aero.minova.rcp.util.OSUtil;

@ExtendWith(SWTBotJunit5Extension.class)
public class MenuUITest {

	private SWTWorkbenchBot bot;
	TranslationService translationService;
	ECommandService commandService;
	EHandlerService handlerService;

	@BeforeEach
	public void beforeClass() {
		bot = new SWTWorkbenchBot(UITestUtil.getEclipseContext(this.getClass()));
		SWTBotPreferences.TIMEOUT = 30000;
		this.translationService = UITestUtil.getEclipseContext(this.getClass()).get(TranslationService.class);

		commandService = UITestUtil.getEclipseContext(this.getClass()).get(ECommandService.class);
		handlerService = UITestUtil.getEclipseContext(this.getClass()).get(EHandlerService.class);
	}

	@Test
	public void openPreferencesAndTestMenu() {

		// Einstellungen über Command öffnen
		Display.getDefault().asyncExec(() -> {
			ParameterizedCommand cmd = commandService.createCommand("org.eclipse.ui.window.preferences", null);
			handlerService.executeHandler(cmd);
		});

		// Workspace-Ordner auslesen
		SWTBotShell shell = bot.shell("Preferences");
		assertNotNull(shell);
		SWTBot childBot = shell.bot();
		SWTBotText currentWorkspaceText = childBot.text(1);
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
			Map<String, Integer> callsToMenu = new HashMap<>();
			SWTBotRootMenu menu = bot.menu();
			for (String menuEntry : menu.menuItems()) {
				int i = callsToMenu.get(menuEntry) == null ? 0 : callsToMenu.get(menuEntry);
				callsToMenu.put(menuEntry, i + 1);
				SWTBotMenu menu2 = bot.menu().menu(menuEntry, false, i);
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
			fail(e.getMessage());
		}
	}

	private void getMenuEntries(List<String> entries, SWTBotMenu menu) {
		entries.add(menu.getText());
		for (String menuEntry : menu.menuItems()) {
			getMenuEntries(entries, menu.menu(menuEntry));
		}
	}

	private int checkEntries(List<String> entries, int counter, Object menuOrEntry) {
		if (menuOrEntry instanceof MenuType mt) {
			String translated = translationService.translate(mt.getText(), null);
			assertEquals(translated, entries.get(counter));
			counter++;
			for (Object o : mt.getEntryOrMenu()) {
				counter = checkEntries(entries, counter, o);
			}
		} else {
			Entry entry = (Entry) menuOrEntry;
			if (entry.getId() instanceof Action action) {
				String name = action.getText();
				String translated = translationService.translate(name, null);
				assertEquals(translated, entries.get(counter));
				counter++;
			}
		}
		return counter;
	}

	@Test
	public void openAbout() {

		// TODO: Unter Linux wird das Fenster anscheinend nicht ordentlich geschlossen
		if (OSUtil.isLinux()) {
			return;
		}

		// About-Fenster über Command öffnen
		Display.getDefault().asyncExec(() -> {
			ParameterizedCommand cmd = commandService.createCommand("org.eclipse.ui.help.aboutAction", null);
			handlerService.executeHandler(cmd);
		});

		SWTBotShell shell = bot.shell("About");
		assertNotNull(shell);
		shell.close();
	}

}
