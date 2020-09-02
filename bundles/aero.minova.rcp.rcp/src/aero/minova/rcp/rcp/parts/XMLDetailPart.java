package aero.minova.rcp.rcp.parts;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.databinding.swt.typed.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;

import aero.minova.rcp.dataservice.IDataFormService;
import aero.minova.rcp.dataservice.IDataService;
import aero.minova.rcp.form.model.xsd.Field;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.Head;
import aero.minova.rcp.form.model.xsd.Page;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.DetailPartBinding;
import aero.minova.rcp.plugin1.model.Row;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.builder.RowBuilder;
import aero.minova.rcp.plugin1.model.builder.TableBuilder;
import aero.minova.rcp.rcp.util.DetailUtil;
import aero.minova.rcp.rcp.widgets.LookupControl;

public class XMLDetailPart {

	@Inject
	private IDataFormService dataFormService;

	@Inject
	private IDataService dataService;

	@Inject
	IEventBroker broker;

	private final FormToolkit formToolkit = new FormToolkit(Display.getDefault());
	private Composite parent;

	private DataBindingContext dbc;
	private Map<String, IObservableValue<?>> fields;
	private DetailPartBinding value = new DetailPartBinding();
	private WritableValue<DetailPartBinding> observableValue = new WritableValue<>();

