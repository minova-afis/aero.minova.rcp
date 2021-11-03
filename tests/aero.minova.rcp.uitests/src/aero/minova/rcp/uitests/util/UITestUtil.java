package aero.minova.rcp.uitests.util;

import java.util.List;

import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.workbench.IWorkbench;
import org.eclipse.swtbot.e4.finder.widgets.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.osgi.framework.FrameworkUtil;

public class UITestUtil {

	public static void sleep() {
		sleep(1000);
	}

	public static void sleep(Integer milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {}
	}

	public static void loadIndex(SWTBotToolbarButton loadToolbarButton) {
		loadToolbarButton.click();

		// Warten bis Daten geladen sind
		do {
			sleep(500);
		} while (!loadToolbarButton.isEnabled());
	}

	public static IEclipseContext getEclipseContext(Class c) {
		final IEclipseContext serviceContext = EclipseContextFactory.getServiceContext(FrameworkUtil.getBundle(c).getBundleContext());
		return serviceContext.get(IWorkbench.class).getApplication().getContext();
	}

	public static void maximizeShell(SWTWorkbenchBot bot) {
		UIThreadRunnable.syncExec(bot.getDisplay(), () -> bot.getDisplay().getActiveShell().setSize(2000, 1500));
	}
}
