
package aero.minova.rcp.rcp.parts;

import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_HEIGHT;
import static aero.minova.rcp.rcp.fields.FieldUtil.COLUMN_WIDTH;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_LEFT;
import static aero.minova.rcp.rcp.fields.FieldUtil.MARGIN_TOP;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.awt.event.FocusEvent;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.di.extensions.Service;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.css.swt.CSSSWTConstants;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.widgets.ButtonFactory;
import org.eclipse.nebula.widgets.opal.textassist.TextAssist;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

import aero.minova.rcp.constants.Constants;
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
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.form.MShortDateField;
import aero.minova.rcp.model.form.MShortTimeField;
import aero.minova.rcp.model.form.MTextField;
import aero.minova.rcp.model.form.ModelToViewModel;
import aero.minova.rcp.model.helper.IHelper;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.rcp.accessor.AbstractValueAccessor;
import aero.minova.rcp.rcp.fields.BooleanField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.ShortTimeField;
import aero.minova.rcp.rcp.fields.TextField;
import aero.minova.rcp.rcp.util.ImageUtil;

import aero.minova.rcp.rcp.util.WFCDetailCASRequestsUtil;
import aero.minova.rcp.rcp.widgets.Lookup;

@SuppressWarnings("restriction")
public class WFCDetailPart extends WFCFormPart {

	private static final int MARGIN_SECTION = 8;
	private static final int SECTION_WIDTH = 4 * COLUMN_WIDTH + 3 * MARGIN_LEFT + 2 * MARGIN_SECTION + 50; // 4 Spalten = 5 Zwischenräume
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

	@Inject
	EPartService partService;

	@Inject
	private ECommandService commandService;

	@Inject
	private EHandlerService handlerService;

