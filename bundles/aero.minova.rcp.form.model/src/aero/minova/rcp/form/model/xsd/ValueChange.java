
package aero.minova.rcp.form.model.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Tritt auf, wenn der Wert einer Komponente geändert wird
 * 			
 * 
 * <p>Java-Klasse für value-change complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="value-change"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="binder" type="{}binder" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element ref="{}procedure" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="instance" type="{}instance" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="method" type="{}method" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="set" type="{}set" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="onfail" type="{}onfail" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="type" default="BOTH"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="USER"/&gt;
 *             &lt;enumeration value="SYSTEM"/&gt;
 *             &lt;enumeration value="BOTH"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="field-name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "value-change", propOrder = {
    "binderOrProcedureOrInstance",
    "onfail"
})
public class ValueChange {

    @XmlElements({
        @XmlElement(name = "binder", type = Binder.class),
        @XmlElement(name = "procedure", type = Procedure.class),
        @XmlElement(name = "instance", type = Instance.class),
        @XmlElement(name = "method", type = Method.class),
        @XmlElement(name = "set", type = Set.class)
    })
    protected List<Object> binderOrProcedureOrInstance;
    protected Onfail onfail;
    @XmlAttribute(name = "type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String type;
    @XmlAttribute(name = "field-name", required = true)
    protected String fieldName;

    /**
     * Gets the value of the binderOrProcedureOrInstance property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the binderOrProcedureOrInstance property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBinderOrProcedureOrInstance().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Binder }
     * {@link Procedure }
     * {@link Instance }
     * {@link Method }
     * {@link Set }
     * 
     * 
     */
    public List<Object> getBinderOrProcedureOrInstance() {
        if (binderOrProcedureOrInstance == null) {
            binderOrProcedureOrInstance = new ArrayList<Object>();
        }
        return this.binderOrProcedureOrInstance;
    }

    /**
     * Ruft den Wert der onfail-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Onfail }
     *     
     */
    public Onfail getOnfail() {
        return onfail;
    }

    /**
     * Legt den Wert der onfail-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Onfail }
     *     
     */
    public void setOnfail(Onfail value) {
        this.onfail = value;
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
            return "BOTH";
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

    /**
     * Ruft den Wert der fieldName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Legt den Wert der fieldName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFieldName(String value) {
        this.fieldName = value;
    }

}
