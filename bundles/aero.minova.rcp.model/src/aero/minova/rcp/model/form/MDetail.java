package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.model.helper.IHelper;

/**
 * Das Modell für den Detailbereich
 *
 * @author saak
 */
public class MDetail {

	private TreeMap<String, MField> fields = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private List<MField> primaryFields = new ArrayList<>();
	private TreeMap<String, MGrid> grids = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private TreeMap<String, MBrowser> browsers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private TreeMap<String, MButton> buttons = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private List<MSection> mSectionList = new ArrayList<>();
	private List<IHelper> helpers = new ArrayList<>();
	private Map<String, Form> optionPages = new HashMap<>();
	private Map<String, Map<String, String>> optionPageKeys = new HashMap<>();
	private IDetailAccessor detailAccessor;
	private boolean clearAfterSave;
	private boolean isBooking;

	/**
	 * Ein neues Feld dem Detail hinzufügen. Dabei muss selbst auf die Eindeutigkeit geachtet werden. Z.B.
	 * <ul>
	 * <li>"KeyLong" = Das Feld KeyLong der Detail-Maske</li>
	 * <li>"CustomerUserCode.UserCode" = Das Feld UserCode in der Maske CustomerUserCode.op.xml</li>
	 * </ul>
	 *
	 * @param field
	 *            das eigentliche Feld
	 */
	public void putField(MField field) {
		if (field == null) {
			return;
		}
		fields.put(field.getName(), field);
		field.setDetail(this);

		if (field.isPrimary()) {
			primaryFields.add(field);
		}
	}

	/**
	 * Ein neues MGrid dem Detail hinzufügen. Dabei muss selbst auf die Eindeutigkeit geachtet werden. Z.B. Um diese Einigkeit zu erreichen wird der
	 * Procedure-Suffix des Grid-Knoten verwendet. Dies ist ein Pflichtfeld!
	 *
	 * @param g
	 *            das MGrid
	 */
	public void putGrid(MGrid g) {
		if (g == null) {
			return;
		}
		grids.put(g.getId(), g);
	}

	public Collection<MGrid> getGrids() {
		return grids.values();
	}

	/**
	 * Liefert das MGrid mit der ID
	 *
	 * @param name
	 *            Name des Grids
	 * @return Das MGrid
	 */
	public MGrid getGrid(String name) {
		return grids.get(name);
	}

	public Collection<MBrowser> getBrowsers() {
		return browsers.values();
	}

	/**
	 * Ein neuer MBrowser dem Detail hinzufügen.
	 *
	 * @param g
	 *            das MGrid
	 */
	public void putBrowser(MBrowser b) {
		if (b == null) {
			return;
		}
		browsers.put(b.getId(), b);
	}

	/**
	 * Liefert den MBrowser mit der ID
	 *
	 * @param id
	 *            Id des Browser
	 * @return den MBrowser
	 */
	public MBrowser getBrowser(String id) {
		return browsers.get(id);
	}

	public void putButton(MButton b) {
		if (b == null) {
			return;
		}
		buttons.put(b.getId(), b);
	}

	public Collection<MButton> getButtons() {
		return buttons.values();
	}

	public MButton getButton(String id) {
		return buttons.get(id);
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

	public MSection getPage(String id) {
		return getSection(id);
	}

	public MSection getSection(String id) {
		for (MSection m : mSectionList) {
			if (Objects.equals(m.getId(), id)) {
				return m;
			}
		}
		return null;
	}

	public List<MSection> getMSectionList() {
		return mSectionList;
	}

	public void setMSectionList(List<MSection> mSectionList) {
		this.mSectionList = mSectionList;
	}

	public void addMSection(MSection mSection) {
		this.mSectionList.add(mSection);
	}

	public void addOptionPage(Form op) {
		this.optionPages.put(op.getDetail().getProcedureSuffix(), op);
	}

	public Form getOptionPage(String name) {
		return optionPages.get(name);
	}

	public void addOptionPageKeys(String name, Map<String, String> keysToValue) {
		this.optionPageKeys.put(name, keysToValue);
	}

	public Map<String, String> getOptionPageKeys(String name) {
		return optionPageKeys.get(name);
	}

	public Collection<Form> getOptionPages() {
		return optionPages.values();
	}

	public List<IHelper> getHelpers() {
		return helpers;
	}

	public void addHelper(IHelper helper) {
		helpers.add(helper);
	}

	public boolean allFieldsAndGridsValid() {
		for (MField field : fields.values()) {
			if (!field.isValid()) {
				return false;
			}
		}
		for (MGrid grid : grids.values()) {
			if (!grid.isValid()) {
				return false;
			}
		}
		return true;
	}

	public List<MField> getPrimaryFields() {
		return primaryFields;
	}

	public IDetailAccessor getDetailAccessor() {
		return detailAccessor;
	}

	public void setDetailAccessor(IDetailAccessor detailAccessor) {
		this.detailAccessor = detailAccessor;
	}

	public boolean isClearAfterSave() {
		return clearAfterSave;
	}

	public void setClearAfterSave(boolean clearAfterSave) {
		this.clearAfterSave = clearAfterSave;
	}

	public void setAllFieldsRequired(boolean required) {
		for (MField f : fields.values()) {
			f.setRequired(required);
		}
	}

	public void setAllFieldsReadOnly(boolean readOnly) {
		for (MField f : fields.values()) {
			f.setReadOnly(readOnly);
		}
	}

	public void resetAllFieldsReadOnlyAndRequired() {
		for (MField f : fields.values()) {
			f.resetReadOnlyAndRequired();
		}
	}

	/**
	 * Setzt für alle Felder und Grids Required auf den gegebenen Wert
	 * 
	 * @param required
	 */
	public void setAllGridsAndFieldsRequired(boolean required) {
		setAllFieldsRequired(required);

		for (MGrid g : grids.values()) {
			g.setGridRequired(required);
		}
	}

	/**
	 * Setzt für alle Felder und Grids ReadOnly auf den gegebenen Wert
	 * 
	 * @param readOnly
	 */
	public void setAllGridsAndFieldsReadOnly(boolean readOnly) {
		setAllFieldsReadOnly(readOnly);

		for (MGrid g : grids.values()) {
			g.setGridReadOnly(readOnly);
		}
	}

	/**
	 * Setzt für alle Felder und Grids die ReadOnly und Required Werte auf die ursprünglichen (aus der .xbs)
	 */
	public void resetAllGridsAndFieldsReadOnlyAndRequired() {
		resetAllFieldsReadOnlyAndRequired();

		for (MGrid g : grids.values()) {
			g.resetReadOnlyAndRequiredColumns();
		}
	}

	public void resetAllFieldsVisibility() {
		for (MField f : fields.values()) {
			f.resetVisibility();
		}
	}

	public void commitAndCloseGridEditors() {
		for (MGrid g : grids.values()) {
			g.closeEditor();
		}
	}

	public boolean isBooking() {
		return isBooking;
	}

	public void setBooking(boolean isBooking) {
		this.isBooking = isBooking;
	}
}
