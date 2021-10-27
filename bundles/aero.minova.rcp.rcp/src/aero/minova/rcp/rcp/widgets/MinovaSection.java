package aero.minova.rcp.rcp.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

public class MinovaSection extends Section {

	private final ImageHyperlink imageLink;

	private boolean expandable;

	public MinovaSection(Composite parent, int style) {
		super(parent, style);

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

	public boolean getExpandable() {
		return expandable;
	}

	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}

}
