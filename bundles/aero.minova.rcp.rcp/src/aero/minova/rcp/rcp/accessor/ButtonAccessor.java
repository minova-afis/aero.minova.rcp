package aero.minova.rcp.rcp.accessor;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolItem;

import aero.minova.rcp.model.form.IButtonAccessor;
import aero.minova.rcp.model.form.MButton;

public class ButtonAccessor implements IButtonAccessor {

	private MButton mButton;
	private ToolItem button;

	// Wenn canBeEnabled false ist, darf der Button nicht enabled werden (z.B.: Löschen in Grids wenn keine Zellen ausgewählt)
	private boolean canBeEnabled = true;
	private boolean enabled = true;

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

	@Override
	public void setEnabled(boolean enabled) {
		button.setEnabled(enabled && canBeEnabled);
		this.enabled = enabled;
	}

	@Override
	public boolean getEnabled() {
		return button.getEnabled();
	}

	@Override
	public void addSelectionListener(SelectionListener listener) {
		button.addSelectionListener(listener);
	}

	@Override
	public void removeSelectionListener(SelectionListener listener) {
		button.removeSelectionListener(listener);
	}

	@Override
	public boolean isCanBeEnabled() {
		return canBeEnabled;
	}

	@Override
	public void setCanBeEnabled(boolean canBeEnabled) {
		this.canBeEnabled = canBeEnabled;
	}

	@Override
	public void updateEnabled() {
		setEnabled(enabled);
	}

}
