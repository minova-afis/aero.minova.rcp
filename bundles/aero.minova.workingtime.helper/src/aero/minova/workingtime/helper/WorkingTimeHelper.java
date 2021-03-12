package aero.minova.workingtime.helper;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.helper.ActionCode;
import aero.minova.rcp.model.helper.IHelper;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.LookupValueAccessor;
import aero.minova.rcp.rcp.util.DateUtil;

@Component
public class WorkingTimeHelper implements IHelper, ValueChangeListener {

	private MDetail detail;
	private MField startDate;
	private MField endDate;
	private MField renderedQuantity;
	private MField chargedQuantity;
	private MField bookingDate;
	private MLookupField employee;
	private MLookupField orderreceiver;
	private MLookupField service;
	private MLookupField serviceobject;
	private MLookupField servicecontract;
	private String user;
	private LookupValue lookupValueUser;

	private Value endDateValue;
	private Value bookingDateValue;
	private LookupValue employeeValue;

	Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);

	EventAdmin eventAdmin;

	void registerEventAdmin(EventAdmin admin) {
		this.eventAdmin = admin;
	}

	void unregisterEventAdmin(EventAdmin admin) {
		this.eventAdmin = null;
	}

	public void postEvent(Value value) {
		Dictionary<String, Object> data = new Hashtable<>(2);
		data.put(EventConstants.EVENT_TOPIC, Constants.BROKER_RESOLVETICKET);
		data.put(IEventBroker.DATA, value);
		Event event = new Event(Constants.BROKER_RESOLVETICKET, data);
		eventAdmin.postEvent(event);
	}

	@Override
	public void setControls(MDetail detail) {
		this.detail = detail;
		initAccessor();
	}

	public void initAccessor() {
		TicketHelper ticketHelper = new TicketHelper(this);
		lookupValueUser = null;
		startDate = detail.getField("StartDate");
		endDate = detail.getField("EndDate");
		bookingDate = detail.getField("BookingDate");
		renderedQuantity = detail.getField("RenderedQuantity");
		chargedQuantity = detail.getField("ChargedQuantity");
		employee = (MLookupField) detail.getField("EmployeeKey");
		orderreceiver = (MLookupField) detail.getField("OrderReceiverKey");
		service = (MLookupField) detail.getField("ServiceKey");
		serviceobject = (MLookupField) detail.getField("ServiceKey");
		servicecontract = (MLookupField) detail.getField("ServiceKey");
		orderreceiver = (MLookupField) detail.getField("OrderReceiverKey");

		// Auf diese werte reagieren wir
		startDate.addValueChangeListener(this);
		endDate.addValueChangeListener(this);
		orderreceiver.addValueChangeListener(ticketHelper);
		servicecontract.addValueChangeListener(ticketHelper);
		serviceobject.addValueChangeListener(ticketHelper);
		service.addValueChangeListener(ticketHelper);

		// Mitarbeiter Setzen
		user = preferences.get(ApplicationPreferences.USER_PRESELECT_DESCRIPTOR, System.getProperty("user.name"));
		if (employeeValue == null) {
			// Hier müssen wir den Keytext auflösen!
			LookupValueAccessor va = (LookupValueAccessor) employee.getValueAccessor();
			CompletableFuture<List<LookupValue>> valueFromAsync = va.getValueFromAsync(null, user);
			valueFromAsync.thenAccept(l -> Display.getDefault().asyncExec(() -> {
				if (!l.isEmpty()) {
					lookupValueUser = l.get(0);
					employeeValue = lookupValueUser;
					employee.setValue(l.get(0), false);
				} else {
					employee.setValue(null, false);
				}
			}));
		}

		bookingDateValue = new Value(DateUtil.getDate("0"));
		bookingDate.setValue(bookingDateValue, false);
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
		if (employee.getValue() instanceof LookupValue) {
			employeeValue = (LookupValue) employee.getValue();
		} else {
			System.err.println(
					"WorkingTimeHelper.calculateTime() --> Kein LookupValue gefunden, es wird falsch gesetzt! ");
		}
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

	@Override
	public void handleDetailAction(ActionCode code) {
		switch (code) {
		case DEL:
			if (lookupValueUser != null) {
				employeeValue = lookupValueUser;
			} else {
				employeeValue = null;
				System.err.println(
						MessageFormat.format("LookupValue für User: {0} konnte nicht aiufgelöst werden!", user));

			}
			employee.setValue(employeeValue, false);
			bookingDateValue = new Value(DateUtil.getDate("0"));
			bookingDate.setValue(bookingDateValue, false);
			endDateValue = null;
			break;
		case SAVE:
			employee.setValue(employeeValue, false);
			if (endDateValue != null) {
				startDate.setValue(endDateValue, false);
			}
			if (bookingDateValue != null) {
				bookingDate.setValue(bookingDateValue, false);
			}
			break;
		case NEW:
			employee.setValue(employeeValue, false);
			if (bookingDateValue == null) {
				bookingDateValue = new Value(DateUtil.getDate("0"));
			}
			bookingDate.setValue(bookingDateValue, false);
			if (endDateValue != null) {
				startDate.setValue(endDateValue, false);
			}
			break;
		default:
			break;
		}

	}
}
