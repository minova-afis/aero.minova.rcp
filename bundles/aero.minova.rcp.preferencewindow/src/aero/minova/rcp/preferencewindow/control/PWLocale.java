package aero.minova.rcp.preferencewindow.control;

import java.util.List;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.nebula.widgets.opal.preferencewindow.PreferenceWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import aero.minova.rcp.preferencewindow.builder.DisplayType;
import aero.minova.rcp.preferencewindow.builder.InstancePreferenceAccessor;

public class PWLocale extends CustomPWWidget {
	Preferences preferences = InstanceScope.INSTANCE.getNode("aero.minova.rcp.preferencewindow");

	private List<String> dataC = CustomLocale.getCountries("language");
	private final List<String> dataL = CustomLocale.getLanguages();
	private final boolean editable;

	/**
	 * Constructor
	 *
	 * @param label       associated label
	 * @param propertyKey associated key
	 */
	public PWLocale(final String label, final String propertyKey, final Object... values) {
		this(label, propertyKey, false, values);
	}

	/**
	 * Constructor
	 *
	 * @param label       associated label
	 * @param propertyKey associated key
	 */
	public PWLocale(final String label, final String propertyKey, final boolean editable, final Object... values) {
		super(label, propertyKey, label == null ? 1 : 2, false);
		this.editable = editable;
	}

	/**
	 * @see org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget#build(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public Control build(final Composite parent) {
		buildLabel(parent, GridData.CENTER);

		final Combo combo = new Combo(parent, SWT.BORDER | (editable ? SWT.NONE : SWT.READ_ONLY));
		addControl(combo);

		for (int i = 0; i < dataL.size(); i++) {
			final Object datum = dataL.get(i);
			combo.add(datum.toString());
			if (datum.equals(PreferenceWindow.getInstance().getValueFor("language"))) {
				combo.select(i);
			}
		}

		combo.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue(getCustomPropertyKey(),
					PWLocale.this.dataL.get(combo.getSelectionIndex()));
			InstancePreferenceAccessor.putValue(preferences, getCustomPropertyKey(), DisplayType.COMBO,
					PWLocale.this.dataL.get(combo.getSelectionIndex()));
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				e.printStackTrace();
			}
		});

		buildLabel(parent, GridData.CENTER);

		final Combo comboC = new Combo(parent, SWT.BORDER | (editable ? SWT.NONE : SWT.READ_ONLY));
		addControl(comboC);

		for (int i = 0; i < dataC.size(); i++) {
			final Object datum = dataC.get(i);
			comboC.add(datum.toString());
			if (datum.equals(PreferenceWindow.getInstance().getValueFor("land"))) {
				comboC.select(i);
			}
		}

		comboC.addListener(SWT.Modify, event -> {
			PreferenceWindow.getInstance().setValue("land", PWLocale.this.dataL.get(combo.getSelectionIndex()));
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
				throw new UnsupportedOperationException("The property '" + getCustomPropertyKey()
						+ "' has to be a String because it is associated to an editable combo");
			}

			if (!dataC.isEmpty()) {
				if (!value.getClass().equals(dataC.get(0).getClass())) {
					throw new UnsupportedOperationException("The property '" + getCustomPropertyKey() + "' has to be a "
							+ dataC.get(0).getClass() + " because it is associated to a combo");
				}
			}

		}
	}
}