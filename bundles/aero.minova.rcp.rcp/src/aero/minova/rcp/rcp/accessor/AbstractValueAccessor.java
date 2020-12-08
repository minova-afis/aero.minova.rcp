package aero.minova.rcp.rcp.accessor;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.ValueAccessor;

public abstract class AbstractValueAccessor implements ValueAccessor {

	protected final MField field;
	protected final Control control;
	protected FocusListener focusListener;
	protected boolean focussed = false;
	private Value displayValue;

	public AbstractValueAccessor(MField field, Control control) {
		super();
		this.field = field;
		this.control = control;
		FocusListener abstractFocusListener = new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				setFocussed(false);
				field.setValue(field.getValue(), false);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setFocussed(true);
			}
		};
		this.focusListener = abstractFocusListener;
		if (control == null) return;
		control.addFocusListener(abstractFocusListener);
	}

	protected abstract void updateControlFromValue(Control control, Value value);

	@Override
	public Value setValue(Value value, boolean user) {
		System.out.println("setValue.isFocussed = " + isFocussed() + "; value = " + value);

		// Wenn der Focus auf dem Control liegt, setzen wir keinen Wert
		if (isFocussed()) {
			return getDisplayValue();
		}


		updateControlFromValue(control, value);
		setDisplayValue(value);
		return value;
	}

	@Override
	public void setValue(Row row) {
		Value value = row.getValue(field.getSqlIndex());
		field.setValue(value, false);
	}

	@Override
	public boolean isFocussed() {
		return focussed;
	}

	public void setFocussed(boolean focussed) {
		this.focussed = focussed;
		if (!focussed) {
			setValue(field.getValue(), false);
		}
	}

	public Value getDisplayValue() {
		return displayValue;
	}

	protected void setDisplayValue(Value displayValue) {
		this.displayValue = displayValue;
	}

}