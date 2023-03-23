
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
 * Erstellt eine Klasseninstanz und speichert sie in einem Objektpool unter dem namen object-name
 * 			
 * 
 * <p>Java-Klasse für instance complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="instance"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="param" type="{}param" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="registry-param" type="{}registry-param" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="ref-param" type="{}ref-param" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="class" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="object-name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="object-access" default="form"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *             &lt;enumeration value="form"/&gt;
 *             &lt;enumeration value="application"/&gt;
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
@XmlType(name = "instance", propOrder = {
    "paramOrRegistryParamOrRefParam"
})
public class Instance {

    @XmlElements({
        @XmlElement(name = "param", type = Param.class),
        @XmlElement(name = "registry-param", type = RegistryParam.class),
        @XmlElement(name = "ref-param", type = RefParam.class)
    })
    protected List<Object> paramOrRegistryParamOrRefParam;
    @XmlAttribute(name = "class", required = true)
    protected String clazz;
    @XmlAttribute(name = "object-name", required = true)
    protected String objectName;
    @XmlAttribute(name = "object-access")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String objectAccess;

    /**
     * Gets the value of the paramOrRegistryParamOrRefParam property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paramOrRegistryParamOrRefParam property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getParamOrRegistryParamOrRefParam().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Param }
     * {@link RegistryParam }
     * {@link RefParam }
     * 
     * 
     */
    public List<Object> getParamOrRegistryParamOrRefParam() {
        if (paramOrRegistryParamOrRefParam == null) {
            paramOrRegistryParamOrRefParam = new ArrayList<Object>();
        }
        return this.paramOrRegistryParamOrRefParam;
    }

    /**
     * Ruft den Wert der clazz-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * Legt den Wert der clazz-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.clazz = value;
    }

    /**
     * Ruft den Wert der objectName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * Legt den Wert der objectName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectName(String value) {
        this.objectName = value;
    }

    /**
     * Ruft den Wert der objectAccess-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectAccess() {
        if (objectAccess == null) {
            return "form";
        } else {
            return objectAccess;
        }
    }

    /**
     * Legt den Wert der objectAccess-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectAccess(String value) {
        this.objectAccess = value;
    }

}
