package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.osgi.framework.FrameworkUtil;

@RunWith(SWTBotJunit4ClassRunner.class)
public class OpenStundenerfassungsTest {

	private SWTWorkbenchBot bot;

	private SWTBotView searchPart;
	private SWTBotView indexPart;
	private SWTBotView detailPart;

	private SWTBotNatTable searchNattable;
	private SWTBotNatTable indexNattable;

	@Before
	public void beforeClass() {
		bot = new SWTWorkbenchBot(getEclipseContext());
		SWTBotPreferences.TIMEOUT = 30000;

		openStundenerfassung();
	}

	public void openStundenerfassung() {

		// Stundenerfassung über das Menü öffnen
		SWTBotMenu adminMenu = bot.menu("Administration");
		assertNotNull(adminMenu);
		SWTBotMenu stundenErfassung = adminMenu.menu("Stundenerfassung");
		assertNotNull(stundenErfassung);
		stundenErfassung.click();

		// Parts finden
		searchPart = bot.partByTitle("@Form.Search");
		assertNotNull(searchPart);
		indexPart = bot.partByTitle("@Form.Index");
		assertNotNull(indexPart);
		detailPart = bot.partByTitle("@Form.Details");
		assertNotNull(detailPart);

		// Nattables finden
		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
		searchNattable = swtNatTableBot.nattable();
		assertNotNull(searchNattable);
		indexNattable = swtNatTableBot.nattable(1);
		assertNotNull(indexNattable);

//		nattable.setCellDataValueByPosition(1, 3, "xxyy");
//		nattable.pressShortcut(Keystrokes.LF);
	}

	@Test
	@DisplayName("Index Laden und Überprüfen, ob Daten geladen wurden")
	public void loadIndex() {

		List<SWTBotToolbarButton> toolbarButtons = indexPart.getToolbarButtons();
		assertTrue(!toolbarButtons.isEmpty());
		toolbarButtons.get(0).click();

		// Warten bis Daten geladen sind
		do {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		} while (!toolbarButtons.get(0).isEnabled());

		// Überprüfen, ob Daten geladen wurden

		String numberEntriesString = indexNattable.getCellDataValueByPosition(2, 1);
		assertNotNull(numberEntriesString);
		assertTrue(Integer.parseInt(numberEntriesString) > 0);
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