package aero.minova.rcp.model.form;

import java.util.List;

import org.eclipse.swt.events.SelectionListener;

public interface IButtonAccessor {

	void setEnabled(boolean enabled);

	void addSelectionListener(SelectionListener listener);

	void setCanBeEnabled(boolean canBeEnabled);

	void updateEnabled();

	boolean isEnabled();
	
	List<SelectionListener> getSelectionListener();
}
