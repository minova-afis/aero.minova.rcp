package aero.minova.rcp.uitests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swtbot.e4.finder.widgets.SWTBotView;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.nebula.nattable.finder.SWTNatTableBot;
import org.eclipse.swtbot.nebula.nattable.finder.widgets.SWTBotNatTable;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
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

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.rcp.parts.WFCDetailPart;

@RunWith(SWTBotJunit4ClassRunner.class)
public class OpenServiceContract {

	private SWTWorkbenchBot bot;

	private SWTBotView searchPart;
	private SWTBotView indexPart;
	private SWTBotView detailPart;

	private SWTBotNatTable searchNattable;
	private SWTBotNatTable indexNattable;
	private SWTBotNatTable gridDetailNattable;

	private List<SWTBotToolbarButton> searchToolbar;
	private List<SWTBotToolbarButton> indexToolbar;
	private List<SWTBotToolbarButton> detailToolbar;

	@Before
	public void beforeClass() {
		bot = new SWTWorkbenchBot(getEclipseContext());
		SWTBotPreferences.TIMEOUT = 30000;
		openStundenerfassung();
	}

	public void maximizeShell() {
		UIThreadRunnable.syncExec(bot.getDisplay(), () -> {
			bot.getDisplay().getActiveShell().setSize(2000, 1500);
		});
	}

	public void openStundenerfassung() {

		// Stundenerfassung über das Menü öffnen
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
		gridDetailNattable = swtNatTableBot.nattable(2);
		assertNotNull(gridDetailNattable);

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
			sleep(500);
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
		loadIndexFull();

		// Überprüfen, ob Daten geladen wurden

		assertTrue(indexNattable.rowCount() > 3);
	}

	private void loadIndexFull() {
		indexToolbar.get(0).click();

		// Warten bis Daten geladen sind
		do {
			sleep(500);
		} while (!indexToolbar.get(0).isEnabled());
	}

	@Test
	@DisplayName("Detail Laden und Überprüfen, ob Daten geladen wurden!")
	public void loadDetail() {

		loadIndexFull();
		indexNattable.click(4, 1);

		// Überprüfen, ob Daten geladen wurden
		sleep(1000);

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

		// maximizeShell();
		sleep(500);

		loadIndexFull();
		indexNattable.click(4, 1);

		// Überprüfen, ob Daten geladen wurden
		sleep(1000);

		MPart part = detailPart.getPart();
		WFCDetailPart wfcPart = (WFCDetailPart) part.getObject();
		MGrid grid = wfcPart.getDetail().getGrid("ServicePrice");
		int size = grid.getDataTable().getRows().size();
		assertEquals(size, 5);
		gridDetailNattable.click(5, 0);

		Control textClient = grid.getmSection().getSection().getTextClient();
		assertTrue(textClient instanceof ToolBar);

		SWTBotToolbarButton btnInsert = bot.toolbarButtonWithId(Constants.CONTROL_GRID_BUTTON_INSERT);
		SWTBotToolbarButton btnDelete = bot.toolbarButtonWithId(Constants.CONTROL_GRID_BUTTON_DELETE);

		btnDelete.click();

		sleep(250);
		int sizeAfterDelete = grid.getDataTable().getRows().size();
		assertEquals(sizeAfterDelete, 4);

		btnInsert.click();
		sleep(250);
		btnInsert.click();
		sleep(250);

		assertEquals(grid.getDataTable().getRows().size(), 6);
	}

	private void sleep(Integer milliseconds) {
		Integer mil = milliseconds;
		if (milliseconds == null) {
			mil = 1000;
		}
		try {
			Thread.sleep(mil);
		} catch (InterruptedException e) {}
	}

	protected static IEclipseContext getEclipseContext() {
		final IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(OpenServiceContract.class).getBundleContext());
		return serviceContext.get(IWorkbench.class).getApplication().getContext();
	}

	@AfterEach
	public void sleep() {
		bot.sleep(10000);
	}
}