
package aero.minova.rcp.form.model.xsd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="button" type="{}button" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;choice&gt;
 *           &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *             &lt;element name="field" type="{}field" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;/choice&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="events" type="{}events" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="procedure-prefix" type="{http://www.w3.org/2001/XMLSchema}NCName" default="xpcor" /&gt;
 *       &lt;attribute name="procedure-suffix" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="read-requires-all-params" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="delete-requires-all-params" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="value-required" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="button-insert-visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="button-delete-visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="read-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="number-rows-spanned" type="{http://www.w3.org/2001/XMLSchema}integer" default="15" /&gt;
 *       &lt;attribute name="key-cols" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}string" default="" /&gt;
 *       &lt;attribute name="sort-by" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="group-by" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="icon" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="data-handler" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="helper-class" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="execute-announce" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="fill"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="none"/&gt;
 *             &lt;enumeration value="horizontal"/&gt;
 *             &lt;enumeration value="vertical"/&gt;
 *             &lt;enumeration value="both"/&gt;
 *             &lt;enumeration value="toright"/&gt;
 *             &lt;enumeration value="toleft"/&gt;
 *             &lt;enumeration value="auto"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="clear-after-new" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="execute-always" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "button",
    "field",
    "events"
})
@XmlRootElement(name = "grid")
public class Grid {

