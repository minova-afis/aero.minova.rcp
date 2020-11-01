package aero.minova.rcp.rcp.fields;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class ValueChangedEvent extends Event {

	public final Widget field;
	public final Object oldValue;
	public final Object newValue;
	public final SourceType sourceType;

	public enum SourceType {
		USER, SYSTEM
	}

	public ValueChangedEvent(Text field, Object oldValue, Object newValue, SourceType type) {
		this.field = field;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.sourceType = type;
	}

}
