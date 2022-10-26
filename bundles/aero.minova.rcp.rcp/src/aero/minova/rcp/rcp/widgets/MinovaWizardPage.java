package aero.minova.rcp.rcp.widgets;

import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import aero.minova.rcp.css.widgets.DetailLayout;
import aero.minova.rcp.css.widgets.MinovaSection;
import aero.minova.rcp.css.widgets.MinovaSectionData;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Lookup;
import aero.minova.rcp.form.model.xsd.TypeParam;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.DateTimeType;
import aero.minova.rcp.model.event.ValueChangeEvent;
import aero.minova.rcp.model.event.ValueChangeListener;
import aero.minova.rcp.model.form.MBooleanField;
import aero.minova.rcp.model.form.MDateTimeField;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.MLookupField;
import aero.minova.rcp.model.form.MNumberField;
import aero.minova.rcp.model.form.MQuantityField;
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.form.MShortDateField;
import aero.minova.rcp.model.form.MShortTimeField;
import aero.minova.rcp.model.form.MTextField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.rcp.accessor.DetailAccessor;
import aero.minova.rcp.rcp.fields.BooleanField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.QuantityField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.ShortTimeField;
import aero.minova.rcp.rcp.fields.TextField;
import aero.minova.rcp.widgets.LookupComposite;

