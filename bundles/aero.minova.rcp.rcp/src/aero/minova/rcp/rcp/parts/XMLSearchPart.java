package aero.minova.rcp.rcp.parts;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.xml.sax.SAXException;

import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.IndexView;
import aero.minova.rcp.nattable.data.MinovaColumnPropertyAccessor;
import aero.minova.rcp.plugin1.model.DataType;
import aero.minova.rcp.plugin1.model.Row;
import aero.minova.rcp.plugin1.model.Table;
import aero.minova.rcp.plugin1.model.Value;

public class XMLSearchPart {

	private XmlProcessor xmlProcessor;

	@PostConstruct
	public void createComposite(Composite parent) {
		parent.setLayout(new GridLayout());

		Form form = readFormData();

		// mapping from property to label, needed for column header labels
		Map<String, String> tableHeadersMap = new HashMap<>();
		List<Column> columns = form.getIndexView().getColumn();
		String[] propertyNames = new String[columns.size()];
		int i = 0;
		for (Column column : columns) {
			tableHeadersMap.put(column.getName(), column.getTextAttribute());
			propertyNames[i++] = column.getName();
		}

		// Datenmodel für die Eingaben
		Table t = new Table();
		t.addColumn(new aero.minova.rcp.plugin1.model.Column("KeyLong", DataType.INTEGER));
		t.addColumn(new aero.minova.rcp.plugin1.model.Column("EmployeeText", DataType.STRING));
		Row r;
		r = new Row();
		r.addValue(null);
		r.addValue(null);
		t.addRow(r);
		r = new Row();
		r.addValue(new Value("56"));
		r.addValue(new Value("Peter"));
		t.addRow(r);

		IColumnPropertyAccessor<Row> columnPropertyAccessor = new MinovaColumnPropertyAccessor(t);

		// build the body layer stack
		IDataProvider bodyDataProvider = new ListDataProvider<Row>(t.getRows(), columnPropertyAccessor);
		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// build the column header layer stack
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames, tableHeadersMap);
		DataLayer columnHeaderDataLayer = new DataLayer(columnHeaderDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, viewportLayer, selectionLayer);

		// build the row header layer stack
		IDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		DataLayer rowHeaderDataLayer = new DataLayer(rowHeaderDataProvider, 40, 20);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);

		// build the corner layer stack
		ILayer cornerLayer = new CornerLayer(
				new DataLayer(new DefaultCornerDataProvider(columnHeaderDataProvider, rowHeaderDataProvider)),
				rowHeaderLayer, columnHeaderLayer);

		// create the grid layer composed with the prior created layer stacks
		GridLayer gridLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		NatTable natTable = new NatTable(parent, gridLayer, true);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
	}

	private Form readFormData() {

		Form form;
//		try {
//			xmlProcessor = new XmlProcessor(Form.class);
//			form = (Form) xmlProcessor.load(new File(
//					"/Users/erlanger/git/aero.minova.rcp/bundles/aero.minova.rcp.rcp/src/aero/minova/rcp/rcp/parts/WorkingTime.xml"));
//
//		} catch (JAXBException | SAXException | IOException e) {
//			e.printStackTrace();
//		}
		// test data for creating a NatTable
		form = new Form();
		form.setIndexView(new IndexView());
		Column c = new Column();
		c.setName("KeyLong"); // das hier ist Attribute im Datenmodell
		c.setTextAttribute("ID");
		form.getIndexView().getColumn().add(c);
		Column c2 = new Column();
		c2.setName("EmployeeText"); // das hier ist Attribute im Datenmodell
		c2.setTextAttribute("Mitarbeiter");
		form.getIndexView().getColumn().add(c2);
		return form;
	}

}
