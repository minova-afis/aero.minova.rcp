package aero.minova.rcp.model.form;

import org.eclipse.ui.forms.widgets.Section;

public interface ISectionAccessor {

	void setVisible(boolean visible);

	MSection getMSection();

	void setMSection(MSection mSection);

	Section getSection();

	void setSection(Section section);

}
