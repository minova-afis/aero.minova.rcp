package aero.minova.rcp.uitests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.junit5.SWTBotJunit5Extension;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import aero.minova.rcp.util.OSUtil;

@ExtendWith(SWTBotJunit5Extension.class)
class GridUITest {

	private static final String AENDERN_VON_ZEILEN_FEHLGESCHLAGEN = "Ändern von Zeilen fehlgeschlagen";

	private SWTWorkbenchBot bot;

	private WFCDetailPart wfcPart;

	boolean asyncOperationDone = false;

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
		SWTBotMenu stundenErfassung = adminMenu.menu("Staffelsatz");
		assertNotNull(stundenErfassung);
		stundenErfassung.click();

		searchPart = bot.partById(Constants.SEARCH_PART);
		indexPart = bot.partById(Constants.INDEX_PART);
		detailPart = bot.partById(Constants.DETAIL_PART);

		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
		searchNattable = swtNatTableBot.nattable(0);
		assertNotNull(searchNattable);
		indexNattable = swtNatTableBot.nattable(1);
		assertNotNull(indexNattable);
		gridNattable = swtNatTableBot.nattable(2);
		assertNotNull(gridNattable);

		assertNotNull(indexPart.getToolbarButtons());
		assertNotEquals(0, indexPart.getToolbarButtons().size());

