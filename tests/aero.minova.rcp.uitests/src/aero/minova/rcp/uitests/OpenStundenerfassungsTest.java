package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.osgi.framework.FrameworkUtil;

@RunWith(SWTBotJunit4ClassRunner.class)
public class OpenStundenerfassungsTest {

	private static SWTWorkbenchBot bot;

	@Before
	public void beforeClass() throws Exception {

		bot = new SWTWorkbenchBot(getEclipseContext());
		SWTBotPreferences.TIMEOUT = 30000;

		openStundenerfassung();
	}

	public void openStundenerfassung() {

		SWTBotMenu adminMenu = bot.menu("Administration");
		assertNotNull(adminMenu);
		SWTBotMenu stundenErfassung = adminMenu.menu("Stundenerfassung");
		assertNotNull(stundenErfassung);
		stundenErfassung.click();
		SWTBotView partByTitle = bot.partByTitle("@Form.Search");
		assertNotNull(partByTitle);

//		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
//		SWTBotNatTable nattable = swtNatTableBot.nattable();
//
//		System.out.println(nattable);
//
//		int row = 5, col = 1;
//		int rowCount = nattable.rowCount();
//		nattable.setCellDataValueByPosition(1, 3, "xxyy");
//		nattable.pressShortcut(Keystrokes.LF);
//		System.out.println(rowCount);
////		int totalRowCount = nattable.preferredRowCount();
//
//		IEclipseContext eclipseContext = getEclipseContext();
//		MApplication application = eclipseContext.get(IWorkbench.class).getApplication();
//		EModelService modelService = eclipseContext.get(EModelService.class);
//
//		List<MPart> findElements = modelService.findElements(application, null, MPart.class, null);
//		System.out.println(findElements);

	}

	@Test
	public void loadIndex() {

		// Wir haben auf einmal Probleme unter Ubuntu
		if (!SWTUtils.isMac()) {
			return;
		}

		SWTBotView indexPart = bot.partByTitle("@Form.Index");
		assertNotNull(indexPart);
		List<SWTBotToolbarButton> toolbarButtons = indexPart.getToolbarButtons();
		assertTrue(!toolbarButtons.isEmpty());
		toolbarButtons.get(0).click();

		// TODO: Tabelle nach einträgen überprüfen
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