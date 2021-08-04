
package aero.minova.rcp.form.setup.xbs;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="major" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="minor" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" /&gt;
 *       &lt;attribute name="patch" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "version")
public class Version {

    @XmlAttribute(name = "major", required = true)
    protected BigInteger major;
    @XmlAttribute(name = "minor")
    protected BigInteger minor;
    @XmlAttribute(name = "patch")
    protected BigInteger patch;

    /**
     * Ruft den Wert der major-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMajor() {
        return major;
    }

    /**
     * Legt den Wert der major-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMajor(BigInteger value) {
        this.major = value;
    }

    /**
     * Ruft den Wert der minor-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMinor() {
        if (minor == null) {
            return new BigInteger("0");
        } else {
            return minor;
        }
    }

    /**
     * Legt den Wert der minor-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMinor(BigInteger value) {
        this.minor = value;
    }

    /**
     * Ruft den Wert der patch-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPatch() {
        if (patch == null) {
            return new BigInteger("0");
        } else {
            return patch;
        }
    }

    /**
     * Legt den Wert der patch-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPatch(BigInteger value) {
        this.patch = value;
    }

}
