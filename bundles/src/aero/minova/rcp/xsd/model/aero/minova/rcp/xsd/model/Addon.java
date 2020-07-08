//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 generiert 
// Siehe <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Änderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2020.07.08 um 09:27:56 AM CEST 
//


package aero.minova.rcp.xsd.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *       &lt;sequence&gt;
 *         &lt;element name="button" type="{}button" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="events" type="{}events" minOccurs="0"/&gt;
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
    "button",
    "events"
})
@XmlRootElement(name = "addon")
public class Addon {

    protected List<Button> button;
    protected Events events;

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
     * Ruft den Wert der events-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Events }
     *     
     */
    public Events getEvents() {
        return events;
    }

    /**
     * Legt den Wert der events-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Events }
     *     
     */
    public void setEvents(Events value) {
        this.events = value;
    }

}
