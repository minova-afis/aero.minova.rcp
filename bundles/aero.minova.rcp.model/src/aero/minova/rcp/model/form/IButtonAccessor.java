package aero.minova.rcp.model.form;

import java.util.List;

import org.eclipse.swt.events.SelectionListener;

public interface IButtonAccessor {

	void setEnabled(boolean enabled);

	boolean isEnabled();

	boolean isCanBeEnabled();

	void addSelectionListener(SelectionListener listener);

	void removeSelectionListener(SelectionListener listener);

	List<SelectionListener> getSelectionListener();

	void setCanBeEnabled(boolean canBeEnabled);

	void updateEnabled();
}
