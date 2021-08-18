package aero.minova.rcp.uitests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.uitests.util.UITestUtil;

@ExtendWith(SWTBotJunit5Extension.class)
class GridTest {

	private SWTWorkbenchBot bot;

//	private List<SWTBotToolbarButton> searchToolbar;
//	private List<SWTBotToolbarButton> indexToolbar;
//	private List<SWTBotToolbarButton> detailToolbar;
//
	private WFCDetailPart wfcPart;

	boolean asyncOperationDone = false;

	private SWTNatTableBot swtNatTableBot;

	private SWTBotNatTable indexNattable;

	private SWTBotNatTable gridNattable;

	private SWTBotNatTable searchNattable;

	private SWTBotView indexPart;

	private SWTBotView searchPart;

	private SWTBotView detailPart;

	@BeforeEach
	void beforeClass() {
		bot = new SWTWorkbenchBot(UITestUtil.getEclipseContext(this.getClass()));
		SWTBotPreferences.TIMEOUT = 30000;
		// Maske über das Menü öffnen
		SWTBotMenu adminMenu = bot.menu("Manuelle Abwicklung");
		assertNotNull(adminMenu);
		SWTBotMenu stundenErfassung = adminMenu.menu("tGraduation");
		assertNotNull(stundenErfassung);
		stundenErfassung.click();

		searchPart = bot.partByTitle("@Form.Search");
		indexPart = bot.partByTitle("@Form.Index");
		detailPart = bot.partByTitle("@Form.Details");

		swtNatTableBot = new SWTNatTableBot();
		searchNattable = swtNatTableBot.nattable(0);
		assertNotNull(searchNattable);
		indexNattable = swtNatTableBot.nattable(1);
		assertNotNull(indexNattable);
		gridNattable = swtNatTableBot.nattable(2);
		assertNotNull(gridNattable);

		// Ensure that the number of visible entries in the nattable is less and
		// possible
		while (indexNattable.preferredRowCount() >= 8) {
			UITestUtil.loadIndex(indexPart.getToolbarButtons());

			indexNattable.click(indexNattable.columnCount() - 1, 1);
			detailPart.getToolbarButtons().get(2).click();
		}
	}

	@AfterEach
	void tearDown() {

	}


	@Test
	void ensurePartsAreAvailable() {
		assertNotNull(searchPart);
		assertNotNull(indexPart);
		assertNotNull(detailPart);
	}

	@Test
	void ensureNatTablesAreAvailable() {

		assertNotNull(searchNattable);
		assertNotNull(indexNattable);
		assertNotNull(gridNattable);

	}

	@Test
	void ensureToolbarsAreNotEmpty() {

		// Toolbarbuttons finden
		SWTBotView searchPart = bot.partByTitle("@Form.Search");
		assertFalse(searchPart.getToolbarButtons().isEmpty());

		SWTBotView indexPart = bot.partByTitle("@Form.Index");
		assertFalse(indexPart.getToolbarButtons().isEmpty());

		SWTBotView detailPart = bot.partByTitle("@Form.Details");
		assertFalse(detailPart.getToolbarButtons().isEmpty());

	}


	@Test
	public void ensureDataEntryCanBeCreated() {

		// Do not start on Linux
		Assumptions.assumeFalse(System.getProperty("os.name").startsWith("Linux"));

		SWTBotView detailPart = bot.partByTitle("@Form.Details");
		wfcPart = (WFCDetailPart) detailPart.getPart().getObject();

		Table table = wfcPart.getDetail().getGrid("GraduationStep").getDataTable();

		// Testeintrag erstellen
		SWTBotView indexPart = bot.partByTitle("@Form.Index");
		UITestUtil.loadIndex(indexPart.getToolbarButtons());

		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
		SWTBotNatTable indexNattable = swtNatTableBot.nattable(1);

		int numberEntries = indexNattable.preferredRowCount();

		createEntry();
		saveDetail();
		reloadIndex();
		assertEquals(numberEntries + 1, indexNattable.preferredRowCount(), "Erstellen eines Eintrags fehlgeschlagen");

	}

	@Test
	@DisplayName("Zeilen in Grids einfügen, ändern und löschen testen und das Speichern überprüfen (nicht Ubuntu)!")
	public void testGridFunctions() {

		// Do not start on Linux
		Assumptions.assumeFalse(System.getProperty("os.name").startsWith("Linux"));

		SWTBotView detailPart = bot.partByTitle("@Form.Details");
		wfcPart = (WFCDetailPart) detailPart.getPart().getObject();


		Table table = wfcPart.getDetail().getGrid("GraduationStep").getDataTable();

		// Testeintrag erstellen
		SWTBotView indexPart = bot.partByTitle("@Form.Index");
		UITestUtil.loadIndex(indexPart.getToolbarButtons());

		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
		SWTBotNatTable indexNattable = swtNatTableBot.nattable(1);
		int numberEntries = indexNattable.preferredRowCount();
		createEntry();
		saveDetail();
		reloadIndex();
		assertEquals(numberEntries + 1, indexNattable.preferredRowCount(), "Erstellen eines Eintrags fehlgeschlagen");

		// Zeilen einfügen und prüfen ob sie gespeichert wurden
		insertRows();
		saveDetail();
		reloadIndex();
		assertEquals(4, table.getRows().size(), "Einfügen von Zeilen fehlgeschlagen");

		// Zeilen verändern und löschen, Speichern prüfen
		modifyAndDeleteRows();
		saveDetail();
		reloadIndex();
		assertEquals(2, table.getRows().size(), "Löschen von Zeilen fehlgeschlagen");

		// Unter Mac werden die Werte die in Nattables geschrieben werden angehängt
		if (System.getProperty("os.name").startsWith("Mac OS")) {
			assertEquals(211, table.getRows().get(0).getValue(2).getIntegerValue(), "Ändern von Zeilen fehlgeschlagen");
			assertEquals(411, table.getRows().get(1).getValue(2).getIntegerValue(), "Ändern von Zeilen fehlgeschlagen");
		} else {
			assertEquals(1, table.getRows().get(0).getValue(2).getIntegerValue(), "Ändern von Zeilen fehlgeschlagen");
			assertEquals(1, table.getRows().get(1).getValue(2).getIntegerValue(), "Ändern von Zeilen fehlgeschlagen");
		}

		// Eintrag wieder löschen
		detailPart = bot.partByTitle("@Form.Details");
		detailPart.getToolbarButtons().get(2).click();
		UITestUtil.sleep();
		UITestUtil.loadIndex(indexPart.getToolbarButtons());
		assertEquals(numberEntries, indexNattable.rowCount(), "Löschen des Eintrages fehlgeschlagen");
	}

