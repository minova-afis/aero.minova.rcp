package aero.minova.rcp.rcp.accessor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

import aero.minova.rcp.model.form.IButtonAccessor;
import aero.minova.rcp.model.form.MButton;

public class ButtonAccessor implements IButtonAccessor {

	private ToolItem toolItem;
	private MenuItem menuItem;
	private MHandledToolItem handledToolItem;
	private MHandledMenuItem handledMenuItem;

	private MButton mButton;

	List<SelectionListener> selectionListeners = new ArrayList<>();

	// Wenn canBeEnabled false ist, darf der Button nicht enabled werden (z.B.: Löschen in Grids wenn keine Zellen ausgewählt)
	private boolean canBeEnabled = true;
	private boolean enabled = true;

	public ButtonAccessor(ToolItem toolItem) {
		this.toolItem = toolItem;
	}

	public ButtonAccessor(MHandledToolItem handledToolItem) {
		this.handledToolItem = handledToolItem;
	}

	public ButtonAccessor(MHandledToolItem handledToolItem, MHandledMenuItem handledMenuItem) {
		this.handledToolItem = handledToolItem;
		this.handledMenuItem = handledMenuItem;
	}

	public ButtonAccessor(ToolItem toolItem, MenuItem menuItem) {
		this.toolItem = toolItem;
		this.menuItem = menuItem;
	}

	public void setmButton(MButton mButton) {
		this.mButton = mButton;
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (toolItem != null) {
			toolItem.setEnabled(enabled && canBeEnabled);
		}

		if (menuItem != null) {
			menuItem.setEnabled(enabled && canBeEnabled);
		}

		if (handledToolItem != null) {
			handledToolItem.setEnabled(enabled && canBeEnabled);
		}

		if (handledMenuItem != null) {
			handledMenuItem.setEnabled(enabled && canBeEnabled);
		}

		this.enabled = enabled;
	}

	@Override
	public void setCanBeEnabled(boolean canBeEnabled) {
		this.canBeEnabled = canBeEnabled;
	}

	@Override
	public void updateEnabled() {
		setEnabled(enabled);
	}

	@Override
	public boolean isEnabled() {
		return enabled && canBeEnabled;
	}

	@Override
	public void addSelectionListener(SelectionListener listener) {
		if (toolItem != null) {
			toolItem.addSelectionListener(listener);
		}

		if (menuItem != null) {
			menuItem.addSelectionListener(listener);
		}

		if (toolItem == null && menuItem == null) {
			selectionListeners.add(listener);
		}
	}

	@Override
	public List<SelectionListener> getSelectionListener() {
		return selectionListeners;
	}

	@Override
	public boolean isCanBeEnabled() {
		return canBeEnabled;
	}

	@Override
	public void removeSelectionListener(SelectionListener listener) {
		if (toolItem != null) {
			toolItem.removeSelectionListener(listener);
		}

		if (menuItem != null) {
			menuItem.removeSelectionListener(listener);
		}

		if (toolItem == null && menuItem == null) {
			selectionListeners.remove(listener);
		}
	}

	public MButton getmButton() {
		return mButton;
	}

	public ToolItem getToolItem() {
		return toolItem;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

}
