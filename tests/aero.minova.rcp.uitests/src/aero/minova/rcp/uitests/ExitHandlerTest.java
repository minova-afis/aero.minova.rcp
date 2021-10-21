package aero.minova.rcp.uitests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SWTBotJunit5Extension.class)
public class ExitHandlerTest {

	private static SWTBot bot;

	@Inject
	TranslationService ts;

	@BeforeEach
	public void beforeClass() throws Exception {
		bot = new SWTBot();
		SWTBotPreferences.TIMEOUT = 30000;
	}

	@Test
	public void executeExit() {
		Assumptions.assumeFalse(SWTUtils.isMac());
		SWTBotMenu fileMenu = bot.menu("File");
		assertNotNull(fileMenu);
		SWTBotMenu exitMenu = fileMenu.menu(ts.translate("@Exit", null));
		assertNotNull(exitMenu);
		exitMenu.click();
		SWTBotShell shell = bot.shell("Confirmation");
		SWTBot childBot = new SWTBot(shell.widget);
		SWTBotButton button = childBot.button("Cancel");
		assertTrue(button.isEnabled());
		button.click();
	}
}