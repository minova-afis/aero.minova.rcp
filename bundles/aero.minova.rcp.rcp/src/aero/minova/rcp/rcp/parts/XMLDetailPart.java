package aero.minova.rcp.rcp.parts;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
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

public class XMLDetailPart {

	@Inject
	protected UISynchronize sync;

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
	private Map<String, Control> controls = new HashMap<>();

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
						DetailUtil.createField((Field) fieldOrGrid, detailFieldComposite, controls);

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
				for (Object fieldOrGrid : page.getFieldOrGrid()) {
					if (fieldOrGrid instanceof Field) {
						DetailUtil.createField((Field) fieldOrGrid, detailFieldComposite, controls);

					}
				}
//				fields.put(DetailPartBinding.BOOKINGDATE,
//						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[1]));
//				fields.put(DetailPartBinding.STARTDATE,
//						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[4]));
//				fields.put(DetailPartBinding.ENDDATE,
//						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[7]));
//				fields.put(DetailPartBinding.RENDEREDQUANTITY,
//						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[10]));
//				fields.put(DetailPartBinding.CHARGEDQUANTIY,
//						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[12]));
//				fields.put(DetailPartBinding.DESCRIPTION,
//						WidgetProperties.text(SWT.Modify).observe(detailFieldComposite.getChildren()[14]));
			}
		}
		
	}

	@Inject
	public void changeSelectedEntry(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) List<Row> rows) {

		if (rows == null || rows.isEmpty()) {
			return;
		}
		int keylong = 0;
		Row row = rows.get(0);
		keylong = row.getValue(0).getIntegerValue();
		Table rowIndexTable = TableBuilder.newTable("spReadWorkingTime").withColumn("KeyLong", DataType.INTEGER)//
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
				.withColumn("Spelling", DataType.BOOLEAN).withKey(keylong).create();

		CompletableFuture<Table> tableFuture = dataService.getDetailDataAsync(rowIndexTable.getName(), rowIndexTable);
		tableFuture.thenAccept(t -> sync.asyncExec(() -> {
			updateSelectedEntry(t);
		}));
	}

	public void updateSelectedEntry(Table table) {
		System.out.println("Table recieved");
		table = getTestTable();
		value = new DetailPartBinding();

		for (int i = 0; i < table.getColumnCount(); i++) {
			String name = table.getColumnName(i);
			Control c = controls.get(name);
			if (c != null) {
				Consumer<Table> consumer = (Consumer<Table>) c.getData("consumer");
				if (consumer != null) {
					try {
						consumer.accept(table);
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
//				Field field = (Field) c.getData("field");
//				Table t = TableBuilder.newTable(field.getLookup().getTable())//
//						.withColumn("KeyLong", DataType.INTEGER)//
//						.withColumn("KeyText", DataType.STRING)//
//						.withColumn("Description", DataType.STRING)//
//						.withKey(r.getValue(i).getIntegerValue())//
//						.create();
//
//				CompletableFuture<Table> tableFuture = dataService.getIndexDataAsync(t.getName(), t);
//				tableFuture.thenAccept(ta -> sync.asyncExec(() -> {
//					updateSelectedLookUpEntry(ta, c);
//				}));
			}
		}
	}

	private void updateSelectedLookUpEntry(Table ta, Control c) {
		// TODO
		System.out.println("Hier werden nun die Daten mit dem UI-Element verknüpft");

	}

	public Table getTestTable() {
		Table rowIndexTable = TableBuilder.newTable("spReadWorkingTime").withColumn("KeyLong", DataType.INTEGER)
				.withColumn("EmployeeKey", DataType.STRING).withColumn("OrderReceiverKey", DataType.STRING)
				.withColumn("ServiceContractKey", DataType.STRING).withColumn("ServiceObjectKey", DataType.STRING)
				.withColumn("ServiceKey", DataType.STRING).withColumn("BookingDate", DataType.ZONED)
				.withColumn("StartDate", DataType.ZONED).withColumn("EndDate", DataType.ZONED)
				.withColumn("RenderedQuantity", DataType.DOUBLE).withColumn("ChargedQuantity", DataType.DOUBLE)
				.withColumn("Description", DataType.STRING).withColumn("Spelling", DataType.BOOLEAN).create();

		Row r = RowBuilder.newRow()//
				.withValue(3)//
				.withValue(3)//
				.withValue(44)//
				.withValue(55)//
				.withValue(66)//
				.withValue(77)//
				.withValue(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin")))//
				.withValue(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin")))//
				.withValue(ZonedDateTime.of(1968, 12, 18, 18, 00, 0, 0, ZoneId.of("Europe/Berlin")))//
				.withValue(44.2)//
				.withValue(33.2)//
				.withValue("test")//
				.withValue(true)//
				.create();
		rowIndexTable.addRow(r);
		return rowIndexTable;
	}

	@PreDestroy
	public void dispose() {
		dbc.dispose();
	}

}
