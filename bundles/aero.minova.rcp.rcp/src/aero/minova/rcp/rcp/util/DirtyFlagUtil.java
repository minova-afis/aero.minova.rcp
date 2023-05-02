package aero.minova.rcp.rcp.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.event.GridChangeEvent;
import aero.minova.rcp.model.event.GridChangeListener;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MGrid;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MParamStringField;
import aero.minova.rcp.model.form.MPeriodField;

public class DirtyFlagUtil implements ValueChangeListener, GridChangeListener {

	@Inject
	@Optional
	WFCDetailCASRequestsUtil casUtil;

	@Inject
	private TranslationService translationService;

	@Inject
	private MPart mPart;

	@Inject
	MWindow mwindow;

	@Inject
	EModelService eModelService;

	@Inject
	Form form;

	@Inject
	@Optional
	MDetail mDetail;

	@Inject
	private IDataFormService dataFormService;

	@Inject
	MPerspective mPerspective;

	@Inject
	MApplication mApplication;

	boolean dirtyFlag;

	/**
	 * Prüfung ob eine Wertänderung in Feldern oder Grids stattgefunden hat. Achtung: Das Dirty-Flag wird NICHT geupdated. Dafür
	 * broker.post(Constants.BROKER_CHECKDIRTY, ""); nutzen
	 *
	 * @return true wenn es eine Änderung gab, false ansonsten.
	 */
	public boolean checkDirty() {
		if (casUtil == null) {
			return false;
		}

		return checkFields() || checkOPs() || checkGrids();
	}

	private boolean checkFields() {
		// Prüfung der mFields ob es einen Value ≠ null gibt
		if (casUtil.getSelectedTable() == null || casUtil.getSelectedTable().getRows().isEmpty()) {
			return checkFieldsEmpty(dataFormService.getFieldsFromForm(form), "", new ArrayList<>());
		}

		return checkFieldsWithTable(casUtil.getSelectedTable(), form);
	}

	private boolean checkFieldsEmpty(List<Field> allFields, String fieldPrefix, List<MField> checkedFields) {
		List<MField> fieldsToCheck = new ArrayList<>();
		for (Field field : allFields) {
			MField mfield = mDetail.getField(fieldPrefix + field.getName());

			if (!checkedFields.contains(mfield)) {
				fieldsToCheck.add(mfield);
			}
		}
		return checkFieldsEmpty(fieldsToCheck);
	}

