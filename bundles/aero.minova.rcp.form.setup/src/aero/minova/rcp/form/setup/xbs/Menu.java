
package aero.minova.rcp.form.setup.xbs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
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
 *       &lt;choice maxOccurs="unbounded"&gt;
 *         &lt;element ref="{}menu"/&gt;
 *         &lt;element name="entry"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
 *                 &lt;attribute name="position" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *                 &lt;attribute name="override" type="{http://www.w3.org/2001/XMLSchema}NCName" default="false" /&gt;
 *                 &lt;attribute name="separator-after" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                 &lt;attribute name="separator-before" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
 *                 &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="position" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" /&gt;
 *       &lt;attribute name="text" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="override" type="{http://www.w3.org/2001/XMLSchema}NCName" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "menuOrEntry"
})
@XmlRootElement(name = "menu")
public class Menu {

    @XmlElements({
        @XmlElement(name = "menu", type = Menu.class),
        @XmlElement(name = "entry", type = Menu.Entry.class)
    })
    protected List<Object> menuOrEntry;
    @XmlAttribute(name = "position", required = true)
    protected float position;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "text")
    protected String text;
    @XmlAttribute(name = "override")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String override;

    /**
     * Gets the value of the menuOrEntry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the menuOrEntry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMenuOrEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Menu }
     * {@link Menu.Entry }
     * 
     * 
     */
    public List<Object> getMenuOrEntry() {
        if (menuOrEntry == null) {
            menuOrEntry = new ArrayList<Object>();
        }
        return this.menuOrEntry;
    }

    /**
     * Ruft den Wert der position-Eigenschaft ab.
     * 
     */
    public float getPosition() {
        return position;
    }

    /**
     * Legt den Wert der position-Eigenschaft fest.
     * 
     */
    public void setPosition(float value) {
        this.position = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Legt den Wert der id-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Ruft den Wert der text-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Legt den Wert der text-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * Ruft den Wert der override-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOverride() {
        if (override == null) {
            return "false";
        } else {
            return override;
        }
    }

    /**
     * Legt den Wert der override-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOverride(String value) {
        this.override = value;
    }


    /**
     * <p>Java-Klasse für anonymous complex type.
     * 
     * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}IDREF" /&gt;
     *       &lt;attribute name="position" use="required" type="{http://www.w3.org/2001/XMLSchema}float" /&gt;
     *       &lt;attribute name="override" type="{http://www.w3.org/2001/XMLSchema}NCName" default="false" /&gt;
     *       &lt;attribute name="separator-after" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="separator-before" type="{http://www.w3.org/2001/XMLSchema}boolean" /&gt;
     *       &lt;attribute name="type" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Entry {

        @XmlAttribute(name = "id")
        @XmlIDREF
        @XmlSchemaType(name = "IDREF")
        protected Object id;
        @XmlAttribute(name = "position", required = true)
        protected float position;
        @XmlAttribute(name = "override")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NCName")
        protected String override;
        @XmlAttribute(name = "separator-after")
        protected Boolean separatorAfter;
        @XmlAttribute(name = "separator-before")
        protected Boolean separatorBefore;
        @XmlAttribute(name = "type", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlSchemaType(name = "NCName")
        protected String type;

        /**
         * Ruft den Wert der id-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     
         */
        public Object getId() {
            return id;
        }

        /**
         * Legt den Wert der id-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     
         */
        public void setId(Object value) {
            this.id = value;
        }

        /**
         * Ruft den Wert der position-Eigenschaft ab.
         * 
         */
        public float getPosition() {
            return position;
        }

        /**
         * Legt den Wert der position-Eigenschaft fest.
         * 
         */
        public void setPosition(float value) {
            this.position = value;
        }

        /**
         * Ruft den Wert der override-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOverride() {
            if (override == null) {
                return "false";
            } else {
                return override;
            }
        }

        /**
         * Legt den Wert der override-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOverride(String value) {
            this.override = value;
        }

        /**
         * Ruft den Wert der separatorAfter-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSeparatorAfter() {
            return separatorAfter;
        }

        /**
         * Legt den Wert der separatorAfter-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSeparatorAfter(Boolean value) {
            this.separatorAfter = value;
        }

        /**
         * Ruft den Wert der separatorBefore-Eigenschaft ab.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSeparatorBefore() {
            return separatorBefore;
        }

        /**
         * Legt den Wert der separatorBefore-Eigenschaft fest.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSeparatorBefore(Boolean value) {
            this.separatorBefore = value;
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
            return type;
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

    }

}
