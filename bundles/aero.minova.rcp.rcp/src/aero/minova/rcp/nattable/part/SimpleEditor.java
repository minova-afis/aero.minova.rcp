package aero.minova.rcp.nattable.part;

import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultGridLayer;
import org.eclipse.swt.widgets.Composite;

import aero.minova.rcp.model.person.Person;
import aero.minova.rcp.model.service.PersonServiceImpl;
import aero.minova.rcp.nattable.data.PersonHeaderDataProvider;

public class SimpleEditor {

	@PostConstruct
	public void postConstruct(Composite parent) {
		PersonServiceImpl personService = new PersonServiceImpl();
		List<Person> persons = personService.getPersons(10);
		ReflectiveColumnPropertyAccessor<Person> columnPropertyAccessor = new ReflectiveColumnPropertyAccessor<>("firstName", "lastName");
		ListDataProvider<Person> dataProvider = new ListDataProvider<>(persons, columnPropertyAccessor);
		
		PersonHeaderDataProvider headerDataProvider = new PersonHeaderDataProvider();
		
		DefaultGridLayer gridLayer = new DefaultGridLayer(dataProvider, headerDataProvider);
		
		NatTable natTable = new NatTable(parent, gridLayer, false);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new AbstractRegistryConfiguration() {

			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);
				
			}
			
		});
		
		natTable.configure();
		
	}
}
