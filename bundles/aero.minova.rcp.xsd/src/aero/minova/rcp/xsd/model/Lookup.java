//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.07.08 um 10:37:42 AM CEST 
//


package aero.minova.rcp.xsd.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für lookup complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="lookup"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="param" type="{}type-param" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="key-long-name" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="key-text-name" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="description-name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="procedure-prefix" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="table" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="filter-blocked" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="filter-last-action" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="form" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="use-resolve-params" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "lookup", propOrder = {
    "param"
})
public class Lookup {

    protected List<TypeParam> param;
    @XmlAttribute(name = "key-long-name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String keyLongName;
    @XmlAttribute(name = "key-text-name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String keyTextName;
    @XmlAttribute(name = "description-name")
    protected String descriptionName;
    @XmlAttribute(name = "procedure-prefix")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String procedurePrefix;
    @XmlAttribute(name = "table")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String table;
    @XmlAttribute(name = "filter-blocked")
    protected java.lang.Boolean filterBlocked;
    @XmlAttribute(name = "filter-last-action")
    protected java.lang.Boolean filterLastAction;
    @XmlAttribute(name = "form")
    protected String form;
    @XmlAttribute(name = "use-resolve-params")
    protected java.lang.Boolean useResolveParams;

    /**
     * Gets the value of the param property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the param property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TypeParam }
     * 
     * 
     */
    public List<TypeParam> getParam() {
        if (param == null) {
            param = new ArrayList<TypeParam>();
        }
        return this.param;
    }

    /**
     * Ruft den Wert der keyLongName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyLongName() {
        return keyLongName;
    }

    /**
     * Legt den Wert der keyLongName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyLongName(String value) {
        this.keyLongName = value;
    }

    /**
     * Ruft den Wert der keyTextName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKeyTextName() {
        return keyTextName;
    }

    /**
     * Legt den Wert der keyTextName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKeyTextName(String value) {
        this.keyTextName = value;
    }

    /**
     * Ruft den Wert der descriptionName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescriptionName() {
        return descriptionName;
    }

    /**
     * Legt den Wert der descriptionName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescriptionName(String value) {
        this.descriptionName = value;
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
        return procedurePrefix;
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
     * Ruft den Wert der table-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTable() {
        return table;
    }

    /**
     * Legt den Wert der table-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTable(String value) {
        this.table = value;
    }

    /**
     * Ruft den Wert der filterBlocked-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isFilterBlocked() {
        if (filterBlocked == null) {
            return false;
        } else {
            return filterBlocked;
        }
    }

    /**
     * Legt den Wert der filterBlocked-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setFilterBlocked(java.lang.Boolean value) {
        this.filterBlocked = value;
    }

    /**
     * Ruft den Wert der filterLastAction-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isFilterLastAction() {
        if (filterLastAction == null) {
            return true;
        } else {
            return filterLastAction;
        }
    }

    /**
     * Legt den Wert der filterLastAction-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setFilterLastAction(java.lang.Boolean value) {
        this.filterLastAction = value;
    }

    /**
     * Ruft den Wert der form-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getForm() {
        return form;
    }

    /**
     * Legt den Wert der form-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setForm(String value) {
        this.form = value;
    }

    /**
     * Ruft den Wert der useResolveParams-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isUseResolveParams() {
        if (useResolveParams == null) {
            return false;
        } else {
            return useResolveParams;
        }
    }

    /**
     * Legt den Wert der useResolveParams-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setUseResolveParams(java.lang.Boolean value) {
        this.useResolveParams = value;
    }

}
