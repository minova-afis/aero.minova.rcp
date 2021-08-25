
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
 *         &lt;element ref="{}required-modules" minOccurs="0"/&gt;
 *         &lt;element ref="{}required-service" minOccurs="0"/&gt;
 *         &lt;element ref="{}sql-code" minOccurs="0"/&gt;
 *         &lt;element ref="{}mdi-code" minOccurs="0"/&gt;
 *         &lt;element ref="{}xbs-code" minOccurs="0"/&gt;
 *         &lt;element ref="{}static-code" minOccurs="0"/&gt;
 *         &lt;element ref="{}copy-file" minOccurs="0"/&gt;
 *         &lt;element ref="{}schema" minOccurs="0"/&gt;
 *         &lt;element ref="{}execute-java" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "requiredModules",
    "requiredService",
    "sqlCode",
    "mdiCode",
    "xbsCode",
    "staticCode",
    "copyFile",
    "schema",
    "executeJava"
})
@XmlRootElement(name = "setup")
public class Setup {

    @XmlElement(name = "required-modules")
    protected RequiredModules requiredModules;
    @XmlElement(name = "required-service")
    protected RequiredService requiredService;
    @XmlElement(name = "sql-code")
    protected SqlCode sqlCode;
    @XmlElement(name = "mdi-code")
    protected MdiCode mdiCode;
    @XmlElement(name = "xbs-code")
    protected XbsCode xbsCode;
    @XmlElement(name = "static-code")
    protected StaticCode staticCode;
    @XmlElement(name = "copy-file")
    protected CopyFile copyFile;
    protected Schema schema;
    @XmlElement(name = "execute-java")
    protected List<ExecuteJava> executeJava;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;

    /**
     * Ruft den Wert der requiredModules-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RequiredModules }
     *     
     */
    public RequiredModules getRequiredModules() {
        return requiredModules;
    }

    /**
     * Legt den Wert der requiredModules-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RequiredModules }
     *     
     */
    public void setRequiredModules(RequiredModules value) {
        this.requiredModules = value;
    }

    /**
     * Ruft den Wert der requiredService-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link RequiredService }
     *     
     */
    public RequiredService getRequiredService() {
        return requiredService;
    }

    /**
     * Legt den Wert der requiredService-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link RequiredService }
     *     
     */
    public void setRequiredService(RequiredService value) {
        this.requiredService = value;
    }

    /**
     * Ruft den Wert der sqlCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link SqlCode }
     *     
     */
    public SqlCode getSqlCode() {
        return sqlCode;
    }

    /**
     * Legt den Wert der sqlCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link SqlCode }
     *     
     */
    public void setSqlCode(SqlCode value) {
        this.sqlCode = value;
    }

    /**
     * Ruft den Wert der mdiCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link MdiCode }
     *     
     */
    public MdiCode getMdiCode() {
        return mdiCode;
    }

    /**
     * Legt den Wert der mdiCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link MdiCode }
     *     
     */
    public void setMdiCode(MdiCode value) {
        this.mdiCode = value;
    }

    /**
     * Ruft den Wert der xbsCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link XbsCode }
     *     
     */
    public XbsCode getXbsCode() {
        return xbsCode;
    }

    /**
     * Legt den Wert der xbsCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link XbsCode }
     *     
     */
    public void setXbsCode(XbsCode value) {
        this.xbsCode = value;
    }

    /**
     * Ruft den Wert der staticCode-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link StaticCode }
     *     
     */
    public StaticCode getStaticCode() {
        return staticCode;
    }

    /**
     * Legt den Wert der staticCode-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link StaticCode }
     *     
     */
    public void setStaticCode(StaticCode value) {
        this.staticCode = value;
    }

    /**
     * Ruft den Wert der copyFile-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link CopyFile }
     *     
     */
    public CopyFile getCopyFile() {
        return copyFile;
    }

    /**
     * Legt den Wert der copyFile-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link CopyFile }
     *     
     */
    public void setCopyFile(CopyFile value) {
        this.copyFile = value;
    }

    /**
     * Ruft den Wert der schema-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link Schema }
     *     
     */
    public Schema getSchema() {
        return schema;
    }

    /**
     * Legt den Wert der schema-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link Schema }
     *     
     */
    public void setSchema(Schema value) {
        this.schema = value;
    }

    /**
     * Gets the value of the executeJava property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the executeJava property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getExecuteJava().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExecuteJava }
     * 
     * 
     */
    public List<ExecuteJava> getExecuteJava() {
        if (executeJava == null) {
            executeJava = new ArrayList<ExecuteJava>();
        }
        return this.executeJava;
    }

    /**
     * Ruft den Wert der name-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Legt den Wert der name-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
