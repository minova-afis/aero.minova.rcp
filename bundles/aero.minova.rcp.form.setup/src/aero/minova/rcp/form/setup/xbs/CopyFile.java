
package aero.minova.rcp.form.setup.xbs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
 *         &lt;choice maxOccurs="unbounded"&gt;
 *           &lt;element ref="{}filecopy" maxOccurs="unbounded"/&gt;
 *           &lt;element ref="{}dircopy" maxOccurs="unbounded"/&gt;
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
@XmlType(name = "", propOrder = {
    "filecopyOrDircopy"
})
@XmlRootElement(name = "copy-file")
public class CopyFile {

    @XmlElements({
        @XmlElement(name = "filecopy", type = Filecopy.class),
        @XmlElement(name = "dircopy", type = Dircopy.class)
    })
    protected List<Object> filecopyOrDircopy;

    /**
     * Gets the value of the filecopyOrDircopy property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the filecopyOrDircopy property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFilecopyOrDircopy().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Filecopy }
     * {@link Dircopy }
     * 
     * 
     */
    public List<Object> getFilecopyOrDircopy() {
        if (filecopyOrDircopy == null) {
            filecopyOrDircopy = new ArrayList<Object>();
        }
        return this.filecopyOrDircopy;
    }

}
