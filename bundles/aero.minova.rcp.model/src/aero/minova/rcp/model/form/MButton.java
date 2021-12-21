package aero.minova.rcp.model.form;

import org.eclipse.swt.events.SelectionListener;

public class MButton {

	private String id;
	private String icon;
	private String text;
	private IButtonAccessor buttonAccessor;

	public MButton(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setButtonAccessor(IButtonAccessor buttonAccessor) {
		this.buttonAccessor = buttonAccessor;
	}

	public IButtonAccessor getButtonAccessor() {
		return buttonAccessor;
	}

	public void setEnabled(boolean enabled) {
		buttonAccessor.setEnabled(enabled);
	}

	public boolean isEnabled() {
		return buttonAccessor.isEnabled();
	}

	public void addSelectionListener(SelectionListener listener) {
		buttonAccessor.addSelectionListener(listener);
	}

}
