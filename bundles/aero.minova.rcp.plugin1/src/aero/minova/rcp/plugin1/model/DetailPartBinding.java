package aero.minova.rcp.plugin1.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.ZonedDateTime;
import java.util.Date;

public class DetailPartBinding {

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	private Integer keylong;
	private String employeeKey;
	private String orderReceiverKey;
	private String serviceContractKey;
	private String serviceKey;
	private String serviceObjectKey;
	private String bookingDate;
	private String startDate;
	private String endDate;
	private String renderedQuantity;
	private String chargedQuantity;
	private String description;
	private String spelling;
	
	
	public static String EMPLOYEEKEY = "employeeKey";
	public static String ORDERRECEIVERKEY = "orderReceiverKey";
	public static String SERVICECONTRACTKEY = "serviceContractKey";
	public static String SERVICEKEY = "serviceKey";
	public static String SERVICEOBJECTKEY = "serviceObjectKey";
	public static String BOOKINGDATE = "bookingDate";
	public static String STARTDATE = "startDate";
	public static String ENDDATE = "endDate";
	public static String RENDEREDQUANTITY = "renderedQuantity";
	public static String CHARGEDQUANTIY = "chargedQuantity";
	public static String DESCRIPTION = "description";

	public Integer getKeylong() {
		return keylong;
	}


	public void setKeylong(Integer keylong) {
		this.keylong = keylong;
	}


	public String getEmployeeKey() {
		return employeeKey;
	}


	public void setEmployeeKey(String employeeKey) {
		this.employeeKey = employeeKey;
	}


	public String getOrderReceiverKey() {
		return orderReceiverKey;
	}


	public void setOrderReceiverKey(String orderReceiverKey) {
		this.orderReceiverKey = orderReceiverKey;
	}


	public String getServiceContractKey() {
		return serviceContractKey;
	}


	public void setServiceContractKey(String serviceContractKey) {
		this.serviceContractKey = serviceContractKey;
	}


	public String getServiceObjectKey() {
		return serviceObjectKey;
	}


	public void setServiceObjectKey(String serviceObjectKey) {
		this.serviceObjectKey = serviceObjectKey;
	}


	public String getBookingDate() {
		return bookingDate;
	}


	public void setBookingDate(String bookingDate) {
		this.bookingDate = bookingDate;
	}


	public String getStartDate() {
		return startDate;
	}


	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}


	public String getEndDate() {
		return endDate;
	}


	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}


	public String getRenderedQuantity() {
		return renderedQuantity;
	}


	public void setRenderedQuantity(String renderedQuantity) {
		this.renderedQuantity = renderedQuantity;
	}


	public String getChargedQuantity() {
		return chargedQuantity;
	}


	public void setChargedQuantity(String chargedQuantity) {
		this.chargedQuantity = chargedQuantity;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		changes.firePropertyChange(DESCRIPTION, this.description, this.description = description);
	}


	public String getSpelling() {
		return spelling;
	}


	public void setSpelling(String spelling) {
		this.spelling = spelling;
		
	}


	public String getServiceKey() {
		return serviceKey;
	}


	public void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        changes.removePropertyChangeListener(l);
    }
	
	
	
}
