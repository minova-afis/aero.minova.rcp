package aero.minova.rcp.rcp.util;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class LookupFieldFocusListener implements FocusListener {

	/**
	 * Wenn der Keylong nicht gesetzt wurde, so wird das Feld bereinigt
	 */
	private void clearColumn(LookupControl lc) {
		if (lc.getData(Constants.CONTROL_KEYLONG) == null) {
			lc.setText("");
		} else {
			Table t = (Table) lc.getData(Constants.CONTROL_OPTIONS);
			if (t != null) {
				for (Row r : t.getRows()) {
					if (r.getValue(t.getColumnIndex(Constants.TABLE_KEYLONG)).getIntegerValue() == lc
							.getData(Constants.CONTROL_KEYLONG)) {
						lc.setText(r.getValue(t.getColumnIndex(Constants.TABLE_KEYTEXT)).getStringValue());
					}
				}
			}
		}
	}

	@Override
	public void focusGained(FocusEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void focusLost(FocusEvent e) {
		Text t = (Text) e.getSource();
		LookupControl lc = (LookupControl) t.getParent();
		clearColumn(lc);

	}

}
