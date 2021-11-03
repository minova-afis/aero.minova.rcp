package aero.minova.rcp.preferencewindow.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class CustomPWCombo extends CustomPWWidget {
	private final List<Object> data;
	private final boolean editable;

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public CustomPWCombo(final String label, final String propertyKey, final Object... values) {
		this(label, propertyKey, false, values);
	}

	/**
	 * Constructor
	 *
	 * @param label associated label
	 * @param propertyKey associated key
	 */
	public CustomPWCombo(final String label, final String propertyKey, final boolean editable, final Object... values) {
		super(label, propertyKey, label == null ? 1 : 2, false);
		data = new ArrayList<Object>(Arrays.asList(values));
		this.editable = editable;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		buildLabel(parent, GridData.CENTER);

		final CCombo combo = new CCombo(parent, SWT.BORDER | (editable ? SWT.NONE : SWT.READ_ONLY));
		addControl(combo);

		for (int i = 0; i < data.size(); i++) {
			final Object datum = data.get(i);
			combo.add(datum.toString());
			if (datum.equals(PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey()))) {
				combo.select(i);
			}
		}

		combo.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), CustomPWCombo.this.data.get(CustomPWCombo.this.data.indexOf(combo.getText())));
		});

		return combo;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#check()
	 */
	@Override
	public void check() {
		final Object value = PreferenceWindow.getInstance().getValueFor(getCustomPropertyKey());
		if (value == null) {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(), null);
		} else {
			if (editable && !(value instanceof String)) {
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey() + "' has to be a String because it is associated to an editable combo");
			}

			if (!data.isEmpty()) {
				if (!value.getClass().equals(data.get(0).getClass())) {
					throw new UnsupportedOperationException("The property '" + getCustomPropertyKey() + "' has to be a " + data.get(0).getClass() + " because it is associated to a combo");
				}
			}

		}
	}
}
