package aero.minova.rcp.model.form;

import org.eclipse.swt.events.SelectionListener;

public interface IButtonAccessor {

	public void setEnabled(boolean enabled);

	public boolean getEnabled();

	public void addSelectionListener(SelectionListener listener);

	public void removeSelectionListener(SelectionListener listener);

	boolean isCanBeEnabled();

	void setCanBeEnabled(boolean canBeEnabled);

	void updateEnabled();

}
