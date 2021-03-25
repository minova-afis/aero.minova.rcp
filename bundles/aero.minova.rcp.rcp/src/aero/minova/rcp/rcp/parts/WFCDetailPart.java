
package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.di.UISynchronize;
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
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MDateTimeField;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.form.MShortDateField;
import aero.minova.rcp.model.form.MShortTimeField;
import aero.minova.rcp.model.form.MTextField;
import aero.minova.rcp.model.form.ModelToViewModel;
import aero.minova.rcp.model.helper.IHelper;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.fields.BooleanField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.ShortTimeField;
import aero.minova.rcp.rcp.fields.TextField;
import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;

@SuppressWarnings("restriction")
public class WFCDetailPart extends WFCFormPart {

	private static final int MARGIN_SECTION = 8;
	private static final int SECTION_WIDTH = 4 * COLUMN_WIDTH + 3 * MARGIN_LEFT + 2 * MARGIN_SECTION; // 4 Spalten = 5
																										// Zwischenräume

	@Inject
	private IEventBroker broker;

	@Inject
	protected UISynchronize sync;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	String timezone;

	@Inject
	@Service
	private List<IHelper> helperlist;

	private FormToolkit formToolkit;

	private Composite composite;

	private MDetail detail = new MDetail();

	private WFCDetailCASRequestsUtil casRequestsUtil = null;

	@Inject
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
		// erstellen der Util-Klasse, welche sämtliche funktionen der Detailansicht
		// steuert

		// erzeuge die util Methoden mit DI
		IEclipseContext localContext = EclipseContextFactory.create();
		localContext.set(Form.class, form);

		localContext.setParent(partContext);

		casRequestsUtil = ContextInjectionFactory.make(WFCDetailCASRequestsUtil.class, localContext);
		casRequestsUtil.setDetail(detail, perspective);
		translate(composite);
	}

	private static class HeadOrPageWrapper {
		private Object headOrPage;
		public boolean isHead = false;

		public HeadOrPageWrapper(Object headOrPage) {
			this.headOrPage = headOrPage;
			if (headOrPage instanceof Head) {
				isHead = true;
			}
		}

		public String getTranslationText() {
			if (isHead) {
				return "@Head";
			}
			return ((Page) headOrPage).getText();
		}

		public List<Object> getFieldOrGrid() {
			if (isHead) {
				return ((Head) headOrPage).getFieldOrGrid();
			}
			return ((Page) headOrPage).getFieldOrGrid();
		}

	}

	private void layoutForm(Composite parent) {
		parent.setLayout(new RowLayout(SWT.VERTICAL));
		for (Object headOrPage : form.getDetail().getHeadAndPage()) {
			HeadOrPageWrapper wrapper = new HeadOrPageWrapper(headOrPage);
			if (headOrPage instanceof Head) {
				layoutHead(parent, wrapper);
			} else if (headOrPage instanceof Page) {
				layoutPage(parent, wrapper);
			}
		}
		// Helper-Klasse initialisieren
		String helperClass = form.getHelperClass();
		if (!Objects.equals(helperClass, helperlist.get(0).getClass().getName())) {
			// TODO Übersetzung!
			throw new RuntimeException("Helperklasse nicht eindeutig! Bitte Prüfen");
		}
		IHelper iHelper = helperlist.get(0);
		iHelper.setControls(detail);
		detail.setHelper(iHelper);

	}

	private void layoutHead(Composite parent, HeadOrPageWrapper head) {
		RowData headLayoutData = new RowData();
		Section headSection;
		if (head.isHead) {
			headSection = formToolkit.createSection(parent,
					ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		} else {
			headSection = formToolkit.createSection(parent,
					ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE);
		}

		headLayoutData.width = SECTION_WIDTH;

		headSection.setLayoutData(headLayoutData);
		headSection.setText(head.getTranslationText());
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

	private void layoutPage(Composite parent, HeadOrPageWrapper page) {
		RowData pageLayoutData = new RowData();
		Section pageSection = formToolkit.createSection(parent,
				ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE);

		pageLayoutData.width = SECTION_WIDTH;

		pageSection.setLayoutData(pageLayoutData);
		pageSection.setText(page.getTranslationText());
		pageSection.setData(TRANSLATE_PROPERTY, page.getTranslationText());

		// Client Area
		Composite composite = formToolkit.createComposite(pageSection);
		composite.setLayout(new FormLayout());
		composite.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "TEST");
		formToolkit.paintBordersFor(composite);
		pageSection.setClient(composite);

		// Fields
		createFields(composite, page);
	}

	private void createFields(Composite composite, HeadOrPageWrapper headOrPage) {
		int row = 0;
		int column = 0;
		int width;
		for (Object fieldOrGrid : headOrPage.getFieldOrGrid()) {
			if (!(fieldOrGrid instanceof Field)) {
				continue; // erst einmal nur Felder
			}
			Field field = (Field) fieldOrGrid;
			MField f = ModelToViewModel.convert(field);
			detail.putField(f);

			if (!field.isVisible()) {
				continue; // nur sichtbare Felder
			}
			width = getWidth(field);
			if (column + width > 4) {
				column = 0;
				row++;
			}
			createField(composite, f, row, column);
			column += width;
			if (!headOrPage.isHead) {
				row += getExtraHeight(field);
			}
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

	private void createField(Composite composite, MField field, int row, int column) {
		if (field instanceof MBooleanField) {
			BooleanField.create(composite, field, row, column, formToolkit, locale);
		} else if (field instanceof MNumberField) {
			NumberField.create(composite, (MNumberField) field, row, column, formToolkit, locale);
		} else if (field instanceof MDateTimeField) {
			DateTimeField.create(composite, field, row, column, formToolkit);
		} else if (field instanceof MShortDateField) {
			ShortDateField.create(composite, field, row, column, formToolkit, locale, timezone);
		} else if (field instanceof MShortTimeField) {
			ShortTimeField.create(composite, field, row, column, formToolkit, locale, timezone);
		} else if (field instanceof MLookupField) {
			LookupField.create(composite, field, row, column, formToolkit, broker, perspective, detail, locale);
		} else if (field instanceof MTextField) {
			TextField.create(composite, field, row, column, formToolkit);
		}
	}

	@Inject
	@Optional
	private void getNotified(@Named(TranslationService.LOCALE) Locale s) {
		this.locale = s;
		if (translationService != null && composite != null) {
			translate(composite);
		}
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
					Label l = ((Label) control);
					Object data = l.getData(LookupField.AERO_MINOVA_RCP_LOOKUP);
					if (data != null) {
						// TODO aus den Preferences Laden
						value = value + " ▼";
					}
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


}