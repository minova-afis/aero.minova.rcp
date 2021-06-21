package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.FrameworkUtil;

@RunWith(SWTBotJunit4ClassRunner.class)
public class OpenStundenerfassungsTest {

	private static SWTWorkbenchBot bot;

	@Before
    public void beforeClass() throws Exception {
		bot = new SWTWorkbenchBot(getEclipseContext());

	}

	@Test
	public void openStundenerfassung() {

		SWTBotMenu adminMenu = bot.menu("Administration");
		assertNotNull(adminMenu);
		SWTBotMenu stundenErfassung = adminMenu.menu("Stundenerfassung");
		assertNotNull(stundenErfassung);
		stundenErfassung.click();
		SWTBotView partByTitle = bot.partByTitle("@Form.Search");
		assertNotNull(partByTitle);
	}



	protected static IEclipseContext getEclipseContext() {
		final IEclipseContext serviceContext = EclipseContextFactory
				.getServiceContext(FrameworkUtil.getBundle(OpenStundenerfassungsTest.class).getBundleContext());
		return serviceContext.get(IWorkbench.class).getApplication().getContext();
	}

//  @AfterEach
//  public void sleep() {
//      bot.sleep(2000);
//  }
}