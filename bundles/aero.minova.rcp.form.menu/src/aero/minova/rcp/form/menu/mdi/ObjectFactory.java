
package aero.minova.rcp.form.menu.mdi;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the aero.minova.rcp.form.menu.mdi package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: aero.minova.rcp.form.menu.mdi
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Main }
     * 
     */
    public Main createMain() {
        return new Main();
    }

    /**
     * Create an instance of {@link Main.Action }
     * 
     */
    public Main.Action createMainAction() {
        return new Main.Action();
    }

    /**
     * Create an instance of {@link Main.Menu }
     * 
     */
    public Main.Menu createMainMenu() {
        return new Main.Menu();
    }

    /**
     * Create an instance of {@link Main.Entry }
     * 
     */
    public Main.Entry createMainEntry() {
        return new Main.Entry();
    }

    /**
     * Create an instance of {@link Main.Toolbar }
     * 
     */
    public Main.Toolbar createMainToolbar() {
        return new Main.Toolbar();
    }

    /**
     * Create an instance of {@link MenuType }
     * 
     */
    public MenuType createMenuType() {
        return new MenuType();
    }

}
