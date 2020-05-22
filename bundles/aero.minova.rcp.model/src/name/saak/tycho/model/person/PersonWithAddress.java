package aero.minova.rcp.model.person;

import java.util.Date;

import aero.minova.rcp.model.person.Person.Gender;

public class PersonWithAddress extends Person {

	private Address address;

	public PersonWithAddress(int id, String firstName, String lastName, Gender gender, boolean married, Date birthday,
			Address address) {
		super(id, firstName, lastName, gender, married, birthday);
		this.address = address;
	}

	public PersonWithAddress(Person person, Address address) {
		super(person.getId(), person.getFirstName(), person.getLastName(), person.getGender(), person.isMarried(),
				person.getBirthday());
		this.address = address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Address getAddress() {
		return address;
	}

}
