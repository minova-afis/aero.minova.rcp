
package aero.minova.rcp.form.model.xsd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für field complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="field"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice&gt;
 *           &lt;element name="number" type="{}number"/&gt;
 *           &lt;element name="bignumber" type="{}bignumber"/&gt;
 *           &lt;element name="lookup" type="{}lookup"/&gt;
 *           &lt;element name="percentage" type="{}percentage"/&gt;
 *           &lt;element name="text" type="{}text"/&gt;
 *           &lt;element name="short-date" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *           &lt;element name="long-date" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *           &lt;element name="short-time" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *           &lt;element name="long-time" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *           &lt;element name="date-time" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *           &lt;element name="week-day" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *           &lt;element name="editor" type="{}editor"/&gt;
 *           &lt;element name="money" type="{}money"/&gt;
 *           &lt;element name="quantity" type="{}quantity"/&gt;
 *           &lt;element name="param-string" type="{}param-string"/&gt;
 *           &lt;element name="void" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *           &lt;element name="boolean" type="{}boolean"/&gt;
 *           &lt;element name="color" type="{}color"/&gt;
 *           &lt;element name="label-text" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *           &lt;element name="period" type="{http://www.w3.org/2001/XMLSchema}anyType"/&gt;
 *           &lt;element name="radiobox" type="{}radiobox"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="msg" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *                 &lt;attribute name="type" default="okcancel"&gt;
 *                   &lt;simpleType&gt;
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                       &lt;enumeration value="okcancel"/&gt;
 *                       &lt;enumeration value="yesnocancel"/&gt;
 *                       &lt;enumeration value="yesno"/&gt;
 *                     &lt;/restriction&gt;
 *                   &lt;/simpleType&gt;
 *                 &lt;/attribute&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="tab-index" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="offline" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *       &lt;attribute name="total" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="sql-index" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="validation-order" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="number-columns-spanned" default="2"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *             &lt;enumeration value="2"/&gt;
 *             &lt;enumeration value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="number-rows-spanned"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="auto"/&gt;
 *             &lt;enumeration value="1"/&gt;
 *             &lt;enumeration value="2"/&gt;
 *             &lt;enumeration value="3"/&gt;
 *             &lt;enumeration value="4"/&gt;
 *             &lt;enumeration value="5"/&gt;
 *             &lt;enumeration value="6"/&gt;
 *             &lt;enumeration value="7"/&gt;
 *             &lt;enumeration value="8"/&gt;
 *             &lt;enumeration value="9"/&gt;
 *             &lt;enumeration value="10"/&gt;
 *             &lt;enumeration value="fill"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="fill"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="none"/&gt;
 *             &lt;enumeration value="horizontal"/&gt;
 *             &lt;enumeration value="vertical"/&gt;
 *             &lt;enumeration value="both"/&gt;
 *             &lt;enumeration value="toright"/&gt;
 *             &lt;enumeration value="toleft"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="key-type"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="primary"/&gt;
 *             &lt;enumeration value="user"/&gt;
 *             &lt;enumeration value="static"/&gt;
 *             &lt;enumeration value="none"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="unit-text" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="read-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="unit-field-name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "field", propOrder = {
    "number",
    "bignumber",
    "lookup",
    "percentage",
    "text",
    "shortDate",
    "longDate",
    "shortTime",
    "longTime",
    "dateTime",
    "weekDay",
    "editor",
    "money",
    "quantity",
    "paramString",
    "_void",
    "_boolean",
    "color",
    "labelText",
    "period",
    "radiobox",
    "msg"
})
public class Field {