    protected List<Button> button;
    protected List<Field> field;
    protected List<Events> events;
    @XmlAttribute(name = "procedure-prefix")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String procedurePrefix;
    @XmlAttribute(name = "procedure-suffix", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String procedureSuffix;
    @XmlAttribute(name = "read-requires-all-params")
    protected java.lang.Boolean readRequiresAllParams;
    @XmlAttribute(name = "delete-requires-all-params")
    protected java.lang.Boolean deleteRequiresAllParams;
    @XmlAttribute(name = "value-required")
    protected java.lang.Boolean valueRequired;
    @XmlAttribute(name = "button-insert-visible")
    protected java.lang.Boolean buttonInsertVisible;
    @XmlAttribute(name = "button-delete-visible")
    protected java.lang.Boolean buttonDeleteVisible;
    @XmlAttribute(name = "read-only")
    protected java.lang.Boolean readOnly;
    @XmlAttribute(name = "number-rows-spanned")
    protected BigInteger numberRowsSpanned;
    @XmlAttribute(name = "key-cols")
    protected String keyCols;
    @XmlAttribute(name = "title")
    protected String title;
    @XmlAttribute(name = "sort-by")
    protected String sortBy;
    @XmlAttribute(name = "group-by")
    protected String groupBy;
    @XmlAttribute(name = "icon")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String icon;
    @XmlAttribute(name = "data-handler")
    protected String dataHandler;
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String id;
    @XmlAttribute(name = "helper-class")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String helperClass;
    @XmlAttribute(name = "execute-announce")
    protected java.lang.Boolean executeAnnounce;
    @XmlAttribute(name = "fill")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String fill;
    @XmlAttribute(name = "clear-after-new")
    protected java.lang.Boolean clearAfterNew;
    @XmlAttribute(name = "execute-always")
    protected java.lang.Boolean executeAlways;

    /**
     * Gets the value of the button property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the button property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getButton().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Button }
     * 
     * 
     */
    public List<Button> getButton() {
        if (button == null) {
            button = new ArrayList<Button>();
        }
        return this.button;
    }

    /**
     * Gets the value of the field property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the field property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getField().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Field }
     * 
     * 
     */
    public List<Field> getField() {
        if (field == null) {
            field = new ArrayList<Field>();
        }
        return this.field;
    }

    /**
     * Gets the value of the events property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the events property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEvents().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Events }
     * 
     * 
     */
    public List<Events> getEvents() {
        if (events == null) {
            events = new ArrayList<Events>();
        }
        return this.events;
    }

    /**
     * Ruft den Wert der procedurePrefix-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedurePrefix() {
        if (procedurePrefix == null) {
            return "xpcor";
        } else {
            return procedurePrefix;
        }
    }

    /**
     * Legt den Wert der procedurePrefix-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedurePrefix(String value) {
        this.procedurePrefix = value;
    }

    /**
     * Ruft den Wert der procedureSuffix-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedureSuffix() {
        return procedureSuffix;
    }

    /**
     * Legt den Wert der procedureSuffix-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedureSuffix(String value) {
        this.procedureSuffix = value;
    }

    /**
     * Ruft den Wert der readRequiresAllParams-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isReadRequiresAllParams() {
        if (readRequiresAllParams == null) {
            return false;
        } else {
            return readRequiresAllParams;
        }
    }

    /**
     * Legt den Wert der readRequiresAllParams-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setReadRequiresAllParams(java.lang.Boolean value) {
        this.readRequiresAllParams = value;
    }

    /**
     * Ruft den Wert der deleteRequiresAllParams-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isDeleteRequiresAllParams() {
        if (deleteRequiresAllParams == null) {
            return false;
        } else {
            return deleteRequiresAllParams;
        }
    }

    /**
     * Legt den Wert der deleteRequiresAllParams-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setDeleteRequiresAllParams(java.lang.Boolean value) {
        this.deleteRequiresAllParams = value;
    }

    /**
     * Ruft den Wert der valueRequired-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isValueRequired() {
        if (valueRequired == null) {
            return false;
        } else {
            return valueRequired;
        }
    }

    /**
     * Legt den Wert der valueRequired-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setValueRequired(java.lang.Boolean value) {
        this.valueRequired = value;
    }

    /**
     * Ruft den Wert der buttonInsertVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isButtonInsertVisible() {
        if (buttonInsertVisible == null) {
            return true;
        } else {
            return buttonInsertVisible;
        }
    }

    /**
     * Legt den Wert der buttonInsertVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setButtonInsertVisible(java.lang.Boolean value) {
        this.buttonInsertVisible = value;
    }

    /**
     * Ruft den Wert der buttonDeleteVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isButtonDeleteVisible() {
        if (buttonDeleteVisible == null) {
            return true;
        } else {
            return buttonDeleteVisible;
        }
    }

    /**
     * Legt den Wert der buttonDeleteVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setButtonDeleteVisible(java.lang.Boolean value) {
        this.buttonDeleteVisible = value;
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
     * Ruft den Wert der numberRowsSpanned-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getNumberRowsSpanned() {
        if (numberRowsSpanned == null) {
            return new BigInteger("15");
        } else {
            return numberRowsSpanned;
        }
    }

    /**
     * Legt den Wert der numberRowsSpanned-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setNumberRowsSpanned(BigInteger value) {
        this.numberRowsSpanned = value;
    }

    /**
     * Ruft den Wert der keyCols-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyCols() {
        return keyCols;
    }

    /**
     * Legt den Wert der keyCols-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyCols(String value) {
        this.keyCols = value;
    }

    /**
     * Ruft den Wert der title-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        if (title == null) {
            return "";
        } else {
            return title;
        }
    }

    /**
     * Legt den Wert der title-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Ruft den Wert der sortBy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortBy() {
        return sortBy;
    }

    /**
     * Legt den Wert der sortBy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortBy(String value) {
        this.sortBy = value;
    }

    /**
     * Ruft den Wert der groupBy-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupBy() {
        return groupBy;
    }

    /**
     * Legt den Wert der groupBy-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupBy(String value) {
        this.groupBy = value;
    }

    /**
     * Ruft den Wert der icon-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Legt den Wert der icon-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIcon(String value) {
        this.icon = value;
    }

    /**
     * Ruft den Wert der dataHandler-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataHandler() {
        return dataHandler;
    }

    /**
     * Legt den Wert der dataHandler-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataHandler(String value) {
        this.dataHandler = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der helperClass-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHelperClass() {
        return helperClass;
    }

    /**
     * Legt den Wert der helperClass-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHelperClass(String value) {
        this.helperClass = value;
    }

    /**
     * Ruft den Wert der executeAnnounce-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isExecuteAnnounce() {
        if (executeAnnounce == null) {
            return false;
        } else {
            return executeAnnounce;
        }
    }

    /**
     * Legt den Wert der executeAnnounce-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setExecuteAnnounce(java.lang.Boolean value) {
        this.executeAnnounce = value;
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
     * Ruft den Wert der clearAfterNew-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isClearAfterNew() {
        if (clearAfterNew == null) {
            return true;
        } else {
            return clearAfterNew;
        }
    }

    /**
     * Legt den Wert der clearAfterNew-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setClearAfterNew(java.lang.Boolean value) {
        this.clearAfterNew = value;
    }

    /**
     * Ruft den Wert der executeAlways-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isExecuteAlways() {
        if (executeAlways == null) {
            return false;
        } else {
            return executeAlways;
        }
    }

    /**
     * Legt den Wert der executeAlways-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setExecuteAlways(java.lang.Boolean value) {
        this.executeAlways = value;
    }

}
