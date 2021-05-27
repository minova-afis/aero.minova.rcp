package aero.minova.rcp.rcp.accessor;

import javax.inject.Inject;

import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.ValueAccessor;
import aero.minova.rcp.rcp.widgets.Lookup;

public abstract class AbstractValueAccessor implements ValueAccessor {

	protected final MField field;
	protected final Control control;
	protected boolean focussed = false;
	private Value displayValue;

	@Inject
	IStylingEngine engine;

	public AbstractValueAccessor(MField field, Control control) {
		super();
		this.field = field;
		this.control = control;
		if (control == null) {
			return;
		}
		control.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				setFocussed(false);
				// Überprüfung ob der eingetragenen Wert in der Liste ist und ebenfalls gültig!
				field.setValue(field.getValue(), false);
				field.getDetail().setSelectedField(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				setFocussed(true);
				field.getDetail().setSelectedField(control);
			}
		});
	}

	@Override
	public void setMessageText(String message) {
		if (control instanceof Lookup) {
			((Lookup) control).setMessage(message);
		} else if (control instanceof Text) {
			((Text) control).setMessage(message);
		}
	}

	@Override
	public void setEditable(boolean editable) {
		if (field.isReadOnly()) {
			editable = false;
		}

		if (control instanceof Lookup) {
			((Lookup) control).setEditable(editable);
		} else if (control instanceof Text) {
			((Text) control).setEditable(editable);
		} else if (control instanceof TextAssist) {
			((TextAssist) control).setEditable(editable);
		}
	}

	protected abstract void updateControlFromValue(Control control, Value value);

	@Override
	public Value setValue(Value value, boolean user) {
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

	public Control getControl() {
		return control;
	}

	@Override
	public void setCSSClass(String classname) {
		if (engine != null) {
			engine.setClassname(control, classname);
		}
	}

}