package aero.minova.rcp.rcp.accessor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.widgets.LookupComposite;

public class LookupValueAccessor extends AbstractValueAccessor {

	private static final boolean LOG = "true".equalsIgnoreCase(Platform.getDebugOption("aero.minova.rcp.rcp/debug/lookupvalueaccessor"));

	@Inject
	IDataService dataService;

	@Inject
	UISynchronize sync;

	public LookupValueAccessor(MField field, LookupComposite control) {
		super(field, control);

	}

	public LookupValue getValueFromSync(Integer keyLong, String keyText) {
		CompletableFuture<List<LookupValue>> resolveLookup = dataService.resolveLookup((MLookupField) field, true, keyLong, keyText);
		List<LookupValue> llv = resolveLookup.join();
		if (!llv.isEmpty()) {
			return llv.get(0);
		}
		return null;
	}

	public CompletableFuture<List<LookupValue>> getValueFromAsync(Integer keyLong, String keyText) {
		return dataService.resolveLookup((MLookupField) field, true, keyLong, keyText);
	}

	/*
	 * Wenn wir einen Wert anfragen sollten wir immer nur einen erhalten. Die Ausnahme funktioniert nur bei einer Anfrage über KeyText. Dann wird der 1.
	 * genommen.
	 */
	@Override
	protected void updateControlFromValue(Control control, Value value) {
		// we see this control disposed in our unit tests
		if (control.isDisposed()) {
			return;
		}
		if (LOG) {
			try {
				System.out.println("updateControlFromValue " + value.toString());
			} catch (NullPointerException npe) {
				System.out.println("updateControlFromValue null");
			}

		}

		if (value == null) {
			((LookupComposite) control).getDescription().setText("");
			((LookupComposite) control).setText("");
			if (((LookupComposite) control).getEditable()) {
				((LookupComposite) control).setMessage("...");
				if (LOG) {
					System.out.println("Lookup " + ((LookupComposite) control).getLabel().getText() + " ist null");
				}
			}
			return;
		}
		if (value instanceof LookupValue) {
			LookupValue lv = (LookupValue) value;
			((LookupComposite) control).getDescription().setText(lv.description);
			((LookupComposite) control).setText(lv.keyText);
			((LookupComposite) control).setMessage("...");
			if (LOG) {
				System.out.println("Lookup " + ((LookupComposite) control).getLabel().getText() + " ist leer");
			}
		} else {
			Integer keyLong = null;
			String keyText = null;
			if (value.getType() == DataType.INTEGER) {
				keyLong = value.getIntegerValue();
			} else {
				keyText = value.getStringValue();
			}

			CompletableFuture<List<LookupValue>> resolveLookup = dataService.resolveLookup((MLookupField) field, true, keyLong, keyText);
			resolveLookup.thenAccept(llv -> sync.asyncExec(() -> {
				Value v = llv.isEmpty() ? null : llv.get(0);
				field.setValue(v, false);
				updateControlFromValue(control, v);
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
			// wenn wir dem Feld das erste Mal den Focus geben laden wir alle möglichkeiten.
			updatePossibleValues();
			return; // wenn wir den Focus erhalten, machen wir nichts
		}

		// Zunächst wird geprüft, ob der FocusListener aktiviert wurde, während keine
		// Optionen vorlagen oder der DisplayValue neu gesetzt wird
		if (field.getValue() == getDisplayValue()) {
			String displayText = ((LookupComposite) control).getText();
			if (("").equals(displayText)) {
				if (((LookupComposite) control).getEditable()) {
					((LookupComposite) control).setMessage("");
				}
				field.setValue(null, false);
			} else {
				LookupValue value = (LookupValue) field.getValue();
				if (value != null && !displayText.equals(value.keyText)) {
					field.setValue(null, false);
				}
			}
		}
	}

	public void updatePossibleValues() {
		LookupComposite up = ((LookupComposite) control);
		CompletableFuture<List<LookupValue>> listLookup = dataService.listLookup((MLookupField) field, true);
		listLookup.thenAccept(l -> Display.getDefault().asyncExec(() -> {
			try {
				up.getContentProvider().setValuesOnly(l);
				if (l.size() == 1) {
					field.setValue(l.get(0), false);
				}
			} catch (SWTException e) {}
		}));
	}

	/**
	 * Wir springen in das Feld!
	 */
	public void setFocus() {
		if (!control.isFocusControl()) {
			control.setFocus();
		}
	}

	@Override
	public void setFilterForLookupContentProvider(Predicate<LookupValue> filter) {
		((LookupComposite) control).getContentProvider().setFilter(filter);
	}
}