	@PostConstruct
	public void createComposite(Composite parent) {
		fields = new HashMap<>();
		dbc = new DataBindingContext();
		// Top-Level_Element
		parent.setLayout(new GridLayout(1, true));
		this.parent = parent;

		Form form = dataFormService.getForm();

		for (Object o : form.getDetail().getHeadAndPage()) {
			if (o instanceof Head) {
				Head head = (Head) o;
				Composite detailFieldComposite = DetailUtil.createSection(formToolkit, parent, head);
				for (Object fieldOrGrid : head.getFieldOrGrid()) {
					if (fieldOrGrid instanceof Field) {
						DetailUtil.createField((Field) fieldOrGrid, detailFieldComposite);
					}
				}
//				// Employee
//				fields.put(DetailPartBinding.EMPLOYEEKEY,
//						WidgetProperties.ccomboSelection().observe((LookupControl) detailFieldComposite.getChildren()[1]));
//				// Customer
//				fields.put(DetailPartBinding.ORDERRECEIVERKEY,
//						WidgetProperties.ccomboSelection().observe((LookupControl) detailFieldComposite.getChildren()[4]));
//				// Contract
//				fields.put(DetailPartBinding.SERVICECONTRACTKEY,
//						WidgetProperties.ccomboSelection().observe((LookupControl) detailFieldComposite.getChildren()[7]));
//				// Project
//				fields.put(DetailPartBinding.SERVICEKEY,
//						WidgetProperties.ccomboSelection().observe((LookupControl) detailFieldComposite.getChildren()[10]));
//				// Service
//				fields.put(DetailPartBinding.SERVICEOBJECTKEY,
//						WidgetProperties.ccomboSelection().observe((LookupControl) detailFieldComposite.getChildren()[13]));

			} else if (o instanceof Page) {
				Page page = (Page) o;
				Composite detailFieldComposite = DetailUtil.createSection(formToolkit, parent, page);
				for (Object o2 : page.getFieldOrGrid()) {
					if (o2 instanceof Field) {
						DetailUtil.createField((Field) o2, detailFieldComposite);
					}
				}
				// BookingDate
				fields.put(DetailPartBinding.BOOKINGDATE,
						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[1]));
				// StartDate
				fields.put(DetailPartBinding.STARTDATE,
						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[4]));
				// EndDate
				fields.put(DetailPartBinding.ENDDATE,
						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[7]));
				// RenderedQuantity
				fields.put(DetailPartBinding.RENDEREDQUANTITY,
						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[10]));
				// ChargedQuantity
				fields.put(DetailPartBinding.CHARGEDQUANTIY,
						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[12]));
				// Description
				fields.put(DetailPartBinding.DESCRIPTION,
						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[14]));
			}
		}
		fields.forEach((k, v) -> dbc.bindValue(v, BeanProperties.value(k).observeDetail(observableValue)));
	}

	@Inject
	public void changeSelectedEntry(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) List<Row> rows) {

		if (rows == null || rows.isEmpty()) {
			return;
		}
		int keylong = 0;
		Row row = rows.get(0);
		keylong = row.getValue(0).getIntegerValue();
		Table rowIndexTable = TableBuilder.newTable("spReadWorkingTime")
				.withColumn("KeyLong", DataType.INTEGER)//
				.withColumn("EmployeeKey", DataType.STRING)//
				.withColumn("OrderReceiverKey", DataType.STRING)//
				.withColumn("ServiceContractKey", DataType.STRING)//
				.withColumn("ServiceObjectKey", DataType.STRING)//
				.withColumn("ServiceKey", DataType.STRING)//
				.withColumn("BookingDate", DataType.ZONED)//
				.withColumn("StartDate", DataType.ZONED)//
				.withColumn("EndDate", DataType.ZONED)//
				.withColumn("RenderedQuantity", DataType.DOUBLE)//
				.withColumn("ChargedQuantity", DataType.DOUBLE)//
				.withColumn("Description", DataType.STRING)//
				.withColumn("Spelling", DataType.STRING).withKey(keylong)
				.create();

		CompletableFuture<Table> tableFuture = dataService.getDataAsync(rowIndexTable.getName(), rowIndexTable);
		tableFuture.thenAccept(this::updateSelectedEntry);
	}

	public void updateSelectedEntry(Table table) {
		System.out.println("Table recieved");
		table = getTestTable();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		for (Row r : table.getRows()) {
			Composite head = (Composite) parent.getChildren()[0];
			head = (Composite) head.getChildren()[1];
			// TODO: request the options for the ccombo-fields from CAS

			value.setKeylong(r.getValue(0).getIntegerValue());
			value.setEmployeeKey(r.getValue(1).getStringValue());
			value.setOrderReceiverKey(r.getValue(2).getStringValue());
			value.setServiceContractKey(r.getValue(3).getStringValue());
			value.setServiceObjectKey(r.getValue(4).getStringValue());
			value.setServiceKey(r.getValue(5).getStringValue());
			value.setBookingDate(r.getValue(6).getZonedDateTimeValue().format(dtf));
			value.setStartDate(r.getValue(7).getZonedDateTimeValue().format(dtf));
			value.setEndDate(r.getValue(8).getZonedDateTimeValue().format(dtf));
			value.setRenderedQuantity(r.getValue(9).getDoubleValue().toString());
			value.setChargedQuantity(r.getValue(10).getDoubleValue().toString());
			value.setDescription(r.getValue(11).getStringValue());
			value.setSpelling(r.getValue(12).getStringValue());

			observableValue.setValue(value);

		}
	}

	public Table getTestTable() {
		Table rowIndexTable = TableBuilder.newTable("spReadWorkingTime")
				.withColumn("KeyLong", DataType.INTEGER)
				.withColumn("EmployeeKey", DataType.STRING).withColumn("OrderReceiverKey", DataType.STRING)
				.withColumn("ServiceContractKey", DataType.STRING).withColumn("ServiceObjectKey", DataType.STRING)
				.withColumn("ServiceKey", DataType.STRING).withColumn("BookingDate", DataType.ZONED)
				.withColumn("StartDate", DataType.ZONED).withColumn("EndDate", DataType.ZONED)
				.withColumn("RenderedQuantity", DataType.DOUBLE).withColumn("ChargedQuantity", DataType.DOUBLE)
				.withColumn("Description", DataType.STRING).withColumn("Spelling", DataType.STRING)
				.create();
		Row r = RowBuilder.newRow().withValue(3)//
				.withValue(3)//
				.withValue(44)//
				.withValue(55)//
				.withValue(66)//
				.withValue(77)//
				.withValue(88)//
				.withValue(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin")))//
				.withValue(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin")))//
				.withValue(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin")))//
				.withValue(44.2)//
				.withValue(33.2)//
				.withValue("test")//
				.withValue("test")//
				.create();
		rowIndexTable.addRow(r);
		return rowIndexTable;
	}

	@PreDestroy
	public void dispose() {
		dbc.dispose();
	}

}
