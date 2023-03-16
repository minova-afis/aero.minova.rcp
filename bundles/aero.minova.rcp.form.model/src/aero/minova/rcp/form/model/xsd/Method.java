
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
 * Führt eine Methode innerhalb eines definierten Objektes aus. Die Objekte können mit instance angelegt werden.
 * 			
 * 
 * <p>Java-Klasse für method complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="method"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;element name="param" type="{}param" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="registry-param" type="{}registry-param" maxOccurs="unbounded" minOccurs="0"/&gt;
 *           &lt;element name="ref-param" type="{}ref-param" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="ref-object-name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="return-object-name" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="method-name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="return-object-access" default="form"&gt;
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
@XmlType(name = "method", propOrder = {
    "paramOrRegistryParamOrRefParam"
})
public class Method {

    @XmlElements({
        @XmlElement(name = "param", type = Param.class),
        @XmlElement(name = "registry-param", type = RegistryParam.class),
        @XmlElement(name = "ref-param", type = RefParam.class)
    })
    protected List<Object> paramOrRegistryParamOrRefParam;
    @XmlAttribute(name = "ref-object-name", required = true)
    protected String refObjectName;
    @XmlAttribute(name = "return-object-name")
    protected String returnObjectName;
    @XmlAttribute(name = "method-name", required = true)
    protected String methodName;
    @XmlAttribute(name = "return-object-access")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String returnObjectAccess;

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
     * Ruft den Wert der refObjectName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefObjectName() {
        return refObjectName;
    }

    /**
     * Legt den Wert der refObjectName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefObjectName(String value) {
        this.refObjectName = value;
    }

    /**
     * Ruft den Wert der returnObjectName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnObjectName() {
        return returnObjectName;
    }

    /**
     * Legt den Wert der returnObjectName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnObjectName(String value) {
        this.returnObjectName = value;
    }

    /**
     * Ruft den Wert der methodName-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Legt den Wert der methodName-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMethodName(String value) {
        this.methodName = value;
    }

    /**
     * Ruft den Wert der returnObjectAccess-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnObjectAccess() {
        if (returnObjectAccess == null) {
            return "form";
        } else {
            return returnObjectAccess;
        }
    }

    /**
     * Legt den Wert der returnObjectAccess-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnObjectAccess(String value) {
        this.returnObjectAccess = value;
    }

}
