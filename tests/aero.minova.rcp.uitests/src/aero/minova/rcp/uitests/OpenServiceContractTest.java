package aero.minova.rcp.uitests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.rcp.accessor.SectionAccessor;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.uitests.util.UITestUtil;

@ExtendWith(SWTBotJunit5Extension.class)
public class OpenServiceContractTest {

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

	@BeforeEach
	public void setup() {
		bot = new SWTWorkbenchBot(UITestUtil.getEclipseContext(this.getClass()));
		SWTBotPreferences.TIMEOUT = 30000;

		// ServiceContract über das Menü öffnen
		SWTBotMenu adminMenu = bot.menu("Manuelle Abwicklung");
		assertNotNull(adminMenu);
		SWTBotMenu stundenErfassung = adminMenu.menu("tServiceContract");
		assertNotNull(stundenErfassung);
		stundenErfassung.click();

		// Parts finden
		searchPart = bot.partById(Constants.SEARCH_PART);
		assertNotNull(searchPart);
		indexPart = bot.partById(Constants.INDEX_PART);
		assertNotNull(indexPart);
		detailPart = bot.partById(Constants.DETAIL_PART);
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
	@Disabled("Currently broken")
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
	@Disabled("Currently broken")
	@DisplayName("Index Laden und Überprüfen, ob Daten geladen wurden!")
	public void loadIndex() {
		
		reloadIndex();

		// Überprüfen, ob Daten geladen wurden
		assertTrue(indexNattable.rowCount() > 3);
	}

	@Test
	@Disabled("Currently broken")

	@DisplayName("Detail Laden und Überprüfen, ob Daten geladen wurden!")
	public void loadDetail() {

		reloadIndex() ;
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
	@Disabled("Currently broken")
	@DisplayName("Detail Laden, eine Zeile aus dem Grid löschen, 2 Neue hinzufügen!")
	public void loadDetailGrid() {

		reloadIndex();
		indexNattable.click(4, 1);

		UITestUtil.sleep();

		// Überprüfen, ob Daten geladen wurden
		MPart part = detailPart.getPart();
		WFCDetailPart wfcPart = (WFCDetailPart) part.getObject();
		MGrid grid = wfcPart.getDetail().getGrid("ServicePrice");
		int size = grid.getDataTable().getRows().size();
		assertEquals(size, 5);
		gridNattable.click(4, 0);

		Control textClient = ((SectionAccessor) grid.getmSection().getSectionAccessor()).getSection().getTextClient();
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
	
	private void reloadIndex() {
		SWTBotView indexPart = bot.partById(Constants.INDEX_PART);
		UITestUtil.loadIndex(indexPart.getToolbarButtons().get(0));

		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
		SWTBotNatTable indexNattable = swtNatTableBot.nattable(1);
		indexNattable.click(indexNattable.preferredRowCount() - 1, 3);
		UITestUtil.sleep();
	}
}