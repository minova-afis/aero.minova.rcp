package aero.minova.rcp.rcp.util;

import java.util.Vector;

import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;

public class ValueAccessor {

	private Control control;
	private int index;
	private Field field;
	private DataType type;
	private Value value;
	private java.util.Vector<ValueChangeListener> listeners;

	public ValueAccessor(int index, Field field, Control control, DataType type) {
		this.index = index;
		this.field = field;
		this.control = control;
		this.type = type;
	}

	@Override
	public String toString() {
		return "ValueAccessor [value=" + value + "]";
	}

	/**
	 * einen neuen Wert setzen
	 * 
	 * @param value
	 *            der neue Wert
	 * @param user
	 *            true, wenn der neue Wert von einer Benutzereingabe herrührt. Wenn dieser Wert false ist, sollten keine Überprüfungen und Berechnungen
	 *            durchgeführt werden.
	 */
	public void setValue(Value value, boolean user) {
		if (this.value == value) return; // auch true, wenn beide null sind
		if (value != null && value.equals(this.value)) return;

		try {
			Value oldValue = this.value;
			this.value = value;
			fire(new ValueChangeEvent(control, field, oldValue, value, user));
			if (control instanceof Text) {
				Text text = (Text) control;
				text.setText(ValueBuilder.value(value, field).getText());
			} else if (control instanceof TextAssist) {
				TextAssist textAssist = (TextAssist) control;
				textAssist.setText(ValueBuilder.value(value, field).getText());
			}
		} catch (NullPointerException e) {
			System.out.println("Error ");
		}
	}

	public void setValue(Row row) {
		setValue(row.getValue(index), false);
	}

	public Value getValue() {
		String input = null;
		if (control instanceof Text) {
			Text text = (Text) control;
			input = text.getText();
		} else if (control instanceof TextAssist) {
			TextAssist textAssist = (TextAssist) control;
			input = textAssist.getText();
		}
		// TODO
		// Umwandlung des Strings in einen Value mit dem Korrekten Datentypen
		// 08:00
		// 14.11.2020
		// 5.7
		return value;
	}

	/**
	 * Mit dieser Methode kann man einen Listener für Wertänderungen anhängen.
	 * 
	 * @param listener
	 */
	public void addValueChangeListener(ValueChangeListener listener) {
		if (listener == null) return;
		if (listeners == null) listeners = new Vector<ValueChangeListener>();
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

	private void fire(ValueChangeEvent event) {
		if (listeners == null) return;
		for (ValueChangeListener listener : listeners) {
			listener.valueChange(event);
		}
	}

}
