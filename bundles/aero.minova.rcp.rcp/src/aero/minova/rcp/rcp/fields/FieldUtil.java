package aero.minova.rcp.rcp.fields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

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

		text.setText("00:00 am");
		shell.layout();
		size = text.getSize();
		SHORT_TIME_WIDTH = size.x + 5;

		text.setText("12.12.2020 22:55 am");
		shell.layout();
		size = text.getSize();
		DATE_TIME_WIDTH = size.x + 5;

		text.setText("00.00.0000");
		shell.layout();
		size = text.getSize();
		SHORT_DATE_WIDTH = size.x + 5;
	}

	public static final String TRANSLATE_PROPERTY = "aero.minova.rcp.translate.property";
	public static final String TRANSLATE_LOCALE = "aero.minova.rcp.translate.locale";
	/**
	 * Anzahl der Nachkommastellen be Zahlen. Ist der Wert > 0 handelt es automatisch im einen Double.
	 */
	public static final String FIELD_DECIMALS = "aero.minova.rcp.field.decimals";
	public static final String FIELD_MAX_VALUE = "aero.minova.rcp.field.maximum";
	public static final String FIELD_MIN_VALUE = "aero.minova.rcp.field.minimum";
	public static final String FIELD_LENGTH = "aero.minova.rcp.field.length";
	/**
	 * Wert des Feldes analog der Definition des Feldes
	 */
	public static final String FIELD_VALUE = "aero.minova.rcp.field.value";
	public static final int COLUMN_HEIGHT;
	public static final int COLUMN_WIDTH; // war 140
	public static final int TEXT_WIDTH = COLUMN_WIDTH;
	public static final int NUMBER_WIDTH = COLUMN_WIDTH - 75; // war 104
	public static final int SHORT_DATE_WIDTH; // war 88
	public static final int DATE_TIME_WIDTH; //
	public static final int SHORT_TIME_WIDTH; // war 52
	public static final int MARGIN_LEFT = 5;
	public static final int MARGIN_TOP = 5;
	public static final int MARGIN_BORDER = 2;

	private FieldUtil() {}

}
