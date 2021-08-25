
package aero.minova.rcp.form.setup.xbs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für anonymous complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence minOccurs="0"&gt;
 *         &lt;sequence maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;choice&gt;
 *             &lt;element ref="{}module" minOccurs="0"/&gt;
 *             &lt;element ref="{}one-of" minOccurs="0"/&gt;
 *           &lt;/choice&gt;
 *         &lt;/sequence&gt;
 *         &lt;element ref="{}min-required" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "moduleOrOneOf",
    "minRequired"
})
@XmlRootElement(name = "required-modules")
public class RequiredModules {

    @XmlElements({
        @XmlElement(name = "module", type = Module.class),
        @XmlElement(name = "one-of", type = OneOf.class)
    })
    protected List<Object> moduleOrOneOf;
    @XmlElement(name = "min-required")
    protected MinRequired minRequired;

    /**
     * Gets the value of the moduleOrOneOf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the moduleOrOneOf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getModuleOrOneOf().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Module }
     * {@link OneOf }
     * 
     * 
     */
    public List<Object> getModuleOrOneOf() {
        if (moduleOrOneOf == null) {
            moduleOrOneOf = new ArrayList<Object>();
        }
        return this.moduleOrOneOf;
    }

    /**
     * Ruft den Wert der minRequired-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MinRequired }
     *     
     */
    public MinRequired getMinRequired() {
        return minRequired;
    }

    /**
     * Legt den Wert der minRequired-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MinRequired }
     *     
     */
    public void setMinRequired(MinRequired value) {
        this.minRequired = value;
    }

}
