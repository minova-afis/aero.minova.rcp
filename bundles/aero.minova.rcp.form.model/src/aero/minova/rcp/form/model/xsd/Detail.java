
package aero.minova.rcp.form.model.xsd;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java-Klasse für detail complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="detail"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence maxOccurs="unbounded"&gt;
 *         &lt;element name="head" type="{}head" minOccurs="0"/&gt;
 *         &lt;element name="page" type="{}page" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}grid" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{}browser" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="procedure-suffix" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="procedure-prefix" type="{http://www.w3.org/2001/XMLSchema}NCName" default="xpcor" /&gt;
 *       &lt;attribute name="import" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="clear-after-save" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="button-delete-visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="button-new-visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="button-block-visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="button-cancel-visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="button-copy-visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="button-save-visible" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *       &lt;attribute name="delete-requires-all-params" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="read-default-values" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="execute-always" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="class" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="documentation" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}NCName" /&gt;
 *       &lt;attribute name="type"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="masterdata"/&gt;
 *             &lt;enumeration value="booking"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="custom" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="layout"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;enumeration value="expandable"/&gt;
 *             &lt;enumeration value="default"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="offline" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "detail", propOrder = {
    "headAndPageAndGrid"
})
public class Detail {

    @XmlElements({
        @XmlElement(name = "head", type = Head.class),
        @XmlElement(name = "page", type = Page.class),
        @XmlElement(name = "grid", type = Grid.class),
        @XmlElement(name = "browser", type = Browser.class)
    })
    protected List<Object> headAndPageAndGrid;
    @XmlAttribute(name = "procedure-suffix")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String procedureSuffix;
    @XmlAttribute(name = "procedure-prefix")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String procedurePrefix;
    @XmlAttribute(name = "import")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String _import;
    @XmlAttribute(name = "clear-after-save")
    protected java.lang.Boolean clearAfterSave;
    @XmlAttribute(name = "button-delete-visible")
    protected java.lang.Boolean buttonDeleteVisible;
    @XmlAttribute(name = "button-new-visible")
    protected java.lang.Boolean buttonNewVisible;
    @XmlAttribute(name = "button-block-visible")
    protected java.lang.Boolean buttonBlockVisible;
    @XmlAttribute(name = "button-cancel-visible")
    protected java.lang.Boolean buttonCancelVisible;
    @XmlAttribute(name = "button-copy-visible")
    protected java.lang.Boolean buttonCopyVisible;
    @XmlAttribute(name = "button-save-visible")
    protected java.lang.Boolean buttonSaveVisible;
    @XmlAttribute(name = "delete-requires-all-params")
    protected java.lang.Boolean deleteRequiresAllParams;
    @XmlAttribute(name = "read-default-values")
    protected java.lang.Boolean readDefaultValues;
    @XmlAttribute(name = "execute-always")
    protected java.lang.Boolean executeAlways;
    @XmlAttribute(name = "class")
    protected String clazz;
    @XmlAttribute(name = "documentation")
    protected String documentation;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String id;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "custom")
    protected java.lang.Boolean custom;
    @XmlAttribute(name = "layout")
    protected String layout;
    @XmlAttribute(name = "offline")
    protected java.lang.Boolean offline;

    /**
     * Gets the value of the headAndPageAndGrid property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the headAndPageAndGrid property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getHeadAndPageAndGrid().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Head }
     * {@link Page }
     * {@link Grid }
     * {@link Browser }
     * 
     * 
     */
    public List<Object> getHeadAndPageAndGrid() {
        if (headAndPageAndGrid == null) {
            headAndPageAndGrid = new ArrayList<Object>();
        }
        return this.headAndPageAndGrid;
    }

    /**
     * Ruft den Wert der procedureSuffix-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedureSuffix() {
        return procedureSuffix;
    }

    /**
     * Legt den Wert der procedureSuffix-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedureSuffix(String value) {
        this.procedureSuffix = value;
    }

    /**
     * Ruft den Wert der procedurePrefix-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedurePrefix() {
        if (procedurePrefix == null) {
            return "xpcor";
        } else {
            return procedurePrefix;
        }
    }

    /**
     * Legt den Wert der procedurePrefix-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedurePrefix(String value) {
        this.procedurePrefix = value;
    }

    /**
     * Ruft den Wert der import-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImport() {
        return _import;
    }

    /**
     * Legt den Wert der import-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImport(String value) {
        this._import = value;
    }

    /**
     * Ruft den Wert der clearAfterSave-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isClearAfterSave() {
        if (clearAfterSave == null) {
            return false;
        } else {
            return clearAfterSave;
        }
    }

    /**
     * Legt den Wert der clearAfterSave-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setClearAfterSave(java.lang.Boolean value) {
        this.clearAfterSave = value;
    }

    /**
     * Ruft den Wert der buttonDeleteVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isButtonDeleteVisible() {
        if (buttonDeleteVisible == null) {
            return true;
        } else {
            return buttonDeleteVisible;
        }
    }

    /**
     * Legt den Wert der buttonDeleteVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setButtonDeleteVisible(java.lang.Boolean value) {
        this.buttonDeleteVisible = value;
    }

    /**
     * Ruft den Wert der buttonNewVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isButtonNewVisible() {
        if (buttonNewVisible == null) {
            return true;
        } else {
            return buttonNewVisible;
        }
    }

    /**
     * Legt den Wert der buttonNewVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setButtonNewVisible(java.lang.Boolean value) {
        this.buttonNewVisible = value;
    }

    /**
     * Ruft den Wert der buttonBlockVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isButtonBlockVisible() {
        if (buttonBlockVisible == null) {
            return false;
        } else {
            return buttonBlockVisible;
        }
    }

    /**
     * Legt den Wert der buttonBlockVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setButtonBlockVisible(java.lang.Boolean value) {
        this.buttonBlockVisible = value;
    }

    /**
     * Ruft den Wert der buttonCancelVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isButtonCancelVisible() {
        if (buttonCancelVisible == null) {
            return true;
        } else {
            return buttonCancelVisible;
        }
    }

    /**
     * Legt den Wert der buttonCancelVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setButtonCancelVisible(java.lang.Boolean value) {
        this.buttonCancelVisible = value;
    }

    /**
     * Ruft den Wert der buttonCopyVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isButtonCopyVisible() {
        if (buttonCopyVisible == null) {
            return false;
        } else {
            return buttonCopyVisible;
        }
    }

    /**
     * Legt den Wert der buttonCopyVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setButtonCopyVisible(java.lang.Boolean value) {
        this.buttonCopyVisible = value;
    }

    /**
     * Ruft den Wert der buttonSaveVisible-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isButtonSaveVisible() {
        if (buttonSaveVisible == null) {
            return true;
        } else {
            return buttonSaveVisible;
        }
    }

    /**
     * Legt den Wert der buttonSaveVisible-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setButtonSaveVisible(java.lang.Boolean value) {
        this.buttonSaveVisible = value;
    }

    /**
     * Ruft den Wert der deleteRequiresAllParams-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isDeleteRequiresAllParams() {
        if (deleteRequiresAllParams == null) {
            return false;
        } else {
            return deleteRequiresAllParams;
        }
    }

    /**
     * Legt den Wert der deleteRequiresAllParams-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setDeleteRequiresAllParams(java.lang.Boolean value) {
        this.deleteRequiresAllParams = value;
    }

    /**
     * Ruft den Wert der readDefaultValues-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isReadDefaultValues() {
        if (readDefaultValues == null) {
            return false;
        } else {
            return readDefaultValues;
        }
    }

    /**
     * Legt den Wert der readDefaultValues-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setReadDefaultValues(java.lang.Boolean value) {
        this.readDefaultValues = value;
    }

    /**
     * Ruft den Wert der executeAlways-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isExecuteAlways() {
        if (executeAlways == null) {
            return false;
        } else {
            return executeAlways;
        }
    }

    /**
     * Legt den Wert der executeAlways-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setExecuteAlways(java.lang.Boolean value) {
        this.executeAlways = value;
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
     * Ruft den Wert der documentation-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Legt den Wert der documentation-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentation(String value) {
        this.documentation = value;
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
     * Ruft den Wert der custom-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isCustom() {
        if (custom == null) {
            return false;
        } else {
            return custom;
        }
    }

    /**
     * Legt den Wert der custom-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setCustom(java.lang.Boolean value) {
        this.custom = value;
    }

    /**
     * Ruft den Wert der layout-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLayout() {
        return layout;
    }

    /**
     * Legt den Wert der layout-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLayout(String value) {
        this.layout = value;
    }

    /**
     * Ruft den Wert der offline-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isOffline() {
        if (offline == null) {
            return false;
        } else {
            return offline;
        }
    }

    /**
     * Legt den Wert der offline-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setOffline(java.lang.Boolean value) {
        this.offline = value;
    }

}
