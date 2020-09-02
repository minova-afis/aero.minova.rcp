package aero.minova.rcp.rcp.widgets;


import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * @author saak
 * @since 11.4.0
 */
public class LookupText extends Text {
	private LookupTwistie twistie;

	public LookupTwistie getTwistie() {
		return twistie;
	}

	@Override
	public void setBackground(Color color) {
		super.setBackground(color);
		if (twistie != null && !twistie.isDisposed()) {
			twistie.setBackground(color);
		}
	}

	public void setTwistie(LookupTwistie twistie) {
		this.twistie = twistie;
		if (twistie != null && !twistie.isDisposed()) {
			twistie.setBackground(getBackground());
		}
	}

	public LookupText(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void checkSubclass() {};
}