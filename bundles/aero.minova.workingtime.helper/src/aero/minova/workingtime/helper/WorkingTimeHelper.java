package aero.minova.workingtime.helper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.component.annotations.Component;

import aero.minova.rcp.dataservice.IHelper;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.ValueAccessor;

@Component
public class WorkingTimeHelper implements IHelper {

	Map<String, Control> controls;
	public static final String CONTROL_CONSUMER = "consumer";
	public static final String CONTROL_FIELD = "field";
	private Control startDate;
	private Control endDate;
	private Control reQty;
	private Control chQty;

	public WorkingTimeHelper() {
		System.out.println("Ich bin da: WorkingTimeHelper");
	}

	@Override
	public void setControls(Map<String, Control> controls) {
		this.controls = controls;
		initAccessor();
	}

	public void initAccessor() {
		startDate = controls.get("StartDate");
		endDate = controls.get("EndDate");
		reQty = controls.get("RenderedQuantity");
		chQty = controls.get("ChargedQuantity");

		startDate.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				calculateTime();
			}
		});
		endDate.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				calculateTime();
			}
		});
	}

	protected void calculateTime() {
		ValueAccessor start = (ValueAccessor) startDate.getData(Constants.VALUE_ACCESSOR);
		ValueAccessor end = (ValueAccessor) endDate.getData(Constants.VALUE_ACCESSOR);
		ValueAccessor reQty = (ValueAccessor) this.reQty.getData(Constants.VALUE_ACCESSOR);
		ValueAccessor chQty = (ValueAccessor) this.chQty.getData(Constants.VALUE_ACCESSOR);

		Instant iStart = start.getValue().getInstantValue();
		Instant iEnd = end.getValue().getInstantValue();
		long min = ChronoUnit.MINUTES.between(iStart, iEnd);
		float renderedQty = getFloatFromMinutes(min);
		float chargedQty = getChargedQuantity(renderedQty);
		Value valueRe = new Value((double) renderedQty);
		Value valueCh = new Value((double) chargedQty);
		Row row = RowBuilder.newRow().withValue(valueRe).withValue(valueCh).create();
		reQty.setValue(row);
		chQty.setValue(row);
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

}
