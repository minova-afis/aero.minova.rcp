package aero.minova.rcp.nattable.part;

import javax.annotation.PostConstruct;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.model.person.Person;
import aero.minova.rcp.model.service.PersonServiceImpl;

public class NatTableLayerStackExamplePart {

	@PostConstruct
	public void postConstruct(Composite parent) {
		PersonServiceImpl personService = new PersonServiceImpl();
		parent.setLayout(new GridLayout());

		String[] propertyNames = { "firstName", "lastName", "gender", "married", "birthday" };
		
		IColumnPropertyAccessor<Person> columnPropertyAccessor = new ReflectiveColumnPropertyAccessor<Person>(
				propertyNames);
		IDataProvider bodyDataProvider = new ListDataProvider<Person>(personService.getPersons(10),
				columnPropertyAccessor);
		
		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
		
		viewportLayer.setRegionName(GridRegion.BODY);
		
		NatTable natTable = new NatTable(parent, viewportLayer);
		
		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

	}

}
