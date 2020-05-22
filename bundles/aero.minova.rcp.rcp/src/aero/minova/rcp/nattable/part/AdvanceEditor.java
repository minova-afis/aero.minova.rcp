
package aero.minova.rcp.nattable.part;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.model.person.Person;
import aero.minova.rcp.model.service.PersonServiceImpl;
import aero.minova.rcp.nattable.data.PersonDataProvider;
import aero.minova.rcp.nattable.data.PersonHeaderDataProvider;
import aero.minova.rcp.nattable.edit.EditConfiguration;

public class AdvanceEditor {

	@PostConstruct
	public void postConstruct(Composite parent) {
		PersonServiceImpl personService = new PersonServiceImpl();
		List<Person> persons = personService.getPersons(10);
		PersonDataProvider dataProvider = new PersonDataProvider(persons);

        PersonHeaderDataProvider headerDataProvider = new PersonHeaderDataProvider();

		DefaultGridLayer gridLayer = new DefaultGridLayer(dataProvider, headerDataProvider);

		ColumnLabelAccumulator columnLabelAccumulator = new ColumnLabelAccumulator(dataProvider);
		((DataLayer) gridLayer.getBodyDataLayer()).setConfigLabelAccumulator(columnLabelAccumulator);

		NatTable natTable = new NatTable(parent, gridLayer, false);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new EditConfiguration());

		natTable.configure();

	}

}