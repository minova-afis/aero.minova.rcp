package aero.minova.rcp.model.form;

import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.ToolItem;

public interface IButtonAccessor {

	public void setEnabled(boolean enabled);

	public boolean getEnabled();

	public void addSelectionListener(SelectionListener listener);

	public void removeSelectionListener(SelectionListener listener);

	boolean isCanBeEnabled();

	void setCanBeEnabled(boolean canBeEnabled);

	void updateEnabled();

	ToolItem getButton();

	void setButton(ToolItem button);

}
