package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;

/**
 * Modell eines Feldes im Detail oder auch im Index evtl. auch im SearchPart
 *
 * @author saak
 */
public abstract class MField {

	private ArrayList<ValueChangeListener> listeners;
	private Value fieldValue;
	private Value displayValue;
	private IValueAccessor valueAccessor;
	private String name;
	private String label;
	private String unitText;
	private final int decimals;
	private Integer sqlIndex;
	private Integer numberColumnsSpanned = 2;
	private Integer numberRowsSpanned = 1;
	private Double maximumValue;
	private Double minimumValue;
	private int maxTextLength;
	private boolean fillToRight = false;
	private String lookupTable;
	private String lookupProcedurePrefix;
	private String lookupDescription;
	private List<String> lookupParameters;
	private final DataType dataType;
	private MDetail detail;
	private boolean originalRequired;
	private boolean required;
	private boolean originalReadOnly;
	private boolean readOnly;
	private int tabIndex;
	private MSection mSection;
	private String cssClass = Constants.CSS_STANDARD;
	// Wenn canBeValid = false, dann ist das Feld aufgrund einer Berechnung auf falsch gesetzt
	private boolean canBeValid = true;
	private boolean primary = false;

	protected MField(DataType dataType) {
		this.dataType = dataType;
		decimals = 0;
	}

	protected MField(int decimals) {
		this.decimals = decimals;
		if (decimals > 0) {
			this.dataType = DataType.DOUBLE;
		} else {
			this.dataType = DataType.INTEGER;
		}
	}

