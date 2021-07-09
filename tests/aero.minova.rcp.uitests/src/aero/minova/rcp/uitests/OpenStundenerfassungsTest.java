package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.Position;
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

	private List<SWTBotToolbarButton> searchToolbar;
	private List<SWTBotToolbarButton> indexToolbar;
	private List<SWTBotToolbarButton> detailToolbar;

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

		// Toolbarbuttons finden
		searchToolbar = searchPart.getToolbarButtons();
		assertNotEquals(0, searchToolbar.size());
		indexToolbar = indexPart.getToolbarButtons();
		assertNotEquals(0, indexToolbar.size());
		detailToolbar = detailPart.getToolbarButtons();
		assertNotEquals(0, detailToolbar.size());

//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	@Test
	@DisplayName("Suchezeile löschen und Suche komplett zurücksetzten")
	public void deleteRowAndRevertSearch() {
		// immer zwei Einträge pro Zeile, da Nattable ansonsten nicht updatet (neue Zeile wird nicht eingefügt)
		searchNattable.setCellDataValueByPosition(1, 3, "row1");
		searchNattable.setCellDataValueByPosition(1, 4, "row1");
		searchNattable.setCellDataValueByPosition(2, 3, "row2");
		searchNattable.setCellDataValueByPosition(2, 4, "row2");
		searchNattable.setCellDataValueByPosition(3, 3, "row3");
		searchNattable.setCellDataValueByPosition(3, 4, "row3");
		assertEquals(5, searchNattable.rowCount());

		// Suchzeile löschen
		searchNattable.click(2, 3);
		searchToolbar.get(1).click();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		assertEquals(4, searchNattable.rowCount());
		assertEquals("\"f-~-s-row1%\"", searchNattable.getCellDataValueByPosition(1, 3));
		assertEquals("\"f-~-s-row3%\"", searchNattable.getCellDataValueByPosition(2, 3));

		// Suche zurücksetzten
		searchToolbar.get(0).click();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		assertEquals(2, searchNattable.rowCount());

		// Sind alle Spalten leer?
		// Es können nur sichtbare Zellen überprüft werden. Deshalb scrollen wir durch die Tabelle und überprüfen, ob wir in der nächsten Spalte sind
		int columnToCheck = 1;
		String prevColumn = "";
		for (int i = 2; i < searchNattable.preferredColumnCount(); i++) {
			searchNattable.scrollViewport(new Position(1, 1), 0, i - 1);
			if (prevColumn.equals(searchNattable.getCellDataValueByPosition(0, columnToCheck))) {
				columnToCheck++;
			}
			prevColumn = searchNattable.getCellDataValueByPosition(0, columnToCheck);
			assertEquals("", searchNattable.getCellDataValueByPosition(1, columnToCheck));
		}
		// Wieder an Anfang scrollen
		searchNattable.scrollViewport(new Position(1, 1), 0, 0);

	}

	@Test
	@DisplayName("Index mit SuchPart filtern")
	public void filterIndex() {
		searchNattable.setCellDataValueByPosition(1, 3, "avm");
		indexToolbar.get(0).click();

		// Warten bis Daten geladen sind
		do {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		} while (!indexToolbar.get(0).isEnabled());

		// Ist Mitarbeiter immer AVM?
		for (int i = 3; i < indexNattable.rowCount(); i++) {
			assertEquals("AVM", indexNattable.getCellDataValueByPosition(i, 2));
		}

		// Suche zurücksetzten
		searchToolbar.get(0).click();
	}

	@Test
	@DisplayName("Index Laden und Überprüfen, ob Daten geladen wurden")
	public void loadIndex() {
		indexToolbar.get(0).click();

		// Warten bis Daten geladen sind
		do {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		} while (!indexToolbar.get(0).isEnabled());

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