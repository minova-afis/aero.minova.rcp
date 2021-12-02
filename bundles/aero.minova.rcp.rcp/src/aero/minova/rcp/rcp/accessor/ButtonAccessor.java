package aero.minova.rcp.rcp.accessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.e4.ui.model.application.ui.menu.MHandledToolItem;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolItem;

import aero.minova.rcp.model.form.IButtonAccessor;

public class ButtonAccessor implements IButtonAccessor {

	private ToolItem toolItem;
	private MHandledToolItem handledToolItem;
	List<SelectionListener> selectionListener = new ArrayList<>();
	
	
	// Wenn canBeEnabled false ist, darf der Button nicht enabled werden (z.B.: Löschen in Grids wenn keine Zellen ausgewählt)
	private boolean canBeEnabled = true;
	private boolean enabled = true;


	public ButtonAccessor(ToolItem toolItem) {
		this.toolItem = toolItem;
	}
	
	public ButtonAccessor(MHandledToolItem handledToolItem) {
		this.handledToolItem = handledToolItem;
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (toolItem !=null) {
			toolItem.setEnabled(enabled && canBeEnabled);
		} else {
			handledToolItem.setEnabled(enabled && canBeEnabled);
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
	
	
	public boolean isEnabled() {
		return enabled && canBeEnabled;
	}

	@Override
	public void addSelectionListener(SelectionListener listener) {
		if (toolItem !=null) {
			toolItem.addSelectionListener(listener);
		} else {
			selectionListener.add(listener);
		}
	}
	
	

	public List<SelectionListener> getSelectionListener() {
		return selectionListener;
	}

}
