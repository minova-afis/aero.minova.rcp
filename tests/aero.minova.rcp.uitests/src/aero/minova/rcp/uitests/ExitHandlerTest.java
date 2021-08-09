package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ExitHandlerTest {

	private static SWTBot bot;

	@Before
	public void beforeClass() throws Exception {
		bot = new SWTBot();
		SWTBotPreferences.TIMEOUT = 30000;
	}

	@Test
	public void executeExit() {
		if (SWTUtils.isMac()) {
			return;
		}
		SWTBotMenu fileMenu = bot.menu("File");
		assertNotNull(fileMenu);
		SWTBotMenu exitMenu = fileMenu.menu("Exit");
		assertNotNull(exitMenu);
		exitMenu.click();
		SWTBotShell shell = bot.shell("Confirmation");
		SWTBot childBot = new SWTBot(shell.widget);
		SWTBotButton button = childBot.button("Cancel");
		assertTrue(button.isEnabled());
		button.click();
	}
}