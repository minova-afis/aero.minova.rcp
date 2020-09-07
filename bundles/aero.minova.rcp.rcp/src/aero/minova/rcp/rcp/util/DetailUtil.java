package aero.minova.rcp.rcp.util;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.jface.widgets.TextFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.Row;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.Value;
import aero.minova.rcp.plugin1.model.builder.RowBuilder;
import aero.minova.rcp.plugin1.model.builder.TableBuilder;
import aero.minova.rcp.plugin1.model.builder.ValueBuilder;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class DetailUtil {

	public static final int LABEL_WIDTH_HINT = 150;
	public static final int TEXT_WIDTH_HINT = 170;
	public static final int UNIT_WIDTH_HINT = 20;
	public static final int BIG_TEXT_WIDTH_HINT = 490;
	public static final int LOOKUP_DESCRIPTION_WIDTH_HINT = 320;

	public static final GridDataFactory gridDataFactory = GridDataFactory.fillDefaults().grab(true, false);
	public static final LabelFactory labelFactory = LabelFactory.newLabel(SWT.NONE);
	private static TextFactory textMultiFactory = TextFactory.newText(SWT.BORDER | SWT.MULTI);
	private static TextFactory textFactory = TextFactory.newText(SWT.BORDER).text("");

	public static void createField(Field field, Composite composite, Map<String, Control> controls) {
		if (!field.isVisible()) {
			return;
		}
		// 2 Felder nebeneinander (Label + Textbox) --> (einspaltig) false
		// 4 Felder nebeneinander (label + Textbox + label + Textbox) --> (zweispaltig)
		// true
		boolean twoColumns = false;
		if (new BigInteger("4").equals(field.getNumberColumnsSpanned())) {
			twoColumns = true;
		}

		// Immer am Anfang ein Label
		labelFactory.text(field.getTextAttribute())
				.supplyLayoutData(gridDataFactory.align(SWT.RIGHT, SWT.TOP).hint(LABEL_WIDTH_HINT, SWT.DEFAULT)::create)
				.create(composite);

		if (field.getLookup() != null) {
			buildLookupField(field, composite, twoColumns, controls);
		} else if (field.getBoolean() == null) {
			buildMiddlePart(field, composite, twoColumns, controls);
		} else if (field.getBoolean() != null) {
			throw new RuntimeException("Not yet supported");
//			Button button = btnFactory.create(composite);
//			button.setLayoutData(getGridDataFactory(twoColumns, field));
		}

		if (field.getUnitText() != null && field.getLookup() == null) {
			Label labelUnit = labelFactory.text(field.getUnitText()).create(composite);
			GridData data2 = gridDataFactory.align(SWT.LEFT, SWT.TOP).create();
			data2.widthHint = UNIT_WIDTH_HINT;
			labelUnit.setLayoutData(data2);
		}
	}

	private static void buildMiddlePart(Field field, Composite composite, boolean twoColumns,
			Map<String, Control> controls) {
		Text text;
		GridData gd;
		Integer numberRowSpand = null;
		gd = getGridDataFactory(twoColumns, field);
		if (field.getNumberRowsSpanned() != null) {
			numberRowSpand = Integer.valueOf(field.getNumberRowsSpanned());
			text = textMultiFactory.create(composite);
			if (numberRowSpand != null) {
				int hight = text.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
				gd.heightHint = hight * numberRowSpand;
			}
		} else {
			text = textFactory.create(composite);
		}
		text.setLayoutData(gd);
		if (twoColumns && gd.horizontalSpan != 5) {
			Label l = new Label(composite, SWT.None);
			GridData data = gridDataFactory.align(SWT.LEFT, SWT.TOP).create();
			data.horizontalSpan = 3;
			data.widthHint = LOOKUP_DESCRIPTION_WIDTH_HINT;
			l.setLayoutData(data);
		}
		text.addVerifyListener(e -> {
			final String oldString = ((Text) e.getSource()).getText();
			String newS = oldString.substring(0, e.start) + e.text + oldString.substring(e.end);
			if(field.getNumber() != null)
			{
				if(field.getNumber().getDecimals() == 2)
				{
					boolean isFloat = true;
					try {
						Float.parseFloat(newS);
					} catch (NumberFormatException ex) {
						isFloat = false;
					}
					if (!isFloat) {
						e.doit = false;
					}
				}

			}
			else if (field.getShortDate() != null || field.getLongDate() != null || field.getDateTime() != null
					|| field.getShortTime() != null) {
				String allowedCharacters;
				if (field.getShortTime() != null) {
					allowedCharacters = "1234567890:";
				} else {
					allowedCharacters = "1234567890.";
				}
				for (int index = 0; index < newS.length(); index++) {
					char character = newS.charAt(index);
					boolean isAllowed = allowedCharacters.indexOf(character) > -1;
					if (!isAllowed) {
						e.doit = false;
						return;
					}
				}
			}
			else if (field.getText() != null) {
				if (newS.length() > field.getText().getLength()) {
					e.doit = false;
				}
			}
		});
		text.setData("field", field);
		text.setData("consumer", (Consumer<Table>) t -> {

			Value rowindex = t.getRows().get(0).getValue(t.getColumnIndex(field.getName()));
			text.setData("dataType", ValueBuilder.newValue(rowindex).dataType());
			text.setText((String) ValueBuilder.newValue(rowindex).create());
		});
		controls.put(field.getName(), text);
	}

	private static void buildLookupField(Field field, Composite composite, boolean twoColumns,
			Map<String, Control> controls) {

		LookupControl lookUpControl = new LookupControl(composite, SWT.LEFT);
		lookUpControl.setLayoutData(getGridDataFactory(twoColumns, field));
		lookUpControl.setData("field", field);
		lookUpControl.setData("lookupConsumer", (Consumer<Map>) m -> {

			int keyLong = (Integer) ValueBuilder.newValue((Value) m.get("value")).create();
			Table t = TableBuilder.newTable(field.getLookup().getTable())//
					.withColumn("KeyLong", DataType.INTEGER)//
					.withColumn("KeyText", DataType.STRING)//
					.withColumn("Description", DataType.STRING)//
					.withKey(keyLong)//
					.create();

			lookUpControl.setData("dataType", ValueBuilder.newValue((Value) m.get("value")).dataType());
			lookUpControl.setData("keyLong", keyLong);

			CompletableFuture<Table> tableFuture = ((IDataService) m.get("dataService")).getIndexDataAsync(t.getName(),
					t);
			tableFuture.thenAccept(ta -> ((UISynchronize) m.get("sync")).asyncExec(() -> {
				updateSelectedLookupEntry(ta, (Control) m.get("control"));
			}));
		});

		if (twoColumns) {
			Label labelDescription = labelFactory.create(composite);
			labelDescription.setText("Description");
			GridData data = gridDataFactory.align(SWT.LEFT, SWT.TOP).create();
			data.horizontalSpan = 3;
			data.widthHint = LOOKUP_DESCRIPTION_WIDTH_HINT;
			labelDescription.setLayoutData(data);
		}
		controls.put(field.getName(), lookUpControl);

	}

	public static Integer getSpannedHintForElement(Field field, boolean twoColumns) {
		if (field.getUnitText() != null) {
			return 1;
		} else if (field.getText() != null && twoColumns) {
			return 5;
		} else {
			return 2;
		}
	}

	public static int getWidthHintForElement(Field field, boolean twoColumns) {
		if (field.getDateTime() != null || field.getShortDate() != null || field.getShortTime() != null) {
			return TEXT_WIDTH_HINT;
		} else if ((field.getText() != null || field.getNumber() != null || field.getMoney() != null)
				&& field.getUnitText() != null) {
			return LABEL_WIDTH_HINT;
		} else if ((field.getText() != null || field.getNumber() != null || field.getMoney() != null
				|| field.getLookup() != null) && field.getUnitText() == null) {
			return TEXT_WIDTH_HINT;
		} else if (field.getBoolean() != null) {
			return UNIT_WIDTH_HINT;
		} else {
			if (twoColumns) {
				return BIG_TEXT_WIDTH_HINT;
			}
		}
		return -1;
	}

	private static Integer getWidthHintForElement(Field field) {
		return getWidthHintForElement(field, false);
	}

	/**
	 * T
	 *
	 * @param twoColumns
	 * @param widthHint
	 * @return
	 */
	private static GridData getGridDataFactory(boolean twoColumns, Field field) {
		GridData data = gridDataFactory.align(SWT.LEFT, SWT.TOP).create();
		data.horizontalSpan = getSpannedHintForElement(field, twoColumns);
		if (twoColumns && data.horizontalSpan > 2) {
//			data.grabExcessHorizontalSpace = true;
			data.horizontalAlignment = SWT.FILL;
		}
		data.widthHint = getWidthHintForElement(field);
		return data;
	}

	public static Composite createSection(FormToolkit formToolkit, Composite parent, Object ob) {
		Section section;
		if (ob instanceof Head) {
			section = formToolkit.createSection(parent, Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX);
			section.setText("Kopfdaten");
		} else {
			section = formToolkit.createSection(parent,
					Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX | Section.TWISTIE);
			section.setText(((Page) ob).getText());
		}
		section.setLayoutData(GridDataFactory.fillDefaults().create());
		formToolkit.paintBordersFor(section);
		section.setExpanded(true);
		Composite composite = formToolkit.createComposite(section, SWT.RIGHT);
		formToolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(6, false));

		return composite;
	}

	public static void updateSelectedLookupEntry(Table ta, Control c) {
		ta = getTestTableLookupFields();
		Row r = ta.getRows().get(0);
		LookupControl lc = (LookupControl) c;
		Value v = r.getValue(1);

		lc.setText((String) ValueBuilder.newValue(v).create());
	}

	public static Table getTestTableLookupFields() {
		Table lookupFieldTable = TableBuilder.newTable("spReadWorkingTime")//
				.withColumn("KeyLong", DataType.INTEGER)//
				.withColumn("KeyText", DataType.STRING)//
				.withColumn("Description", DataType.STRING)//
				.create();
		Row r = RowBuilder.newRow()//
				.withValue("2242")//
				.withValue("lookupfield-value")//
				.withValue("blabla")//
				.create();
		lookupFieldTable.addRow(r);
		return lookupFieldTable;
	}
}
