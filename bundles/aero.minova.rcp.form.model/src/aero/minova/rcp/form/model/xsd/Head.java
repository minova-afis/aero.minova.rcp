
package aero.minova.rcp.form.model.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse für head complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="head"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice minOccurs="0"&gt;
 *           &lt;element name="button" type="{}button" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="toolbar" type="{}toolbar" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *         &lt;choice&gt;
 *           &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *             &lt;element name="field" type="{}field" maxOccurs="unbounded" minOccurs="0"/&gt;
 *             &lt;element name="separator" type="{}separator" maxOccurs="unbounded" minOccurs="0"/&gt;
 *             &lt;element ref="{}grid" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;/choice&gt;
 *           &lt;element name="section" type="{}section" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "head", propOrder = {
    "button",
    "toolbar",
    "fieldOrSeparatorOrGrid",
    "section"
})
public class Head {

    protected List<Button> button;
    protected Toolbar toolbar;
    @XmlElements({
        @XmlElement(name = "field", type = Field.class),
        @XmlElement(name = "separator", type = Separator.class),
        @XmlElement(name = "grid", type = Grid.class)
    })
    protected List<Object> fieldOrSeparatorOrGrid;
    protected List<Section> section;

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
     * Gets the value of the fieldOrSeparatorOrGrid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fieldOrSeparatorOrGrid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFieldOrSeparatorOrGrid().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Field }
     * {@link Separator }
     * {@link Grid }
     * 
     * 
     */
    public List<Object> getFieldOrSeparatorOrGrid() {
        if (fieldOrSeparatorOrGrid == null) {
            fieldOrSeparatorOrGrid = new ArrayList<Object>();
        }
        return this.fieldOrSeparatorOrGrid;
    }

    /**
     * Gets the value of the section property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the section property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSection().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Section }
     * 
     * 
     */
    public List<Section> getSection() {
        if (section == null) {
            section = new ArrayList<Section>();
        }
        return this.section;
    }

}
