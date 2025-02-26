package aero.minova.rcp.rcp.parts;

import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * Druck-Vorschau
 *
 * @author saak
 * @since 11.0.0
 */
public class Preview {

	@Inject
	protected EPartService partService;

	@Inject
	private MPart part;

	ILog logger = Platform.getLog(this.getClass());

	private Browser browser;

	private boolean loading;

	@PostConstruct
	public void postConstruct(Composite parent, MWindow window) {
		window.getContext().set(Preview.class, this);
		parent.setLayout(new FillLayout());

		browser = new Browser(parent, SWT.NONE);

		DisposeListener dl = e -> window.getContext().set(Preview.class, null);
		browser.addDisposeListener(dl);

		browser.addProgressListener(new ProgressListener() {
			@Override
			public void completed(ProgressEvent event) {
				loading = false;
			}

			@Override
			public void changed(ProgressEvent event) {
				loading = true;
			}
		});
	}

	/**
	 * öffne die Adresse im integrierten Browser
	 *
	 * @param url
	 */
	public void openURL(String url) {
		if (part.getObject() == null) {
			partService.showPart(part, PartState.VISIBLE);
		}
		part.setVisible(true);
		part.getParent().setSelectedElement(part);

		logger.info(MessageFormat.format("verwende Browser {0} um URL {1} zu öffnen", browser.getBrowserType(), url));
		loadPage(url);
	}

	/**
	 * Synchrone Methode eine URL zu laden
	 * 
	 * @param url
	 * @return
	 */
	private boolean loadPage(String url) {
		Display display = Display.getCurrent();
		boolean set = browser.setUrl(url); // URL content loading is asynchronous
		loading = true;
		while (loading) { // Add synchronous behavior: wait till it finishes loading
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return set;
	}

	/**
	 * zeigt im Browser eine leere Seite an, um die angezeigte Datei wieder freizugeben
	 *
	 * @author wild
	 */
	public void clear() {
		loadPage("about:blank");
		logger.info("--Setting blank");
		// lasse dem Thread Zeit, den Zugriff auf die geöffnete Datei zu schließen
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}
}