	/**
	 * Speichert das aktuelle Detail und lädt es wieder
	 */
	private void saveDetail() {
		SWTBotView detailPart = bot.partByTitle("@Form.Details");
		List<SWTBotToolbarButton>  detailToolbarButtons = detailPart.getToolbarButtons();
		
		detailToolbarButtons.get(0).click();

		UITestUtil.sleep();
	
	}
	
	private void reloadIndex() {
		SWTBotView indexPart = bot.partByTitle("@Form.Index");
		UITestUtil.loadIndex(indexPart.getToolbarButtons());

		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
		SWTBotNatTable indexNattable = swtNatTableBot.nattable(1);
		indexNattable.click(indexNattable.preferredRowCount() - 1, 3);
		UITestUtil.sleep();
	}

	/**
	 * Erstellen eines komplett neuen Eintrags
	 */
	private void createEntry() {
		UIThreadRunnable.syncExec(bot.getDisplay(), () -> {
			MField f = wfcPart.getDetail().getField("KeyText");
			f.setValue(new Value("UITEST" + (int) ((Math.random() * (999 - 100)) + 100)), false);

			f = wfcPart.getDetail().getField("Description");
			f.setValue(new Value("Testing the UI"), false);

			f = wfcPart.getDetail().getField("OrderReceiverKey");
			f.setValue(new LookupValue(2, "AFIS", "AFIS GmbH & CO. KG"), false);

			f = wfcPart.getDetail().getField("UnitKey");
			f.setValue(new LookupValue(19, "100L", "100 Liter"), false);

			f = wfcPart.getDetail().getField("ResetQuantityEach");
			f.setValue(new LookupValue(3, "J", "Jahr"), false);

			f = wfcPart.getDetail().getField("WarrantyQuantity");
			f.setValue(new Value(12.0), false);
			asyncOperationDone = true;
		});
		while (!asyncOperationDone) {
			UITestUtil.sleep(100);
		}
	}

	/**
	 * Erstellt einige Testzeilen im Grid
	 */
	private void insertRows() {
		bot.text().setFocus();
		SWTBotToolbarButton btnInsert = bot.toolbarButtonWithId(Constants.CONTROL_GRID_BUTTON_INSERT);

		UITestUtil.sleep();
		btnInsert.click();
		btnInsert.click();
		btnInsert.click();
		btnInsert.click();
		UITestUtil.sleep();

		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
		SWTBotNatTable gridNattable = swtNatTableBot.nattable(2);

		gridNattable.setCellDataValueByPosition(1, 1, "11");
		gridNattable.setCellDataValueByPosition(1, 2, "12");

		gridNattable.setCellDataValueByPosition(2, 1, "21");
		gridNattable.setCellDataValueByPosition(2, 2, "22");

		gridNattable.setCellDataValueByPosition(3, 1, "31");
		gridNattable.setCellDataValueByPosition(3, 2, "32");

		gridNattable.setCellDataValueByPosition(4, 1, "41");
		gridNattable.setCellDataValueByPosition(4, 2, "42");

		bot.text().setFocus();
		UITestUtil.sleep();
	}

	/**
	 * Löscht einige Zeilen und Verändert andere
	 */
	private void modifyAndDeleteRows() {
		bot.text().setFocus();
		SWTBotToolbarButton btnDelete = bot.toolbarButtonWithId(Constants.CONTROL_GRID_BUTTON_DELETE);

		UITestUtil.sleep();

		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
		SWTBotNatTable gridNattable = swtNatTableBot.nattable(2);

		// Löscht Zeile mit Werten 11, 12
		gridNattable.click(1, 0);
		btnDelete.click();

		UITestUtil.sleep(500);

		// Löscht Zeile mit Werten 31, 32
		gridNattable.click(2, 0);
		btnDelete.click();

		UITestUtil.sleep(500);

		// Ändert Zeile 21, 22
		gridNattable.setCellDataValueByPosition(1, 1, "1");

		// Ändert Zeile 41, 42
		gridNattable.setCellDataValueByPosition(2, 1, "1");

		bot.text().setFocus();
		UITestUtil.sleep();
	}


}
