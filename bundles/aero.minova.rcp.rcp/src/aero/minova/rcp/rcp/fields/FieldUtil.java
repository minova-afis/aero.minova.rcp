package aero.minova.rcp.rcp.fields;

import java.util.function.Consumer;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.rcp.util.Constants;

@SuppressWarnings("restriction")
public class FieldUtil {

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
	public static final int COLUMN_WIDTH = 140;
	public static final int TEXT_WIDTH = COLUMN_WIDTH;
	public static final int NUMBER_WIDTH = 104;
	public static final int SHORT_DATE_WIDTH = 88;
	public static final int SHORT_TIME_WIDTH = 52;
	public static final int MARGIN_LEFT = 5;
	public static final int MARGIN_TOP = 5;
	public static final int COLUMN_HEIGHT = 28;
	public static final int MARGIN_BORDER = 2;

	private FieldUtil() {
	}

	protected static void addDataToText(Widget control, Field f, DataType datatype) {
		control.setData(Constants.CONTROL_FIELD, f);
		control.setData(Constants.CONTROL_DATATYPE, datatype);
	}

	public static int getRowIndex(Table t, Field field) {
		return t.getColumnIndex(field.getName());
	}

	protected static void addConsumer(Object o, Field field) {
		if (o instanceof Text) {
			Text text = (Text) o;
			text.setData(Constants.CONTROL_CONSUMER, (Consumer<Table>) t -> {

				Value value = t.getRows().get(0).getValue(t.getColumnIndex(field.getName()));
				Field f = (Field) text.getData(Constants.CONTROL_FIELD);
				text.setText(ValueBuilder.value(value, f).getText());
				text.setData(Constants.CONTROL_DATATYPE, ValueBuilder.value(value).getDataType());
				text.setData(Constants.CONTROL_VALUE, value);
			});
		} else if (o instanceof Button) {
			Button b = (Button) o;
			b.setData(Constants.CONTROL_CONSUMER, (Consumer<Table>) t -> {

				Value value = t.getRows().get(0).getValue(t.getColumnIndex(field.getName()));
				Field f = (Field) b.getData(Constants.CONTROL_FIELD);
				b.setText(ValueBuilder.value(value, f).getText());
				b.setData(Constants.CONTROL_DATATYPE, ValueBuilder.value(value).getDataType());
			});
		}
	}

}
