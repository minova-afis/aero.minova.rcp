package aero.minova.rcp.preferencewindow.control;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Diese Klasse gewährt Zugang zu den Methoden aus PWWidget, die package gebunden sind. Ansonsten fungiert sie genau wie PWWidget.
 * 
 * @author bauer
 */
public abstract class CustomPWWidget extends PWWidget {
	private final String propertyKey;
	private final String tooltip;

	protected CustomPWWidget(String label, @Optional String tooltip, String propertyKey, int numberOfColumns, boolean singleWidget) {
		super(label, propertyKey, numberOfColumns, singleWidget);
		this.propertyKey = propertyKey;
		this.tooltip = tooltip;
	}

	@Override
	protected abstract Control build(Composite parent);

	@Override
	protected abstract void check();

	public String getCustomPropertyKey() {
		return this.propertyKey;
	}

	public String getTooltip() {
		return this.tooltip;
	}

	@Override
	protected void buildLabel(final Composite parent, final int verticalAlignment) {
		if (getLabel() != null) {
			final Label label = new Label(parent, SWT.NONE);
			label.setText(getLabel());
			setTooltip(label);
			final GridData labelGridData = new GridData(GridData.END, verticalAlignment, false, false);
			labelGridData.horizontalIndent = 25;
			label.setLayoutData(labelGridData);
			addControl(label);
		}
	}

	protected void setTooltip(Control control) {
		if (getTooltip() != null) {
			control.setToolTipText(getTooltip());
		}
	}

}
