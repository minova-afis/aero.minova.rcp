package aero.minova.rcp.model.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.osgi.service.component.annotations.Component;

import aero.minova.rcp.model.person.Address;
import aero.minova.rcp.model.person.ExtendedPersonWithAddress;
import aero.minova.rcp.model.person.Person;
import aero.minova.rcp.model.person.PersonService;
import aero.minova.rcp.model.person.PersonWithAddress;

/**
 * Class that acts as service for accessing numerous {@link Person}s. The values
 * are randomly put together out of names and places from "The Simpsons"
 * 
 * @author Dirk Fauth
 */
@Component(service = PersonService.class)
public class PersonServiceImpl implements PersonService {

	private static final String[] propertyNames = { "firstName", "lastName", "gender", "married", "birthday" };

	private static final Map<String, String> propertyToLabelMap;

	static {
		propertyToLabelMap = new HashMap<>();
		propertyToLabelMap.put("firstName", "Firstname");
		propertyToLabelMap.put("lastName", "Lastname");
		propertyToLabelMap.put("gender", "Gender");
		propertyToLabelMap.put("married", "Married");
		propertyToLabelMap.put("birthday", "Birthday");
	}

	@Override
	public List<Person> getPersons(int numberOfPersons) {
		List<Person> result = new ArrayList<Person>();

		for (int i = 0; i < numberOfPersons; i++) {
			result.add(createPerson(i));
		}

		return result;
	}

	@Override
	public List<PersonWithAddress> getPersonsWithAddress(int numberOfPersons) {
		List<PersonWithAddress> result = new ArrayList<PersonWithAddress>();

		for (int i = 0; i < numberOfPersons; i++) {
			result.add(new PersonWithAddress(createPerson(i), createAddress()));
		}

		return result;
	}

	@Override
	public List<ExtendedPersonWithAddress> getExtendedPersonsWithAddress(int numberOfPersons) {
		List<ExtendedPersonWithAddress> result = new ArrayList<ExtendedPersonWithAddress>();

		for (int i = 0; i < numberOfPersons; i++) {
			result.add(new ExtendedPersonWithAddress(createPerson(i), createAddress(), generateSimplePassword(),
					createRandomLengthText(), createRandomMoneyAmount(), createFavouriteFood(),
					createFavouriteDrinks()));
		}

		return result;
	}

	@Override
	public Person createPerson(int id) {
		Random randomGenerator = new Random();

		Person result = new Person(id);
		result.setGender(Person.Gender.values()[randomGenerator.nextInt(2)]);

		if (result.getGender().equals(Person.Gender.MALE)) {
			result.setFirstName(maleNames[randomGenerator.nextInt(maleNames.length)]);
		} else {
			result.setFirstName(femaleNames[randomGenerator.nextInt(femaleNames.length)]);
		}

		result.setLastName(lastNames[randomGenerator.nextInt(lastNames.length)]);
		result.setMarried(randomGenerator.nextBoolean());

		int month = randomGenerator.nextInt(12);
		int day = 0;
		if (month == 2) {
			day = randomGenerator.nextInt(28);
		} else {
			day = randomGenerator.nextInt(30);
		}
		int year = 1920 + randomGenerator.nextInt(90);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			result.setBirthday(sdf.parse("" + year + "-" + month + "-" + day));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public Address createAddress() {
		String[] streets = getStreetNames();
		int[] plz = { 11111, 22222, 33333, 44444, 55555, 66666 };
		String[] cities = getCityNames();

		Random randomGenerator = new Random();

		Address result = new Address();

		result.setStreet(streets[randomGenerator.nextInt(streets.length)]);
		result.setHousenumber(randomGenerator.nextInt(200));
		int cityRandom = randomGenerator.nextInt(cities.length);
		result.setPostalCode(plz[cityRandom]);
		result.setCity(cities[cityRandom]);

		return result;
	}

	@Override
	public String generateSimplePassword() {
		String result = "";
		for (int i = 0; i < 7; i++) {
			int rnd = (int) (Math.random() * 52);
			char base = (rnd < 26) ? 'A' : 'a';
			result += (char) (base + rnd % 26);
		}
		return result;
	}

	@Override
	public List<String> createFavouriteFood() {
		String[] food = getFoodList();
		Random rand = new Random();
		int favCount = rand.nextInt(food.length);

		List<String> result = new ArrayList<String>();
		for (int i = 0; i < favCount; i++) {
			int randIndex = rand.nextInt(food.length);
			if (!result.contains(food[randIndex])) {
				result.add(food[randIndex]);
			}
		}
		return result;
	}

	@Override
	public List<String> createFavouriteDrinks() {
		String[] drinks = getDrinkList();
		Random rand = new Random();
		int favCount = rand.nextInt(drinks.length);

		List<String> result = new ArrayList<String>();
		for (int i = 0; i < favCount; i++) {
			int randIndex = rand.nextInt(drinks.length);
			if (!result.contains(drinks[randIndex])) {
				result.add(drinks[randIndex]);
			}
		}
		return result;
	}

	@Override
	public String[] getStreetNames() {
		return streetNames;
	}

	@Override
	public String[] getCityNames() {
		return cityNames;
	}

	@Override
	public String[] getFoodList() {
		return foodList;
	}

	@Override
	public String[] getDrinkList() {
		return drinkList;
	}

	@Override
	public String createRandomLengthText() {
		String[] words = baseText.split(" ");

		Random wordRandom = new Random();
		String msg = "";
		int randWords = wordRandom.nextInt(words.length);
		for (int j = 0; j < randWords; j++) {
			msg += words[j];
			if (msg.endsWith(",") || msg.endsWith(".")) {
				msg += "\n";
			} else {
				msg += " ";
			}
		}

		return msg;
	}

	@Override
	public Double createRandomMoneyAmount() {
		Double result = new Random().nextDouble() * 1000;
		BigDecimal bd = new BigDecimal(result);
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

	@Override
	public String[] getDefaultPropertyNames() {
		return propertyNames;
	}

	@Override
	public Map<String, String> getDefaultPropertyToLabelMap() {
		return propertyToLabelMap;
	}
}