	@PostConstruct
	public void postConstruct(Composite parent, IEclipseContext partContext) {
		composite = parent;
		formToolkit = new FormToolkit(parent.getDisplay());
		if (getForm(parent) == null) {
			return;
		}
		layoutForm(parent, partContext);

		// erzeuge die util Methoden mit DI
		IEclipseContext localContext = EclipseContextFactory.create();
		localContext.set(Form.class, form);

		localContext.setParent(partContext);

		// erstellen der Util-Klasse, welche sämtliche funktionen der Detailansicht steuert
		casRequestsUtil = ContextInjectionFactory.make(WFCDetailCASRequestsUtil.class, localContext);
		casRequestsUtil.initializeCasRequestUtil(getDetail(), perspective);
		partContext.set("Detail_Width", SECTION_WIDTH);
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

	private void layoutForm(Composite parent, IEclipseContext context) {
		parent.setLayout(new RowLayout(SWT.VERTICAL));
		for (Object headOrPage : form.getDetail().getHeadAndPage()) {
			HeadOrPageWrapper wrapper = new HeadOrPageWrapper(headOrPage);
			layoutSection(parent, wrapper, context);

		}
		// Helper-Klasse initialisieren
		if (form.getHelperClass() != null) {
			String helperClass = form.getHelperClass();
			if (!Objects.equals(helperClass, helperlist.get(0).getClass().getName())) {
				// TODO Übersetzung!
				throw new RuntimeException("Helperklasse nicht eindeutig! Bitte Prüfen");
			}
			IHelper iHelper = helperlist.get(0);
			iHelper.setControls(getDetail());
			getDetail().setHelper(iHelper);
		}

	}

	/**
	 * Diese Methode bekommt einen Composite übergeben, und erstellt aus dem übergenen Objekt ein Section. Diese Sektion ist entweder der Head (Kopfdaten) oder
	 * eine OptionPage die sich unterhalb der Kopfdaten eingliedert. Zusätzlich wird ein TraverseListener übergeben, der das Verhalten für TAB und Enter
	 * festlegt.
	 *
	 * @param parent
	 * @param headOrPage
	 * @param traverseListener
	 */

	private void layoutSection(Composite parent, HeadOrPageWrapper headOrPage, IEclipseContext context) {
		RowData headLayoutData = new RowData();
		Section section;
		Control sectionControl = null;
		if (headOrPage.isHead) {
			section = formToolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		} else {
			section = formToolkit.createSection(parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED | ExpandableComposite.TWISTIE);
			sectionControl = section.getChildren()[0];
		}

		headLayoutData.width = SECTION_WIDTH;

		section.setData(TRANSLATE_PROPERTY, headOrPage.getTranslationText());
		section.setLayoutData(headLayoutData);
		section.setText(headOrPage.getTranslationText());

		// Client Area
		Composite composite = formToolkit.createComposite(section);
		composite.setLayout(new FormLayout());
		composite.setData(CSSSWTConstants.CSS_CLASS_NAME_KEY, "TEST");
		formToolkit.paintBordersFor(composite);
		section.setClient(composite);

		// Wir erstellen die HEAD Section des Details.
		MSection mSection = new MSection(true, "open", detail, section.getText(), sectionControl);
		// Button erstellen, falls vorhanden
		// createButton(composite, headOrPage, mSection, context);
		// Erstellen der Field des Section.
		createFields(composite, headOrPage, mSection);
		// Sortieren der Fields nach Tab-Index.
		sortTabList(mSection);
		composite.setTabList(getTabList(mSection, composite));

		// Section wird zum Detail hinzugefügt.
		detail.addPage(mSection);

	}

	/**
	 * Erstellt einen oder mehrere Button auf der übergebenen Section. Die Button werden in der ausgelesenen Reihelfolge erstellt und in eine Reihe gesetzt.
	 *
	 * @param composite2
	 * @param headOrPage
	 * @param mSection
	 */
	private void createButton(Composite composite, HeadOrPageWrapper headOrPage, MSection mSection, IEclipseContext partContext) {
		if (headOrPage.isHead) {
			return;
		}

		Page page = (Page) headOrPage.headOrPage;
		for (aero.minova.rcp.form.model.xsd.Button btn : page.getButton()) {
			String btnLabel = btn.getText();
			Button button = ButtonFactory.newButton(SWT.PUSH).text(btnLabel).image(ImageUtil.getImageDefault(btn.getIcon())).create(composite);
			button.setData(Constants.CONTROL_ID, btn.getId());
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Map<String, String> parameter = Map.of(//
							Constants.CONTROL_WIZARD, "aero.minova.workingtime.wizard.FillWorkingTimeWizard");

					ParameterizedCommand command = commandService.createCommand("aero.minova.rcp.rcp.command.dynamicbuttoncommand", parameter);
					handlerService.executeHandler(command);
				}
			});

		}
	}

	/**
	 * Sortiert die Tab Reihenfolge der Fields in der Section(Page)
	 *
	 * @param mSection
	 *            die Section in der die Fields sortiert werden müssen
	 * @param traverseListener
	 *            der zuzuweisende TraverseListener für die Fields
	 */
	private void sortTabList(MSection mSection) {
		List<MField> tabList = mSection.getTabList();
		Collections.sort(tabList, new Comparator<MField>() {

			@Override
			public int compare(MField f1, MField f2) {
				if (f1.getTabIndex() == f2.getTabIndex()) {
					return 0;
				} else if (f1.getTabIndex() < f2.getTabIndex()) {
					return -1;
				} else {
					return 1;
				}
			}
		});

	}

	private Control[] getTabList(MSection section, Composite composite) {
		List<Control> tabList = new ArrayList<Control>();
		Control[] compositeChilds = composite.getChildren();
		for (Control control : compositeChilds) {
			if (control instanceof Lookup || control instanceof TextAssist || control instanceof Text)
				for (MField field : section.getTabList()) {
					if (control == ((AbstractValueAccessor) field.getValueAccessor()).getControl()) {
						if (!field.isReadOnly()) {
							tabList.add(control);
							break;
						}
					}
				}
		}
		Control[] tabArray = new Control[tabList.size()];
		int i = 0;
		while (i < tabList.size()) {
			tabArray[i] = tabList.get(i);
			i++;
		}
		return tabArray;

	}

	/**
	 * Erstellt die Field einer Section.
	 *
	 * @param composite
	 *            der parent des Fields
	 * @param headOrPage
	 *            bestimmt ob die Fields nach den Regeln des Heads erstellt werden oder der einer Page.
	 * @param page
	 *            die Section deren Fields erstellt werden.
	 */
	private void createFields(Composite composite, HeadOrPageWrapper headOrPage, MSection page) {
		int row = 0;
		int column = 0;
		int width;
		for (Object fieldOrGrid : headOrPage.getFieldOrGrid()) {
			if (!(fieldOrGrid instanceof Field)) {
				continue; // erst einmal nur Felder
			}
			Field field = (Field) fieldOrGrid;
			MField f = ModelToViewModel.convert(field);
			getDetail().putField(f);

			if (!field.isVisible()) {
				continue; // nur sichtbare Felder
			}
			width = getWidth(field);
			if (column + width > 4) {
				column = 0;
				row++;
			}
			createField(composite, f, row, column);
			f.setmPage(page);
			page.addTabField(f);

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
			BooleanField.create(composite, field, row, column, locale, perspective);
		} else if (field instanceof MNumberField) {
			NumberField.create(composite, (MNumberField) field, row, column, locale, perspective);
		} else if (field instanceof MDateTimeField) {
			DateTimeField.create(composite, field, row, column, locale, timezone, perspective);
		} else if (field instanceof MShortDateField) {
			ShortDateField.create(composite, field, row, column, locale, timezone, perspective);
		} else if (field instanceof MShortTimeField) {
			ShortTimeField.create(composite, field, row, column, locale, timezone, perspective);
		} else if (field instanceof MLookupField) {
			LookupField.create(composite, field, row, column, locale, perspective);
		} else if (field instanceof MTextField) {
			TextField.create(composite, field, row, column, perspective);
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

	public MDetail getDetail() {
		return detail;
	}

}