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

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public IButtonAccessor getButtonAccessor() {
		return buttonAccessor;
	}

	public void setButtonAccessor(IButtonAccessor buttonAccessor) {
		this.buttonAccessor = buttonAccessor;
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

	public void removeSelectionListener(SelectionListener listener) {
		buttonAccessor.removeSelectionListener(listener);
	}

	public boolean isCanBeEnabled() {
		return buttonAccessor.isCanBeEnabled();
	}

	public void setCanBeEnabled(boolean canBeEnabled) {
		buttonAccessor.setCanBeEnabled(canBeEnabled);
	}

}
