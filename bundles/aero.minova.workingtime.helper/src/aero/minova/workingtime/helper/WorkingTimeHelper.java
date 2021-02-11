package aero.minova.workingtime.helper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.helper.IHelper;
import aero.minova.rcp.preferences.ApplicationPreferences;

@Component
public class WorkingTimeHelper implements IHelper, ValueChangeListener {

	private MDetail detail;
	public static final String CONTROL_CONSUMER = "consumer";
	public static final String CONTROL_FIELD = "field";
	private MField startDate;
	private MField endDate;
	private MField renderedQuantity;
	private MField chargedQuantity;
	private MField bookingDate;
	private MLookupField employee;
	private String user;

	private Value endDateValue;
	private Value bookingDateValue;
	private Value employeeValue;

	Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);

	public WorkingTimeHelper() {
	}

	@Override
	public void setControls(MDetail detail) {
		this.detail = detail;
		initAccessor();
	}

	public void initAccessor() {
		startDate = detail.getField("StartDate");
		endDate = detail.getField("EndDate");
		bookingDate = detail.getField("BookingDate");
		renderedQuantity = detail.getField("RenderedQuantity");
		chargedQuantity = detail.getField("ChargedQuantity");
		employee = (MLookupField) detail.getField("EmployeeKey");

		// Auf diese werte reagieren wir
		startDate.addValueChangeListener(this);
		endDate.addValueChangeListener(this);

		// Mitarbeiter Setzen
		user = preferences.get(ApplicationPreferences.USER_PRESELECT_DESCRIPTOR, System.getProperty("user.name"));
		if (employeeValue == null) {
			// Hier müssen wir den Keytext auflösen!
			employeeValue = new Value(user);
		}
		employee.setValue(employeeValue, false);

		if (endDateValue != null) {
			startDate.setValue(endDateValue, false);
		}
		if (bookingDateValue != null) {
			bookingDate.setValue(bookingDateValue, false);
		}
	}

	protected void calculateTime() {
		if (startDate.getValue() == null) {
			return;
		}
		if (endDate.getValue() == null) {
			return;
		}

		Instant start = startDate.getValue().getInstantValue();
		Instant end = endDate.getValue().getInstantValue();
		long min = ChronoUnit.MINUTES.between(start, end);
		float renderedQty = getFloatFromMinutes(min);
		float chargedQty = getChargedQuantity(renderedQty);
		Value valueRe = new Value((double) renderedQty);
		Value valueCh = new Value((double) chargedQty);
		renderedQuantity.setValue(valueRe, true);
		chargedQuantity.setValue(valueCh, true);
		endDateValue = endDate.getValue();
		bookingDateValue = bookingDate.getValue();
		employeeValue = employee.getValue();
	}

	public float getFloatFromMinutes(long min) {
		float f = 0.0f;
		f = Math.round(min * 100.0 / 60.0);
		f = (float) (f / 100.0);
		return f;
	}

	public float getChargedQuantity(float f) {
		float chargedQty = f;
		// z.B. >= 0,75 -> 1
		// z.B. >= 0,25 -> 0,5
		int hours = (int) f;
		float dummy = chargedQty - (hours * 1.0f);
		chargedQty = hours * 1.0f;
		if (dummy >= 0.25f && dummy < 0.75f) {
			chargedQty += 0.5f;
		} else if (dummy >= 0.75f) {
			chargedQty += 1.0f;
		}
		return chargedQty;
	}

	@Override
	public void valueChange(ValueChangeEvent event) {
		if (!event.isUser()) {
			return;
		}
		calculateTime();
	}

}
