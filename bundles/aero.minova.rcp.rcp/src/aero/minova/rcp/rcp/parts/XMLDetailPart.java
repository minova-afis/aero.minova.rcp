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
import org.eclipse.e4.ui.di.UIEventTopic;
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
import aero.minova.rcp.plugin1.model.Column;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.DetailPartBinding;
import aero.minova.rcp.plugin1.model.Row;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.Value;
import aero.minova.rcp.rcp.util.DetailUtil;

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
		//Top-Level_Element
		parent.setLayout(new GridLayout(1, true));
		this.parent=parent;

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
				//Employee
				fields.put(DetailPartBinding.EMPLOYEEKEY,WidgetProperties.ccomboSelection().observe( (CCombo) detailFieldComposite.getChildren()[1]));
				//Customer
				fields.put(DetailPartBinding.ORDERRECEIVERKEY,WidgetProperties.ccomboSelection().observe( (CCombo) detailFieldComposite.getChildren()[4]));
				//Contract
				fields.put(DetailPartBinding.SERVICECONTRACTKEY,WidgetProperties.ccomboSelection().observe( (CCombo) detailFieldComposite.getChildren()[7]));
				//Project
				fields.put(DetailPartBinding.SERVICEKEY,WidgetProperties.ccomboSelection().observe( (CCombo) detailFieldComposite.getChildren()[10]));
				//Service
				fields.put(DetailPartBinding.SERVICEOBJECTKEY,WidgetProperties.ccomboSelection().observe( (CCombo) detailFieldComposite.getChildren()[13]));

			} else if (o instanceof Page) {
				Page page = (Page) o;
				Composite detailFieldComposite = DetailUtil.createSection(formToolkit, parent, page);
				for (Object o2 : page.getFieldOrGrid()) {
					if (o2 instanceof Field) {
						DetailUtil.createField((Field) o2, detailFieldComposite);
					}
				}
				//BookingDate
				fields.put(DetailPartBinding.BOOKINGDATE,WidgetProperties.text(SWT.Modify).observe( detailFieldComposite.getChildren()[1]));
				//StartDate
				fields.put(DetailPartBinding.STARTDATE,WidgetProperties.text(SWT.Modify).observe( detailFieldComposite.getChildren()[4]));
				//EndDate
				fields.put(DetailPartBinding.ENDDATE,WidgetProperties.text(SWT.Modify).observe( detailFieldComposite.getChildren()[7]));
				//RenderedQuantity
				fields.put(DetailPartBinding.RENDEREDQUANTITY,WidgetProperties.text(SWT.Modify).observe( detailFieldComposite.getChildren()[10]));
				//ChargedQuantity
				fields.put(DetailPartBinding.CHARGEDQUANTIY,WidgetProperties.text(SWT.Modify).observe( detailFieldComposite.getChildren()[12]));
				//Description
				fields.put(DetailPartBinding.DESCRIPTION,WidgetProperties.text(SWT.Modify).observe( detailFieldComposite.getChildren()[14]));
			}
		}
		fields.forEach((k, v) -> dbc.bindValue(v, BeanProperties.value(k).observeDetail(observableValue)));
	}

	public void changeSelectedEntry(@Named(IServiceConstants.ACTIVE_SELECTION) List<Row> rows) {

		int keylong = 0;
		Row row = rows.get(0);
		keylong = row.getValue(0).getIntegerValue();

		Table rowIndexTable = new Table();
		rowIndexTable.setName("spReadWorkingTime");
		rowIndexTable.addColumn(new Column("KeyLong", DataType.INTEGER));
		rowIndexTable.addColumn(new Column("EmployeeKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("OrderReceiverKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("ServiceContractKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("ServiceObjectKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("ServiceKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("BookingDate", DataType.ZONED));
		rowIndexTable.addColumn(new Column("StartDate", DataType.ZONED));
		rowIndexTable.addColumn(new Column("EndDate", DataType.ZONED));
		rowIndexTable.addColumn(new Column("RenderedQuantity", DataType.DOUBLE));
		rowIndexTable.addColumn(new Column("ChargedQuantity", DataType.DOUBLE));
		rowIndexTable.addColumn(new Column("Description", DataType.STRING));
		rowIndexTable.addColumn(new Column("Spelling", DataType.STRING));
		Row r = new Row();
		r.addValue(new Value(keylong));
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		r.addValue(null);
		rowIndexTable.addRow(r);
		CompletableFuture<Table> tableFuture = dataService.getDataAsync(rowIndexTable.getName(), rowIndexTable);
		tableFuture.thenAccept(t -> broker.post("CAS_request_selected_entry", t));
	}

	@Inject
	@Optional
	public void UpdateSelectedEntry(@UIEventTopic("CAS_request_selected_entry") Table table) {
		System.out.println("Table recieved");
		table = getTestTable();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		for (Row r : table.getRows()) {
			Composite head = (Composite) parent.getChildren()[0];
			head = (Composite) head.getChildren()[1];
			// TODO: request the options for the ccombo-fields from CAS
			// refillCComboboxes(head);

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

	//This function starts async-CAS-requests for all CComboboxes to load all Options for the selected Data
	public void refillCComboboxes(Composite c)
	{
		Table CASRequest = new Table();
		CompletableFuture<Table> tableFuture;
		CCombo serviceObjectKey = (CCombo)c.getChildren()[13];
		CCombo serviceContractKey = (CCombo)c.getChildren()[7];
		CCombo serviceKey = (CCombo)c.getChildren()[10];
		CCombo orderReceiverKey = (CCombo)c.getChildren()[4];
		Row r;

		//TODO:Employee

		//OrderReceiver
		CASRequest.setName("spReadWorkingTimeOrderReceiver");
		CASRequest.addColumn(new Column("ServiceObjectKey", DataType.STRING));
		CASRequest.addColumn(new Column("ServiceContractKey", DataType.STRING));
		CASRequest.addColumn(new Column("ServiceKey", DataType.STRING));
		CASRequest.addColumn(new Column("BookingDate", DataType.ZONED));
		r = new Row();
		if(serviceObjectKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(serviceObjectKey.getItem(serviceObjectKey.getSelectionIndex())));
		}
		if(serviceContractKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(serviceContractKey.getItem(serviceContractKey.getSelectionIndex())));
		}
		if(serviceKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(serviceKey.getItem(serviceKey.getSelectionIndex()).toString()));
		}
		r.addValue(null);
		CASRequest.addRow(r);
		tableFuture = dataService.getDataAsync(CASRequest.getName(), CASRequest);
		tableFuture.thenAccept(this::UpdateCComboOrderReceiver);

		//ServiceContract
		CASRequest = new Table();
		CASRequest.setName("spReadWorkingTimeServiceContract");
		CASRequest.addColumn(new Column("OrderReceiverKey", DataType.STRING));
		CASRequest.addColumn(new Column("ServiceObjectKey", DataType.STRING));
		CASRequest.addColumn(new Column("ServiceKey", DataType.STRING));
		CASRequest.addColumn(new Column("BookingDate", DataType.ZONED));
		r = new Row();
		if(orderReceiverKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(orderReceiverKey.getItem(orderReceiverKey.getSelectionIndex())));
		}
		if(serviceObjectKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(serviceObjectKey.getItem(serviceObjectKey.getSelectionIndex())));
		}
		if(serviceKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(serviceKey.getItem(serviceKey.getSelectionIndex()).toString()));
		}
		r.addValue(null);
		CASRequest.addRow(r);
		tableFuture = dataService.getDataAsync(CASRequest.getName(), CASRequest);
		tableFuture.thenAccept(this::UpdateCComboServiceContract);

		//ServiceObject
		CASRequest = new Table();
		CASRequest.setName("spReadWorkingTimeServiceObject");
		CASRequest.addColumn(new Column("OrderReceiverKey", DataType.STRING));
		CASRequest.addColumn(new Column("ServiceContractKey", DataType.STRING));
		CASRequest.addColumn(new Column("ServiceKey", DataType.STRING));
		CASRequest.addColumn(new Column("BookingDate", DataType.ZONED));
		r = new Row();
		if(orderReceiverKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(orderReceiverKey.getItem(orderReceiverKey.getSelectionIndex())));
		}
		if(serviceContractKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(serviceContractKey.getItem(serviceContractKey.getSelectionIndex())));
		}
		if(serviceKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(serviceKey.getItem(serviceKey.getSelectionIndex()).toString()));
		}
		r.addValue(null);
		CASRequest.addRow(r);
		tableFuture = dataService.getDataAsync(CASRequest.getName(), CASRequest);
		tableFuture.thenAccept(this::UpdateCComboServiceObject);

		//Service
		CASRequest = new Table();
		CASRequest.setName("spReadWorkingTimeService");
		CASRequest.addColumn(new Column("OrderReceiverKey", DataType.STRING));
		CASRequest.addColumn(new Column("ServiceObjectKey", DataType.STRING));
		CASRequest.addColumn(new Column("ServiceContractKey", DataType.STRING));
		CASRequest.addColumn(new Column("BookingDate", DataType.ZONED));
		r = new Row();
		if(orderReceiverKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(orderReceiverKey.getItem(orderReceiverKey.getSelectionIndex())));
		}
		if(serviceObjectKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(serviceObjectKey.getItem(serviceObjectKey.getSelectionIndex())));
		}
		if(serviceContractKey.getSelectionIndex() == -1){
			r.addValue(null);
		}
		else
		{
			r.addValue(new Value(serviceContractKey.getItem(serviceContractKey.getSelectionIndex())));
		}
		r.addValue(null);
		CASRequest.addRow(r);
		tableFuture = dataService.getDataAsync(CASRequest.getName(), CASRequest);
		tableFuture.thenAccept(this::UpdateCComboService);

	}
	//these Functions get the responce of the CAS for the Async Requests, filling the recieved-data into the ccomboboxes
	//TODO: implementation!
	@Inject
	@Optional
	public void UpdateCComboEmployee(@UIEventTopic("CAS_request_tEmployee") Table table)
	{
		System.out.println("recieved employee-table");
		for(Row r: table.getRows())
		{
			System.out.print("new row:");
			int i = 0;
			while(i<table.getColumnCount())
			{
				System.out.println(r.getValue(i).toString());
			}
		}
	}

	public void UpdateCComboOrderReceiver(Table table) {
		System.out.println("recieved orderreceiver-table");
		for(Row r: table.getRows())
		{
			System.out.print("new row:");
			int i = 0;
			while(i<table.getColumnCount())
			{
				System.out.println(r.getValue(i).toString());
			}
		}
	}
	public void UpdateCComboServiceContract(Table table)
	{
		System.out.println("recieved servicecontract-table");
		for(Row r: table.getRows())
		{
			System.out.print("new row:");
			int i = 0;
			while(i<table.getColumnCount())
			{
				System.out.println(r.getValue(i).toString());
			}
		}
	}
	public void UpdateCComboServiceObject(Table table)
	{
		System.out.println("recieved serviceobject-table");
		for(Row r: table.getRows())
		{
			System.out.print("new row:");
			int i = 0;
			while(i<table.getColumnCount())
			{
				System.out.println(r.getValue(i).toString());
			}
		}
	}
	public void UpdateCComboService(Table table)
	{
		System.out.println("recieved service-table");
		for(Row r: table.getRows())
		{
			System.out.print("new row:");
			int i = 0;
			while(i<table.getColumnCount())
			{
				System.out.println(r.getValue(i).toString());
			}
		}
	}

	public Table getTestTable() {
		Table rowIndexTable = new Table();
		rowIndexTable.setName("spReadWorkingTime");
		rowIndexTable.addColumn(new Column("KeyLong", DataType.INTEGER));
		rowIndexTable.addColumn(new Column("EmployeeKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("OrderReceiverKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("ServiceContractKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("ServiceObjectKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("ServiceKey", DataType.STRING));
		rowIndexTable.addColumn(new Column("BookingDate", DataType.ZONED));
		rowIndexTable.addColumn(new Column("StartDate", DataType.ZONED));
		rowIndexTable.addColumn(new Column("EndDate", DataType.ZONED));
		rowIndexTable.addColumn(new Column("RenderedQuantity", DataType.DOUBLE));
		rowIndexTable.addColumn(new Column("ChargedQuantity", DataType.DOUBLE));
		rowIndexTable.addColumn(new Column("Description", DataType.STRING));
		rowIndexTable.addColumn(new Column("Spelling", DataType.STRING));
		Row r = new Row();
		r.addValue(new Value(3));
		r.addValue(new Value(44));
		r.addValue(new Value(55));
		r.addValue(new Value(66));
		r.addValue(new Value(77));
		r.addValue(new Value(88));
		r.addValue(new Value(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin"))));
		r.addValue(new Value(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin"))));
		r.addValue(new Value(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin"))));
		r.addValue(new Value(44.2));
		r.addValue(new Value(33.2));
		r.addValue(new Value("test"));
		r.addValue(new Value("test"));
		rowIndexTable.addRow(r);
		return rowIndexTable;
	}

	@PreDestroy
	public void dispose() {
		dbc.dispose();
	}

}
