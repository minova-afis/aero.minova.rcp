package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;
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
	private IValueAccessor valueAccessor;
	private String name;
	private String label;
	private String unitText;
	private final int decimals;
	private Integer sqlIndex;
	private int numberColumnsSpanned = 2;
	private int numberRowsSpanned = 1;
	private Double maximumValue;
	private Double minimumValue;
	private int maxTextLength;
	private boolean fillToRight = false;
	private boolean fillHorizontal = false;
	private String lookupTable;
	private String lookupProcedurePrefix;
	private String lookupDescription;
	private List<String> lookupParameters;
	private final DataType dataType;
	private DateTimeType dateTimeType;
	private MDetail mDetail;
	private boolean originalRequired;
	private boolean required;
	private boolean originalReadOnly;
	private boolean readOnly;
	private boolean originalVisible;
	private boolean visible;
	private int tabIndex = Integer.MAX_VALUE;
	private MSection mSection;
	private String cssClass = Constants.CSS_STANDARD;
	// Wenn canBeValid = false, dann ist das Feld aufgrund einer Berechnung auf falsch gesetzt
	private boolean canBeValid = true;
	private boolean primary = false;
	private boolean keyTypeUser = false;
	private boolean labelText;
	private boolean useResolveParms = false;
	private boolean filterLastAction = true;
	private String defaultValueString;

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

	protected MField(int decimals, DataType dataType) {
		this.decimals = decimals;
		this.dataType = dataType;
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
		checkDataType(value);

		Value oldValue = this.fieldValue;
		this.fieldValue = value;
		if (getValueAccessor() != null) {
			getValueAccessor().setValue(value, user);
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

	public int getNumberColumnsSpanned() {
		return numberColumnsSpanned;
	}

	public void setNumberColumnsSpanned(int numberColumnsSpanned) {
		this.numberColumnsSpanned = numberColumnsSpanned;
	}

	public int getNumberRowsSpanned() {
		return numberRowsSpanned;
	}

	public void setNumberRowsSpanned(int numberRowsSpanned) {
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

	public boolean isFillHorizontal() {
		return fillHorizontal;
	}

	public void setFillHorizontal(boolean fillHorizontal) {
		this.fillHorizontal = fillHorizontal;
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

	public IValueAccessor getValueAccessor() {
		return valueAccessor;
	}

	public void setValueAccessor(IValueAccessor valueAccessor) {
		this.valueAccessor = valueAccessor;
		updateCssClass(cssClass);
		valueAccessor.setEditable(!readOnly);
	}

	public MDetail getDetail() {
		return mDetail;
	}

	public void setDetail(MDetail detail) {
		this.mDetail = detail;
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
		} else {
			cssClass = Constants.CSS_REQUIRED;
		}

		isValid(); // Überprüfen und Farbe entsprechend setzen
		if (valueAccessor != null) {
			valueAccessor.setEditable(!readOnly); // Editierbarkeit entsprechend updaten
		}

		// Tabliste updaten, damit Feld (nicht) angesprungen werden kann
		if (mSection != null) {
			mSection.updateTabList();
		}
	}

	public void resetReadOnlyAndRequired() {
		setReadOnly(originalReadOnly);
		setRequired(originalRequired);
	}

	/**
	 * Ändert die Sichtbarkeit des Feldes. <br>
	 * Die gesamte Section muss dafür neu gezeichnet werden, also möglichst sparsam einsetzten.
	 *
	 * @param visible
	 */
	public void setVisible(boolean visible) {

		// Wenn es keine Änderung gab nichts tun, um Resourcen zu sparen
		if (this.visible == visible) {
			return;
		}

		this.visible = visible;

		// Section neu Zeichnen
		if (mDetail != null && mDetail.getDetailAccessor() != null) {
			mDetail.getDetailAccessor().redrawSection(mSection);
		}

		// Value Accessor entfernen, da das UI-Feld nicht mehr existiert
		if (!visible) {
			valueAccessor = null;
		}

	}

	public boolean isVisible() {
		return visible;
	}

	public void setOriginalVisible(boolean originalVisible) {
		this.originalVisible = originalVisible;
		setVisible(originalVisible);
	}

	public boolean getOriginalVisible() {
		return originalVisible;
	}

	/**
	 * Setzt die Sichtbarkeit auf den in der Maske definierten Zustand
	 */
	public void resetVisibility() {
		setVisible(originalVisible);
	}

	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public MSection getMSection() {
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

	public boolean isKeyTypeUser() {
		return keyTypeUser;
	}

	public void setKeyTypeUser(boolean keyTypeUser) {
		this.keyTypeUser = keyTypeUser;
	}

	public boolean isLabelText() {
		return labelText;
	}

	public void setLabelText(boolean b) {
		labelText = b;
	}

	public DateTimeType getDateTimeType() {
		return dateTimeType;
	}

	public void setDateTimeType(DateTimeType dateTimeType) {
		this.dateTimeType = dateTimeType;
	}

	public boolean isUseResolveParms() {
		return useResolveParms;
	}

	public void setUseResolveParms(boolean useResolveParms) {
		this.useResolveParms = useResolveParms;
	}

	public String getDefaultValueString() {
		return defaultValueString;
	}

	public void setDefaultValueString(String defaultValueString) {
		this.defaultValueString = defaultValueString;
	}

	/**
	 * Setzt den Tooltip für das Feld. Wird übersetzt
	 * 
	 * @param tooltip
	 */
	public void setTooltip(String tooltip) {
		if (getValueAccessor() != null) {
			valueAccessor.setTooltip(tooltip);
		}
	}

	public boolean isFilterLastAction() {
		return filterLastAction;
	}

	public void setFilterLastAction(boolean filterLastAction) {
		this.filterLastAction = filterLastAction;
	}

}
