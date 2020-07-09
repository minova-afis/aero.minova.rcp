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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Tritt auf, wenn der Binder etwas tut
 * 			
 * 
 * <p>Java-Klasse für onbinder complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="onbinder"&gt;
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
 *       &lt;attribute name="action" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="beforeread"/&gt;
 *             &lt;enumeration value="beforesave"/&gt;
 *             &lt;enumeration value="beforedelete"/&gt;
 *             &lt;enumeration value="beforeclear"/&gt;
 *             &lt;enumeration value="userdefined"/&gt;
 *             &lt;enumeration value="read"/&gt;
 *             &lt;enumeration value="save"/&gt;
 *             &lt;enumeration value="delete"/&gt;
 *             &lt;enumeration value="clear"/&gt;
 *             &lt;enumeration value="userdefined"/&gt;
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
@XmlType(name = "onbinder", propOrder = {
    "binderOrProcedureOrInstance",
    "onfail"
})
public class Onbinder {

    @XmlElements({
        @XmlElement(name = "binder", type = Binder.class),
        @XmlElement(name = "procedure", type = Procedure.class),
        @XmlElement(name = "instance", type = Instance.class),
        @XmlElement(name = "method", type = Method.class),
        @XmlElement(name = "set", type = Set.class)
    })
    protected List<Object> binderOrProcedureOrInstance;
    protected Onfail onfail;
    @XmlAttribute(name = "action", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String action;

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
     * Ruft den Wert der action-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAction() {
        return action;
    }

    /**
     * Legt den Wert der action-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAction(String value) {
        this.action = value;
    }

}
