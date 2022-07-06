package aero.minova.rcp.rcp.fields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import aero.minova.rcp.constants.Constants;

public class FieldUtil {

	static {
		Display display = Display.getCurrent();
		Shell shell = new Shell(display);
		Text text = new Text(shell, SWT.NONE);
		shell.setLayout(new GridLayout());

		text.setText("000.000.000");
		shell.layout();
		Point size = text.getSize();
		COLUMN_HEIGHT = size.y + 20;
		COLUMN_WIDTH = size.x + 80;

		text.setText("25.März.2024");
		shell.layout();
		size = text.getSize();
		SHORT_DATE_WIDTH = size.x + 5;
	}

	public static final String TRANSLATE_PROPERTY = Constants.TRANSLATE_PROPERTY;
	public static final String TRANSLATE_LOCALE = Constants.TRANSLATE_LOCALE;
	/**
	 * Anzahl der Nachkommastellen bei Zahlen. Ist der Wert > 0, handelt es automatisch im einen Double.
	 */
	public static final String FIELD_DECIMALS = Constants.FIELD_DECIMALS;
	public static final String FIELD_MAX_VALUE = Constants.FIELD_MAX_VALUE;
	public static final String FIELD_MIN_VALUE = Constants.FIELD_MIN_VALUE;
	public static final int COLUMN_HEIGHT;
	public static final int COLUMN_WIDTH; // war 140
	public static final int TEXT_WIDTH = COLUMN_WIDTH;
	public static final int NUMBER_WIDTH = COLUMN_WIDTH - 75; // war 104
	public static final int SHORT_DATE_WIDTH; // war 88
	public static final int MARGIN_LEFT = 5;
	public static final int MARGIN_TOP = 5;
	public static final int MARGIN_BORDER = 2;

	/**
	 * Lücke zwischen
	 * <ol>
	 * <li>NumberField und Unit {@link NumberField}</li>
	 * <li>LookupWidget und AusgabeText {@link LookupField}</li>
	 * </ol>
	 */
	public static final int UNIT_GAP = 5;

	private FieldUtil() {}

}
