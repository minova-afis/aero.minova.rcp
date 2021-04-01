package aero.minova.rcp.rcp.parts;

import java.text.MessageFormat;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * Druck-Vorschau
 *
 * @author saak
 * @since 11.0.0
 */
public class Preview {
	public static final String PART_ID = "aero.minova.rcp.rcp.part.formpreview";

	@Inject
	protected EPartService partService;

	@Inject
	private MPart part;

	private Browser browser;

	@PostConstruct
	public void postConstruct(Composite parent, MWindow window) {
		window.getContext().set(Preview.class, this);
		parent.setLayout(new FillLayout());

		browser = new Browser(parent, SWT.NONE);

		DisposeListener dl = new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
//				browser, "disposed");
				// damit ist auch der Preview nicht mehr gültig, es muss ein neuer erzeugt werden
				window.getContext().set(Preview.class, null);
			}
		};
		browser.addDisposeListener(dl);
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

		System.out.println(MessageFormat.format("verwende Browser {0}", browser.getBrowserType()));
		browser.setUrl(url);
	}

	/**
	 * zeigt im Browser eine leere Seite an, um die angezeigte Datei wieder freizugeben
	 *
	 * @author wild
	 */
	public void clear() {
		browser.setUrl("about:blank");
		// lasse dem Thread Zeit, den Zugriff auf die geöffnete Datei zu schließen
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {}
	}
}