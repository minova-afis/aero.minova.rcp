package aero.minova.rcp.nattable.data;

import java.util.Date;
import java.util.List;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

import aero.minova.rcp.model.person.Person;
import aero.minova.rcp.model.person.Person.Gender;

public class PersonDataProvider implements IDataProvider{
	
	private List<Person> persons;

	public PersonDataProvider(List<Person> persons) {
		this.persons = persons;
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		Person person = persons.get(rowIndex);
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
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		Person person = persons.get(rowIndex);
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
	public int getRowCount() {
		return persons.size();
	}


}
