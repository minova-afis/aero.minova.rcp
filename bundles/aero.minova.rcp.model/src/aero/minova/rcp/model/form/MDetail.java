package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.model.helper.IHelper;

/**
 * Das Modell für den Detailbereich
 *
 * @author saak
 */
public class MDetail {

	private HashMap<String, MField> fields = new HashMap<>();
	private HashMap<String, MGrid> grids = new HashMap<>();

	private List<MSection> pageList = new ArrayList<>();

	private IHelper helper;

	private Control selectedField;

	/**
	 * Ein neues Feld dem Detail hinzufügen. Dabei muss selbst auf die Eindeutigkeit geachtet werden. Z.B.
	 * <ul>
	 * <li>"KeyLong" = Das Feld KeyLong der Detail-Maske</li>
	 * <li>"CustomerUserCode.UserCode" = Das Feld UserCode in der Maske CustomerUserCode.op.xml</li>
	 * </ul>
	 *
	 * @param name
	 *            Name / ID des Feldes
	 * @param field
	 *            das eigentliche Feld
	 */
	public void putField(MField field) {
		if (field == null) {
			return;
		}
		fields.put(field.getName(), field);
		field.setDetail(this);
	}

	/**
	 * Ein neues MGrid dem Detail hinzufügen. Dabei muss selbst auf die Eindeutigkeit geachtet werden. Z.B. Um diese Einigkeit zu erreichen wird der
	 * Procedure-Suffix des Grid-Knoten verwendet. Dies ist ein Pflichtfeld!
	 *
	 * @param name
	 *            Name / ID des Feldes
	 * @param MGrid
	 *            das eigentliche MGrid
	 */
	public void putGrid(MGrid g) {
		if (g == null) {
			return;
		}
		grids.put(g.getProcedureSuffix(), g);
	}

	public Collection<MGrid> getGrids() {
		return grids.values();
	}

	/**
	 * Liefert das MGrid mit dem Procedure-Suffix.
	 *
	 * @param name
	 *            Name des Grids
	 * @return Das MGrid
	 */
	public MGrid getGrid(String name) {
		return grids.get(name);
	}

	/**
	 * Liefert das Feld mit dem Namen. Felder im Detail haben kein Präfix. Felder in einer OptionPage haben das Präfix aus der XBS. z.B.
	 * <ul>
	 * <li>"KeyLong" = Das Feld KeyLong der Detail-Maske</li>
	 * <li>"CustomerUserCode.UserCode" = Das Feld UserCode in der Maske CustomerUserCode.op.xml</li>
	 * </ul>
	 *
	 * @param name
	 *            Name des Feldes
	 * @return Das Feld
	 */
	public MField getField(String name) {
		return fields.get(name);
	}

	public Collection<MField> getFields() {
		return fields.values();
	}

	public List<MSection> getPageList() {
		return pageList;
	}

	public void setPageList(List<MSection> pageList) {
		this.pageList = pageList;
	}

	public void addPage(MSection page) {
		this.pageList.add(page);

	}

	public IHelper getHelper() {
		return helper;
	}

	public void setHelper(IHelper helper) {
		this.helper = helper;
	}

	public boolean allFieldsValid() {
		for (MField field : fields.values()) {
			if (!field.isValid()) {
				return false;
			}
		}
		return true;
	}

	public Control getSelectedField() {
		return selectedField;
	}

	public void setSelectedField(Control selectedField) {
		this.selectedField = selectedField;
	}

}
