
package aero.minova.rcp.rcp.parts;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
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
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;
import aero.minova.rcp.rcp.util.WFCDetailFieldUtil;
import aero.minova.rcp.rcp.util.WFCDetailLookupFieldUtil;
import aero.minova.rcp.rcp.util.WFCDetailUtil;

@SuppressWarnings("restriction")
public class WFCDetailPart extends WFCFormPart {

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
	private IEventBroker broker;

	private FormToolkit formToolkit;

	private Composite composite;

	private Map<String, Control> controls = new HashMap<>();

	private WFCDetailUtil wfcDetailUtil = null;

	private WFCDetailCASRequestsUtil casRequestsUtil = null;

	public WFCDetailPart() {

	}

	private TranslationService translationService;

	@PostConstruct
	public void postConstruct(Composite parent) {
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		if (getForm(parent) == null) {
			return;
		}
		layoutForm(parent);
		translate(translationService);
		// erstellen der Util-Klasse, welche sämtliche funktionen der Detailansicht
		// steuert
		casRequestsUtil = new WFCDetailCASRequestsUtil(controls, form);
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
			return WFCDetailFieldUtil.createBooleanField(composite, field, row, column, formToolkit);
		} else if (field.getNumber() != null) {
			return WFCDetailFieldUtil.createNumberField(composite, field, row, column, formToolkit);
		} else if (field.getDateTime() != null) {
			return WFCDetailFieldUtil.createDateTimeField(composite, field, row, column, formToolkit);
		} else if (field.getShortDate() != null) {
			return WFCDetailFieldUtil.createShortDateField(composite, field, row, column, formToolkit);
		} else if (field.getShortTime() != null) {
			return WFCDetailFieldUtil.createShortTimeField(composite, field, row, column, formToolkit);
		} else if (field.getLookup() != null) {
			return WFCDetailLookupFieldUtil.createLookupField(composite, field, row, column, formToolkit, broker,
					controls);

		} else if (field.getText() != null) {
			return WFCDetailFieldUtil.createTextField(composite, field, row, column, formToolkit);
		}
		return null;
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

	private int getWidth(Field field) {
		BigInteger numberColumnsSpanned = field.getNumberColumnsSpanned();
		return numberColumnsSpanned == null ? 2 : numberColumnsSpanned.intValue();
	}
}