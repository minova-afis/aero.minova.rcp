package aero.minova.rcp.model.person;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

public class Person {
	protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	public enum Gender {
		MALE, FEMALE
	}

	private final int id;
	private String firstName;
	private String lastName;
	private Gender gender;
	private boolean married;
	private Date birthday;

	public Person(int id) {
		this.id = id;
	}

	public Person(int id, String firstName, String lastName, Gender gender, boolean married, Date birthday) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gender = gender;
		this.married = married;
		this.birthday = birthday;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changeSupport.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changeSupport.removePropertyChangeListener(l);
	}

	public int getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		changeSupport.firePropertyChange("firstName", this.firstName, this.firstName = firstName);
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		changeSupport.firePropertyChange("lastName", this.firstName, this.lastName = lastName);
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		changeSupport.firePropertyChange("gender", this.gender, this.gender = gender);
	}

	public boolean isMarried() {
		return married;
	}

	public void setMarried(boolean married) {
		changeSupport.firePropertyChange("married", this.married, this.married = married);
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		changeSupport.firePropertyChange("birthday", this.birthday, this.birthday = birthday);
	}

	@Override
	public String toString() {
		return getFirstName() + " " + getLastName();
	}
}
