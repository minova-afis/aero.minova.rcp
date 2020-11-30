package aero.minova.rcp.model.form;

import java.util.ArrayList;

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
	private Value value;
	private ValueAccessor valueAccessor;
	private String name;
	private String label;
	private String unitText;
	private Integer decimals;
	private Integer sqlIndex;
	private Integer numberColumnsSpanned = 2;
	private Integer numberRowsSpanned = 1;
	private Double maximumValue;
	private Double minimumValue;
	private boolean fillToRight = false;

	/**
	 * Mit dieser Methode kann man einen Listener für Wertänderungen anhängen.
	 * 
	 * @param listener
	 */
	public void addValueChangeListener(ValueChangeListener listener) {
		if (listener == null) return;
		if (listeners == null) listeners = new ArrayList<>();
		if (!listeners.contains(listener)) listeners.add(listener);
	}

	/**
	 * Mit dieser Methode kann man einen Listener für Wertänderungen entfernen.
	 * 
	 * @param listener
	 */
	public void removeValueChangeListener(ValueChangeListener listener) {
		if (listener == null) return;
		if (listeners == null) return;
		if (listeners.contains(listener)) listeners.remove(listener);
	}

	protected void fire(ValueChangeEvent event) {
		if (listeners == null) return;
		for (ValueChangeListener listener : listeners) {
			listener.valueChange(event);
		}
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value, boolean user) {
		if (this.value == value) return; // auch true, wenn beide null sind
		if (value != null && value.equals(this.value)) return;
		checkDataType(value);

		Value oldValue = this.value;
		this.value = value;
		if (valueAccessor != null) valueAccessor.setValue(value, user);
		fire(new ValueChangeEvent(this, oldValue, value, user));
	}

	/**
	 * Der Datentyp muss geprüft werden, bevor er gesetzt werden darf.
	 * 
	 * @param value
	 *            zu prüfender Datentyp
	 * @throws IllegalArgumentException
	 *             Wenn der Typ ungültig für das Feld ist.
	 */
	protected abstract void checkDataType(Value value);

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

	public void setDecimals(int decimals) {
		this.decimals = decimals;
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
}
