package aero.minova.rcp.nattable.part;

import javax.annotation.PostConstruct;

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

import aero.minova.rcp.model.person.Person;
import aero.minova.rcp.model.service.PersonServiceImpl;

public class NatTableDataExample{
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		PersonServiceImpl personService = new PersonServiceImpl();
		parent.setLayout(new GridLayout());

		// property names of the Person class
		String[] propertyNames = { "firstName", "lastName", "gender", "married", "birthday" };

		// create the data provider
		IColumnPropertyAccessor<Person> columnPropertyAccessor = new ReflectiveColumnPropertyAccessor<Person>(
				propertyNames);
		IDataProvider bodyDataProvider = new ListDataProvider<Person>(personService.getPersons(10),
				columnPropertyAccessor);

		final DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);

		// use different style bits to avoid rendering of inactive scrollbars for small
		// table
		// Note: The enabling/disabling and showing of the scrollbars is handled by the
		// ViewportLayer. Without the ViewportLayer the scrollbars will always be
		// visible with the default style bits of NatTable.
		final NatTable natTable = new NatTable(parent, SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.BORDER,
				bodyDataLayer);

		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
	}

}