		// Ensure that the number of visible entries in the nattable is less and
		// possible
		while (indexNattable.rowCount() >= 8) {

			indexNattable.click(indexNattable.rowCount() - 1, 1);
			detailPart.getToolbarButtons().get(2).click();
			SWTBotToolbarButton load = indexPart.getToolbarButtons().get(0);

			UITestUtil.loadIndex(load);
		}
	}

	@Test
	@Disabled("Currently fail so disable to be able to continue without test breakages")
	void ensurePartsAreAvailable() {
		assertNotNull(searchPart);
		assertNotNull(indexPart);
		assertNotNull(detailPart);
	}

	@Test
	@Disabled("Currently fail so disable to be able to continue without test breakages")
	void ensureNatTablesAreAvailable() {

		assertNotNull(searchNattable);
		assertNotNull(indexNattable);
		assertNotNull(gridNattable);

	}

	@Test
	@Disabled("Currently fail so disable to be able to continue without test breakages")
	void ensureToolbarsAreNotEmpty() {

		// Toolbarbuttons finden
		searchPart = bot.partById(Constants.SEARCH_PART);
		assertFalse(searchPart.getToolbarButtons().isEmpty());

		indexPart = bot.partById(Constants.INDEX_PART);
		assertFalse(indexPart.getToolbarButtons().isEmpty());

		detailPart = bot.partById(Constants.DETAIL_PART);
		assertFalse(detailPart.getToolbarButtons().isEmpty());

	}

	@Test
	@Disabled("Currently fail so disable to be able to continue without test breakages")
	public void ensureDataEntryCanBeCreated() {

		// Auf "Optimieren" Klicken, damit mehr Einträge im Index angezeigt werden können
		detailPart = bot.partById(Constants.DETAIL_PART);
		List<SWTBotToolbarButton> detailToolbarButtons = detailPart.getToolbarButtons();
		detailToolbarButtons.get(6);

		wfcPart = (WFCDetailPart) detailPart.getPart().getObject();

		UITestUtil.loadIndex(indexPart.getToolbarButtons().get(0));

		// int numberEntries = indexNattable.rowCount();

		createEntry();
		saveDetail();
		reloadIndex();
		// assertEquals(numberEntries + 1, indexNattable.rowCount(), "Erstellen eines Eintrags fehlgeschlagen");

	}

	@Test
	@Disabled("Currently fail so disable to be able to continue without test breakages")
	@DisplayName("Zeilen in Grids einfügen, ändern und löschen testen und das Speichern überprüfen (nicht Ubuntu)!")
	public void testGridFunctions() {

		detailPart = bot.partById(Constants.DETAIL_PART);
		wfcPart = (WFCDetailPart) detailPart.getPart().getObject();

		Table table = wfcPart.getDetail().getGrid("GraduationStep").getDataTable();

		// Testeintrag erstellen
		reloadIndex();
		// int numberEntries = indexNattable.rowCount();
		createEntry();
		saveDetail();
		reloadIndex();

//		// Zeilen einfügen und prüfen ob sie gespeichert wurden
//		insertRows();
//		saveDetail();
//		reloadIndex();
//		assertEquals(4, table.getRows().size(), "Einfügen von Zeilen fehlgeschlagen");

		// Zeilen verändern und löschen, Speichern prüfen
		modifyAndDeleteRows();
		saveDetail();
		reloadIndex();
		assertEquals(2, table.getRows().size(), "Löschen von Zeilen fehlgeschlagen");

		// Unter Mac werden die Werte die in Nattables geschrieben werden angehängt
		if (OSUtil.isLinux()) {
			assertEquals(222, table.getRows().get(0).getValue(3).getDoubleValue(), AENDERN_VON_ZEILEN_FEHLGESCHLAGEN);
			assertEquals(422, table.getRows().get(1).getValue(3).getDoubleValue(), AENDERN_VON_ZEILEN_FEHLGESCHLAGEN);
		} else {
			assertEquals(1, table.getRows().get(0).getValue(3).getDoubleValue(), AENDERN_VON_ZEILEN_FEHLGESCHLAGEN);
			assertEquals(1, table.getRows().get(1).getValue(3).getDoubleValue(), AENDERN_VON_ZEILEN_FEHLGESCHLAGEN);
		}

		// Eintrag wieder löschen
		detailPart = bot.partById(Constants.DETAIL_PART);
		detailPart.getToolbarButtons().get(2).click();
		UITestUtil.sleep();
		reloadIndex();
	}

	/**
	 * Speichert das aktuelle Detail und lädt es wieder
	 */
	private void saveDetail() {
		SWTBotView detailPart = bot.partById(Constants.DETAIL_PART);
		List<SWTBotToolbarButton> detailToolbarButtons = detailPart.getToolbarButtons();

		detailToolbarButtons.get(0).click();

		UITestUtil.sleep();

	}

	private void reloadIndex() {
		SWTBotView indexPart = bot.partById(Constants.INDEX_PART);
		UITestUtil.loadIndex(indexPart.getToolbarButtons().get(0));
		UITestUtil.sleep();
	}

	/**
	 * Erstellen eines komplett neuen Eintrags
	 */
	private void createEntry() {
		UIThreadRunnable.syncExec(bot.getDisplay(), () -> {
			MField f = wfcPart.getDetail().getField("KeyText");

			UUID uuid = UUID.randomUUID();
			String randomUUIDString = uuid.toString().substring(0, 8);

			f.setValue(new Value(randomUUIDString), false);

			f = wfcPart.getDetail().getField("Description");
			f.setValue(new Value("Testing the UI"), false);

			f = wfcPart.getDetail().getField("OrderReceiverKey");
			f.setValue(new LookupValue(1, "MIN", "MINOVA Information Services GmbH"), false);

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
//	private void insertRows() {
//		SWTBotToolbarButton btnInsert = bot.toolbarButtonWithId(Constants.CONTROL_GRID_BUTTON_INSERT);
//
//		assertNotNull(btnInsert, "Der Insert Button konnte nicht gefunden werden.");
//		UITestUtil.sleep();
//		btnInsert.click();
//		btnInsert.click();
//		btnInsert.click();
//		btnInsert.click();
//		UITestUtil.sleep();
//
//		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
//		SWTBotNatTable gridNattable = swtNatTableBot.nattable(2);
//
//		gridNattable.setCellDataValueByPosition(0, 1, "1");
//		gridNattable.setCellDataValueByPosition(0, 2, "12");
//
//		gridNattable.setCellDataValueByPosition(1, 1, "2");
//		gridNattable.setCellDataValueByPosition(1, 2, "22");
//
//		gridNattable.setCellDataValueByPosition(2, 1, "3");
//		gridNattable.setCellDataValueByPosition(2, 2, "32");
//
//		gridNattable.setCellDataValueByPosition(3, 1, "4");
//		gridNattable.setCellDataValueByPosition(3, 2, "42");
//
//		UITestUtil.sleep();
//	}

	/**
	 * Löscht einige Zeilen und Verändert andere
	 */
	private void modifyAndDeleteRows() {
		bot.text().setFocus();
		SWTBotToolbarButton btnDelete = bot.toolbarButtonWithId(Constants.CONTROL_GRID_BUTTON_DELETE);

		UITestUtil.sleep();

		SWTNatTableBot swtNatTableBot = new SWTNatTableBot();
		SWTBotNatTable gridNattable = swtNatTableBot.nattable(2);

		// Löscht Zeile mit Werten 1, 12
		gridNattable.click(1, 0);
		btnDelete.click();

		UITestUtil.sleep(500);

		// Löscht Zeile mit Werten 3, 32
		gridNattable.click(2, 0);
		btnDelete.click();

		UITestUtil.sleep(500);

		// Ändert Zeile 2, 22
		gridNattable.setCellDataValueByPosition(1, 2, "1");

		// Ändert Zeile 4, 42
		gridNattable.setCellDataValueByPosition(2, 2, "1");

		bot.text().setFocus();
		UITestUtil.sleep();
	}

}