    protected Number number;
    protected Bignumber bignumber;
    protected Lookup lookup;
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
    protected Editor editor;
    protected Money money;
    protected Quantity quantity;
    @XmlElement(name = "param-string")
    protected ParamString paramString;
    @XmlElement(name = "void")
    protected Object _void;
    @XmlElement(name = "boolean")
    protected aero.minova.rcp.form.model.xsd.Boolean _boolean;
    protected Color color;
    @XmlElement(name = "label-text")
    protected Object labelText;
    protected Object period;
    protected Radiobox radiobox;
    protected List<Field.Msg> msg;
    @XmlAttribute(name = "text")
    protected String label;
    @XmlAttribute(name = "tab-index")
    protected BigInteger tabIndex;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String name;
    @XmlAttribute(name = "default")
    protected String _default;
    @XmlAttribute(name = "required")
    protected java.lang.Boolean required;
    @XmlAttribute(name = "offline")
    protected java.lang.Boolean offline;
    @XmlAttribute(name = "total")
    protected java.lang.Boolean total;
    @XmlAttribute(name = "sql-index", required = true)
    protected BigInteger sqlIndex;
    @XmlAttribute(name = "validation-order")
    protected BigInteger validationOrder;
    @XmlAttribute(name = "number-columns-spanned")
    protected BigInteger numberColumnsSpanned;
    @XmlAttribute(name = "number-rows-spanned")
    protected String numberRowsSpanned;
    @XmlAttribute(name = "fill")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fill;
    @XmlAttribute(name = "key-type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String keyType;
    @XmlAttribute(name = "visible")
    protected java.lang.Boolean visible;
    @XmlAttribute(name = "unit-text")
    protected String unitText;
    @XmlAttribute(name = "read-only")
    protected java.lang.Boolean readOnly;
    @XmlAttribute(name = "unit-field-name")
    protected String unitFieldName;

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
     * Ruft den Wert der lookup-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Lookup }
     *     
     */
    public Lookup getLookup() {
        return lookup;
    }

    /**
     * Legt den Wert der lookup-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Lookup }
     *     
     */
    public void setLookup(Lookup value) {
        this.lookup = value;
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
     * Ruft den Wert der editor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Editor }
     *     
     */
    public Editor getEditor() {
        return editor;
    }

    /**
     * Legt den Wert der editor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Editor }
     *     
     */
    public void setEditor(Editor value) {
        this.editor = value;
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
     * Ruft den Wert der quantity-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Quantity }
     *     
     */
    public Quantity getQuantity() {
        return quantity;
    }

    /**
     * Legt den Wert der quantity-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Quantity }
     *     
     */
    public void setQuantity(Quantity value) {
        this.quantity = value;
    }

    /**
     * Ruft den Wert der paramString-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link ParamString }
     *     
     */
    public ParamString getParamString() {
        return paramString;
    }

    /**
     * Legt den Wert der paramString-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link ParamString }
     *     
     */
    public void setParamString(ParamString value) {
        this.paramString = value;
    }

    /**
     * Ruft den Wert der void-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getVoid() {
        return _void;
    }

    /**
     * Legt den Wert der void-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setVoid(Object value) {
        this._void = value;
    }

    /**
     * Ruft den Wert der boolean-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link aero.minova.rcp.form.model.xsd.Boolean }
     *     
     */
    public aero.minova.rcp.form.model.xsd.Boolean getBoolean() {
        return _boolean;
    }

    /**
     * Legt den Wert der boolean-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link aero.minova.rcp.form.model.xsd.Boolean }
     *     
     */
    public void setBoolean(aero.minova.rcp.form.model.xsd.Boolean value) {
        this._boolean = value;
    }

    /**
     * Ruft den Wert der color-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Color }
     *     
     */
    public Color getColor() {
        return color;
    }

    /**
     * Legt den Wert der color-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Color }
     *     
     */
    public void setColor(Color value) {
        this.color = value;
    }

    /**
     * Ruft den Wert der labelText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getLabelText() {
        return labelText;
    }

    /**
     * Legt den Wert der labelText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setLabelText(Object value) {
        this.labelText = value;
    }

    /**
     * Ruft den Wert der period-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getPeriod() {
        return period;
    }

    /**
     * Legt den Wert der period-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setPeriod(Object value) {
        this.period = value;
    }

    /**
     * Ruft den Wert der radiobox-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Radiobox }
     *     
     */
    public Radiobox getRadiobox() {
        return radiobox;
    }

    /**
     * Legt den Wert der radiobox-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Radiobox }
     *     
     */
    public void setRadiobox(Radiobox value) {
        this.radiobox = value;
    }

