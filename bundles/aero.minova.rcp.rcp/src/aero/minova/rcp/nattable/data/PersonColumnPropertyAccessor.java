package aero.minova.rcp.nattable.data;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

import aero.minova.rcp.model.person.Person;
import aero.minova.rcp.model.person.Person.Gender;

public class PersonColumnPropertyAccessor implements IColumnPropertyAccessor<Person> {

	private static final List<String> propertyNames = Arrays.asList("firstName", "lastName", "gender", "married",
			"birthday");

	@Override
	public Object getDataValue(Person person, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return person.getFirstName();
		case 1:
			return person.getLastName();
		case 2:
			return person.getGender();
		case 3:
			return person.isMarried();
		case 4:
			return person.getBirthday();

		}
		return person;
	}

	@Override
	public void setDataValue(Person person, int columnIndex, Object newValue) {
		switch (columnIndex) {
		case 0:
			String firstName = String.valueOf(newValue);
			person.setFirstName(firstName);
			break;
		case 1:
			String lastName = String.valueOf(newValue);
			person.setLastName(lastName);
			break;
		case 2:
			person.setGender((Gender) newValue);
			break;
		case 3:
			person.setMarried((boolean) newValue);
			break;
		case 4:
			person.setBirthday((Date) newValue);
			break;
		}
	}
	
	@Override
	public int getColumnCount() {
		return 5;
	}
	
	@Override
	public String getColumnProperty(int columnIndex) {
		return propertyNames.get(columnIndex);
	}
	
	@Override
	public int getColumnIndex(String propertyName) {
		return propertyNames.indexOf(propertyName);
	}

}
