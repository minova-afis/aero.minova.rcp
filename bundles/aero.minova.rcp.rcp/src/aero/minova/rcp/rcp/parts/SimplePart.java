package aero.minova.rcp.rcp.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class SimplePart {
	protected Text text;

	@Inject
	public SimplePart() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		text = new Text(parent, SWT.NONE);
	}

	public void setText(String text) {
		this.text.setText(text);
	}
}