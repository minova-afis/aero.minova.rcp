package aero.minova.rcp.css.widgets;

import java.awt.event.KeyListener;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.css.MinovaSectionStyler;
import aero.minova.rcp.util.ScreenshotUtil;

public class MinovaSection extends Section {
	private ICssStyler cssStyler;

	private final ImageHyperlink imageLink;

	private boolean expandable;

	private boolean minimized;

	public MinovaSection(Composite parent, int style, MPerspective mPerspective) {
		super(parent, style);

		cssStyler = new MinovaSectionStyler(this);

		expandable = (style & ExpandableComposite.TWISTIE) != 0;

		this.imageLink = new ImageHyperlink(this, SWT.LEFT | getOrientation() | SWT.NO_FOCUS);
		this.imageLink.setUnderlined(false);
		this.imageLink.setBackground(getTitleBarGradientBackground());
		this.imageLink.setForeground(getTitleBarForeground());
		this.imageLink.setFont(parent.getFont());
		super.textLabel = this.imageLink;

		// Bei Klick auf Titel Section ein-/ausklappen
		this.imageLink.addMouseListener(new MouseAdapter() {
			private boolean doubleClick;

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				doubleClick = true;
			}

			@Override
			public void mouseDown(MouseEvent e) {
				doubleClick = false;
				// Erst sichergehen, dass es sich nicht um einen Doppelklick handelt
				Display.getDefault().timerExec(Display.getDefault().getDoubleClickTime(), () -> {
					if (!doubleClick && e.button == 1) {
						if (!isExpanded()) {
							setExpanded(true);
						} else if (MinovaSection.this.getExpandable()) {
							setExpanded(false);
						}
					}
				});
			}
		});

		this.imageLink.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {

				// Aus- /Einklappen bei drücken von Leertaste
				// Für weitere KeyBindings aus if switch Statement machen!! 
				if (e.keyCode == SWT.SPACE) {
					if (!isExpanded()) {
						setExpanded(true);
					} else if (MinovaSection.this.getExpandable()) {
						setExpanded(false);
					}
				}
			}
		});

		// Rechtsklick-Menü für Screenshot erstellen
		TranslationService ts = mPerspective.getContext().get(TranslationService.class);
		String maskTitle = mPerspective.getPersistedState().get(Constants.FORM_NAME).replace(".xml", "");
		this.addMenuDetectListener(e -> ScreenshotUtil.menuDetectAction(e, this, maskTitle + "_" + this.getData(Constants.SECTION_NAME), ts));
		this.imageLink.addMenuDetectListener(e -> ScreenshotUtil.menuDetectAction(e, this, maskTitle + "_" + this.getData(Constants.SECTION_NAME), ts));

	}

	public void setImage(final Image image) {
		if (image != null) {
			this.imageLink.setImage(image);
		}
	}

	@Override
	public void setText(String title) {
		this.imageLink.setText(title);
		this.imageLink.requestLayout();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		((MinovaSectionData) this.getLayoutData()).setVisible(visible);
		this.getParent().requestLayout();
	}

	public boolean getExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}

	/**
	 * @return Style-Engine, der man die Properties geben kann
	 * @author Wilfried Saak
	 */
	public ICssStyler getCssStyler() {
		return cssStyler;
	}

	public void style() {
		cssStyler.style();
	}

	public ImageHyperlink getImageLink() {
		return imageLink;
	}

	public boolean isMinimized() {
		return minimized;
	}

	public void setMinimized(boolean minimized) {
		this.minimized = minimized;
	}
}
