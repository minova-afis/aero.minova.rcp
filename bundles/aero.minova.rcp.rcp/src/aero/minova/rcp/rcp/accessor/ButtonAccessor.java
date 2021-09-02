package aero.minova.rcp.rcp.accessor;

import org.eclipse.swt.widgets.ToolItem;

import aero.minova.rcp.model.form.IButtonAccessor;
import aero.minova.rcp.model.form.MButton;

public class ButtonAccessor implements IButtonAccessor {

	private MButton mButton;
	private ToolItem button;

	public ButtonAccessor(MButton mButton, ToolItem button) {
		this.mButton = mButton;
		this.button = button;
	}

	public MButton getmButton() {
		return mButton;
	}

	public void setmButton(MButton mButton) {
		this.mButton = mButton;
	}

	public ToolItem getButton() {
		return button;
	}

	public void setButton(ToolItem button) {
		this.button = button;
	}

}
