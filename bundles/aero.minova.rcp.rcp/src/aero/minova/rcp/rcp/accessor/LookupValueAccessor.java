package aero.minova.rcp.rcp.accessor;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.rcp.widgets.Lookup;

public class LookupValueAccessor extends AbstractValueAccessor {

	@Inject
	IDataService dataService;

	@Inject
	UISynchronize sync;

	private MDetail detail;

	private Label description;

	public LookupValueAccessor(MField field, MDetail detail, Lookup control, Label description) {
		super(field, control);
		this.description = description;
		this.detail = detail;

	}

	/*
	 * Wenn wir einen Wert anfragen sollten wir immer nur einen erhalten. Die Ausnahme funktiniert nur bei einer Anfrage über KeyText. Dann wird der 1.
	 * genommen.
	 */
	@Override
	protected void updateControlFromValue(Control control, Value value) {
		try {
		System.out.println("updateControlFromValue " + value.toString());
		} catch (NullPointerException npe) {
			System.out.println("updateControlFromValue null");
		}

		if (value == null) {
			((Lookup) control).getDescription().setText("");
			((Lookup) control).setText("");
			((Lookup) control).setMessage("");
			return;
		}
		if (value instanceof LookupValue) {
			LookupValue lv = (LookupValue) value;
			((Lookup) control).getDescription().setText(lv.description);
			((Lookup) control).setText(lv.keyText);
			((Lookup) control).setMessage("");
		} else {
			Integer keyLong = null;
			String keyText = null;
			if (value.getType() == DataType.INTEGER) keyLong = value.getIntegerValue();
			else keyText = value.getStringValue();

			CompletableFuture<List<LookupValue>> resolveLookup = dataService.resolveLookup((MLookupField) field, true, keyLong, keyText);
			resolveLookup.thenAccept(llv -> sync.asyncExec(() -> {
				if (llv.isEmpty()) {
					((Lookup) control).getDescription().setText("");
					((Lookup) control).setText("");
					((Lookup) control).setMessage("?");
				} else {
					updateControlFromValue(control, llv.get(0)); // wir setzen ja oben und verlassen dann diese Methode
				}
			}));
		}
	}

	@Override
	/**
	 * Wenn das Feld den Focus verliert wird der Textinhalt überprüft. Ist der Inhalt in keiner Option vorhanden oder ist der Inhalt leer wird das Feld und die
	 * Description bereinigt Ist der Wert vorhanden, so wird geschaut ob er bereits gesetzt wurde oder ob dies getan Werden muss
	 */
	public void setFocussed(boolean focussed) {
		if (focussed) {
			return; // wenn wir den Focus erhalten, machen wir nichts
		}

		// Zunächst wird geprüft, ob der FocusListener aktiviert wurde, während keine Optionen vorlagen oder der DisplayValue neu gesetzt wird
		if (((MLookupField) field).getOptions() != null && field.getValue() == getDisplayValue()) {
			((Lookup) control).setMessage("");
			String displayText = ((Lookup) control).getText();
			if (displayText != null && !displayText.equals("")) {
//				Table optionTable = ((MLookupField) field).getOptions();
//				int indexKeyText = optionTable.getColumnIndex(Constants.TABLE_KEYTEXT);
//				int indexKeyLong = optionTable.getColumnIndex(Constants.TABLE_KEYLONG);
//
//				for (Row r : optionTable.getRows()) {
//					if (r.getValue(indexKeyText).getStringValue().toLowerCase().startsWith(displayText.toLowerCase())) {
//						Value rowValue = r.getValue(indexKeyLong);
//						// Der Wert wurde bereits gesetzt und wurde möglicherweise in der Zeile gekürzt
//						if (field.getValue() != null && field.getValue().getValue().equals(rowValue.getValue())) {
//							((Lookup) control).setText(r.getValue(indexKeyText).getStringValue());
//							return;
//						} else {
//							// Ist der Wert noch nicht gesetzt, so wird dies nun getan
//							field.setValue(rowValue, false);
//							return;
//						}
//					}
//				}
				return;
			}
			field.setValue(null, false);
		}
	}
}
