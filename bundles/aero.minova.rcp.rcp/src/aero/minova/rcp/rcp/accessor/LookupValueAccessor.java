package aero.minova.rcp.rcp.accessor;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.swt.widgets.Control;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.LookupValue;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.widgets.LookupComposite;

public class LookupValueAccessor extends AbstractValueAccessor {

	@Inject
	IDataService dataService;

	@Inject
	UISynchronize sync;

	ILog logger = Platform.getLog(this.getClass());

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

		LookupComposite lc = (LookupComposite) control;

		if (value == null) {
			lc.getDescription().setText("");
			lc.setText("");
			if (lc.getEditable()) {
				lc.setMessage("...");
			}
			return;
		}
		if (value instanceof LookupValue lv) {
			lc.getContentProvider().translateLookup(lv);
			lc.getDescription().setText(lv.description);
			lc.setText(lv.keyText);
			lc.setMessage("...");
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
		if (field.getValue() == getDisplayValue() && control instanceof LookupComposite lc) {
			String displayText = lc.getText();
			if (("").equals(displayText)) {
				if (lc.getEditable()) {
					lc.setMessage("");
				}
				field.setValue(null, false);
			} else if (field.getValue() instanceof LookupValue value && !displayText.equals(value.keyText)) {
				field.setValue(null, false);
			}
		}
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		if (control != null && getDisplayValue() != null) {
			updateControlFromValue(control, getDisplayValue());
		}
	}

	public void updatePossibleValues() {

		// Für Read-Only Lookups müssen die Werte nicht angefragt werden, da sie eh nicht per Hand eingetragen werden können
		if (field.isReadOnly()) {
			return;
		}

		LookupComposite up = ((LookupComposite) control);
		CompletableFuture<List<LookupValue>> listLookup = dataService.listLookup((MLookupField) field, true);

		try {
			List<LookupValue> l = listLookup.get();
			up.getContentProvider().setValuesOnly(l);
			if (l.size() == 1 && field.isRequired()) {
				field.setValue(l.get(0), true);
			}
		} catch (ExecutionException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
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
