package aero.minova.rcp.rcp.util;

public class CustomerPrintData {

	private String name;
	private String street;
	private String city;
	private String phone;
	private String fax;

	public CustomerPrintData(String name, String street, String city, String phone, String fax) {
		this.name = name;
		this.street = street;
		this.city = city;
		this.phone = phone;
		this.fax = fax;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

}
