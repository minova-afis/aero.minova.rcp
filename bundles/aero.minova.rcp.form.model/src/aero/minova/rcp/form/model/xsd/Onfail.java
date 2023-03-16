
package aero.minova.rcp.form.model.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * Wird ausgeführt, wenn die Aktionen innerhalb eines Events einen Fehler liefern
 * 			
 * 
 * <p>Java-Klasse für onfail complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="onfail"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="binder" type="{}binder" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element ref="{}procedure" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="instance" type="{}instance" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="method" type="{}method" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="set" type="{}set" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="wizard" type="{}wizard" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "onfail", propOrder = {
    "binderOrProcedureOrInstance"
})
public class Onfail {

    @XmlElements({
        @XmlElement(name = "binder", type = Binder.class),
        @XmlElement(name = "procedure", type = Procedure.class),
        @XmlElement(name = "instance", type = Instance.class),
        @XmlElement(name = "method", type = Method.class),
        @XmlElement(name = "set", type = Set.class),
        @XmlElement(name = "wizard", type = Wizard.class)
    })
    protected List<Object> binderOrProcedureOrInstance;

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
     * {@link Wizard }
     * 
     * 
     */
    public List<Object> getBinderOrProcedureOrInstance() {
        if (binderOrProcedureOrInstance == null) {
            binderOrProcedureOrInstance = new ArrayList<Object>();
        }
        return this.binderOrProcedureOrInstance;
    }

}
