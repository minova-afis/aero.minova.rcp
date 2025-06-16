package aero.minova.rcp.rcp.util;

public class CustomerPrintData {

	private String name;
	private String street;
	private String city;
	private String phone;
	private String fax;
	private String email;

	public CustomerPrintData(String name, String street, String city, String phone, String fax, String email) {
		this.name = name;
		this.street = street;
		this.city = city;
		this.phone = phone;
		this.fax = fax;
		this.email = email;
	}

	public String getXMLString() {
		StringBuilder xml = new StringBuilder();
		xml.append("<Site>\n");
		xml.append("<Address1><![CDATA[" + name + "]]></Address1>\n");
		xml.append("<Address2><![CDATA[" + street + "]]></Address2>\n");
		xml.append("<Address3><![CDATA[" + city + "]]></Address3>\n");
		xml.append("<Phone><![CDATA[" + phone + "]]></Phone>\n");
		xml.append("<Fax><![CDATA[" + fax + "]]></Fax>\n");
		xml.append("<Email><![CDATA[" + email + "]]></Email>\n");
		xml.append("""
				<Application>FreeTables</Application>
				<Logo>logo.gif</Logo>
				</Site>""");
		return xml.toString();
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
