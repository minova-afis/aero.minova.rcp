package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
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

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.uitests.util.UITestUtil;

@RunWith(SWTBotJunit4ClassRunner.class)
public class OpenServiceContract {

	private SWTWorkbenchBot bot;

	private SWTBotView searchPart;
	private SWTBotView indexPart;
	private SWTBotView detailPart;

	private SWTBotNatTable searchNattable;
	private SWTBotNatTable indexNattable;
	private SWTBotNatTable gridNattable;

	private List<SWTBotToolbarButton> searchToolbar;
	private List<SWTBotToolbarButton> indexToolbar;
	private List<SWTBotToolbarButton> detailToolbar;

	@Before
	public void beforeClass() {
		bot = new SWTWorkbenchBot(UITestUtil.getEclipseContext(this.getClass()));
		SWTBotPreferences.TIMEOUT = 30000;
		openServiceContract();
	}

	public void openServiceContract() {

		// ServiceContract über das Menü öffnen
		SWTBotMenu adminMenu = bot.menu("Manuelle Abwicklung");
		assertNotNull(adminMenu);
		SWTBotMenu stundenErfassung = adminMenu.menu("tServiceContract");
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
		gridNattable = swtNatTableBot.nattable(2);
		assertNotNull(gridNattable);

		// Toolbarbuttons finden
		searchToolbar = searchPart.getToolbarButtons();
		assertNotEquals(0, searchToolbar.size());
		indexToolbar = indexPart.getToolbarButtons();
		assertNotEquals(0, indexToolbar.size());
		detailToolbar = detailPart.getToolbarButtons();
		assertNotEquals(0, detailToolbar.size());
	}

	@Test
	@DisplayName("Index mit SuchPart filtern!")
	public void filterIndex() {
		searchNattable.setCellDataValueByPosition(1, 3, "wfctest");
		indexToolbar.get(0).click();

		// Warten bis Daten geladen sind
		do {
			UITestUtil.sleep(500);
		} while (!indexToolbar.get(0).isEnabled());

		// Ist Mitarbeiter immer AVM?
		for (int i = 3; i < indexNattable.rowCount(); i++) {
			assertEquals("WFCTEST", indexNattable.getCellDataValueByPosition(i, 2));
		}

		// Suche zurücksetzten
		searchToolbar.get(0).click();
	}

	@Test
	@DisplayName("Index Laden und Überprüfen, ob Daten geladen wurden!")
	public void loadIndex() {
		UITestUtil.loadIndex(indexToolbar);

		// Überprüfen, ob Daten geladen wurden
		assertTrue(indexNattable.rowCount() > 3);
	}

	@Test
	@DisplayName("Detail Laden und Überprüfen, ob Daten geladen wurden!")
	public void loadDetail() {

		UITestUtil.loadIndex(indexToolbar);
		indexNattable.click(4, 1);

		UITestUtil.sleep();

		// Überprüfen, ob Daten geladen wurden
		MPart part = detailPart.getPart();
		WFCDetailPart wfcPart = (WFCDetailPart) part.getObject();
		MField keyText = wfcPart.getDetail().getField("KeyText");
		Value value = keyText.getValue();
		assertEquals(value.getStringValue(), "WFCTEST");
		MField description = wfcPart.getDetail().getField("Description");
		value = description.getValue();
		assertEquals(value.getStringValue(), "WFC Testen der Anwendung");
	}

	@Test
	@DisplayName("Detail Laden, eine Zeile aus dem Grid löschen, 2 Neue hinzufügen!")
	public void loadDetailGrid() {

		UITestUtil.loadIndex(indexToolbar);
		indexNattable.click(4, 1);

		UITestUtil.sleep();

		// Überprüfen, ob Daten geladen wurden
		MPart part = detailPart.getPart();
		WFCDetailPart wfcPart = (WFCDetailPart) part.getObject();
		MGrid grid = wfcPart.getDetail().getGrid("ServicePrice");
		int size = grid.getDataTable().getRows().size();
		assertEquals(size, 5);
		gridNattable.click(5, 0);

		Control textClient = grid.getmSection().getSection().getTextClient();
		assertTrue(textClient instanceof ToolBar);

		SWTBotToolbarButton btnInsert = bot.toolbarButtonWithId(Constants.CONTROL_GRID_BUTTON_INSERT);
		SWTBotToolbarButton btnDelete = bot.toolbarButtonWithId(Constants.CONTROL_GRID_BUTTON_DELETE);

		btnDelete.click();

		UITestUtil.sleep(250);
		int sizeAfterDelete = grid.getDataTable().getRows().size();
		assertEquals(sizeAfterDelete, 4);

		btnInsert.click();
		UITestUtil.sleep(250);
		btnInsert.click();
		UITestUtil.sleep(250);

		assertEquals(grid.getDataTable().getRows().size(), 6);
	}

	@AfterEach
	public void sleep() {
		bot.sleep(10000);
	}
}