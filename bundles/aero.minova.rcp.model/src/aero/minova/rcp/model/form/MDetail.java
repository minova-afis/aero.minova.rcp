package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import aero.minova.rcp.model.helper.IHelper;

/**
 * Das Modell für den Detailbereich
 *
 * @author saak
 */
public class MDetail {

	private HashMap<String, MField> fields = new HashMap<>();

	private List<MPage> pageList = new ArrayList<MPage>();

	private IHelper helper;

	/**
	 * Ein neues Feld dem Detail hinzufügen. Dabei muss selbst auf die Eindeutigkeit
	 * geachtet werden. Z.B.
	 * <ul>
	 * <li>"KeyLong" = Das Feld KeyLong der Detail-Maske</li>
	 * <li>"CustomerUserCode.UserCode" = Das Feld UserCode in der Maske
	 * CustomerUserCode.op.xml</li>
	 * </ul>
	 *
	 * @param name  Name / ID des Feldes
	 * @param field das eigentliche Feld
	 */
	public void putField(MField field) {
		if (field == null)
			return;
		fields.put(field.getName(), field);
		field.setDetail(this);
	}

	/**
	 * Liefert das Feld mit dem Namen. Felder im Detail haben kein Präfix. Felder in
	 * einer OptionPage haben das Präfix aus der XBS. z.B.
	 * <ul>
	 * <li>"KeyLong" = Das Feld KeyLong der Detail-Maske</li>
	 * <li>"CustomerUserCode.UserCode" = Das Feld UserCode in der Maske
	 * CustomerUserCode.op.xml</li>
	 * </ul>
	 *
	 * @param name Name des Feldes
	 * @return Das Feld
	 */
	public MField getField(String name) {
		return fields.get(name);
	}

	public Collection<MField> getFields() {
		return fields.values();
	}

	public List<MPage> getPageList() {
		return pageList;
	}

	public void setPageList(List<MPage> pageList) {
		this.pageList = pageList;
	}

	public void addPage(MPage page) {
		this.pageList.add(page);

	}

	public IHelper getHelper() {
		return helper;
	}

	public void setHelper(IHelper helper) {
		this.helper = helper;
	}
}
