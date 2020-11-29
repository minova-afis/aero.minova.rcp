package aero.minova.rcp.rcp.util;

import java.util.EventObject;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.Value;

public class ValueChangeEvent extends EventObject {
	private static final long serialVersionUID = 202011291511L;
	private transient Field field;
	private Value oldValue;
	private Value newValue;
	private boolean user;

	public ValueChangeEvent(Object source, Field field, Value oldValue, Value newValue) {
		this(source, field, oldValue, newValue, false);
	}

	public ValueChangeEvent(Object source, Field field, Value oldValue, Value newValue, boolean user) {
		super(source);
		this.field = field;
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

	public Field getField() {
		return field;
	}

	public boolean isUser() {
		return user;
	}

}