public class MinovaWizardPage extends WizardPage implements ValueChangeListener {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	protected String timezone;

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.ENTER_SELECTS_FIRST_REQUIRED)
	protected boolean selectFirstRequired;

	@Inject
	protected TranslationService translationService;

	@Inject
	protected MPerspective mPerspective;

	@Inject
	protected IEclipseContext context;

	@Inject
	protected IDataService dataService;

	protected Locale locale = CustomLocale.getLocale();

	// Leider für die Required Prüfung Notwendig
	MSection mSection;

	private static final MinovaWizardPageChangedListener mwpcl = new MinovaWizardPageChangedListener();
	protected MDetail mDetail;

	protected MinovaWizardPageEnterHelper enterHelper;

	public MinovaWizardPage(String pageName, String pageTitle, String pageDescription) {
		super(pageName);
		setTitle(pageTitle);
		setDescription(pageDescription);

		mDetail = new MDetail();
		mDetail.setDetailAccessor(new DetailAccessor(mDetail));

		mSection = new MSection(false, "", mDetail, "", "");
		enterHelper = new MinovaWizardPageEnterHelper(selectFirstRequired);
	}

	@Override
	/**
	 * Von Unterklassen überschrieben. Wichtig: setControl() und init() Aufruf am Ende!
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		init();
	}

	/**
	 * kann nachdem der Container (WizardDialog) gesetzt wurde noch etwas initialisieren (z.B. Listener). <br>
	 * Ohne wird die onSelect() Methode nicht aufgerufen!
	 */
	protected void init() {
		IWizardContainer wizardContainer = this.getContainer();
		if (wizardContainer instanceof WizardDialog) {
			// und was anderes kommt eigentlich nicht vor...

			// füge den oben definierten Listener hinzu, der onSelect aufruft
			// intern wird bereits geprüft, ob der Listener doppelt ist
			((WizardDialog) wizardContainer).addPageChangedListener(mwpcl);

			// von abgeleiteten Klassen können auch mehr Listener hinzugefügt werden
		}
	}

	/**
	 * wird aufgerufen, wenn die Seite ausgewählt wird
	 */
	protected void onSelect() {
		// Von Unterklassen implementiert wenn bei Auswahl etwas geschehen soll
	}

	/**
	 * Erstellt ein MField <br>
	 * Achtung: Für Lookups Methode createMLookupField verwenden!
	 *
	 * @param composite
	 * @param dataType
	 * @param dateTimeType
	 * @param label
	 * @param row
	 * @param column
	 * @param required
	 * @return
	 */
	protected MField createMField(DataType dataType, DateTimeType dateTimeType, String label, boolean required) {

		MField mField = null;
		switch (dataType) {
		case BIGDECIMAL:
		case DOUBLE:
			mField = new MNumberField(2);
			break;
		case BOOLEAN:
			mField = new MBooleanField();
			break;
		case FILTER:
			// Sollte nicht vorkommen
			break;
		case INSTANT:
		case ZONED:
			switch (dateTimeType) {
			case DATE:
				mField = new MShortDateField();
				break;
			case DATETIME:
				mField = new MDateTimeField();
				break;
			case TIME:
				mField = new MShortTimeField();
				break;
			}
			break;
		case INTEGER:
			mField = new MNumberField(0);
			break;
		case REFERENCE:
			// Sollte nicht vorkommen
			break;
		case STRING:
			mField = new MTextField();
			((MTextField) mField).setMaxTextLength(200);
			break;
		}

		initField(label, required, mField);

		return mField;
	}

	private void initField(String label, boolean required, MField mField) {
		if (mField == null) {
			return;
		}
		mField.setLabel(label);
		mField.setName(label);
		mField.setRequired(required);
		mField.addValueChangeListener(this);
		mDetail.putField(mField);
		mField.setMSection(mSection);
		enterHelper.addField(mField);
	}

	/**
	 * Erstellt das UI-Feld
	 *
	 * @param mField
	 * @param dateTimeType
	 * @param composite
	 * @param row
	 * @param column
	 * @return
	 */
	protected Control createUIField(MField mField, DateTimeType dateTimeType, Composite composite, int row, int column) {
		Control c = null;
		switch (mField.getDataType()) {
		case BIGDECIMAL:
		case DOUBLE:
			c = NumberField.create(composite, (MNumberField) mField, row, column, locale, mPerspective);
			break;
		case BOOLEAN:
			c = BooleanField.create(composite, mField, row, column, locale, mPerspective);
			break;
		case FILTER:
			// Sollte nicht vorkommen
			break;
		case INSTANT:
		case ZONED:
			switch (dateTimeType) {
			case DATE:
				c = ShortDateField.create(composite, mField, row, column, locale, timezone, mPerspective, translationService);
				break;
			case DATETIME:
				c = DateTimeField.create(composite, mField, row, column, locale, timezone, mPerspective, translationService);
				break;
			case TIME:
				c = ShortTimeField.create(composite, mField, row, column, locale, timezone, mPerspective, translationService);
				break;
			}
			break;
		case INTEGER:
			c = NumberField.create(composite, (MNumberField) mField, row, column, locale, mPerspective);
			break;
		case REFERENCE:
			// Sollte nicht vorkommen
			break;
		case STRING:
			c = TextField.create(composite, mField, row, column, mPerspective);
			break;
		case QUANTITY:
			c = QuantityField.create(composite, (MQuantityField) mField, row, column, locale, mPerspective);
		}

		if (c != null) {
			c.addListener(SWT.KeyDown, createKeyListener(mField));
			c.addListener(SWT.FocusOut, createKeyListener(mField));
		}

		return c;
	}

	protected MLookupField createMLookupField(Lookup lookup, String label, boolean required) {
		MLookupField mField = new MLookupField();
		mField.setLookupTable(lookup.getTable());
		mField.setLookupProcedurePrefix(lookup.getProcedurePrefix());
		mField.setLookupDescription(lookup.getDescriptionName());
		mField.setNumberColumnsSpanned(4);
		for (TypeParam typeParam : lookup.getParam()) {
			mField.addLookupParameter(typeParam.getFieldName());
		}

		initField(label, required, mField);
		return mField;
	}

	protected Control createUILookupField(MField mField, Composite composite, int row, int column) {
		Control c = LookupField.create(composite, mField, row, column, locale, mPerspective);
		c.addListener(SWT.KeyDown, createKeyListener(mField));
		c.addListener(SWT.FocusOut, createKeyListener(mField));
		return c;
	}

	private static class MinovaWizardPageChangedListener implements IPageChangedListener {
		@Override
		public void pageChanged(PageChangedEvent event) {
			Object selectedPage = event.getSelectedPage();
			if (selectedPage instanceof MinovaWizardPage) {
				((MinovaWizardPage) selectedPage).onSelect();
			}
		}
	}

	@Override
	/**
	 * Knöpfe updaten
	 */
	public void valueChange(ValueChangeEvent evt) {
		setPageComplete(mDetail.allFieldsAndGridsValid() && !popupIsOpen());
	}

	private Listener createKeyListener(MField mField) {
		return event -> {
			if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR) {
				enterHelper.selectNextField(mField);
				event.doit = false;
			}
			setPageComplete(mDetail.allFieldsAndGridsValid() && !popupIsOpen());
		};
	}

	public boolean popupIsOpen() {
		Control focussedControl = ((DetailAccessor) mDetail.getDetailAccessor()).getSelectedControl();
		if (focussedControl instanceof LookupComposite) {
			return ((LookupComposite) focussedControl).popupIsOpen();
		}
		return false;
	}

	/**
	 * Erstellt ein Composite mit dem MinovaLayout. Für Wizardpages mit "normalen" Feldern.
	 *
	 * @param parent
	 * @return
	 */
	protected Composite getComposite(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new DetailLayout());
		setControl(container); // Von wizard benötigt

		MinovaSection section = new MinovaSection(container, ExpandableComposite.TITLE_BAR | ExpandableComposite.EXPANDED);
		MinovaSectionData sectionData = new MinovaSectionData();
		section.setLayoutData(sectionData);

		Composite composite = new Composite(section, SWT.None);
		composite.setLayout(new FormLayout());
		section.setClient(composite);

		return composite;
	}

	/**
	 * Erstellt ein Composite für Wizardpages mit Tabelle
	 *
	 * @param parent
	 * @return
	 */
	protected Composite getCompositeNattable(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		setControl(container);// Von wizard benötigt
		return container;
	}
}