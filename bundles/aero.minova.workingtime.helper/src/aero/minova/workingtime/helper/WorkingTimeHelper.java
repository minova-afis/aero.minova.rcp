package aero.minova.workingtime.helper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.constants.Constants;
import aero.minova.rcp.model.Column;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
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
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;
import aero.minova.rcp.util.DateUtil;

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

	private Value endDateValue;
	private Value bookingDateValue;
	private LookupValue employeeValue;

	Preferences preferences = InstanceScope.INSTANCE.getNode(ApplicationPreferences.PREFERENCES_NODE);

	EventAdmin eventAdmin;

	@Inject
	MPerspective mPerspective;

	@Reference(policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.MANDATORY)
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
		startDate = detail.getField("StartDate");
		endDate = detail.getField("EndDate");
		bookingDate = detail.getField("BookingDate");
		renderedQuantity = detail.getField("RenderedQuantity");
		chargedQuantity = detail.getField("ChargedQuantity");
		employee = (MLookupField) detail.getField("EmployeeKey");
		orderreceiver = (MLookupField) detail.getField("OrderReceiverKey");
		service = (MLookupField) detail.getField("ServiceKey");
		serviceobject = (MLookupField) detail.getField("ServiceObjectKey");
		servicecontract = (MLookupField) detail.getField("ServiceContractKey");
		orderreceiver = (MLookupField) detail.getField("OrderReceiverKey");

		// Auf diese werte reagieren wir
		startDate.addValueChangeListener(this);
		endDate.addValueChangeListener(this);
		bookingDate.addValueChangeListener(this);
		employee.addValueChangeListener(this);
		orderreceiver.addValueChangeListener(ticketHelper);
		servicecontract.addValueChangeListener(ticketHelper);
		serviceobject.addValueChangeListener(ticketHelper);
		service.addValueChangeListener(ticketHelper);

		// Vorbelegte Werte werden für das DirtyFlag ans WFCDetailCASRequestUtil geliefert
		Table table = new Table();
		table.setName("WorkingTime");
		Row r = new Row();

		// Mitarbeiter Setzen
		user = preferences.get(ApplicationPreferences.USER_PRESELECT_DESCRIPTOR, System.getProperty("user.name"));
		if (employeeValue == null) {
			// Hier müssen wir den Keytext auflösen!
			LookupValueAccessor va = (LookupValueAccessor) employee.getValueAccessor();
			CompletableFuture<List<LookupValue>> valueFromAsync = va.getValueFromAsync(null, user);
			valueFromAsync.thenAccept(l -> Display.getDefault().asyncExec(() -> {
				if (!l.isEmpty()) {
					employeeValue = l.get(0);
					employee.setValue(employeeValue, false);

					// Da das Auflösen etwas dauern kann muss die Tabelle evtl vor dem hinzufügen der Spalte geleert werden
					if (!table.getRows().isEmpty()) {
						table.getRows().clear();
						table.addColumn(new Column(employee.getName(), employee.getDataType()));
						r.addValue(employeeValue);
						setTable(table, r);
					} else {
						table.addColumn(new Column(employee.getName(), employee.getDataType()));
						r.addValue(employeeValue);
					}
				}
			}));
		}

		bookingDateValue = new Value(DateUtil.getDate("0"));
		bookingDate.setValue(bookingDateValue, false);
		table.addColumn(new Column(bookingDate.getName(), bookingDate.getDataType()));
		r.addValue(bookingDateValue);

		setTable(table, r);
	}

	protected void calculateTime() {
		// Zuerst Werte speichern
		bookingDateValue = bookingDate.getValue();
		employeeValue = (LookupValue) employee.getValue();
		endDateValue = endDate.getValue();

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
		// falls Enddate vor StartDate, dann negative renderedQuantity => darf nicht abgespeichert werden
		if (renderedQty < 0) {
			endDate.setCanBeValid(false);
			endDate.setInvalidColor();
		} else {
			endDate.setCanBeValid(true);
			endDate.setValidColor();
		}
		Value valueRe = new Value((double) renderedQty);
		renderedQuantity.setValue(valueRe, true);
		Value valueCh = new Value((double) chargedQty);
		chargedQuantity.setValue(valueCh, true);
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

		// Vorbelegte Werte werden für das DirtyFlag ans WFCDetailCASRequestUtil geliefert
		Table table = new Table();
		table.setName("WorkingTime");
		Row r = new Row();

		switch (code) {
		case DEL:
			employee.setValue(employeeValue, false);
			table.addColumn(new Column(employee.getName(), employee.getDataType()));
			r.addValue(employeeValue);

			bookingDateValue = new Value(DateUtil.getDate("0"));
			bookingDate.setValue(bookingDateValue, false);
			table.addColumn(new Column(bookingDate.getName(), bookingDate.getDataType()));
			r.addValue(bookingDateValue);

			endDateValue = null;
			break;
		case SAVE:
			employee.setValue(employeeValue, false);
			table.addColumn(new Column(employee.getName(), employee.getDataType()));
			r.addValue(employeeValue);

			if (endDateValue != null) {
				startDate.setValue(endDateValue, false);
				table.addColumn(new Column(startDate.getName(), startDate.getDataType()));
				r.addValue(endDateValue);
			}
			if (bookingDateValue != null) {
				bookingDate.setValue(bookingDateValue, false);
				table.addColumn(new Column(bookingDate.getName(), bookingDate.getDataType()));
				r.addValue(bookingDateValue);
			}
			break;
		case NEW:
			employee.setValue(employeeValue, false);
			table.addColumn(new Column(employee.getName(), employee.getDataType()));
			r.addValue(employeeValue);

			if (bookingDateValue == null) {
				bookingDateValue = new Value(DateUtil.getDate("0"));
			}
			bookingDate.setValue(bookingDateValue, false);
			table.addColumn(new Column(bookingDate.getName(), bookingDate.getDataType()));
			r.addValue(bookingDateValue);

			if (endDateValue != null) {
				startDate.setValue(endDateValue, false);
				table.addColumn(new Column(startDate.getName(), startDate.getDataType()));
				r.addValue(endDateValue);
			}
			break;
		default:
			break;
		}

		setTable(table, r);
	}

	/**
	 * Setzt die übergebene Zeile in die Tabelle, setzt die Tabelle als selectedTable ins WFCDetailCASRequestsUtil, und checkt das DirtyFlag
	 * 
	 * @param t
	 * @param r
	 */
	private void setTable(Table t, Row r) {
		WFCDetailCASRequestsUtil casUtil = (WFCDetailCASRequestsUtil) mPerspective.getContext().get("WFCDetailCASRequestsUtil");

		t.addRow(r);
		casUtil.setSelectedTable(t);
		Value val = employee.getValue() != null ? new Value(employee.getValue().getValue()) : null;
		employee.setValue(val, false); // Check triggern
	}
}