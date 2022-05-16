package aero.minova.rcp.rcp.widgets;

import java.util.Locale;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

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
import aero.minova.rcp.model.form.MSection;
import aero.minova.rcp.model.form.MShortDateField;
import aero.minova.rcp.model.form.MShortTimeField;
import aero.minova.rcp.model.form.MTextField;
import aero.minova.rcp.preferences.ApplicationPreferences;
import aero.minova.rcp.preferencewindow.control.CustomLocale;
import aero.minova.rcp.rcp.accessor.DetailAccessor;
import aero.minova.rcp.rcp.fields.BooleanField;
import aero.minova.rcp.rcp.fields.DateTimeField;
import aero.minova.rcp.rcp.fields.NumberField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.ShortTimeField;
import aero.minova.rcp.rcp.fields.TextField;

public class MinovaWizardPage extends WizardPage implements ValueChangeListener {

	@Inject
	@Preference(nodePath = ApplicationPreferences.PREFERENCES_NODE, value = ApplicationPreferences.TIMEZONE)
	String timezone;

	@Inject
	private TranslationService translationService;

	@Inject
	private MPerspective mPerspective;

	Locale locale = CustomLocale.getLocale();

	// Leider für die Required Prüfung Notwendig
	MSection mSection;

	private static final MinovaWizardPageChangedListener mwpcl = new MinovaWizardPageChangedListener();
	protected MDetail mDetail;

	public MinovaWizardPage(String pageName, String pageTitle, String pageDescription) {
		super(pageName);
		setTitle(pageTitle);
		setDescription(pageDescription);

		mDetail = new MDetail();
		mDetail.setDetailAccessor(new DetailAccessor(mDetail));

		mSection = new MSection(false, "", mDetail, "", "");
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
	 * Erstellt ein MField inkl dem UI. <br>
	 * Achtung: Für Lookups methode createMLookupField verwenden!
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
	protected MField createMField(Composite composite, DataType dataType, DateTimeType dateTimeType, String label, int row, int column, boolean required) {

		MField mField = null;
		switch (dataType) {
		case BIGDECIMAL:
		case DOUBLE:
			mField = new MNumberField(2);
			initField(label, required, mField);
			NumberField.create(composite, (MNumberField) mField, row, column, locale, mPerspective);
			break;
		case BOOLEAN:
			mField = new MBooleanField();
			initField(label, required, mField);
			BooleanField.create(composite, mField, row, column, locale, mPerspective);
			break;
		case FILTER:
			// Sollte nicht vorkommen
			break;
		case INSTANT:
		case ZONED:
			mField = createInstantField(composite, dateTimeType, row, column, mField, label, required);
			break;
		case INTEGER:
			mField = new MNumberField(0);
			initField(label, required, mField);
			NumberField.create(composite, (MNumberField) mField, row, column, locale, mPerspective);
			break;
		case REFERENCE:
			// Sollte nicht vorkommen
			break;
		case STRING:
			mField = new MTextField();
			initField(label, required, mField);
			TextField.create(composite, mField, row, column, mPerspective);
			break;
		}

		return mField;
	}

	private void initField(String label, boolean required, MField mField) {
		mField.setLabel(label);
		mField.setName(label);
		mField.setRequired(required);
		mField.addValueChangeListener(this);
		mDetail.putField(mField);
		mField.setMSection(mSection);
	}

	private MField createInstantField(Composite composite, DateTimeType dateTimeType, int row, int column, MField mField, String label, boolean required) {
		switch (dateTimeType) {
		case DATE:
			mField = new MShortDateField();
			initField(label, required, mField);
			ShortDateField.create(composite, mField, row, column, locale, timezone, mPerspective, translationService);
			break;
		case DATETIME:
			mField = new MDateTimeField();
			initField(label, required, mField);
			DateTimeField.create(composite, mField, row, column, locale, timezone, mPerspective, translationService);
			break;
		case TIME:
			mField = new MShortTimeField();
			initField(label, required, mField);
			ShortTimeField.create(composite, mField, row, column, locale, timezone, mPerspective, translationService);
			break;
		}
		return mField;
	}

	protected MLookupField createMLookupField() {
		// TODO
		return new MLookupField();
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
	public void valueChange(ValueChangeEvent evt) {
		setPageComplete(mDetail.allFieldsAndGridsValid());
	}
}