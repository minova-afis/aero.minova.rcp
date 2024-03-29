
package aero.minova.rcp.form.model.xsd;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für quantity complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="quantity"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="unit" type="{}unit" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="decimals" default="0"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="11"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="min-value" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="max-value" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="unit-field-name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="unit-field-sql-index" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "quantity", propOrder = {
    "unit"
})
public class Quantity {

    @XmlElement(required = true)
    protected List<Unit> unit;
    @XmlAttribute(name = "decimals")
    protected Integer decimals;
    @XmlAttribute(name = "min-value")
    protected Float minValue;
    @XmlAttribute(name = "max-value")
    protected Float maxValue;
    @XmlAttribute(name = "unit-field-name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String unitFieldName;
    @XmlAttribute(name = "unit-field-sql-index", required = true)
    protected BigInteger unitFieldSqlIndex;

    /**
     * Gets the value of the unit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the unit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUnit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Unit }
     * 
     * 
     */
    public List<Unit> getUnit() {
        if (unit == null) {
            unit = new ArrayList<Unit>();
        }
        return this.unit;
    }

    /**
     * Ruft den Wert der decimals-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getDecimals() {
        if (decimals == null) {
            return  0;
        } else {
            return decimals;
        }
    }

    /**
     * Legt den Wert der decimals-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDecimals(Integer value) {
        this.decimals = value;
    }

    /**
     * Ruft den Wert der minValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMinValue() {
        return minValue;
    }

    /**
     * Legt den Wert der minValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMinValue(Float value) {
        this.minValue = value;
    }

    /**
     * Ruft den Wert der maxValue-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getMaxValue() {
        return maxValue;
    }

    /**
     * Legt den Wert der maxValue-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setMaxValue(Float value) {
        this.maxValue = value;
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
     * Ruft den Wert der unitFieldSqlIndex-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getUnitFieldSqlIndex() {
        return unitFieldSqlIndex;
    }

    /**
     * Legt den Wert der unitFieldSqlIndex-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setUnitFieldSqlIndex(BigInteger value) {
        this.unitFieldSqlIndex = value;
    }

}