	private boolean checkFieldsEmpty(Collection<MField> fields) {
		for (MField mfield : fields) {
			if (mfield instanceof MBooleanField) { // Boolean Felder haben nie null Wert -> Prüfung auf false
				if (Boolean.TRUE.equals(mfield.getValue().getBooleanValue())) {
					return true;
				}
			} else if (mfield instanceof MParamStringField psf) {
				if (!psf.isNullValue()) { // Auch "leeren" ParamString Wert prüfen
					return true;
				}
			} else if (mfield instanceof MPeriodField pf) {
				if (!pf.isNullValue()) { // Auch "leeren" Period Wert prüfen
					return true;
				}
			} else if (mfield.getValue() != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Vergleicht Feld-Wert mit Wert aus Tabelle (vom CAS oder vorbelegte Werte aus Helpern)
	 */
	private boolean checkFieldsWithTable(Table t, Form f) {
		String fieldPrefix = f == form ? "" : f.getDetail().getProcedureSuffix() + "."; // OP-Felder haben OP-Namen als Prefix
		List<MField> checkedFields = new ArrayList<>();

		if (t != null) {
			for (int i = 0; i < t.getColumnCount(); i++) {
				MField c = mDetail.getField(fieldPrefix + t.getColumnName(i));
				checkedFields.add(c);
				if (c == null) {
					continue;
				}

				Value sV = t.getRows().get(0).getValue(i);
				if (!checkFieldWithValue(c, sV)) {
					return true;
				}
			}
		}

		// Sind die Felder in der Maske, die nicht in der ausgelesenen Tabelle sind, leer?
		return checkFieldsEmpty(dataFormService.getFieldsFromForm(f), fieldPrefix, checkedFields);
	}

	/**
	 * @param mField
	 * @param value
	 * @return true, wenn die Werte zusammenpassen, false wenn sie NICHT passen
	 */
	private boolean checkFieldWithValue(MField mField, Value value) {

		if (mField instanceof MLookupField) {
			// LU mit index 0 gibt es nie
			if (mField.getValue() == null && value != null && value.getIntegerValue() == 0) {
				return true;
			}

			// Solang KeyLong und Integerwert gleich sind sind LookupFelder nicht dirty
			return !(value == null && mField.getValue() != null || //
					value != null && mField.getValue() == null || //
					mField.getValue() != null && !mField.getValue().getIntegerValue().equals(value.getIntegerValue()));
		} else if (mField instanceof MBooleanField && value == null && Boolean.TRUE.equals(!mField.getValue().getBooleanValue())) {
			// TableValue null ->Booleanfeld Wert soll false sein
			return true;
		} else if (mField instanceof MParamStringField psf && value == null && psf.isNullValue()) {
			// Auch "leeren" ParamString Wert prüfen
			return true;
		} else if (mField instanceof MPeriodField pf && value == null && pf.isNullValue()) {
			// Auch "leeren" Period Wert prüfen
			return true;
		}

		return Objects.equals(mField.getValue(), value);
	}

	private boolean checkOPs() {
		// Sind die OP Felder leer?
		if (casUtil.getSelectedOptionPages().isEmpty()) {
			for (Form opform : mDetail.getOptionPages()) {
				if (checkFieldsEmpty(dataFormService.getFieldsFromForm(opform), opform.getDetail().getProcedureSuffix() + ".", new ArrayList<>())) {
					return true;
				}
			}
		}

		// Felder der OPs mit Werten vom CAS vergleichen
		for (Entry<String, Table> entry : casUtil.getSelectedOptionPages().entrySet()) {
			Form opform = mDetail.getOptionPage(entry.getKey());
			Table table = entry.getValue();
			if (checkFieldsWithTable(table, opform)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkGrids() {
		// Prüfen, ob die MGrids Zeilen haben
		if (casUtil.getSelectedGrids().isEmpty()) {
			for (MGrid grid : mDetail.getGrids()) {
				if (!grid.getDataTable().getRows().isEmpty()) {
					return true;
				}
			}
		}

		// Aktuellen Grids mit Werten der CAS-Zurückgabe vergleichen
		for (Entry<String, Table> entry : casUtil.getSelectedGrids().entrySet()) {
			MGrid mGrid = mDetail.getGrid(entry.getKey());
			if (!entry.getValue().equals(mGrid.getDataTable())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Liefert den Stand des DirtyFlags, true wenn es Änderungen gab, false ansonsten. Achtung, es wird keine erneute Prüfung aller Felder durchgeführt, dafür
	 * muss {@link DirtyFlagUtil#checkDirty()} ausgeführt werden
	 * 
	 * @return
	 */
	public boolean isDirty() {
		return dirtyFlag;
	}

	@Override
	public void gridChange(GridChangeEvent evt) {
		checkDirtyFlag();
	}

	@Override
	public void valueChange(ValueChangeEvent evt) {
		checkDirtyFlag();
	}

	@Inject
	@Optional
	public void checkDirtyFromBroker(@UIEventTopic(Constants.BROKER_CHECKDIRTY) String message) {
		MPerspective activePerspective = eModelService.getActivePerspective(mwindow);
		if (activePerspective.equals(mPerspective)) {
			checkDirtyFlag();
		}
	}

	private void checkDirtyFlag() {
		boolean setDirty = checkDirty();
		if (this.dirtyFlag != setDirty) {
			setDirtyFlag(setDirty);
		}
	}

	private void setDirtyFlag(boolean dirtyFlag) {
		this.dirtyFlag = dirtyFlag;

		mPart.setDirty(dirtyFlag);
		@SuppressWarnings("unchecked")
		List<MPerspective> pList = (List<MPerspective>) mApplication.getContext().get(Constants.DIRTY_PERSPECTIVES);

		if (dirtyFlag) {
			if (pList == null) {
				pList = new ArrayList<>();
				mApplication.getContext().set(Constants.DIRTY_PERSPECTIVES, pList);
			}
			if (!pList.contains(mPerspective)) {
				pList.add(mPerspective);
				refreshToolbar();
			}
		} else {
			if (pList != null) {
				pList.remove(mPerspective);
				refreshToolbar();
			}
		}
	}

	public void refreshToolbar() {
		List<MTrimBar> findElements = eModelService.findElements(mwindow, "aero.minova.rcp.rcp.trimbar.0", MTrimBar.class);
		MTrimBar tBar = findElements.get(0);
		Composite c = (Composite) (tBar.getChildren().get(0)).getWidget();
		if (c == null) {
			return;
		}
		ToolBar tb = (ToolBar) c.getChildren()[0];

		String perspectiveLabel = translationService.translate(mPerspective.getLabel(), null);
		for (ToolItem item : tb.getItems()) {
			if (item.getText().replace("*", "").equals(perspectiveLabel)) {
				item.setText((dirtyFlag ? "*" : "") + perspectiveLabel);
			}
		}
		tb.requestLayout();
	}

}
