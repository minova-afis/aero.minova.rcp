package aero.minova.rcp.model.person;

import java.util.Date;
import java.util.List;


public class ExtendedPersonWithAddress extends PersonWithAddress {
	private String password;
	private String description;
	private List<String> favouriteFood;
	private List<String> favouriteDrinks;
	private int age;
	private double money;
	private String filename;

	@SuppressWarnings("deprecation")
	public ExtendedPersonWithAddress(int id, String firstName, String lastName, Gender gender, boolean married,
			Date birthday, Address address, String password, String description, double money,
			List<String> favouriteFood, List<String> favouriteDrinks) {
		super(id, firstName, lastName, gender, married, birthday, address);

		this.password = password;
		this.description = description;
		this.money = money;
		this.favouriteFood = favouriteFood;
		this.favouriteDrinks = favouriteDrinks;
		this.age = new Date().getYear() - getBirthday().getYear();
	}

	@SuppressWarnings("deprecation")
	public ExtendedPersonWithAddress(Person person, Address address, String password, String description, double money,
			List<String> favouriteFood, List<String> favouriteDrinks) {
		super(person, address);

		this.password = password;
		this.description = description;
		this.money = money;
		this.favouriteFood = favouriteFood;
		this.favouriteDrinks = favouriteDrinks;
		this.age = new Date().getYear() - getBirthday().getYear();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getFavouriteFood() {
		return favouriteFood;
	}

	public void setFavouriteFood(List<String> favouriteFood) {
		this.favouriteFood = favouriteFood;
	}

	public List<String> getFavouriteDrinks() {
		return favouriteDrinks;
	}

	public void setFavouriteDrinks(List<String> favouriteDrinks) {
		this.favouriteDrinks = favouriteDrinks;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
