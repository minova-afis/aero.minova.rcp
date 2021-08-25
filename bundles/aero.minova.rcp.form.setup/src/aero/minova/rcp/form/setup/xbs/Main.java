
package aero.minova.rcp.form.setup.xbs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *         &lt;element ref="{}action" maxOccurs="unbounded"/&gt;
 *         &lt;element ref="{}menu"/&gt;
 *         &lt;element ref="{}toolbar" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="icon" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="titel" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "action",
    "menu",
    "toolbar"
})
@XmlRootElement(name = "main")
public class Main {

    @XmlElement(required = true)
    protected List<Action> action;
    @XmlElement(required = true)
    protected Menu menu;
    protected Toolbar toolbar;
    @XmlAttribute(name = "icon")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String icon;
    @XmlAttribute(name = "titel")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String titel;

    /**
     * Gets the value of the action property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the action property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Action }
     * 
     * 
     */
    public List<Action> getAction() {
        if (action == null) {
            action = new ArrayList<Action>();
        }
        return this.action;
    }

    /**
     * Ruft den Wert der menu-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Menu }
     *     
     */
    public Menu getMenu() {
        return menu;
    }

    /**
     * Legt den Wert der menu-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Menu }
     *     
     */
    public void setMenu(Menu value) {
        this.menu = value;
    }

    /**
     * Ruft den Wert der toolbar-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Toolbar }
     *     
     */
    public Toolbar getToolbar() {
        return toolbar;
    }

    /**
     * Legt den Wert der toolbar-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Toolbar }
     *     
     */
    public void setToolbar(Toolbar value) {
        this.toolbar = value;
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
     * Ruft den Wert der titel-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitel() {
        return titel;
    }

    /**
     * Legt den Wert der titel-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitel(String value) {
        this.titel = value;
    }

}
