package aero.minova.rcp.model.form;

import java.util.List;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;

public interface IDetailAccessor {

	Control getSelectedControl();

	void setSelectedControl(Control selectedControl);
	
	List<Section> getSectionList();

}
