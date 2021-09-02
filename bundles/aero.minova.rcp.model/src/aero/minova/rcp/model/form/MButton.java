package aero.minova.rcp.model.form;

public class MButton {

	private String id;
	private String icon;
	private String text;
	private IButtonAccessor buttonAccessor;

	public MButton(String id) {
		this.id = id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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

	public IButtonAccessor getButtonAccessor() {
		return buttonAccessor;
	}

	public void setButtonAccessor(IButtonAccessor buttonAccessor) {
		this.buttonAccessor = buttonAccessor;
	}

}
