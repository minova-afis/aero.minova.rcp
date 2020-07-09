//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.07.09 um 09:15:59 AM CEST 
//


package aero.minova.rcp.form.model.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für events complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="events"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="onclick" type="{}onclick" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="onbinder" type="{}onbinder" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="value-change" type="{}value-change" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "events", propOrder = {
    "onclick",
    "onbinder",
    "valueChange"
})
public class Events {

    protected List<Onclick> onclick;
    protected List<Onbinder> onbinder;
    @XmlElement(name = "value-change")
    protected List<ValueChange> valueChange;

    /**
     * Gets the value of the onclick property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onclick property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnclick().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Onclick }
     * 
     * 
     */
    public List<Onclick> getOnclick() {
        if (onclick == null) {
            onclick = new ArrayList<Onclick>();
        }
        return this.onclick;
    }

    /**
     * Gets the value of the onbinder property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the onbinder property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOnbinder().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Onbinder }
     * 
     * 
     */
    public List<Onbinder> getOnbinder() {
        if (onbinder == null) {
            onbinder = new ArrayList<Onbinder>();
        }
        return this.onbinder;
    }

    /**
     * Gets the value of the valueChange property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the valueChange property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValueChange().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ValueChange }
     * 
     * 
     */
    public List<ValueChange> getValueChange() {
        if (valueChange == null) {
            valueChange = new ArrayList<ValueChange>();
        }
        return this.valueChange;
    }

}
