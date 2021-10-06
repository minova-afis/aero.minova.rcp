package aero.minova.rcp.rcp.accessor;

import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.model.form.ISectionAccessor;
import aero.minova.rcp.model.form.MSection;

public class SectionAccessor implements ISectionAccessor {

	private MSection mSection;
	private Section section;

	public SectionAccessor(MSection mSection, Section section) {
		this.mSection = mSection;
		this.section = section;
	}

	public MSection getMSection() {
		return mSection;
	}

	public void setMSection(MSection mSection) {
		this.mSection = mSection;
	}

	public Section getSection() {
		return section;
	}

	public void setSection(Section section) {
		this.section = section;
	}

	/**
	 * Ändert die Sichtbarkeit zum gegeben Wert. Außerdem wird die Section ein-/ausgeklappt damit sie weniger Platz wegnimmt <br>
	 * TODO: Komplett entfernen, nicht nur ausblenden
	 * 
	 * @param visible
	 */
	@Override
	public void setVisible(boolean visible) {
		section.setVisible(visible);
		section.setExpanded(visible);
	}

}
