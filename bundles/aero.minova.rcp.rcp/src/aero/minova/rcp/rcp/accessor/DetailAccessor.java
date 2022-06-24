package aero.minova.rcp.rcp.accessor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.model.form.IDetailAccessor;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;

public class DetailAccessor implements IDetailAccessor {

	private MDetail mDetail;
	private Control selectedControl;

	@Inject
	MPerspective mPerspective;

	public DetailAccessor(MDetail mDetail) {
		this.mDetail = mDetail;
	}

	public Control getSelectedControl() {
		return selectedControl;
	}

	public void setSelectedControl(Control selectedControl) {
		this.selectedControl = selectedControl;
	}

	public List<Section> getSectionList() {
		List<Section> sectionList = new ArrayList<>();
		for (MSection mSection : mDetail.getMSectionList()) {
			sectionList.add(((SectionAccessor) mSection.getSectionAccessor()).getSection());
		}
		return sectionList;
	}

	@Override
	public void redrawSection(MSection mSection) {
		if (mPerspective == null) {
			return;
		}
		WFCDetailCASRequestsUtil casUtil = mPerspective.getContext().get(WFCDetailCASRequestsUtil.class);
		if (casUtil != null) {
			casUtil.redrawSection(mSection);
		}
	}

}
