package aero.minova.rcp.rcp.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWTException;

import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.widgets.LookupComposite;

public class MinovaWizardPageEnterHelper {

	private List<MField> fields;
	boolean selectFirstRequired;

	public MinovaWizardPageEnterHelper(boolean selectFirstRequired) {
		this.selectFirstRequired = selectFirstRequired;
		fields = new ArrayList<>();
	}

	public void addField(MField field) {
		fields.add(field);
	}

	public void setFocus(MField field) {
		((AbstractValueAccessor) field.getValueAccessor()).getControl().setFocus();
	}

	public void selectNextField(MField currentField) {
		int startIndex = 0;
		if (!selectFirstRequired) {
			startIndex = fields.indexOf(currentField) + 1;
		}

		// Alle Felder durchgehen, falls eines noch keinen Wert hat dieses auswählen
		int max = fields.size() + startIndex;
		while (startIndex < max) {
			MField f = fields.get(startIndex % fields.size());
			startIndex++;
			if (f.getValue() == null && f.isRequired() && !f.isReadOnly()) {
				try {
					setFocus(f);
				} catch (SWTException e) {
					continue;
				}
				return;
			}
		}

		// Wenn als letztes ein Popup Fenster offen war, dieses schließen und noch nicht speichern
		if (((AbstractValueAccessor) currentField.getValueAccessor()).getControl() instanceof LookupComposite l) {
			l.closePopup();
		}
	}
}
