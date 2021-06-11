package aero.minova.workingtime.wizard;

import java.util.ArrayList;
import java.util.List;

import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.widgets.Lookup;

public class PeriodPageEnterHelper {

	private List<MField> fields;
	boolean selectFirstRequired;
	private PeriodPage periodPage;

	public PeriodPageEnterHelper(PeriodPage periodPage, boolean selectFirstRequired) {
		this.periodPage = periodPage;
		this.selectFirstRequired = selectFirstRequired;
		fields = new ArrayList<>();
	}

	public void addField(MField field) {
		fields.add(field);
	}

	public void setFocus(MField field) {
		((AbstractValueAccessor) field.getValueAccessor()).getControl().setFocus();
	}

	public void selectNewFieldOrSave(MField currentField) {
		int startIndex = 0;
		if (!selectFirstRequired) {
			startIndex = fields.indexOf(currentField) + 1;
		}

		// Alle Felder durchgehen, falls eines noch keinen Wert hat dieses auswählen
		int max = fields.size() + startIndex;
		while (startIndex < max) {
			MField f = fields.get(startIndex % fields.size());
			startIndex++;
			if (f.getValue() == null) {
				setFocus(f);
				return;
			}
		}

		// Wenn als letztes ein Popup Fenster offen war, dieses schließen und noch nicht speichern
		if (((AbstractValueAccessor) currentField.getValueAccessor()).getControl() instanceof Lookup) {
			Lookup l = (Lookup) ((AbstractValueAccessor) currentField.getValueAccessor()).getControl();
			l.closePopup();
			return;
		}

		// Wenn alle Felder gefüllt sind speichern
		periodPage.save();
	}
}
