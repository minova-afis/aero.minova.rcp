package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
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
    }

    @Test
    public void executeExit() {

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
    
	@Test
	public void openStundenerfassung() {

		SWTBotMenu adminMenu = bot.menu("Administration");
		assertNotNull(adminMenu);
		SWTBotMenu stundenErfassung = adminMenu.menu("Stundenerfassung");
		assertNotNull(stundenErfassung);
		stundenErfassung.click();
	}




//  @AfterEach
//  public void sleep() {
//      bot.sleep(2000);
//  }
}