    /**
     * Gets the value of the msg property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the msg property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMsg().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Field.Msg }
     * 
     * 
     */
    public List<Field.Msg> getMsg() {
        if (msg == null) {
            msg = new ArrayList<Field.Msg>();
        }
        return this.msg;
    }

    /**
     * Ruft den Wert der label-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLabel() {
        return label;
    }

    /**
     * Legt den Wert der label-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLabel(String value) {
        this.label = value;
    }

    /**
     * Ruft den Wert der tabIndex-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTabIndex() {
        return tabIndex;
    }

    /**
     * Legt den Wert der tabIndex-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTabIndex(BigInteger value) {
        this.tabIndex = value;
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
     * Ruft den Wert der default-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefault() {
        return _default;
    }

    /**
     * Legt den Wert der default-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefault(String value) {
        this._default = value;
    }

    /**
     * Ruft den Wert der required-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isRequired() {
        if (required == null) {
            return false;
        } else {
            return required;
        }
    }

    /**
     * Legt den Wert der required-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setRequired(java.lang.Boolean value) {
        this.required = value;
    }

    /**
     * Ruft den Wert der offline-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public java.lang.Boolean isOffline() {
        return offline;
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
     * Ruft den Wert der total-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isTotal() {
        if (total == null) {
            return false;
        } else {
            return total;
        }
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
     * Ruft den Wert der sqlIndex-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getSqlIndex() {
        return sqlIndex;
    }

    /**
     * Legt den Wert der sqlIndex-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setSqlIndex(BigInteger value) {
        this.sqlIndex = value;
    }

    /**
     * Ruft den Wert der validationOrder-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getValidationOrder() {
        return validationOrder;
    }

    /**
     * Legt den Wert der validationOrder-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setValidationOrder(BigInteger value) {
        this.validationOrder = value;
    }

    /**
     * Ruft den Wert der numberColumnsSpanned-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberColumnsSpanned() {
        if (numberColumnsSpanned == null) {
            return new BigInteger("2");
        } else {
            return numberColumnsSpanned;
        }
    }

    /**
     * Legt den Wert der numberColumnsSpanned-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberColumnsSpanned(BigInteger value) {
        this.numberColumnsSpanned = value;
    }

    /**
     * Ruft den Wert der numberRowsSpanned-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberRowsSpanned() {
        return numberRowsSpanned;
    }

    /**
     * Legt den Wert der numberRowsSpanned-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberRowsSpanned(String value) {
        this.numberRowsSpanned = value;
    }

    /**
     * Ruft den Wert der fill-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFill() {
        return fill;
    }

    /**
     * Legt den Wert der fill-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFill(String value) {
        this.fill = value;
    }

    /**
     * Ruft den Wert der keyType-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyType() {
        return keyType;
    }

    /**
     * Legt den Wert der keyType-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyType(String value) {
        this.keyType = value;
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
     * Ruft den Wert der unitText-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitText() {
        return unitText;
    }

    /**
     * Legt den Wert der unitText-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitText(String value) {
        this.unitText = value;
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
            return false;
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
     * Ruft den Wert der unitFieldName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnitFieldName() {
        return unitFieldName;
    }

    /**
     * Legt den Wert der unitFieldName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnitFieldName(String value) {
        this.unitFieldName = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;attribute name="key" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
     *       &lt;attribute name="type" default="okcancel"&gt;
     *         &lt;simpleType&gt;
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
     *             &lt;enumeration value="okcancel"/&gt;
     *             &lt;enumeration value="yesnocancel"/&gt;
     *             &lt;enumeration value="yesno"/&gt;
     *           &lt;/restriction&gt;
     *         &lt;/simpleType&gt;
     *       &lt;/attribute&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Msg {

        @XmlAttribute(name = "key", required = true)
        protected String key;
        @XmlAttribute(name = "type")
        protected String type;

        /**
         * Ruft den Wert der key-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getKey() {
            return key;
        }

        /**
         * Legt den Wert der key-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setKey(String value) {
            this.key = value;
        }

        /**
         * Ruft den Wert der type-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getType() {
            if (type == null) {
                return "okcancel";
            } else {
                return type;
            }
        }

        /**
         * Legt den Wert der type-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setType(String value) {
            this.type = value;
        }

    }

}
