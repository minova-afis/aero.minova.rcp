package aero.minova.rcp.model.person;

import java.util.List;
import java.util.Map;

public interface PersonService {
	public static String[] maleNames = { "Bart", "Homer", "Lenny", "Carl", "Waylon", "Ned", "Timothy" };
	public static String[] femaleNames = { "Marge", "Lisa", "Maggie", "Edna", "Helen", "Jessica" };
	public static String[] lastNames = { "Simpson", "Leonard", "Carlson", "Smithers", "Flanders", "Krabappel",
			"Lovejoy" };
	public static String[] streetNames = new String[] { "Evergreen Terrace", "Main Street", "South Street",
			"Plympton Street", "Highland Avenue", "Elm Street", "Oak Grove Street" };
	public static String[] cityNames = new String[] { "Springfield", "Shelbyville", "Ogdenville", "Waverly Hills",
			"North Haverbrook", "Capital City" };
	public static String[] foodList = new String[] { "Donut", "Bacon", "Fish", "Vegetables", "Ham", "Prezels" };
	public static String[] drinkList = new String[] { "Beer", "Water", "Soda", "Milk", "Coke", "Fizzy Bubblech" };
	public static String baseText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
			+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore " + "magna aliquyam erat, sed diam voluptua.";

	/**
	 * Creates a list of {@link Person}s.
	 * 
	 * @param numberOfPersons The number of {@link Person}s that should be
	 *                        generated.
	 * @return
	 */
	List<Person> getPersons(int numberOfPersons);

	/**
	 * Creates a list of {@link PersonWithAddress}.
	 * 
	 * @param numberOfPersons The number of {@link PersonWithAddress} that should be
	 *                        generated.
	 * @return
	 */
	List<PersonWithAddress> getPersonsWithAddress(int numberOfPersons);

	/**
	 * Creates a list of {@link ExtendedPersonWithAddress}.
	 * 
	 * @param numberOfPersons The number of {@link ExtendedPersonWithAddress} that
	 *                        should be generated.
	 * @return
	 */
	List<ExtendedPersonWithAddress> getExtendedPersonsWithAddress(int numberOfPersons);

	/**
	 * Creates a random person out of names which are taken from "The Simpsons" and
	 * enrich them with random generated married state and birthday date.
	 * 
	 * @return
	 */
	Person createPerson(int id);

	/**
	 * Creates a random address out of street names, postal codes and city names
	 * which are taken from "The Simpsons" (i haven't found postal codes, so here i
	 * invented some for the example)
	 * 
	 * @return
	 */
	Address createAddress();

	/**
	 * @return A simple password consisting of 8 characters in the value ranges a-z,
	 *         A-Z
	 */
	String generateSimplePassword();

	/**
	 * @return A random size list that contains some food values.
	 */
	List<String> createFavouriteFood();

	/**
	 * @return A random size list that contains drink values.
	 */
	List<String> createFavouriteDrinks();

	/**
	 * @return An array of street names that are also used to create random
	 *         addresses.
	 */
	String[] getStreetNames();

	/**
	 * @return An array of city names that are also used to create random addresses.
	 */
	String[] getCityNames();

	/**
	 * @return An array of food names.
	 */
	String[] getFoodList();

	/**
	 * @return An array of drink names.
	 */
	String[] getDrinkList();

	/**
	 * @return A custom length text containing line breaks
	 */
	String createRandomLengthText();

	/**
	 * @return A random money amount between 0 and 1000.
	 */
	Double createRandomMoneyAmount();

	String[] getDefaultPropertyNames();

	Map<String, String> getDefaultPropertyToLabelMap();

}
