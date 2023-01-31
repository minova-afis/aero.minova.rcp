package aero.minova.rcp.rcp.accessor;

import java.util.function.Predicate;

import javax.inject.Inject;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IStylingEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.IValueAccessor;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.widgets.LookupComposite;

public abstract class AbstractValueAccessor implements IValueAccessor {

	protected final MField field;
	protected final Control control;
	protected boolean focussed = false;
	private Value displayValue;

	@Inject
	IStylingEngine engine;

	@Inject
	IEventBroker broker;

	protected AbstractValueAccessor(MField field, Control control) {
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
				((DetailAccessor) field.getDetail().getDetailAccessor()).setSelectedControl(null);
			}

			@Override
			public void focusGained(FocusEvent e) {
				((DetailAccessor) field.getDetail().getDetailAccessor()).setSelectedControl(control);
				setFocussed(true);
			}
		});
	}

	@Override
	public void setMessageText(String message) {
		if (control instanceof LookupComposite lc) {
			lc.setMessage(message);
		} else if (control instanceof Text t) {
			t.setMessage(message);
		}
	}

	@Override
	public void setEditable(boolean editable) {
		if (field.isReadOnly()) {
			editable = false;
		}

		if (control instanceof LookupComposite lc) {
			lc.setEditable(editable);
		} else if (control instanceof Text t) {
			t.setEditable(editable);
		} else if (control instanceof TextAssist ta) {
			ta.setEditable(editable);
		} else if (control instanceof Button b) {
			b.setEnabled(editable);
		}
	}

	protected abstract void updateControlFromValue(Control control, Value value);

	@Override
	public Value setValue(Value value, boolean user) {
		// Wenn der Focus auf dem Control liegt, setzen wir keinen Wert
		if (isFocussed() && user) {
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
		if (engine != null && !control.isDisposed()) {
			engine.setClassname(control, classname);
		}
	}

	@Override
	public void updateSaveButton() {
		broker.send(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, Constants.SAVE_DETAIL_BUTTON);
	}

	@Override
	public void setFilterForLookupContentProvider(Predicate<LookupValue> filter) {
		// Tut nichts für Felder außer Lookups, ist im LookupValueAccessor überschrieben
	}
}