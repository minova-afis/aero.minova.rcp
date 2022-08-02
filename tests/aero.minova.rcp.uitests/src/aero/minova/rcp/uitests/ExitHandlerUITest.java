package aero.minova.rcp.uitests;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SWTBotJunit5Extension.class)
public class ExitHandlerTest {

	private static SWTBot bot;

	@BeforeEach
	public void beforeClass() throws Exception {
		bot = new SWTBot();
		SWTBotPreferences.TIMEOUT = 30000;
	}

	@Test
	@Ignore
	public void executeExit() {
		// TODO: "Exit" muss übersetzt werden
//		Assumptions.assumeFalse(SWTUtils.isMac());
//		SWTBotMenu fileMenu = bot.menu("File");
//		assertNotNull(fileMenu);
//		SWTBotMenu exitMenu = fileMenu.menu("Exit");
//		assertNotNull(exitMenu);
//		exitMenu.click();
//		SWTBotShell shell = bot.shell("Confirmation");
//		SWTBot childBot = new SWTBot(shell.widget);
//		SWTBotButton button = childBot.button("Cancel");
//		assertTrue(button.isEnabled());
//		button.click();
	}
}