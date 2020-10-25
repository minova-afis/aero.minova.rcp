
package aero.minova.rcp.rcp.parts;

import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
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
import aero.minova.rcp.perspectiveswitcher.commands.E4WorkbenchParameterConstants;
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
	private IDataService dataService;

	private Form form;

	private FormToolkit formToolkit;

	private Composite composite;

	private Map<String, Control> controls = new HashMap<>();

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
			return createLookupField(composite, field, row, column);
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

		return button;
	}

	private Control createLookupField(Composite composite, Field field, int row, int column) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		LookupControl lookupControl = new LookupControl(composite, SWT.LEFT);
		Label descriptionLabel = formToolkit.createLabel(composite, "", SWT.LEFT);
		FormData lookupFormData = new FormData();
		FormData labelFormData = new FormData();
		FormData descriptionLabelFormData = new FormData();

		lookupFormData.top = new FormAttachment(composite, MARGIN_TOP + row * COLUMN_HEIGHT);
		lookupFormData.left = new FormAttachment(composite, MARGIN_LEFT * (column + 1) + (column + 1) * COLUMN_WIDTH);
		lookupFormData.width = COLUMN_WIDTH;

		labelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		labelFormData.right = new FormAttachment(lookupControl, MARGIN_LEFT * -1, SWT.LEFT);
		labelFormData.width = COLUMN_WIDTH;

		descriptionLabelFormData.top = new FormAttachment(lookupControl, 0, SWT.CENTER);
		descriptionLabelFormData.left = new FormAttachment(lookupControl, 0, SWT.RIGHT);
//		descriptionLabelFormData.right = new FormAttachment(composite, 0, SWT.RIGHT);
		if (field.getNumberColumnsSpanned() != null && field.getNumberColumnsSpanned().intValue() == 4) {
			descriptionLabelFormData.width = MARGIN_LEFT * 2 + COLUMN_WIDTH * 2;
		} else {
			descriptionLabelFormData.width = 0;
		}

		label.setData(AERO_MINOVA_RCP_TRANSLATE_PROPERTY, labelText);
		label.setLayoutData(labelFormData);

		lookupControl.setLayoutData(lookupFormData);
		lookupControl.setDescription(descriptionLabel);

		descriptionLabel.setLayoutData(descriptionLabelFormData);

		return lookupControl;
	}

	private Control createNumberField(Composite composite, Field field, int row, int column) {
		String labelText = field.getTextAttribute() == null ? "" : field.getTextAttribute();
		String unitText = field.getUnitText() == null ? "" : field.getUnitText();
		Label label = formToolkit.createLabel(composite, labelText, SWT.RIGHT);
		Text text = formToolkit.createText(composite, "", SWT.BORDER | SWT.RIGHT);
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

}