	/**
	 * Mit dieser Methode kann man einen Listener für Wertänderungen anhängen.
	 *
	 * @param listener
	 */
	public void addValueChangeListener(ValueChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (listeners == null) {
			listeners = new ArrayList<>();
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * Mit dieser Methode kann man einen Listener für Wertänderungen entfernen.
	 *
	 * @param listener
	 */
	public void removeValueChangeListener(ValueChangeListener listener) {
		if (listener == null) {
			return;
		}
		if (listeners == null) {
			return;
		}
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	protected void fire(ValueChangeEvent event) {
		if (listeners == null) {
			return;
		}
		for (ValueChangeListener listener : listeners) {
			listener.valueChange(event);
		}
	}

	public Value getValue() {
		return fieldValue;
	}

	public void setValue(Value value, boolean user) {
		if (fieldValue != null && fieldValue.equals(value)) {
			return; // auch true, wenn beide null sind
		}
		checkDataType(value);

		Value oldValue = this.fieldValue;
		this.fieldValue = value;
		if (getValueAccessor() != null) {
			displayValue = getValueAccessor().setValue(value, user);
		}
		fire(new ValueChangeEvent(this, oldValue, value, user));

		isValid();
	}

	/**
	 * Der Datentyp muss geprüft werden, bevor er gesetzt werden darf.
	 *
	 * @param value
	 *            zu prüfender Datentyp
	 * @throws IllegalArgumentException
	 *             Wenn der Typ ungültig für das Feld ist.
	 */
	protected void checkDataType(Value value) {
		if (value == null) {
			return;
		}
		if (value.getType() != getDataType()) {
			throw new IllegalArgumentException("Value of field " + getName() + " must be of type " + getDataType().toString() + "!");
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getUnitText() {
		return unitText;
	}

	public void setUnitText(String unitText) {
		this.unitText = unitText;
	}

	public String getName() {
		return name;
	}

	public Integer getSqlIndex() {
		return sqlIndex;
	}

	public void setSqlIndex(Integer sqlIndex) {
		this.sqlIndex = sqlIndex;
	}

	public Integer getNumberColumnsSpanned() {
		return numberColumnsSpanned;
	}

	public void setNumberColumnsSpanned(Integer numberColumnsSpanned) {
		this.numberColumnsSpanned = numberColumnsSpanned;
	}

	public Integer getNumberRowsSpanned() {
		return numberRowsSpanned;
	}

	public void setNumberRowsSpanned(Integer numberRowsSpanned) {
		this.numberRowsSpanned = numberRowsSpanned;
	}

	public Integer getDecimals() {
		return decimals;
	}

	public Double getMaximumValue() {
		return maximumValue;
	}

	public void setMaximumValue(Double maximumValue) {
		this.maximumValue = maximumValue;
	}

	public Double getMinimumValue() {
		return minimumValue;
	}

	public void setMinimumValue(Double maximumValue) {
		this.minimumValue = maximumValue;
	}

	public int getMaxTextLength() {
		return maxTextLength;
	}

	public void setMaxTextLength(int maxTextLength) {
		this.maxTextLength = maxTextLength;
	}

	public boolean isFillToRight() {
		return fillToRight;
	}

	public void setFillToRight(boolean fillToRight) {
		this.fillToRight = fillToRight;
	}

	public String getLookupProcedurePrefix() {
		return lookupProcedurePrefix;
	}

	public void setLookupProcedurePrefix(String lookupProcedurePrefix) {
		this.lookupProcedurePrefix = lookupProcedurePrefix;
	}

	public String getLookupTable() {
		return lookupTable;
	}

	public void setLookupTable(String lookupTable) {
		this.lookupTable = lookupTable;
	}

	public String getLookupDescription() {
		return lookupDescription == null ? Constants.TABLE_DESCRIPTION : lookupDescription;
	}

	public void setLookupDescription(String lookupDescription) {
		this.lookupDescription = lookupDescription == null ? Constants.TABLE_DESCRIPTION : lookupDescription;
	}

	public List<String> getLookupParameters() {
		return lookupParameters;
	}

	public void addLookupParameter(String fieldname) {
		if (lookupParameters == null) {
			lookupParameters = new ArrayList<>();
		}
		lookupParameters.add(fieldname);
	}

	public DataType getDataType() {
		return dataType;
	}

	/**
	 * Wenn das Feld anzeigen soll, dass wir auf Daten warten, muss dieses Methode aufgerufen werden. Dabei wird auch der Wert
	 * {@link #setValue(Value, boolean)}} auf null gesetzt.
	 */
	public void indicateWaiting() {
		setValue(null, false);
	}

	public Consumer<Table> getConsumer() {
		return table -> setValue(table.getRows().get(0).getValue(sqlIndex), false);
	}

	public IValueAccessor getValueAccessor() {
		return valueAccessor;
	}

	public void setValueAccessor(IValueAccessor valueAccessor) {
		this.valueAccessor = valueAccessor;
		updateCssClass(cssClass);
		valueAccessor.setEditable(!readOnly);
	}

	public MDetail getDetail() {
		return detail;
	}

	public void setDetail(MDetail detail) {
		this.detail = detail;
	}

	public void setOriginalRequired(boolean originalRequired) {
		this.originalRequired = originalRequired;
		setRequired(originalRequired);
	}

	public void setOriginalReadOnly(boolean originalReadOnly) {
		this.originalReadOnly = originalReadOnly;
		setReadOnly(originalReadOnly);
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
		// Readonly hat Vorrang
		if (required && !readOnly) {
			cssClass = Constants.CSS_REQUIRED;
		} else if (!required && !readOnly) {
			cssClass = Constants.CSS_STANDARD;
		}

		isValid(); // Überprüfen und Farbe entsprechend setzen
		if (valueAccessor != null) {
			valueAccessor.updateSaveButton(); // Speicherknopf updaten
		}
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		if (readOnly) {
			cssClass = Constants.CSS_READONLY;
		} else if (!required) {
			cssClass = Constants.CSS_STANDARD;
		}

		isValid(); // Überprüfen und Farbe entsprechend setzen
		if (valueAccessor != null) {
			valueAccessor.setEditable(!readOnly); // Editierbarkeit entsprechend updaten
		}
	}

	public void resetReadOnlyAndRequired() {
		setReadOnly(originalReadOnly);
		setRequired(originalRequired);
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public MSection getmSection() {
		return mSection;
	}

	public void setMSection(MSection mSection) {
		this.mSection = mSection;
	}

	@Override
	public String toString() {
		return "MField(" + getName() + ")";
	}

	public void updateCssClass(String newClass) {
		if (valueAccessor == null) {
			return;
		}

		// Readonly wird nie geändert
		if (cssClass.equals(Constants.CSS_READONLY)) {
			valueAccessor.setCSSClass(Constants.CSS_READONLY);
			return;
		}

		valueAccessor.setCSSClass(newClass);
	}

	/**
	 * Kann (laut diesem Feld) gespeichtert werden
	 * <p>
	 * <ul>
	 * <li>Nicht angezeigte Felder (mSection == null) oder Felder ohne "required" brauchen keinen Wert
	 * <li>Ansonsten kann gespeichtert werden, wenn ein Wert eingetragen ist
	 * </ul>
	 * <p>
	 * Weitere Validierung findet in Unterklassen statt (z.B. Textlänge in MTextField)
	 */
	public boolean isValid() {
		if (!canBeValid) {
			setInvalidColor();
			return false;
		}

		if (fieldValue == null) {
			updateCssClass(cssClass);
		} else {
			updateCssClass(Constants.CSS_STANDARD);
		}

		if (!isRequired() || mSection == null) {
			return true;
		}
		return fieldValue != null;
	}

	public void setInvalidColor() {
		updateCssClass(Constants.CSS_INVALID);
	}

	public void setValidColor() {
		updateCssClass(Constants.CSS_STANDARD);
	}

	public boolean isCanBeValid() {
		return canBeValid;
	}

	public void setCanBeValid(boolean canBeValid) {
		this.canBeValid = canBeValid;
	}

	public boolean isPrimary() {
		return primary;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}
}
