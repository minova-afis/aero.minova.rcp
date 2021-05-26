package aero.minova.workingtime.wizard;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.Locale;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.TextField;

/**
 * Wizard-Page mit allen Daten zum Arbeitszeit auffüllen
 *
 * @author erlanger
 * @since 12.0.0
 */
public class PeriodPage extends WizardPage {

	protected PeriodPage(String pageName) {
		super(pageName);
	}

	private MDetail mDetail;
	private MPerspective mPerspective;
	private TranslationService translationService;

	public void setMDetail(MDetail mDetail) {
		this.mDetail = mDetail;
	}

	public void setMPerspective(MPerspective mPerspective) {
		this.mPerspective = mPerspective;
	}

	public void setTranslationService(TranslationService translationService) {
		this.translationService = translationService;
	}

	public Table getDataTable() {
		return null;
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
				control.setData(TRANSLATE_LOCALE, Locale.getDefault());
			}
		}
	}

	@Override
	public void createControl(Composite composite) {
		composite.setLayout(new FormLayout());
		LookupField.create(composite, mDetail.getField("EmployeeKey"), 0, 0, Locale.getDefault(), mPerspective);
		LookupField.create(composite, mDetail.getField("ServiceKey"), 1, 0, Locale.getDefault(), mPerspective);
		ShortDateField.create(composite, mDetail.getField("BookingDate"), 2, 0, Locale.getDefault(), "UTC", mPerspective);
		ShortDateField.create(composite, mDetail.getField("BookingDate"), 3, 0, Locale.getDefault(), "UTC", mPerspective);
		TextField.create(composite, mDetail.getField("Description"), 4, 0, mPerspective);
		translate(composite);
		composite.layout();
		super.setControl(composite);
	}

}