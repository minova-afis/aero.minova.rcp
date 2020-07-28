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
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
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
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.xml.sax.SAXException;

import aero.minova.rcp.form.model.xsd.Column;
import aero.minova.rcp.form.model.xsd.Form;
import aero.minova.rcp.form.model.xsd.IndexView;

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

		IColumnPropertyAccessor<Column> columnPropertyAccessor = new ReflectiveColumnPropertyAccessor<Column>(
				propertyNames);

		// build the body layer stack
		IDataProvider bodyDataProvider = new ListDataProvider<Column>(columns, columnPropertyAccessor);
//                new ListDataProvider<Person>(
//                        personService.getPersons(50),
//                        columnPropertyAccessor);
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

//		try {
//		xmlProcessor = new XmlProcessor(Form.class);
//		form = (Form) xmlProcessor
//				.load(new File("/Users/erlanger/git/aero.minova.rcp/bundles/aero.minova.rcp.rcp/src/aero/minova/rcp/rcp/parts/WorkingTime.xml"));
//
//	} catch (JAXBException | SAXException | IOException e) {
//		e.printStackTrace();
//	}
		// test data for creating a NatTable
		Form form;
		form = new Form();
		form.setIndexView(new IndexView());
		Column c = new Column();
		c.setName("name"); // das hier ist Attribute im Datenmodell
		c.setTextAttribute("testing");
		form.getIndexView().getColumn().add(c);
		return form;
	}

}
