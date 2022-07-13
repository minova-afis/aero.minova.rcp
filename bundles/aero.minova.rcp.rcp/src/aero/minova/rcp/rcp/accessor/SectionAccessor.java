package aero.minova.rcp.rcp.accessor;

import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.css.widgets.MinovaSection;
import aero.minova.rcp.model.form.ISectionAccessor;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.rcp.util.TabUtil;

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

	@Override
	public void updateTabList() {
		TabUtil.updateTabListOfSectionComposite((Composite) section.getClient());
	}
}
