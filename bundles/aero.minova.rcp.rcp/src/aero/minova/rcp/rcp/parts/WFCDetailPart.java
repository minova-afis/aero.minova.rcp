
package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
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

import aero.minova.rcp.model.SqlProcedureResult;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.WFCDetailFieldUtil;
import aero.minova.rcp.rcp.util.Constants;
import aero.minova.rcp.rcp.util.LookupCASRequestUtil;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;
import aero.minova.rcp.rcp.util.WFCDetailLookupFieldUtil;
import aero.minova.rcp.rcp.util.WFCDetailUtil;
import aero.minova.rcp.rcp.util.WFCDetailsLookupUtil;
import aero.minova.rcp.rcp.widgets.LookupControl;

@SuppressWarnings("restriction")
public class WFCDetailPart extends WFCFormPart {

	private static final int COLUMN_WIDTH = 140;
	private static final int MARGIN_LEFT = 5;
	private static final int MARGIN_TOP = 5;
	private static final int MARGIN_SECTION = 8;
	private static final int SECTION_WIDTH = 4 * COLUMN_WIDTH + 3 * MARGIN_LEFT + 2 * MARGIN_SECTION; // 4 Spalten = 5
																										// Zwischenräume
	private static final int COLUMN_HEIGHT = 28;

	@Inject
	private IEventBroker broker;

	@Inject
	protected UISynchronize sync;

	private FormToolkit formToolkit;

	private Composite composite;

	private Map<String, Control> controls = new HashMap<>();

	private WFCDetailUtil wfcDetailUtil = null;

	private WFCDetailCASRequestsUtil casRequestsUtil = null;

	private TranslationService translationService;
	private Locale locale;

	@PostConstruct
	public void postConstruct(Composite parent, IEclipseContext partContext) {
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		if (getForm(parent) == null) {
			return;
		}
		layoutForm(parent);
		translate(translationService);
		// erstellen der Util-Klasse, welche sämtliche funktionen der Detailansicht
		// steuert

		// erzeuge die util Methoden mit DI
		IEclipseContext localContext = EclipseContextFactory.create();
		localContext.set(Form.class, form);

		localContext.setParent(partContext);

		casRequestsUtil = ContextInjectionFactory.make(WFCDetailCASRequestsUtil.class, localContext);
		wfcDetailUtil = ContextInjectionFactory.make(WFCDetailUtil.class, localContext);
		wfcDetailUtil.bindValues(controls, perspective);
		casRequestsUtil.setControls(controls, perspective);
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
		headSection.setData(TRANSLATE_PROPERTY, "@Head");

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
		pageSection.setData(TRANSLATE_PROPERTY, page.getText());

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
			if (!(fieldOrGrid instanceof Field)) continue; // erst einmal nur Felder
			Field field = (Field) fieldOrGrid;
			if (!field.isVisible()) continue; // nur sichtbare Felder
			width = getWidth(field);
			if (column + width > 4) {
				column = 0;
				row++;
			}
			control = createField(composite, field, row, column);
			if (control != null) controls.put(field.getName(), control);
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
			if (!(fieldOrGrid instanceof Field)) continue; // erst einmal nur Felder
			Field field = (Field) fieldOrGrid;
			if (!field.isVisible()) continue; // nur sichtbare Felder
			width = getWidth(field);
			if (column + width > 4) {
				column = 0;
				row++;
			}
			control = createField(composite, field, row, column);
			if (control != null) controls.put(field.getName(), control);
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
			return NumberField.create(composite, field, row, column, formToolkit, locale);
		} else if (field.getDateTime() != null) {
			return DateTimeField.create(composite, field, row, column, formToolkit);
		} else if (field.getShortDate() != null) {
			return ShortDateField.create(composite, field, row, column, formToolkit, locale);
		} else if (field.getShortTime() != null) {
			return WFCDetailFieldUtil.createShortTimeField(composite, field, row, column, formToolkit);
		} else if (field.getLookup() != null) {
			return WFCDetailLookupFieldUtil.createLookupField(composite, field, row, column, formToolkit, broker,
					controls, perspective);

		} else if (field.getText() != null) {
			return WFCDetailFieldUtil.createTextField(composite, field, row, column, formToolkit);
		}
		return null;
	}

	@Inject
	@Optional
	private void getNotified1(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
		translate(translationService);
	}

	@Inject
	private void translate(TranslationService translationService) {
		this.translationService = translationService;
		if (translationService != null && composite != null) translate(composite);
	}

	private void translate(Composite composite) {
		for (Control control : composite.getChildren()) {
			if (control.getData(TRANSLATE_PROPERTY) != null) {
				String property = (String) control.getData(TRANSLATE_PROPERTY);
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
		for (Control control : composite.getChildren()) {
			if (control.getData(TRANSLATE_LOCALE) != null) {
				control.setData(TRANSLATE_LOCALE, locale);
			}
		}
	}

	private int getWidth(Field field) {
		BigInteger numberColumnsSpanned = field.getNumberColumnsSpanned();
		return numberColumnsSpanned == null ? 2 : numberColumnsSpanned.intValue();
	}

	/**
	 * Auslesen aller bereits einhgetragenen key die mit diesem Controll in Zusammenhang stehen Es wird eine Liste von
	 * Ergebnissen Erstellt, diese wird dem benutzer zur verfügung gestellt.
	 *
	 * @param luc
	 */
	@Inject
	@Optional
	public void requestLookUpEntriesAll(@UIEventTopic("WFCLoadAllLookUpValues") Map<MPerspective, String> map) {
		if (map.get(perspective) != null) {
			String name = map.get(perspective);
			Control control = controls.get(name);
			if (control instanceof LookupControl) {
				Field field = (Field) control.getData(Constants.CONTROL_FIELD);
				CompletableFuture<?> tableFuture;
				tableFuture = LookupCASRequestUtil.getRequestedTable(0, null, field, controls, dataService, sync,
						"List");

				tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
					WFCDetailsLookupUtil lookupUtil = wfcDetailUtil.getLookupUtil();
					if (ta instanceof SqlProcedureResult) {
						SqlProcedureResult sql = (SqlProcedureResult) ta;
						lookupUtil.changeOptionsForLookupField(sql.getResultSet(), control, true);
					} else if (ta instanceof Table) {
						Table t1 = (Table) ta;
						lookupUtil.changeOptionsForLookupField(t1, control, true);
					}

				}));
			}

		}
	}
}