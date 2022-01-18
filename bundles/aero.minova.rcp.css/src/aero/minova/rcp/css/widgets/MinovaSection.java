package aero.minova.rcp.css.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.css.ICssStyler;
import aero.minova.rcp.css.MinovaSectionStyler;

public class MinovaSection extends Section {
	private ICssStyler cssStyler;

	private final ImageHyperlink imageLink;

	private boolean expandable;

	public MinovaSection(Composite parent, int style) {
		super(parent, style);

		cssStyler = new MinovaSectionStyler(this);

		expandable = (style & ExpandableComposite.TWISTIE) != 0;

		this.imageLink = new ImageHyperlink(this, SWT.LEFT | getOrientation() | SWT.NO_FOCUS);
		this.imageLink.setUnderlined(false);
		this.imageLink.setBackground(getTitleBarGradientBackground());
		this.imageLink.setForeground(getTitleBarForeground());
		this.imageLink.setFont(parent.getFont());
		super.textLabel = this.imageLink;

		this.imageLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(final HyperlinkEvent e) {
				if (!isExpanded()) {
					setExpanded(true);
				} else if (MinovaSection.this.getExpandable()) {
					setExpanded(false);
				}
			}
		});
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
		((MinovaSectionData) this.getLayoutData()).visible = visible;
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
}
