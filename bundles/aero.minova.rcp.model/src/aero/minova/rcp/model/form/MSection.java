package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;

public class MSection {

	// nur für die 1. Page möglich. Befindet sich immer an oberster Stelle
	private boolean isHead;
	// Zustand der Page (invisible, open, closed, minimized)
	private String status;
	// Das übergeordnete Element, welches alle Sections enthält
	private MDetail mDetail;
	// Liste aller MFields der Section
	private List<MField> mFields = new ArrayList<>();
	// ID dieser Page ("Head" für Head)
	private String id;
	// Text für diese Page
	private String label;
	// Symbol für diese Page
	private Image icon;
	private ISectionAccessor sectionAccessor;

	/**
	 * Erstellt eine neue MSection.
	 *
	 * @param isHead
	 *            makiert die Section als HEAD. Es gibt nur einen HEAD in einem Part.
	 * @param status
	 *            der Zustand der Section (invisible, open, closed, minimized)
	 * @param mDetail
	 *            das Detail in dem die Section erstellt werden soll.
	 * @param label
	 *            das Label der Section.
	 * @param control
	 *            das Twistie Element einer Section. Der HEAD hat keinen Twistie.
	 * @param section
	 *            das org.eclipse.ui.forms.widgets.Section Element
	 */
	public MSection(boolean isHead, String status, MDetail mDetail, String id, String label) {
		this.isHead = isHead;
		this.status = status;
		this.mDetail = mDetail;
		this.id = id;
		this.label = label;
	}

	public boolean isHead() {
		return isHead;
	}

	public void setHead(boolean isHead) {
		this.isHead = isHead;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public MDetail getmDetail() {
		return mDetail;
	}

	public void setmDetail(MDetail mDetail) {
		this.mDetail = mDetail;
	}

	public void setMFieldList(List<MField> mFields) {
		this.mFields = mFields;
	}

	public void addMField(MField mField) {
		this.mFields.add(mField);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public ISectionAccessor getSectionAccessor() {
		return sectionAccessor;
	}

	public void setSectionAccessor(ISectionAccessor sectionAccessor) {
		this.sectionAccessor = sectionAccessor;
	}

	public void setVisible(boolean visible) {
		sectionAccessor.setVisible(visible);
	}

	public void updateTabList() {
		if (sectionAccessor != null) {
			sectionAccessor.updateTabList();
		}
	}

	public List<MField> getMFields() {
		return mFields;
	}

}
