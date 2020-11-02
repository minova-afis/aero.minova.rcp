package aero.minova.rcp.rcp.util;

import java.math.BigInteger;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.jface.widgets.TextFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class DetailUtil {

	private IEventBroker broker;

	public DetailUtil(TranslationService translationService, IEventBroker broker) {
		this.translationService = translationService;
		this.broker = broker;
	}

	public static final int LABEL_WIDTH_HINT = 150;
	public static final int TEXT_WIDTH_HINT = 170;
	public static final int UNIT_WIDTH_HINT = 20;
	public static final int BIG_TEXT_WIDTH_HINT = 490;
	public static final int LOOKUP_DESCRIPTION_WIDTH_HINT = 320;

	public static final GridDataFactory gridDataFactory = GridDataFactory.fillDefaults().grab(true, false);
	public static final LabelFactory labelFactory = LabelFactory.newLabel(SWT.NONE);
	private static TextFactory textMultiFactory = TextFactory.newText(SWT.BORDER | SWT.MULTI);
	private static TextFactory textFactory = TextFactory.newText(SWT.BORDER).text("");

	TranslationService translationService;

	public void createField(Field field, Composite composite, Map<String, Control> controls) {
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
			buildLookupField(field, composite, twoColumns, controls, broker);
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

	private void buildMiddlePart(Field field, Composite composite, boolean twoColumns, Map<String, Control> controls) {
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
		text.setData(Constants.CONTROL_FIELD, field);
		text.setData(Constants.CONTROL_DATATYPE, getDataType(field));
		// hinterlegen einer Methode in die component, um stehts die Daten des richtigen
		// Indexes in der Detailview aufzulisten
		if (field.getNumber() != null) {
			text.setData(Constants.CONTROL_DECIMALS, field.getNumber().getDecimals());
		}
		text.setData(Constants.CONTROL_CONSUMER, (Consumer<Table>) t -> {

			Value value = t.getRows().get(0).getValue(t.getColumnIndex(field.getName()));
			Field f = (Field) text.getData(Constants.CONTROL_FIELD);
			text.setText(ValueBuilder.value(value, f).getText());
			text.setData(Constants.CONTROL_DATATYPE, ValueBuilder.value(value).getDataType());
		});
		controls.put(field.getName(), text);
	}

	@SuppressWarnings("rawtypes")
	private static void buildLookupField(Field field, Composite composite, boolean twoColumns,
			Map<String, Control> controls, IEventBroker broker) {

		LookupControl lookUpControl = new LookupControl(composite, SWT.LEFT);
		lookUpControl.setLayoutData(getGridDataFactory(twoColumns, field));
		lookUpControl.setData(Constants.CONTROL_FIELD, field);
		// hinterlegen einer Methode in die component, um stehts die Daten des richtigen
		// Indexes in der Detailview aufzulisten. Hierfür wird eine Anfrage an den CAS
		// gestartet, um die Werte des zugehörigen Keys zu erhalten
		lookUpControl.setData(Constants.CONTROL_LOOKUPCONSUMER, (Consumer<Map>) m -> {

			int keyLong = (Integer) ValueBuilder.value((Value) m.get("value")).create();
			lookUpControl.setData(Constants.CONTROL_DATATYPE, ValueBuilder.value((Value) m.get("value")).getDataType());
			lookUpControl.setData(Constants.CONTROL_KEYLONG, keyLong);

			CompletableFuture<?> tableFuture;
			tableFuture = LookupCASRequestUtil.getRequestedTable(keyLong, null, field, controls,
					(IDataService) m.get("dataService"), (UISynchronize) m.get("sync"), "Resolve");
			tableFuture.thenAccept(ta -> ((UISynchronize) m.get("sync")).asyncExec(() -> {
				Table t = null;
				if (ta instanceof SqlProcedureResult) {
					SqlProcedureResult sql = (SqlProcedureResult) ta;
					t = sql.getResultSet();
				} else if (ta instanceof Table) {
					t = (Table) ta;
				}
				updateSelectedLookupEntry(t, (Control) m.get("control"));

			}));
		});

		if (twoColumns) {
			Label labelDescription = labelFactory.create(composite);
			labelDescription.setText(Constants.TABLE_DESCRIPTION);
			GridData data = gridDataFactory.align(SWT.LEFT, SWT.TOP).create();
			data.horizontalSpan = 3;
			data.widthHint = LOOKUP_DESCRIPTION_WIDTH_HINT;
			labelDescription.setLayoutData(data);
			lookUpControl.setDescription(labelDescription);
			lookUpControl.addTwistieMouseListener(new MouseListener() {

				@Override
				public void mouseDoubleClick(MouseEvent e) {
					// TODO Auto-generated method stub
				}

				@Override
				/*
				 * Aufruf der Prozedur mit um den Datensatz zu laden. prüfen ob noch andere
				 * LookUpFelder eingetragen wurden
				 */
				public void mouseDown(MouseEvent e) {
					broker.post("LoadAllLookUpValues", field.getName());
				}

				@Override
				public void mouseUp(MouseEvent e) {
					// TODO Auto-generated method stub

				}

			});
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

	public static DataType getDataType(Field field) {
		if (field.getDateTime() != null || field.getShortDate() != null || field.getShortTime() != null) {
			return DataType.INSTANT;
		} else if (field.getNumber() != null && field.getNumber().getDecimals() > 0) {
			return DataType.INTEGER;
		} else if ((field.getNumber() != null && field.getNumber().getDecimals() > 0) || field.getMoney() != null) {
			return DataType.DOUBLE;
		} else if (field.getBoolean() != null) {
			return DataType.BOOLEAN;
		} else {
			return DataType.STRING;
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

	public Composite createSection(FormToolkit formToolkit, Composite parent, Object ob) {
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

	/**
	 * Abfangen der Table der in der Consume-Methode versendeten CAS-Abfrage mit
	 * Bindung zur Componente
	 *
	 * @param ta
	 * @param c
	 */
	public static void updateSelectedLookupEntry(Table ta, Control c) {
		Row r = ta.getRows().get(0);
		LookupControl lc = (LookupControl) c;
		int index = ta.getColumnIndex(Constants.TABLE_KEYTEXT);
		Value v = r.getValue(index);

		lc.setText((String) ValueBuilder.value(v).create());
		lc.getTextControl().setMessage("");
		if (lc.getDescription() != null && ta.getColumnIndex(Constants.TABLE_DESCRIPTION) > -1) {
			if (r.getValue(ta.getColumnIndex(Constants.TABLE_DESCRIPTION)) != null) {
				lc.getDescription().setText((String) ValueBuilder
						.value(r.getValue(ta.getColumnIndex(Constants.TABLE_DESCRIPTION))).create());
			} else {
				lc.getDescription().setText("");
			}
		}
	}
}
