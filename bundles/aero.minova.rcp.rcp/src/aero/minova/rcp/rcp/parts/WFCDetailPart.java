
package aero.minova.rcp.rcp.parts;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.widgets.LabelFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.Value;
import aero.minova.rcp.model.builder.ValueBuilder;
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;
import aero.minova.rcp.rcp.util.WFCDetailLookupFieldUtil;
import aero.minova.rcp.rcp.util.WFCDetailUtil;
import aero.minova.rcp.rcp.widgets.LookupControl;

@SuppressWarnings("restriction")
public class WFCDetailPart {

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

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private IEventBroker broker;

	@Inject
	private IDataService dataService;

	private Form form;

	private FormToolkit formToolkit;

	private Composite composite;

	private Map<String, Control> controls = new HashMap<>();

	private WFCDetailUtil wfcDetailUtil = null;

	public WFCDetailPart() {

	}

	@Inject
	@Named(E4WorkbenchParameterConstants.FORM_NAME)
	String formName;

	@Inject
	MPerspective perspective;
	private TranslationService translationService;

	@PostConstruct
	public void postConstruct(Composite parent) {
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		form = perspective.getContext().get(Form.class);
		if (form == null) {
			dataService.getFileSynch(formName); // Datei ggf. vom Server holen
			form = dataFormService.getForm(formName);
		}
		if (form == null) {
			LabelFactory.newLabel(SWT.CENTER).align(SWT.CENTER).text(formName).create(parent);
			return;
		}
		perspective.getContext().set(Form.class, form); // Wir merken es uns im Context; so können andere es nutzen
		layoutForm(parent);
		translate(translationService);
		// erstellen der Util-Klasse, welche sämtliche funktionen der Detailansicht
		// steuert
		wfcDetailUtil = new WFCDetailUtil(form, controls);
	}

	private void layoutForm(Composite parent) {
		parent.setLayout(new RowLayout(SWT.VERTICAL));

		for (Object headOrPage : form.getDetail().getHeadAndPage()) {
			if (headOrPage instanceof Head) {
				layoutHead(parent, (Head) headOrPage);
			} else if (headOrPage instanceof Page) {
				layoutPage(parent, (Page) headOrPage);
			}
		}
	}

	private void layoutHead(Composite parent, Head head) {
		RowData headLayoutData = new RowData();
		Section headSection = formToolkit.createSection(parent,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);

		headLayoutData.width = SECTION_WIDTH;

		headSection.setLayoutData(headLayoutData);
		headSection.setText("@Head");
		headSection.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, "@Head");

