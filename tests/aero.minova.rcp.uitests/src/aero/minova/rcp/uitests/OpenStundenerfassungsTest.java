package aero.minova.rcp.uitests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
<<<<<<< HEAD
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
=======
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
>>>>>>> branch 'swtbot' of https://github.com/minova-afis/aero.minova.rcp.git
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
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
		SWTBotPreferences.TIMEOUT = 30000;

	}

	@Test
	public void openStundenerfassung() {

		SWTBotMenu adminMenu = bot.menu("Administration");
		assertNotNull(adminMenu);
		SWTBotMenu stundenErfassung = adminMenu.menu("Stundenerfassung");
		assertNotNull(stundenErfassung);
		stundenErfassung.click();
		SWTBotView partByTitle = bot.partByTitle("@Form.Search");
		List<SWTBotToolbarButton> toolbarButtons = partByTitle.getToolbarButtons();
		toolbarButtons.get(0).click();

		assertNotNull(partByTitle);

		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
		SWTBotNatTable nattable = swtNatTableBot.nattable();

		System.out.println(nattable);

		int row = 5, col = 1;
		int rowCount = nattable.rowCount();
		nattable.setCellDataValueByPosition(1, 3, "xxyy");
		nattable.pressShortcut(Keystrokes.LF);
		System.out.println(rowCount);
//		int totalRowCount = nattable.preferredRowCount();

		IEclipseContext eclipseContext = getEclipseContext();
		MApplication application = eclipseContext.get(IWorkbench.class).getApplication();
		EModelService modelService = eclipseContext.get(EModelService.class);

		List<MPart> findElements = modelService.findElements(application, null, MPart.class, null);
		System.out.println(findElements);
	}

	@Test
	public void openPreferences() {
		Menu systemMenu = Display.getCurrent().getSystemMenu();
		Item menuItem = systemMenu.getItem(2);
		assertEquals(menuItem.toString(), "MenuItem {Preferences...}");
		menuItem.notifyListeners(SWT.Selection, new SelectionEvent(null));

		SWTBotMenu adminMenu = bot.menu("WFC");
		assertNotNull(adminMenu);
		SWTBotMenu preferences = adminMenu.menu("Preferences...");
		assertNotNull(preferences);
		preferences.click();

		SWTBotShell botShell = bot.activeShell();

//		workspaceAccessPreferences
		SWTBotView partByTitle = bot.partByTitle("@Form.Search");
		List<SWTBotToolbarButton> toolbarButtons = partByTitle.getToolbarButtons();
		toolbarButtons.get(0).click();

		assertNotNull(partByTitle);

//		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
//		SWTBotNatTable nattable = bot.nattable();
//		int rowCount = nattable.rowCount();
//		int totalRowCount = nattable.preferredRowCount();

		IEclipseContext eclipseContext = getEclipseContext();
		MApplication application = eclipseContext.get(IWorkbench.class).getApplication();
		EModelService modelService = eclipseContext.get(EModelService.class);

		List<MPart> findElements = modelService.findElements(application, null, MPart.class, null);
		System.out.println(findElements);
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