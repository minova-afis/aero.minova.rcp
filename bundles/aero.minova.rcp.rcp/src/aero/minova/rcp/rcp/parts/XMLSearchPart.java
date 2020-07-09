package aero.minova.rcp.rcp.parts;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.xml.sax.SAXException;

import aero.minova.rcp.form.model.xsd.Form;

public class XMLSearchPart {

	private XmlProcessor xmlProcessor;

	@PostConstruct
	public void createComposite(Composite parent) {
		try {
			xmlProcessor = new XmlProcessor(Form.class);
			Form object = (Form) xmlProcessor
					.load(new File("/home/hofmann/git/aero.minova.rcp/bundles/aero.minova.rcp.rcp/src/aero/minova/rcp/rcp/parts/WorkingTime.xml"));
			System.out.println(object);

			parent.setLayout(new GridLayout());

			// property names of the Person class
			String[] propertyNames = { "firstName", "lastName", "gender", "married", "birthday" };

			// create the data provider
			IColumnPropertyAccessor<String> columnPropertyAccessor = new ReflectiveColumnPropertyAccessor<String>(propertyNames);
			IDataProvider bodyDataProvider = new ListDataProvider<String>(new ArrayList<String>(), columnPropertyAccessor);

			final DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);

//			IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames);
//			DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);

			// use different style bits to avoid rendering of inactive scrollbars for small
			// table
			// Note: The enabling/disabling and showing of the scrollbars is handled by the
			// ViewportLayer. Without the ViewportLayer the scrollbars will always be
			// visible with the default style bits of NatTable.
			final NatTable natTable = new NatTable(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER, bodyDataLayer);

			GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

		} catch (JAXBException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
}
