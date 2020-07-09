
package aero.minova.rcp.form.model.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * Konstruktor Parameter
 * 
 * <p>Java-Klasse für param complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="param"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="param" type="{}param" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="registry-param" type="{}registry-param" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="ref-param" type="{}ref-param" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="type" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;union&gt;
 *             &lt;simpleType&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *                 &lt;enumeration value="long"/&gt;
 *                 &lt;enumeration value="int"/&gt;
 *                 &lt;enumeration value="float"/&gt;
 *                 &lt;enumeration value="double"/&gt;
 *                 &lt;enumeration value="boolean"/&gt;
 *                 &lt;enumeration value="byte"/&gt;
 *                 &lt;enumeration value="char"/&gt;
 *                 &lt;enumeration value="short"/&gt;
 *                 &lt;enumeration value="String"/&gt;
 *                 &lt;enumeration value="date"/&gt;
 *                 &lt;enumeration value="Object[]"/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/simpleType&gt;
 *             &lt;simpleType&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *                 &lt;pattern value=".+"/&gt;
 *               &lt;/restriction&gt;
 *             &lt;/simpleType&gt;
 *           &lt;/union&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "param", propOrder = {
    "paramOrRegistryParamOrRefParam"
})
public class Param {

    @XmlElements({
        @XmlElement(name = "param", type = Param.class),
        @XmlElement(name = "registry-param", type = RegistryParam.class),
        @XmlElement(name = "ref-param", type = RefParam.class)
    })
    protected List<Object> paramOrRegistryParamOrRefParam;
    @XmlAttribute(name = "type", required = true)
    protected String type;
    @XmlAttribute(name = "value")
    protected String value;

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

    /**
     * Ruft den Wert der value-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Legt den Wert der value-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

}
