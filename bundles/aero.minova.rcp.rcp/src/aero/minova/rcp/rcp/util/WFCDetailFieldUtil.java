package aero.minova.rcp.rcp.util;

import java.text.MessageFormat;
import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;

public class WFCDetailFieldUtil {

	private static final String AERO_MINOVA_RCP_TRANSLATE_PROPERTY = "aero.minova.rcp.translate.property";
	private static final int COLUMN_WIDTH = 140;
	private static final int TEXT_WIDTH = COLUMN_WIDTH;
	private static final int NUMBER_WIDTH = 104;
	private static final int SHORT_DATE_WIDTH = 88;
	private static final int SHORT_TIME_WIDTH = 52;
	private static final int MARGIN_LEFT = 5;
	private static final int MARGIN_TOP = 5;
	private static final int MARGIN_SECTION = 8;
	private static final int SECTION_WIDTH = 4 * COLUMN_WIDTH + 3 * MARGIN_LEFT + 2 * MARGIN_SECTION; // 4 Spalten = 5
																										// Zwischenräume
	private static final int COLUMN_HEIGHT = 28;
	private static final int MARGIN_BORDER = 2;

	public static Control createBooleanField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();
		FormData formData = new FormData();
		Button button = formToolkit.createButton(composite, field.getTextAttribute(), SWT.CHECK);

		formData.width = COLUMN_WIDTH;
		formData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		formData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);

		button.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, labelText);
		button.setLayoutData(formData);
		button.setData(Constants.CONTROL_FIELD, field);
		button.setData(Constants.CONTROL_DATATYPE, DataType.BOOLEAN);
		addConsumer(button, field);

		return button;
	}

	public static Control createDateTimeField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();

		FormData formData = new FormData();
		formData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		formData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		formData.width = COLUMN_WIDTH;

		Button button = formToolkit.createButton(composite, field.getTextAttribute(), SWT.CHECK);
		button.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, labelText);
		button.setLayoutData(formData);
		button.setData(Constants.CONTROL_FIELD, field);
		button.setData(Constants.CONTROL_DATATYPE, DataType.INSTANT);
		addConsumer(button, field);

		return button;
	}

	public static Control createNumberField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();
		String unitText = field.getUnitText() == null ? "" : field.getUnitText();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		Text text = formToolkit.createText(composite, "", SWT.BORDER | SWT.RIGHT);
		addDataToText(text, field, DataType.DOUBLE);
		addConsumer(text, field);
		Label unit = formToolkit.createLabel(composite, unitText, SWT.LEFT);
		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();
		FormData unitFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		textFormData.width = NUMBER_WIDTH;

		unitFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		unitFormData.left = new FormAttachment(text, 0, SWT.RIGHT);
		unitFormData.width = COLUMN_WIDTH - NUMBER_WIDTH;

		label.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		int decimals = field.getNumber().getDecimals();
		if (field.isReadOnly() == true) {
			text.setEditable(false);
		}
		String format = "";
		while (decimals > format.length()) {
			format += "0";
		}
		if (format.length() > 0)
			format = "." + format;
		int x = 1;
		while (format.length() < 11) {
			x %= 4;
			if (x == 0)
				format = "," + format;
			else
				format = "0" + format;
			x++;

		}
		if (format.startsWith(","))
			format = format.substring(1);
		text.setMessage(MessageFormat.format(format, 0.0));
		text.setLayoutData(textFormData);

		unit.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, unitText);
		unit.setLayoutData(unitFormData);

		return text;
	}

	public static Control createShortDateField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		Text text = formToolkit.createText(composite, "", SWT.BORDER);
		addDataToText(text, field, DataType.INSTANT);
		addConsumer(text, field);
		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		textFormData.width = SHORT_DATE_WIDTH;

		label.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		text.setMessage("01.01.2000");
		text.setLayoutData(textFormData);

		return text;
	}

	public static Control createShortTimeField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		Text text = formToolkit.createText(composite, "", SWT.BORDER);
		addDataToText(text, field, DataType.INSTANT);
		addConsumer(text, field);
		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		textFormData.width = SHORT_TIME_WIDTH;

		label.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		text.setMessage("23:59");
		text.setLayoutData(textFormData);

		return text;
	}

	public static Control createTextField(Composite composite, Field field, int row, int column,
			FormToolkit formToolkit) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		Text text = formToolkit.createText(composite, "",
				SWT.BORDER | (getExtraHeight(field) > 0 ? SWT.MULTI : SWT.NONE));
		addDataToText(text, field, DataType.STRING);
		addConsumer(text, field);
		FormData labelFormData = new FormData();
		FormData textFormData = new FormData();

		labelFormData.top = new FormAttachment(text, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(text, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		textFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		textFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		if (field.getNumberColumnsSpanned() != null && field.getNumberColumnsSpanned().intValue() > 2
				&& "toright".equals(field.getFill())) {
			textFormData.width = COLUMN_WIDTH * 3 + MARGIN_LEFT * 2 + MARGIN_BORDER;
		} else {
			textFormData.width = TEXT_WIDTH;
		}
		if (field.getNumberRowsSpanned() != null && field.getNumberRowsSpanned().length() > 0) {
			textFormData.height = COLUMN_HEIGHT * Integer.parseInt(field.getNumberRowsSpanned()) - MARGIN_TOP;
		}

		label.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		text.setLayoutData(textFormData);

		return text;
	}

	private static int getExtraHeight(Field field) {
		if (field.getNumberRowsSpanned() != null && field.getNumberRowsSpanned().length() > 0) {
			return Integer.parseInt(field.getNumberRowsSpanned()) - 1;
		}
		return 0;
	}

	public static void addDataToText(Text t, Field f, DataType datatype) {
		t.setData(Constants.CONTROL_FIELD, f);
		t.setData(Constants.CONTROL_DATATYPE, datatype);
	}

	public static void addConsumer(Object o, Field field) {
		if (o instanceof Text) {
			Text text = (Text) o;
			text.setData(Constants.CONTROL_CONSUMER, (Consumer<Table>) t -> {

				Value value = t.getRows().get(0).getValue(t.getColumnIndex(field.getName()));
				Field f = (Field) text.getData(Constants.CONTROL_FIELD);
				String rowText = ValueBuilder.value(value, f).getText();
				if (ValueBuilder.value(value, f).getDataType() == DataType.DOUBLE) {
					String format = "%1." + f.getNumber().getDecimals() + "f";
					Double doublevalue = Double.valueOf(rowText);
					rowText = String.format(format, doublevalue);
					rowText = rowText.replace(',', '.');
				}
				text.setText(rowText);
				text.setData(Constants.CONTROL_DATATYPE, ValueBuilder.value(value).getDataType());
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
