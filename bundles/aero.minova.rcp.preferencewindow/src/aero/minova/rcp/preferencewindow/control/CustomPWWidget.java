package aero.minova.rcp.preferencewindow.control;

import org.eclipse.nebula.widgets.opal.preferencewindow.widgets.PWWidget;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Diese Klasse gewährt Zugang zu den Methoden aus PWWidget, die package gebunden sind.
 * Ansonsten fungiert sie genau wie PWWidget.
 * 
 * @author bauer
 *
 */
public abstract class CustomPWWidget extends PWWidget {
	private final String propertyKey;

	protected CustomPWWidget(String label, String propertyKey, int numberOfColumns, boolean singleWidget) {
		super(label, propertyKey, numberOfColumns, singleWidget);
		this.propertyKey = propertyKey;
	}

	@Override
	protected abstract Control build(Composite parent);

	@Override
	protected abstract void check();

	public String getCustomPropertyKey() {
		return this.propertyKey;
	}

}
