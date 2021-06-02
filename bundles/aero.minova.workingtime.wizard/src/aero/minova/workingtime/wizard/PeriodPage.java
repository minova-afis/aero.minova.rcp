package aero.minova.workingtime.wizard;

import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_LOCALE;
import static aero.minova.rcp.rcp.fields.FieldUtil.TRANSLATE_PROPERTY;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import aero.minova.rcp.form.model.xsd.Detail;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.model.DataType;
import aero.minova.rcp.model.Row;
import aero.minova.rcp.model.Table;
import aero.minova.rcp.model.builder.RowBuilder;
import aero.minova.rcp.model.builder.TableBuilder;
import aero.minova.rcp.model.form.MDetail;
import aero.minova.rcp.model.form.MField;
import aero.minova.rcp.model.form.ModelToViewModel;
import aero.minova.rcp.rcp.fields.LookupField;
import aero.minova.rcp.rcp.fields.ShortDateField;
import aero.minova.rcp.rcp.fields.TextField;
import aero.minova.rcp.rcp.parts.WFCDetailPart;

/**
 * Wizard-Page mit allen Daten zum Arbeitszeit auffüllen
 *
 * @author erlanger
 * @since 12.0.0
 */
public class PeriodPage extends WizardPage {

	private MDetail mDetail = new MDetail();
	private MDetail originalMDetail;
	private MPart mPart;
	private MPerspective mPerspective;
	private TranslationService translationService;
	private Control employee;
	private Control service;
	private Control from;
	private Control until;
	private Control description;
	private Map<String, Field> fieldMap;
	private MField employeeField;
	private MField serviceField;
	private MField fromField;
	private MField untilField;
	private MField descriptionField;

	protected PeriodPage(String pageName, String pageTitle, String pageDescription) {
		super(pageName, pageTitle, null);
		setDescription(pageDescription);
	}

	public void setMPerspective(MPerspective mPerspective) {
		this.mPerspective = mPerspective;
	}

	public void setTranslationService(TranslationService translationService) {
		this.translationService = translationService;
	}

	public void setmPart(MPart mPart) {
		this.mPart = mPart;
	}

	public Table getDataTable() {

		TableBuilder tb = TableBuilder.newTable("xpcasWorkingTimeFill");
		tb = tb.withColumn("EmployeeKey", DataType.INTEGER)//
				.withColumn("ServiceKey", DataType.INTEGER)//
				.withColumn("Description", DataType.STRING)//
				.withColumn("StartDate", DataType.INSTANT)//
				.withColumn("EndDate", DataType.INSTANT);//

		// Row mit Daten füllen
		RowBuilder rb = RowBuilder.newRow();
		rb.withValue(employeeField.getValue() != null ? employeeField.getValue().getValue() : null);
		rb.withValue(serviceField.getValue() != null ? serviceField.getValue().getValue() : null);
		rb.withValue(descriptionField.getValue() != null ? descriptionField.getValue().getValue() : null);
		rb.withValue(fromField.getValue() != null ? fromField.getValue().getValue() : null);
		rb.withValue(untilField.getValue() != null ? untilField.getValue().getValue() : null);

		Table t = tb.create();
		Row r = rb.create();
		t.addRow(r);
		return t;
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

		// Fields sammeln, damit neue MFields erstellt werden können
		fieldMap = new HashMap<>();
		WFCDetailPart wfcDetailPart = (WFCDetailPart) mPart.getObject();
		Detail detail = wfcDetailPart.getForm(null).getDetail();
		List<Object> headAndPageList = detail.getHeadAndPage();
		for (Object headOrPage : headAndPageList) {
			HeadOrPageWrapper wrapper = new HeadOrPageWrapper(headOrPage);
			for (Object fieldOrGrid : wrapper.getFieldOrGrid()) {
				if ((fieldOrGrid instanceof Field)) {
					Field field = (Field) fieldOrGrid;
					MField mField = ModelToViewModel.convert(field);
					mDetail.putField(mField);
					fieldMap.put(field.getName(), field);
				}
			}
		}

		// Neue MFields und Controls erstellen (Damit echter DetailPart nicht beinflusst wird)
		employeeField = ModelToViewModel.convert(fieldMap.get("EmployeeKey"));
		employeeField.setDetail(mDetail);
		employee = LookupField.create(composite, employeeField, 0, 0, Locale.getDefault(), mPerspective);

		serviceField = ModelToViewModel.convert(fieldMap.get("ServiceKey"));
		serviceField.setDetail(mDetail);
		service = LookupField.create(composite, serviceField, 1, 0, Locale.getDefault(), mPerspective);

		fromField = ModelToViewModel.convert(fieldMap.get("BookingDate"));
		fromField.setName("from");
		fromField.setLabel("@TimeFrom");
		fromField.setDetail(mDetail);
		from = ShortDateField.create(composite, fromField, 2, 0, Locale.getDefault(), "UTC", mPerspective);

		untilField = ModelToViewModel.convert(fieldMap.get("BookingDate"));
		untilField.setName("until");
		untilField.setLabel("@TimeUntil");
		untilField.setDetail(mDetail);
		until = ShortDateField.create(composite, untilField, 3, 0, Locale.getDefault(), "UTC", mPerspective);

		descriptionField = ModelToViewModel.convert(fieldMap.get("Description"));
		descriptionField.setDetail(mDetail);
		description = TextField.create(composite, descriptionField, 4, 0, mPerspective);

		translate(composite);
		composite.layout();
		super.setControl(composite);
	}

	private static class HeadOrPageWrapper {
		private Object headOrPage;
		private boolean isHead;

		public HeadOrPageWrapper(Object headOrPage) {
			this.headOrPage = headOrPage;
			isHead = headOrPage instanceof Head;
		}

		public List<Object> getFieldOrGrid() {
			if (isHead) {
				return ((Head) headOrPage).getFieldOrGrid();
			}
			return ((Page) headOrPage).getFieldOrGrid();
		}
	}
}