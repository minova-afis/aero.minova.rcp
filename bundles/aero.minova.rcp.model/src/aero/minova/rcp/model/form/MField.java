package aero.minova.rcp.model.form;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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
	private ValueAccessor valueAccessor;
	private String name;
	private String label;
	private String unitText;
	private final int decimals;
	private Integer sqlIndex;
	private Integer numberColumnsSpanned = 2;
	private Integer numberRowsSpanned = 1;
	private Double maximumValue;
	private Double minimumValue;
	private boolean fillToRight = false;
	private String lookupTable;
	private String lookupProcedurePrefix;
	private List<String> lookupParameters;
	private final DataType dataType;
	private MDetail detail;

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
		if (displayValue != null && displayValue.equals(value)) {
			return; // auch true, wenn beide null sind
		}
		checkDataType(value);

		Value oldValue = this.fieldValue;
		this.fieldValue = value;
		if (getValueAccessor() != null) {
			displayValue = getValueAccessor().setValue(value, user);
		}
		fire(new ValueChangeEvent(this, oldValue, value, user));
	}

	/**
	 * Der Datentyp muss geprüft werden, bevor er gesetzt werden darf.
	 *
	 * @param value zu prüfender Datentyp
	 * @throws IllegalArgumentException Wenn der Typ ungültig für das Feld ist.
	 */
	protected void checkDataType(Value value) {
		if (value == null) {
			return;
		}
		if (value.getType() != getDataType()) {
			throw new IllegalArgumentException(
					"Value of field " + getName() + " must be of type " + getDataType().toString() + "!");
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
	 * Wenn das Feld anzeigen soll, dass wir auf Daten warten, muss dieses Methode
	 * aufgerufen werden. Dabei wird auch der Wert
	 * {@link #setValue(Value, boolean)}} auf null gesetzt.
	 */
	public void indicateWaiting() {
		setValue(null, false);
	}

	public Consumer<Table> getConsumer() {
		return table -> setValue(table.getRows().get(0).getValue(sqlIndex), false);
	}

	public ValueAccessor getValueAccessor() {
		return valueAccessor;
	}

	public void setValueAccessor(ValueAccessor valueAccessor) {
		this.valueAccessor = valueAccessor;
	}

	public MDetail getDetail() {
		return detail;
	}

	void setDetail(MDetail detail) {
		this.detail = detail;
	}
}
