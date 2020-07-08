//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.07.08 um 09:27:56 AM CEST 
//


package aero.minova.rcp.xsd.model;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für column complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="column"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;choice&gt;
 *         &lt;element name="number" type="{}number"/&gt;
 *         &lt;element name="bignumber" type="{}bignumber"/&gt;
 *         &lt;element name="boolean" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element name="percentage" type="{}percentage"/&gt;
 *         &lt;element name="text" type="{}text"/&gt;
 *         &lt;element name="short-date" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element name="long-date" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element name="short-time" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element name="long-time" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element name="date-time" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element name="week-day" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *         &lt;element name="money" type="{}money"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="offline" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="key" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="aggregate"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="SUM"/&gt;
 *             &lt;enumeration value="MIN"/&gt;
 *             &lt;enumeration value="MAX"/&gt;
 *             &lt;enumeration value="AVERAGE"/&gt;
 *             &lt;enumeration value="COUNT"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="size" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="localize-key" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="localize-prefix" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="read-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="group" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "column", propOrder = {
    "number",
    "bignumber",
    "_boolean",
    "percentage",
    "text",
    "shortDate",
    "longDate",
    "shortTime",
    "longTime",
    "dateTime",
    "weekDay",
    "money"
})
public class Column {

    protected Number number;
    protected Bignumber bignumber;
    @XmlElement(name = "boolean")
    protected Object _boolean;
    protected Percentage percentage;
    protected Text text;
    @XmlElement(name = "short-date")
    protected Object shortDate;
    @XmlElement(name = "long-date")
    protected Object longDate;
    @XmlElement(name = "short-time")
    protected Object shortTime;
    @XmlElement(name = "long-time")
    protected Object longTime;
    @XmlElement(name = "date-time")
    protected Object dateTime;
    @XmlElement(name = "week-day")
    protected Object weekDay;
    protected Money money;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "offline")
    protected java.lang.Boolean offline;
    @XmlAttribute(name = "key")
    protected java.lang.Boolean key;
    @XmlAttribute(name = "total")
    protected java.lang.Boolean total;
    @XmlAttribute(name = "aggregate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String aggregate;
    @XmlAttribute(name = "size")
    protected BigInteger size;
    @XmlAttribute(name = "visible")
    protected java.lang.Boolean visible;
    @XmlAttribute(name = "localize-key")
    protected String localizeKey;
    @XmlAttribute(name = "localize-prefix")
    protected String localizePrefix;
    @XmlAttribute(name = "read-only")
    protected java.lang.Boolean readOnly;
    @XmlAttribute(name = "group")
    protected String group;

    /**
     * Ruft den Wert der number-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Number }
     *     
     */
    public Number getNumber() {
        return number;
    }

    /**
     * Legt den Wert der number-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Number }
     *     
     */
    public void setNumber(Number value) {
        this.number = value;
    }

    /**
     * Ruft den Wert der bignumber-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Bignumber }
     *     
     */
    public Bignumber getBignumber() {
        return bignumber;
    }

    /**
     * Legt den Wert der bignumber-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Bignumber }
     *     
     */
    public void setBignumber(Bignumber value) {
        this.bignumber = value;
    }

    /**
     * Ruft den Wert der boolean-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getBoolean() {
        return _boolean;
    }

    /**
     * Legt den Wert der boolean-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setBoolean(Object value) {
        this._boolean = value;
    }

    /**
     * Ruft den Wert der percentage-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Percentage }
     *     
     */
    public Percentage getPercentage() {
        return percentage;
    }

    /**
     * Legt den Wert der percentage-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Percentage }
     *     
     */
    public void setPercentage(Percentage value) {
        this.percentage = value;
    }

    /**
     * Ruft den Wert der text-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Text }
     *     
     */
    public Text getText() {
        return text;
    }

    /**
     * Legt den Wert der text-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Text }
     *     
     */
    public void setText(Text value) {
        this.text = value;
    }

    /**
     * Ruft den Wert der shortDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getShortDate() {
        return shortDate;
    }

    /**
     * Legt den Wert der shortDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setShortDate(Object value) {
        this.shortDate = value;
    }

    /**
     * Ruft den Wert der longDate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getLongDate() {
        return longDate;
    }

    /**
     * Legt den Wert der longDate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setLongDate(Object value) {
        this.longDate = value;
    }

    /**
     * Ruft den Wert der shortTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getShortTime() {
        return shortTime;
    }

    /**
     * Legt den Wert der shortTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setShortTime(Object value) {
        this.shortTime = value;
    }

    /**
     * Ruft den Wert der longTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getLongTime() {
        return longTime;
    }

    /**
     * Legt den Wert der longTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setLongTime(Object value) {
        this.longTime = value;
    }

    /**
     * Ruft den Wert der dateTime-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getDateTime() {
        return dateTime;
    }

    /**
     * Legt den Wert der dateTime-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setDateTime(Object value) {
        this.dateTime = value;
    }

    /**
     * Ruft den Wert der weekDay-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getWeekDay() {
        return weekDay;
    }

    /**
     * Legt den Wert der weekDay-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setWeekDay(Object value) {
        this.weekDay = value;
    }

    /**
     * Ruft den Wert der money-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getMoney() {
        return money;
    }

    /**
     * Legt den Wert der money-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setMoney(Money value) {
        this.money = value;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Ruft den Wert der offline-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isOffline() {
        if (offline == null) {
            return false;
        } else {
            return offline;
        }
    }

    /**
     * Legt den Wert der offline-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setOffline(java.lang.Boolean value) {
        this.offline = value;
    }

    /**
     * Ruft den Wert der key-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean isKey() {
        return key;
    }

    /**
     * Legt den Wert der key-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setKey(java.lang.Boolean value) {
        this.key = value;
    }

    /**
     * Ruft den Wert der total-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean isTotal() {
        return total;
    }

    /**
     * Legt den Wert der total-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setTotal(java.lang.Boolean value) {
        this.total = value;
    }

    /**
     * Ruft den Wert der aggregate-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAggregate() {
        return aggregate;
    }

    /**
     * Legt den Wert der aggregate-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAggregate(String value) {
        this.aggregate = value;
    }

    /**
     * Ruft den Wert der size-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSize() {
        return size;
    }

    /**
     * Legt den Wert der size-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSize(BigInteger value) {
        this.size = value;
    }

    /**
     * Ruft den Wert der visible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isVisible() {
        if (visible == null) {
            return true;
        } else {
            return visible;
        }
    }

    /**
     * Legt den Wert der visible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setVisible(java.lang.Boolean value) {
        this.visible = value;
    }

    /**
     * Ruft den Wert der localizeKey-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalizeKey() {
        return localizeKey;
    }

    /**
     * Legt den Wert der localizeKey-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalizeKey(String value) {
        this.localizeKey = value;
    }

    /**
     * Ruft den Wert der localizePrefix-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalizePrefix() {
        return localizePrefix;
    }

    /**
     * Legt den Wert der localizePrefix-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalizePrefix(String value) {
        this.localizePrefix = value;
    }

    /**
     * Ruft den Wert der readOnly-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isReadOnly() {
        if (readOnly == null) {
            return true;
        } else {
            return readOnly;
        }
    }

    /**
     * Legt den Wert der readOnly-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setReadOnly(java.lang.Boolean value) {
        this.readOnly = value;
    }

    /**
     * Ruft den Wert der group-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroup() {
        return group;
    }

    /**
     * Legt den Wert der group-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroup(String value) {
        this.group = value;
    }

}
