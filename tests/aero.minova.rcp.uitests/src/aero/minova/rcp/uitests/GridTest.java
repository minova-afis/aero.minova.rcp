package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.parts.WFCDetailPart;
import aero.minova.rcp.uitests.util.UITestUtil;

public class GridTest {

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

	private WFCDetailPart wfcPart;

	@Before
	public void beforeClass() {
		// Auf Ubuntu nicht testen
		if (System.getProperty("os.name").startsWith("Linux")) {
			return;
		}
		bot = new SWTWorkbenchBot(UITestUtil.getEclipseContext(this.getClass()));
		SWTBotPreferences.TIMEOUT = 30000;
		openMask();
	}

	public void openMask() {

		// Maske über das Menü öffnen
		SWTBotMenu adminMenu = bot.menu("Manuelle Abwicklung");
		assertNotNull(adminMenu);
		SWTBotMenu stundenErfassung = adminMenu.menu("tGraduation");
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

		wfcPart = (WFCDetailPart) detailPart.getPart().getObject();
	}

	@Test
	@DisplayName("Zeilen in Grids einfügen, ändern und löschen testen und das Speichern überprüfen!")
	public void testGridFunctions() {
		System.out.println(System.getProperty("os.name"));
		// Auf Ubuntu nicht testen
		if (System.getProperty("os.name").startsWith("Linux")) {
			return;
		}

		Table table = wfcPart.getDetail().getGrid("GraduationStep").getDataTable();

		// Testeintrag erstellen
		UITestUtil.loadIndex(indexToolbar);
		int numberEntries = indexNattable.rowCount();
		createEntry();
		saveDetailAndReload();
		assertEquals(numberEntries + 1, indexNattable.rowCount(), "Erstellen eines Eintrags fehlgeschlagen");

		// Zeilen einfügen und prüfen ob sie gespeichert wurden
		insertRows();
		saveDetailAndReload();
		assertEquals(4, table.getRows().size(), "Einfügen von Zeilen fehlgeschlagen");

		// Zeilen verändern und löschen, Speichern prüfen
		modifyAndDeleteRows();
		saveDetailAndReload();
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
		detailToolbar.get(2).click();
		UITestUtil.sleep();
		UITestUtil.loadIndex(indexToolbar);
		assertEquals(numberEntries, indexNattable.rowCount(), "Löschen des Eintrages fehlgeschlagen");
		UITestUtil.sleep(10000);
	}

	/**
	 * Speichert das aktuelle Detail und lädt es wieder
	 */
	private void saveDetailAndReload() {
		detailToolbar.get(0).click();
		UITestUtil.sleep();
		UITestUtil.loadIndex(indexToolbar);
		UITestUtil.sleep();
		indexNattable.click(indexNattable.rowCount() - 1, 3);
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
		});
	}

	/**
	 * Erstellt einige Testzeilen im Grid
	 */
	private void insertRows() {
		bot.text().setFocus();
		SWTBotToolbarButton btnInsert = bot.toolbarButtonWithId(Constants.CONTROL_GRID_BUTTON_INSERT);

		btnInsert.click();
		btnInsert.click();
		btnInsert.click();
		btnInsert.click();

		gridNattable.setCellDataValueByPosition(1, 1, "11");
		gridNattable.setCellDataValueByPosition(1, 2, "12");

		gridNattable.setCellDataValueByPosition(2, 1, "21");
		gridNattable.setCellDataValueByPosition(2, 2, "22");

		gridNattable.setCellDataValueByPosition(3, 1, "31");
		gridNattable.setCellDataValueByPosition(3, 2, "32");

		gridNattable.setCellDataValueByPosition(4, 1, "41");
		gridNattable.setCellDataValueByPosition(4, 2, "42");

		bot.text().setFocus();
	}

	/**
	 * Löscht einige Zeilen und Verändert andere
	 */
	private void modifyAndDeleteRows() {
		bot.text().setFocus();
		SWTBotToolbarButton btnDelete = bot.toolbarButtonWithId(Constants.CONTROL_GRID_BUTTON_DELETE);

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
	}

	@AfterEach
	public void sleep() {
		bot.sleep(10000);
	}
}
