package aero.minova.rcp.rcp.accessor;

import aero.minova.rcp.css.widgets.MinovaSection;
import aero.minova.rcp.model.form.ISectionAccessor;
import aero.minova.rcp.model.form.MSection;

public class SectionAccessor implements ISectionAccessor {

	private MSection mSection;
	private MinovaSection section;

	public SectionAccessor(MSection mSection, MinovaSection section) {
		this.mSection = mSection;
		this.section = section;
	}

	public MSection getMSection() {
		return mSection;
	}

	public void setMSection(MSection mSection) {
		this.mSection = mSection;
	}

	public MinovaSection getSection() {
		return section;
	}

	public void setSection(MinovaSection section) {
		this.section = section;
	}

	/**
	 * Ändert die Sichtbarkeit zum gegeben Wert.
	 * 
	 * @param visible
	 */
	@Override
	public void setVisible(boolean visible) {
		section.setVisible(visible);
	}
}
