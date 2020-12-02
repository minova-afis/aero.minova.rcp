package aero.minova.rcp.model.event;

import java.util.EventObject;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;

public class ValueChangeEvent extends EventObject {
	private static final long serialVersionUID = 202011291511L;
	private Value oldValue;
	private Value newValue;
	private boolean user;

	public ValueChangeEvent(MField field, Value oldValue, Value newValue) {
		this(field, oldValue, newValue, false);
	}

	public ValueChangeEvent(MField field, Value oldValue, Value newValue, boolean user) {
		super(field);
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.user = user;
	}

	public Value getNewValue() {
		return newValue;
	}

	public Value getOldValue() {
		return oldValue;
	}

	public MField getField() {
		return (MField) getSource();
	}

	public boolean isUser() {
		return user;
	}

}
