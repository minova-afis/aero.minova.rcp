package aero.minova.rcp.rcp.util;

/**
 * A "ValueChange" event gets fired whenever a control (field) changes a "bound" value. You can register a ValueChangeListener with a source bean so as to be
 * notified of any bound value updates.
 * 
 * @since 1.0
 */
public interface ValueChangeListener extends java.util.EventListener {

	/**
	 * This method gets called when a bound value is changed.
	 * 
	 * @param evt
	 *            A ValueChangeEvent object describing the event source and the property that has changed.
	 */
	void valueChange(ValueChangeEvent evt);

}