		// Client Area
		Composite composite = formToolkit.createComposite(headSection);
		composite.setLayout(new FormLayout());
		composite.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "TEST");
		formToolkit.paintBordersFor(composite);
		headSection.setClient(composite);

		// Fields
		createFields(composite, head);
	}

	private void layoutPage(Composite parent, Page page) {
		RowData pageLayoutData = new RowData();
		Section pageSection = formToolkit.createSection(parent,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE);

		pageLayoutData.width = SECTION_WIDTH;

		pageSection.setLayoutData(pageLayoutData);
		pageSection.setText(page.getText());
		pageSection.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, page.getText());

		// Client Area
		Composite composite = formToolkit.createComposite(pageSection);
		composite.setLayout(new FormLayout());
		composite.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "TEST");
		formToolkit.paintBordersFor(composite);
		pageSection.setClient(composite);

		// Fields
		createFields(composite, page);
	}

	private void createFields(Composite composite, Head head) {
		int row = 0;
		int column = 0;
		int width = 2;
		Control control = null;
		for (Object fieldOrGrid : head.getFieldOrGrid()) {
			if (!(fieldOrGrid instanceof Field))
				continue; // erst einmal nur Felder
			Field field = (Field) fieldOrGrid;
			if (!field.isVisible())
				continue; // nur sichtbare Felder
			width = getWidth(field);
			if (column + width > 4) {
				column = 0;
				row++;
			}
			control = createField(composite, field, row, column);
			if (control != null)
				controls.put(field.getName(), control);
			column += width;
		}

		addBottonMargin(composite, row + 1, column);
	}

	private void createFields(Composite composite, Page page) {
		int row = 0;
		int column = 0;
		int width;
		Control control;
		for (Object fieldOrGrid : page.getFieldOrGrid()) {
			if (!(fieldOrGrid instanceof Field))
				continue; // erst einmal nur Felder
			Field field = (Field) fieldOrGrid;
			if (!field.isVisible())
				continue; // nur sichtbare Felder
			width = getWidth(field);
			if (column + width > 4) {
				column = 0;
				row++;
			}
			control = createField(composite, field, row, column);
			if (control != null)
				controls.put(field.getName(), control);
			column += width;
			row += getExtraHeight(field);
		}

		addBottonMargin(composite, row + 1, column);
	}

	private int getExtraHeight(Field field) {
		if (field.getNumberRowsSpanned() != null && field.getNumberRowsSpanned().length() > 0) {
			return Integer.parseInt(field.getNumberRowsSpanned()) - 1;
		}
		return 0;
	}

	private void addBottonMargin(Composite composite, int row, int column) {
		// Abstand nach unten
		Label spacing = new Label(composite, SWT.NONE);
		FormData spacingFormData = new FormData();
		spacingFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT + MARGIN_TOP);
		spacingFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		spacingFormData.height = 0;
		spacing.setLayoutData(spacingFormData);
	}

	private Control createField(Composite composite, Field field, int row, int column) {
		if (field.getBoolean() != null) {
			return createBooleanField(composite, field, row, column);
		} else if (field.getNumber() != null) {
			return createNumberField(composite, field, row, column);
		} else if (field.getDateTime() != null) {
			return createDateTimeField(composite, field, row, column);
		} else if (field.getShortDate() != null) {
			return createShortDateField(composite, field, row, column);
		} else if (field.getShortTime() != null) {
			return createShortTimeField(composite, field, row, column);
		} else if (field.getLookup() != null) {
			LookupControl c = (LookupControl) WFCDetailLookupFieldUtil.createLookupField(composite, field, row, column,
					formToolkit, broker);
			// hinterlegen einer Methode in die component, um stehts die Daten des richtigen
			// Indexes in der Detailview aufzulisten. Hierfür wird eine Anfrage an den CAS
			// gestartet, um die Werte des zugehörigen Keys zu erhalten
			c.setData(Constants.CONTROL_LOOKUPCONSUMER, (Consumer<Map>) m -> {

				int keyLong = (Integer) ValueBuilder.value((Value) m.get("value")).create();
				c.setData(Constants.CONTROL_DATATYPE, ValueBuilder.value((Value) m.get("value")).getDataType());
				c.setData(Constants.CONTROL_KEYLONG, keyLong);

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
		} else if (field.getText() != null) {
			return createTextField(composite, field, row, column);
		}
		return null;
	}

	private Control createBooleanField(Composite composite, Field field, int row, int column) {
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

	private Control createDateTimeField(Composite composite, Field field, int row, int column) {
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

	private Control createNumberField(Composite composite, Field field, int row, int column) {
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

	private Control createShortDateField(Composite composite, Field field, int row, int column) {
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

	private Control createShortTimeField(Composite composite, Field field, int row, int column) {
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

	private Control createTextField(Composite composite, Field field, int row, int column) {
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

	public void addDataToText(Text t, Field f, DataType datatype) {
		t.setData(Constants.CONTROL_FIELD, f);
		t.setData(Constants.CONTROL_DATATYPE, datatype);
	}

	public void addConsumer(Object o, Field field) {
		if (o instanceof Text) {
			Text text = (Text) o;
			text.setData(Constants.CONTROL_CONSUMER, (Consumer<Table>) t -> {

				Value value = t.getRows().get(0).getValue(t.getColumnIndex(field.getName()));
				Field f = (Field) text.getData(Constants.CONTROL_FIELD);
				text.setText(ValueBuilder.value(value, f).getText());
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

	private int getWidth(Field field) {
		BigInteger numberColumnsSpanned = field.getNumberColumnsSpanned();
		return numberColumnsSpanned == null ? 2 : numberColumnsSpanned.intValue();
	}

	@Inject
	@Optional
	private void getNotified1(@Named(TranslationService.LOCALE) Locale s) {
		translate(translationService);
	}

	@Inject
	private void translate(TranslationService translationService) {
		this.translationService = translationService;
		if (translationService != null && composite != null)
			translate(composite);
	}

	private void translate(Composite composite) {
		for (Control control : composite.getChildren()) {
			if (control.getData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY) != null) {
				String property = (String) control.getData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY);
				String value = translationService.translate(property, null);
				if (control instanceof ExpandableComposite) {
					ExpandableComposite expandableComposite = (ExpandableComposite) control;
					expandableComposite.setText(value);
					translate((Composite) expandableComposite.getClient());
				} else if (control instanceof Label) {
					((Label) control).setText(value);
				} else if (control instanceof Button) {
					((Button) control).setText(value);
				}
				if (control instanceof Composite) {
					translate((Composite) control);
				}
			}
		}
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